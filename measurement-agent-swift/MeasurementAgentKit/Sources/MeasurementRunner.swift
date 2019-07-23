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
import nntool_shared_swift

///
public class MeasurementRunner {

    private let agent: MeasurementAgent

    private let controlService: ControlService
    private let agentUuid: String
    private let programs: [MeasurementTypeDto: ProgramConfiguration]
    private let programOrder: [MeasurementTypeDto]

    private var currentProgram: ProgramProtocol?

    private var isCanceled = false

    public var delegate: MeasurementRunnerDelegate?

    ////

    init(agent: MeasurementAgent, controlService: ControlService, agentUuid: String, programOrder: [MeasurementTypeDto], programs: [MeasurementTypeDto: ProgramConfiguration]) {
        self.agent = agent
        self.controlService = controlService
        self.agentUuid = agentUuid
        self.programs = programs
        self.programOrder = programOrder
    }

    ///
    public func startMeasurement(preferredSpeedMeasurementPeer: SpeedMeasurementPeerResponse.SpeedMeasurementPeer? = nil) {
        let controlDto = LmapControlDto()

        controlDto.agent = LmapAgentDto()
        controlDto.agent?.agentId = agentUuid

        ////

        var preferredPeers = [MeasurementTypeDto: String]()
        if let psmp = preferredSpeedMeasurementPeer {
            preferredPeers[.speed] = psmp.identifier
        }

        let capabilities = getCapabilities(preferredPeers: preferredPeers)

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

    private func getCapabilities(preferredPeers: [MeasurementTypeDto: String]) -> [LmapCapabilityTaskDto] {
        let capabilities = programOrder.map { name -> LmapCapabilityTaskDto in
            let config = programs[name]

            let task = LmapCapabilityTaskDto()

            //logger.debug("add task \(name.rawValue) to capabilities")

            task.taskName = name.rawValue
            task.version = config?.version
            //task.program = name.rawValue

            task.preferredMeasurementPeerIdentifier = preferredPeers[name]

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

        let startTime = Date()
        let startTimeNs = TimeHelper.currentTimeNs()

        let informationCollector = SystemInformationCollector.defaultCollector(connectivityService: agent.newIPConnectivityInfo())
        informationCollector.start(startNs: startTimeNs)

        var taskResultDict = [MeasurementTypeDto: SubMeasurementResult]()

        for task in tasks {
            if isCanceled {
                logger.info("Measurement runner is cancelled.")

                informationCollector.stop()
                finish() // TODO: stop? fail?
                return
            }

            guard let taskName = task.name else {
                logger.info("Skipping task because name is not set.")
                continue
            }

            logger.info("Running task \(taskName) with options \(String(describing: task.options)).")

            guard let taskType = MeasurementTypeDto(rawValue: taskName) else {
                continue
            }

            guard let programConfiguration = programs[taskType] else {
                // TODO: should we fail if we don't have the requested program?
                logger.debug("---- NO PROGRAM FOR TASK TYPE \(taskType) ----")
                continue
            }

            guard let programInstance = try? programConfiguration.newInstance(task) else {
                continue
            }

            currentProgram = programInstance

            delegate?.measurementRunner(self, willStartProgramWithName: taskName, implementation: programInstance)

            do {
                // TODO: how to cancel measurement?
                let result = try programInstance.run(relativeStartTimeNs: TimeHelper.currentTimeNs() - startTimeNs)
                //logger.debug(":: program \(taskName) returned result:")
                //logger.debug(result)
                //logger.debug(":: -------")

                taskResultDict[taskType] = result
            } catch {
                // TODO: fail whole measurement or just submeasurement?
            }

            delegate?.measurementRunner(self, didFinishProgramWithName: taskName, implementation: programInstance)

            currentProgram = nil
        }

        informationCollector.stop()

        logger.info("-- all finished")

        submitMeasurementResult(tasks: tasks, taskResultDict: taskResultDict, startTime: startTime, startTimeNs: startTimeNs, timeBasedResult: informationCollector.getResult())
    }

    private func submitMeasurementResult(tasks: [LmapTaskDto], taskResultDict: [MeasurementTypeDto: SubMeasurementResult], startTime: Date, startTimeNs: UInt64, timeBasedResult: TimeBasedResultDto) {
        let endTime = Date()
        let endTimeNs = TimeHelper.currentTimeNs()

        // TODO: measurement finished vs results submitted -> add additional state

        // send_results

        let reportModel = LmapReportDto()

        reportModel.agentId = agentUuid
        //reportModel.groupId = "" // TODO
        //reportModel.measurementPoint = "" // TODO
        reportModel.date = Date()

        timeBasedResult.startTime = startTime
        timeBasedResult.endTime = endTime
        timeBasedResult.durationNs = endTimeNs - startTimeNs

        reportModel.timeBasedResult = timeBasedResult

        reportModel.additionalRequestInfo = ApiRequestHelper.buildApiRequestInfo(
            agentUuid: agentUuid,
            geoLocation: timeBasedResult.geoLocations?.first
        )

        ////

        logger.debug(taskResultDict)

        reportModel.results = []

        for task in tasks {
            guard let taskName = task.name, let taskType = MeasurementTypeDto(rawValue: taskName) else {
                continue
            }

            let taskResult = LmapResultDto()

            taskResult.task = taskName

            if let r = taskResultDict[taskType] {
                taskResult.results = [ r ]
            }

            reportModel.results?.append(taskResult)
        }

        ////

        guard let collectorUrl = extractFirstValidCollectorUrlFromTaskOptions(tasks) else {
            logger.error("No collector URL provieded, aborting measurement.")
            self.fail()
            return
        }

        logger.info("Found collector URL: \(collectorUrl)")

        DispatchQueue.main.sync {
            let collectorService = CollectorService(baseURL: collectorUrl, agent: agent)

            collectorService.storeMeasurement(reportDto: reportModel, onSuccess: { _ in
                self.finish()
            }, onFailure: { error in
                logger.debug(error)
                self.fail() // TODO: error
            })
        }

        // /send_results
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

extension MeasurementRunner {

    func extractFirstValidCollectorUrlFromTaskOptions(_ tasks: [LmapTaskDto]) -> String? {
        for task in tasks {
            guard let url = task.getOptionByName("result_collector_base_url") else {
                continue
            }

            // check if url is valid
            guard URL(string: url) != nil else {
                continue
            }

            // return first valid url
            return url
        }

        return nil
    }
}
