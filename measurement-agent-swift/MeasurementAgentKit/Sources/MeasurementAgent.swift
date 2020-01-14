/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
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

    private(set) var controlService: ControlService!

    private(set) var controlServiceV4: ControlService?
    private(set) var controlServiceV6: ControlService?

    public private(set) var resultService: ResultService?
    public private(set) var mapService: MapService?

    private var settings: LocalSettings

    public var uuid: String? {
        return settings.uuid
    }

    public var isIpv4Only = false {
        didSet {
            updateControlService()
        }
    }

    private var programConfigurations = [MeasurementTypeDto: ProgramConfiguration]()
    private var programOrder = [MeasurementTypeDto]()

    public var programs: [ProgramConfiguration] {
        get {
            return [ProgramConfiguration](programConfigurations.values).sorted { $0.name < $1.name }
        }
    }

    public func enableProgram(_ programName: String, enable: Bool = true) {
        guard let measurementTypeDto = programConfigurations.filter({ $0.value.name == programName }).first?.key else {
            return
        }

        programConfigurations[measurementTypeDto]?.isEnabled = enable
    }

    public func enableProgramTask(_ programName: String, taskName: String, enable: Bool = true) {
        guard let measurementTypeDto = programConfigurations.filter({ $0.value.name == programName }).first?.key else {
            return
        }

        programConfigurations[measurementTypeDto]?.enableTask(name: taskName, enable: enable)
    }

    public init(configuration: MeasurementAgentConfiguration) {
        do {
            settings = try MeasurementAgentSettingsHelper.getLocalSettings(controllerServiceBaseUrl: configuration.controllerServiceBaseUrl)
        } catch {
            settings = LocalSettings(controllerServiceBaseUrl: configuration.controllerServiceBaseUrl)
        }

        createServices(localSettings: self.settings)

        // TODO: check if registered -> get settings // currently done in AppDelegate
        // TODO: allow override of specific urls via config.
    }

    public func registerProgramForTask(_ task: MeasurementTypeDto, withConfiguration config: ProgramConfiguration) {
        programOrder.append(task)
        programConfigurations[task] = config
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
            self.settings.uuid = response.agentUuid
            self.settings.tcAcceptedVersion = registrationRequest.termsAndConditionsAcceptedVersion

            if let s = response.settings {
                self.updateAndSaveSettings(settings: s)
            }

            success()
        }, onFailure: { error in
            // TODO
            logger.error(error)
            failure()
        })
    }

    public func updateSettings(success: (() -> Void)? = nil, failure: (() -> Void)? = nil) {
        guard let agentUuid = uuid else {
            // TODO: error -> not registered
            failure?()
            return
        }

        controlService.getSettings(agentUuid: agentUuid, onSuccess: { response in
            self.updateAndSaveSettings(settings: response)

            success?()
        }, onFailure: { _ in
            // TODO
            failure?()
        })
    }

    private func updateAndSaveSettings(settings: SettingsResponse) {
        guard let controllerServiceBaseUrl = settings.urls.controllerService else {
            return
        }

        self.settings.controllerServiceBaseUrl = controllerServiceBaseUrl
        self.settings.controllerServiceBaseUrlIpv4 = settings.urls.controllerServiceIpv4
        self.settings.controllerServiceBaseUrlIpv6 = settings.urls.controllerServiceIpv6

        self.settings.resultServiceBaseUrl = settings.urls.resultService
        self.settings.mapServiceBaseUrl = settings.urls.mapService
        self.settings.websiteBaseUrl = settings.urls.website

        createServices(localSettings: self.settings)

        if let qoSProgram = programs.filter({ $0.name.lowercased() == "qos" }).first {
            for i in 0..<qoSProgram.availableTasks.count {
                if let val = settings.qosTypeInfo?[qoSProgram.availableTasks[i].name.uppercased()] {
                    qoSProgram.availableTasks[i].updateLocalization(localizedName: val.name, localizedDescription: val.description)
                }
            }
        }

        try? MeasurementAgentSettingsHelper.saveLocalSettings(self.settings)
    }
    
    public func deleteAgent(success: @escaping () -> Void, failure: @escaping () -> Void) {
        resultService?.disassociateAgentMeasurements(onSuccess: { r in
            self.settings.uuid = nil
            self.settings.tcAcceptedVersion = nil
            
            try? MeasurementAgentSettingsHelper.saveLocalSettings(self.settings)
            
            success()
        }, onFailure: { error in
            logger.error(error)
            failure()
        })
    }

    private func createServices(localSettings: LocalSettings) {
        if let controllerServiceBaseUrlV4 = localSettings.controllerServiceBaseUrlIpv4 {
            controlServiceV4 = ControlService(baseURL: controllerServiceBaseUrlV4, agent: self)
        }

        // controllerServiceBaseUrl has both A and AAAA recods. If IPv6 is available, it will be used as default.
        // Therefore it is save to use controllerServiceBaseUrl for the controlServiceV6.
        controlServiceV6 = ControlService(baseURL: localSettings.controllerServiceBaseUrl, agent: self)

        updateControlService()

        if let resultServiceBaseUrl = localSettings.resultServiceBaseUrl {
            resultService = ResultService(baseURL: resultServiceBaseUrl, agent: self)
        }

        if let mapServiceBaseUrl = localSettings.mapServiceBaseUrl {
            mapService = MapService(baseURL: mapServiceBaseUrl, agent: self)
        }
    }

    private func updateControlService() {
        if isIpv4Only {
            controlService = controlServiceV4
            logger.debug("<--> controlService = controlServiceV4")
        } else {
            controlService = controlServiceV6
            logger.debug("<--> controlService = controlServiceV6")
        }
    }

    //////////

    public func isAtLeastOneMeasurementTaskEnabled() -> Bool {
        return enabledProgramsCount() > 0
    }

    public func isProgramTypeEnabled(_ type: String) -> Bool {
        guard let key = MeasurementTypeDto(rawValue: type) else {
            return false
        }

        return programConfigurations.filter({ $0.key == key && $0.value.isEnabled && !$0.value.enabledTasks.isEmpty }).count > 0
    }

    public func enabledProgramsCount() -> Int {
        return programConfigurations.filter({ $0.value.isEnabled && !$0.value.enabledTasks.isEmpty }).count
    }

    public func newMeasurementRunner() -> MeasurementRunner? {
        guard let agentUuid = uuid else {
            return nil
        }

        return MeasurementRunner(agent: self, controlService: controlService, agentUuid: agentUuid, programOrder: programOrder, programs: programConfigurations)
    }

    public func getSpeedMeasurementPeers(onSuccess: @escaping ([SpeedMeasurementPeerResponse.SpeedMeasurementPeer]) -> Void, onFailure: @escaping (Error) -> Void) {
        controlService.getSpeedMeasurementPeers(onSuccess: { response in
            if response.speedMeasurementPeers.isEmpty {
                onFailure(NSError(domain: "todo", code: -1234, userInfo: nil)) // TODO: improve error
            } else {
                onSuccess(response.speedMeasurementPeers)
            }
        }, onFailure: { error in
            onFailure(error)
        })
    }

    public func newIPConnectivityInfo() -> IPConnectivityInfo {
        let controllerServiceBaseUrlV4 = settings.controllerServiceBaseUrlIpv4 ?? settings.controllerServiceBaseUrl
        let controllerServiceBaseUrlV6 = settings.controllerServiceBaseUrlIpv6 ?? settings.controllerServiceBaseUrl

        let createIPConnectivityInfo = { () -> IPConnectivityInfo in
            let controlServiceV4 = ControlService(baseURL: controllerServiceBaseUrlV4, agent: self)
            let controlServiceV6 = ControlService(baseURL: controllerServiceBaseUrlV6, agent: self)

            return IPConnectivityInfo(controlServiceV4: controlServiceV4, controlServiceV6: controlServiceV6)
        }

        var connectivityInfo: IPConnectivityInfo!

        if Thread.isMainThread {
            connectivityInfo = createIPConnectivityInfo()
        } else {
            DispatchQueue.main.sync {
                connectivityInfo = createIPConnectivityInfo()
            }
        }

        return connectivityInfo
    }
}
