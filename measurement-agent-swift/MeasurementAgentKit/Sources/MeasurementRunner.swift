// MeasurementAgentKit: MeasurementRunner.swift, created on 16.04.19
/*******************************************************************************
 * Copyright 2019 Benjamin Pucher (alladin-IT GmbH)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import Foundation
import CodableJSON

///
public class MeasurementRunner {

    private let controlService: ControlService
    private let agentUuid: String
    private let programs: [MeasurementTypeDto: ProgramConfiguration]
    private let programOrder: [MeasurementTypeDto]

    private var currentProgram: ProgramProtocol?

    private var isCanceled = false

    public var delegate: MeasurementRunnerDelegate?

    ////

    private var startTime: Date?
    private var startTimeNs: UInt64?

    init(controlService: ControlService, agentUuid: String, programOrder: [MeasurementTypeDto], programs: [MeasurementTypeDto: ProgramConfiguration]) {
        self.controlService = controlService
        self.agentUuid = agentUuid
        self.programs = programs
        self.programOrder = programOrder
    }

    ///
    public func startMeasurement() {
        let controlDto = LmapControlDto()

        controlDto.agent = LmapAgentDto()
        controlDto.agent?.agentId = agentUuid

        ////

        let capabilities = getCapabilities()

        controlDto.capabilities = LmapCapabilityDto()
        controlDto.capabilities?.tasks = capabilities

        delegate?.measurementWillStartRequestingControlModel(self)
        // TODO: delegate queue

        controlService.initiateMeasurement(controlDto: controlDto, onSuccess: { responseControlDto in
            self.delegate?.measurementDidReceiveControlModel(self)

            DispatchQueue.global(qos: .unspecified).async {
                self.runMeasurement(controlDto: responseControlDto)
            }
        }, onFailure: { err in
            logger.error("err: \(err)")
            self.delegate?.measurementDidFail(self)
        })
    }

    public func stopMeasurement() {
        isCanceled = true
        currentProgram?.cancel()
    }

    ////

    private func getCapabilities() -> [LmapCapabilityTaskDto] {
        let capabilities = programOrder.map { name -> LmapCapabilityTaskDto in
            let config = programs[name]

            let task = LmapCapabilityTaskDto()

            //print("add task \(name.rawValue) to capabilities")

            task.taskName = name.rawValue
            task.version = config?.version
            //task.program = name.rawValue

            return task
        }

        return capabilities
    }

    private func runMeasurement(controlDto: LmapControlDto) {
        delegate?.measurementDidStart(self)

        // TODO: which queue?
        // TODO: parse events and schedules
        // TODO: timeout
        // TODO: user abort

        // task order: schedule -> action

        guard let tasks = controlDto.tasks, tasks.count > 0 else {
            fail()
            return
        }

        startTime = Date()
        //startTimeNs = // TODO

        var taskResultDict = [MeasurementTypeDto: SubMeasurementResult]()

        for task in tasks {
            logger.info("run task \(task.name), \(task.options)")

            if isCanceled {
                logger.info("measurement runner is cancelled")

                finish() // TODO: stop? fail?
                return
            }

            //task.name
            //task.options

            let taskType = MeasurementTypeDto(rawValue: task.name!)! // TODO: !, !

            guard let programConfiguration = programs[taskType] else {
                // TODO: should we fail if we don't have the requested program?
                logger.debug("---- NO PROGRAM FOR TASK TYPE \(taskType) ----")
                continue
            }

            guard let programInstance = try? programConfiguration.newInstance(task) else {
                continue
            }

            currentProgram = programInstance

            delegate?.measurementRunner(self, willStartProgramWithName: task.name!, implementation: programInstance) // TODO: !

            do {
                // TODO: how to cancel measurement?
                let result = try programInstance.run()
                logger.debug(":: program \(task.name!) returned result:")
                logger.debug(result)
                logger.debug(":: -------")

                taskResultDict[taskType] = result
            } catch {
                // TODO
            }

            delegate?.measurementRunner(self, didFinishProgramWithName: task.name!, implementation: programInstance) // !

            currentProgram = nil
        }

        logger.info("-- all finished")
        
        // TODO: measurement finished vs results submitted -> add additional state

        // send_results
        // TODO: send results

        // TODO: get collector service url from LMAP control model
        var collectorService: CollectorService?
        DispatchQueue.main.sync {
             collectorService = CollectorService(baseURL: "http://localhost:18081/api/v1")
        }

        let reportModel = LmapReportDto()

        reportModel.agentId = agentUuid
        reportModel.groupId = "TODO"
        reportModel.measurementPoint = "TODO"
        reportModel.date = Date()

        let apiRequestInfo = ApiRequestInfo()

        apiRequestInfo.agentId = agentUuid
        apiRequestInfo.agentType = .mobile
        apiRequestInfo.apiLevel = "Swift 5" // ?
        apiRequestInfo.appGitRevision = "TODO"
        apiRequestInfo.appVersionCode = nil
        apiRequestInfo.appVersionName = "TODO"
        apiRequestInfo.codeName = UIDevice.current.model
        apiRequestInfo.geoLocation = nil
        apiRequestInfo.language = {
            let preferredLanguages = Locale.preferredLanguages

            if preferredLanguages.count < 1 {
                return "en" //"de"
            }

            let sep = preferredLanguages[0].components(separatedBy: "-")
            return sep[0]
        }()
        apiRequestInfo.model = "TODO"//UIDevice.current.model
        apiRequestInfo.osName = UIDevice.current.systemName
        apiRequestInfo.osVersion = UIDevice.current.systemVersion
        apiRequestInfo.timezone = TimeZone.current.identifier

        reportModel.additionalRequestInfo = apiRequestInfo

        ////

        let timeBasedResult = TimeBasedResultDto()

        timeBasedResult.cellLocations = nil // not available on iOS
        timeBasedResult.cpuUsage = nil // TODO
        //timeBasedResult.durationNs
        timeBasedResult.endTime = Date()
        timeBasedResult.geoLocations = nil // TODO
        timeBasedResult.memUsage = nil
        timeBasedResult.networkPointsInTime = nil
        timeBasedResult.signals = nil // not available on iOS
        timeBasedResult.startTime = startTime

        reportModel.timeBasedResult = timeBasedResult

        logger.debug(taskResultDict)

        reportModel.results = []

        for task in tasks {
            let taskResult = LmapResultDto()

            taskResult.task = task.name

            let taskType = MeasurementTypeDto(rawValue: task.name!)!

            if let r = taskResultDict[taskType] {
                logger.debug("!!!!---!!!")
                logger.debug((r as? QoSMeasurementResult)?.objectiveResults)
                taskResult.results = [ r ]
            }

            reportModel.results?.append(taskResult)
        }

        logger.debug((reportModel.results?.first?.results?.first as? QoSMeasurementResult)?.objectiveResults)

        ////

        DispatchQueue.main.sync {
            collectorService?.storeMeasurement(reportDto: reportModel, onSuccess: { _ in
                self.finish()
            }, onFailure: { error in
                print(error)
                self.fail() // TODO: error
            })
        }

        // /send_results

        //finish() // TODO: remove if submission works
    }

    private func finish() {
        delegate?.measurementDidFinish(self)
        stop()
    }

    private func fail(/*error: Error*/) {
        delegate?.measurementDidFail(self)
        stop()
    }

    private func stop() {
        delegate?.measurementDidStop(self)
    }
}
