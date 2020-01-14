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
import Siesta

///
public class ResultService: RestApiService {

    init(baseURL: URLConvertible, agent: MeasurementAgent) {
        super.init(baseURL: baseURL, agent: agent)

        configureTransformer("/measurement-agents/*/measurements", forType: ApiResponse<ApiPagination<BriefMeasurementResponse>>.self)
        configureTransformer("/measurement-agents/*/measurements/*/details", forType: ApiResponse<DetailMeasurementResponse>.self)
        configureTransformer("/measurement-agents/*/measurements/*", requestMethods: [.get], forType: ApiResponse<FullMeasurementResponse>.self)
        configureTransformer("/measurement-agents/*/measurements/*", requestMethods: [.delete], forType: ApiResponse<DisassociateResponse>.self)
        configureTransformer("/measurement-agents/*/measurements", requestMethods: [.delete], forType: ApiResponse<DisassociateResponse>.self)
    }

    public func getMeasurements(page: Int = 0, pageSize: Int = 20, onSuccess: SuccessCallback<ApiPagination<BriefMeasurementResponse>>?, onFailure: FailureCallback?) {
        guard let uuid = agent.uuid else {
            onFailure?(RequestError(userMessage: "TODO", cause: NSError(domain: "todo", code: -1234, userInfo: nil)))
            return
        }

        request("/measurement-agents/\(uuid)/measurements", method: .get, responseEntityType: ApiPagination<BriefMeasurementResponse>.self, params: ["page": "\(page)", "size": "\(pageSize)"], onSuccess: onSuccess, onFailure: onFailure)
    }

    ///
    public func getFullMeasurementResult(measurementUuid: String, onSuccess: SuccessCallback<FullMeasurementResponse>?, onFailure: FailureCallback?) {
        guard let uuid = agent.uuid else {
            onFailure?(RequestError(userMessage: "TODO", cause: NSError(domain: "todo", code: -1234, userInfo: nil)))
            return
        }

        request("/measurement-agents/\(uuid)/measurements/\(measurementUuid)", method: .get, responseEntityType: FullMeasurementResponse.self, onSuccess: onSuccess, onFailure: onFailure)
    }

    ///
    public func getDetailedMeasurementResult(measurementUuid: String, onSuccess: SuccessCallback<DetailMeasurementResponse>?, onFailure: FailureCallback?) {
        guard let uuid = agent.uuid else {
            onFailure?(RequestError(userMessage: "TODO", cause: NSError(domain: "todo", code: -1234, userInfo: nil)))
            return
        }

        request("/measurement-agents/\(uuid)/measurements/\(measurementUuid)/details", method: .get, responseEntityType: DetailMeasurementResponse.self, onSuccess: onSuccess, onFailure: onFailure)
    }

    ///
    public func disassociateMeasurement(measurementUuid: String, onSuccess: SuccessCallback<DisassociateResponse>?, onFailure: FailureCallback?) {
        guard let uuid = agent.uuid else {
            onFailure?(RequestError(userMessage: "TODO", cause: NSError(domain: "todo", code: -1234, userInfo: nil)))
            return
        }

        request("/measurement-agents/\(uuid)/measurements/\(measurementUuid)", method: .delete, responseEntityType: DisassociateResponse.self, onSuccess: onSuccess, onFailure: onFailure)
    }
    
    ///
    public func disassociateAgentMeasurements(onSuccess: SuccessCallback<DisassociateResponse>?, onFailure: FailureCallback?) {
        guard let uuid = agent.uuid else {
            onFailure?(RequestError(userMessage: "TODO", cause: NSError(domain: "todo", code: -1234, userInfo: nil)))
            return
        }

        request("/measurement-agents/\(uuid)/measurements", method: .delete, responseEntityType: DisassociateResponse.self, onSuccess: onSuccess, onFailure: onFailure)
    }
}
