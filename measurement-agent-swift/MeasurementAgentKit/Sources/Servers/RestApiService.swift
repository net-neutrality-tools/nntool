// MeasurementAgentKit: RestApiService.swift, created on 29.03.19
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
import XCGLogger
import nntool_shared_swift

///
public class RestApiService {

    public typealias SuccessCallback<R> = (_ entity: R) -> Void
    public typealias FailureCallback = (_ error: RequestError) -> Void

    enum ApiError: Error {
        case emptyData
        case requestEntityDecodeFailed
    }

    struct AnyCodable: Codable { }

    struct ResponseLogExtractor: ResponseTransformer {

        func process(_ response: Response) -> Response {
            switch response {
            case .success(let entity):

                logger.debug("--- RESPONSE ---")
                logger.debug(String(data: entity.content as? Data ?? Data(), encoding: .utf8)!)
                logger.debug("--- /RESPONSE ---")

                return response
            default:
                return response
            }
        }
    }

    struct ErrorMessageExtractor: ResponseTransformer {

        let decoder: JSONDecoder

        func process(_ response: Response) -> Response {
            switch response {
            case .success:
                return response
            case .failure(var error):
                if let errorData = error.entity?.content as? Data {
                    if let apiResponse = try? decoder.decode(ApiResponse<AnyCodable>.self, from: errorData) {
                        let firstError = apiResponse.errors?.first

                        logger.debugExec {
                            logger.debug("--- REST API error ---")
                            logger.debug(String(describing: firstError?.path))
                            logger.debug(String(describing: firstError?.error))
                            logger.debug(String(describing: firstError?.message))
                            logger.debug(String(describing: firstError?.exception))
                            logger.debug(String(describing: firstError?.trace))
                            logger.debug("--- / REST API error ---")
                        }

                        if let firstMessage = firstError?.message {
                            error.userMessage = firstMessage
                        }
                    }
                }

                //return logTransformation(.failure(error))
                return .failure(error)
            }
        }
    }

    ////

    let service: Service

    let agent: MeasurementAgent

    let jsonDecoder = JsonHelper.getPreconfiguredJSONDecoder()
    let jsonEncoder = JsonHelper.getPreconfiguredJSONEncoder()

    init(baseURL: URLConvertible?, agent: MeasurementAgent) {
        service = Service(baseURL: baseURL, standardTransformers: [])
        self.agent = agent

        configureTransformer("/versions", forType: ApiResponse<VersionResponse>.self)

        service.configure {
            $0.pipeline[.rawData].add(ResponseLogExtractor())
            $0.pipeline[.cleanup].add(ErrorMessageExtractor(decoder: self.jsonDecoder))
        }
    }

    func configureTransformer<T: Decodable>(_ pattern: ConfigurationPatternConvertible, forType type: T.Type) {
        service.configureTransformer(pattern) {
            try self.jsonDecoder.decode(type, from: $0.content)
        }
    }

    ////

    func request<R: Decodable, W: ApiResponse<R>>(_ path: String, method: RequestMethod, responseEntityType: R.Type, params: [String: String]? = nil, onSuccess: SuccessCallback<R>?, onFailure: FailureCallback?) {

        logger.debug("REQUESTING \(path)")

        UIApplication.shared.isNetworkActivityIndicatorVisible = true

        let request =
            buildResource(path: path, params: params)
                .request(method)

        handleRequest(request: request, responseEntityType: responseEntityType, onSuccess: onSuccess, onFailure: onFailure)
    }

    func request<T: Codable, R: Decodable, W: ApiResponse<R>>(_ path: String, method: RequestMethod, requestEntity: T, wrapInApiRequest: Bool = true, responseEntityType: R.Type, params: [String: String]? = nil, onSuccess: SuccessCallback<R>?, onFailure: FailureCallback?) {

        logger.debug("REQUESTING \(path)")

        UIApplication.shared.isNetworkActivityIndicatorVisible = true

        let requestData: Data
        do {
            if wrapInApiRequest {
                let apiRequest = ApiRequest<T>()
                apiRequest.data = requestEntity

                apiRequest.requestInfo = ApiRequestHelper.buildApiRequestInfo(
                    agentUuid: agent.uuid,
                    geoLocation: nil // TODO
                )

                requestData = try jsonEncoder.encode(apiRequest)
            } else {
                requestData = try jsonEncoder.encode(requestEntity)
            }
        } catch {
            onFailure?(RequestError(userMessage: "could not encode request entity", cause: ApiError.requestEntityDecodeFailed))
            return
        }

        logger.debug("--- REQUEST BODY ---")
        logger.debug(String(data: requestData, encoding: .utf8)!) // debug print
        logger.debug("--- /REQUEST BODY ---")

        let request =
            buildResource(path: path, params: params)
                .request(method, data: requestData, contentType: "application/json")

        handleRequest(request: request, responseEntityType: responseEntityType, onSuccess: onSuccess, onFailure: onFailure)
    }

    private func handleRequest<R: Decodable, W: ApiResponse<R>>(request: Request, responseEntityType: R.Type, onSuccess: SuccessCallback<R>?, onFailure: FailureCallback?) {
        request.onSuccess({ responseEntity in
            if let r = responseEntity.content as? W { // Extract data if enclosed in ApiResponse
                if let d = r.data {
                    onSuccess?(d)
                    UIApplication.shared.isNetworkActivityIndicatorVisible = false
                    return
                }
            } else if let r = responseEntity.content as? R { // Use entity directly
                onSuccess?(r)
                UIApplication.shared.isNetworkActivityIndicatorVisible = false
                return
            }

            // TODO: or request entity has wrong type!
            onFailure?(RequestError(userMessage: "no data from server (TODO: better description)", cause: ApiError.emptyData))
        }).onFailure { requestError in
            onFailure?(requestError)
        }

        UIApplication.shared.isNetworkActivityIndicatorVisible = false
    }

    private func buildResource(path: String, params: [String: String]?) -> Resource {
        var resource = service.resource(path)

        params?.forEach({ param in
            let (key, value) = param

            logger.debug("ADDING PARAM \(key) -> \(value)")

            resource = resource.withParam(key, value)
        })

        return resource
    }

    ////

    public func getVersion(onSuccess: @escaping SuccessCallback<VersionResponse>, onFailure: @escaping FailureCallback) {
        request("/versions", method: .get, responseEntityType: VersionResponse.self, onSuccess: onSuccess, onFailure: onFailure)
    }
}
