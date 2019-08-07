// MeasurementAgentKit: ControlService.swift, created on 29.03.19
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
import Siesta

///
class ControlService: RestApiService {

    init(baseURL: URLConvertible = "http://localhost:8080/api/v1", agent: MeasurementAgent) {
        super.init(baseURL: baseURL, agent: agent)

        configureTransformer("/measurement-agents", forType: ApiResponse<RegistrationResponse>.self)
        configureTransformer("/measurement-agents/*/settings", forType: ApiResponse<SettingsResponse>.self)
        configureTransformer("/measurements", forType: LmapControlDto.self)
        configureTransformer("/speed-measurement-peers", forType: ApiResponse<SpeedMeasurementPeerResponse>.self)
        configureTransformer("/ip", forType: ApiResponse<IpResponse>.self)
    }

    ///
    func registerAgent(registrationRequest: RegistrationRequest, onSuccess: SuccessCallback<RegistrationResponse>?, onFailure: FailureCallback?) {
        request(
            "/measurement-agents", method: .post, requestEntity: registrationRequest, wrapInApiRequest: true,
            responseEntityType: RegistrationResponse.self, onSuccess: onSuccess, onFailure: onFailure
        )
    }

    ///
    func getSettings(agentUuid: String/*, settingsRequest: SettingsRequest*/, onSuccess: SuccessCallback<SettingsResponse>?, onFailure: FailureCallback?) {
        //request("/measurement-agents/\(agentUuid)/settings", method: .get, responseEntityType: SettingsResponse.self, onSuccess: onSuccess, onFailure: onFailure)
    }

    func initiateMeasurement(controlDto: LmapControlDto, onSuccess: SuccessCallback<LmapControlDto>?, onFailure: FailureCallback?) {
        request("/measurements", method: .post, requestEntity: controlDto, wrapInApiRequest: false, responseEntityType: LmapControlDto.self, onSuccess: onSuccess, onFailure: onFailure)
    }

    func getSpeedMeasuremnetPeers(onSuccess: SuccessCallback<SpeedMeasurementPeerResponse>?, onFailure: FailureCallback?) {
        request("/speed-measurement-peers", method: .get, responseEntityType: SpeedMeasurementPeerResponse.self, onSuccess: onSuccess, onFailure: onFailure)
    }

    func getIp(onSuccess: SuccessCallback<IpResponse>?, onFailure: FailureCallback?) {
        request("/ip", method: .get, responseEntityType: IpResponse.self, onSuccess: onSuccess, onFailure: onFailure)
    }
}
