// ios-app: MeasurementAgent.swift, created on 28.03.19
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
public class MeasurementAgent {

    private let standardUserDefaults = UserDefaults.standard

    let uuidKey: String
    let tcKey = "tc_accepted_version"

    let controlService: ControlService

    var uuid: String? {
        get {
            return standardUserDefaults.string(forKey: uuidKey)
        }
        set {
            standardUserDefaults.set(newValue, forKey: uuidKey)
        }
    }

    var tcAcceptedVersion: Int? {
        get {
            return standardUserDefaults.integer(forKey: tcKey)
        }
        set {
            standardUserDefaults.set(newValue, forKey: tcKey)
        }
    }

    private var programs = [MeasurementTypeDto: ProgramConfiguration]()
    private var programOrder = [MeasurementTypeDto]()

    public init(configuration: MeasurementAgentConfiguration) {
        uuidKey = URL(string: configuration.controlServiceBaseUrl)?.host ?? "default_uuid_key" // TODO: based on controller url or system uuid

        controlService = ControlService(baseURL: configuration.controlServiceBaseUrl)

        // TODO: check if registered -> get settings
    }

    public func registerProgramForTask(_ task: MeasurementTypeDto, withConfiguration config: ProgramConfiguration) {
        programOrder.append(task)
        programs[task] = config
    }

    public func isRegistered() -> Bool {
        return uuid != nil
    }

    public func register(success: @escaping () -> Void, failure: @escaping () -> Void) {
        if uuid != nil {
            success()
            return // already registered
        }

        let registrationRequest = RegistrationRequest()
        registrationRequest.termsAndConditionsAccepted = true
        registrationRequest.termsAndConditionsAcceptedVersion = 1
        registrationRequest.groupName = "test_group_name"

        controlService.registerAgent(registrationRequest: registrationRequest, onSuccess: { response in
            self.uuid = response.agentUuid
            self.tcAcceptedVersion = registrationRequest.termsAndConditionsAcceptedVersion

            //response.settings

            //response.settings

            success()
        }) { _ in
            // TODO
            failure()
        }
    }

    public func updateSettings(success: (() -> Void)? = nil, failure: (() -> Void)? = nil) {
        guard let agentUuid = uuid else {
            // TODO: error -> not registered
            failure?()
            return
        }

        controlService.getSettings(agentUuid: agentUuid, onSuccess: { _ in
            success?()
        }) { _ in
            // TODO
            failure?()
        }
    }

    public func newMeasurementRunner() -> MeasurementRunner? {
        guard let agentUuid = uuid else {
            return nil
        }

        return MeasurementRunner(controlService: controlService, agentUuid: agentUuid, programOrder: programOrder, programs: programs)
    }

    var settings = Settings()

    class Settings {

    }
}
