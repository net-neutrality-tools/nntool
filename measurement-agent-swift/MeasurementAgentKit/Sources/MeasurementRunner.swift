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

///
public class MeasurementRunner {

    private let controlService: ControlService
    private let agentUuid: String
    private let programs: [MeasurementTypeDto: ProgramConfiguration]
    private let programOrder: [MeasurementTypeDto]

    public var delegate: MeasurementRunnerDelegate?

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

        let capabilities = programOrder.map { name -> LmapCapabilityTaskDto in
            let config = programs[name]

            let task = LmapCapabilityTaskDto()

            //print("add task \(name.rawValue) to capabilities")

            task.taskName = name.rawValue
            task.version = config?.version
            //task.program = name.rawValue

            return task
        }

        controlDto.capabilities = LmapCapabilityDto()
        controlDto.capabilities?.tasks = capabilities

        delegate?.measurementWillStartRequestingControlModel(self)
        // TODO: delegate queue

        controlService.initiateMeasurement(controlDto: controlDto, onSuccess: { responseControlDto in
            self.delegate?.measurementDidReceiveControlModel(self)

            DispatchQueue.global(qos: .unspecified).async {
                self.runMeasurement(controlDto: responseControlDto)
            }
        }) { _ in
            self.delegate?.measurementDidFail(self)
        }
    }

    public func stopMeasurement() {

    }

    ////

    private func runMeasurement(controlDto: LmapControlDto) {
        delegate?.measurementDidStart(self)

        // TODO: which queue?
        // TODO: parse events and schedules
        // TODO: timeout
        // TODO: user abort

        // task order: schedule -> action

        if let tasks = controlDto.tasks {
            if tasks.count == 0 {
                // TODO: fail measurement, no tasks provided
                self.delegate?.measurementDidFail(self)
                return
            }

            for task in tasks {
                print("run task \(task.name), \(task.options)")
                
                //task.name
                //task.options
                
                let taskType = MeasurementTypeDto(rawValue: task.name!)! // TODO: !, !
                
                guard let programConfiguration = programs[taskType] else {
                    // TODO: should we fail if we don't have the requested program?
                    return
                }
                
                let programInstance = programConfiguration.newInstance(task)
                
                do {
                    let result = try programInstance.run()
                } catch {
                    // TODO
                }
            }
            
            print("-- all finished")
            
            delegate?.measurementDidFinish(self)
            return
        }

        delegate?.measurementDidFail(self)
    }
}
