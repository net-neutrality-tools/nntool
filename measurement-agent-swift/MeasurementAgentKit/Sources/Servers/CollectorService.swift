// MeasurementAgentKit: CollectorService.swift, created on 29.03.19
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
class CollectorService: RestApiService {

    init(baseURL: URLConvertible = "http://localhost:8081/api/v1/measurements") {
        super.init(baseURL: baseURL)

        configureTransformer("/", forType: ApiResponse<MeasurementResultResponse>.self)
    }

    ///
    func storeMeasurement(reportDto: LmapReportDto, onSuccess: SuccessCallback<MeasurementResultResponse>?, onFailure: FailureCallback?) {
        request("/", method: .post, requestEntity: reportDto, wrapInApiRequest: false, responseEntityType: MeasurementResultResponse.self, onSuccess: onSuccess, onFailure: onFailure)
    }
}
