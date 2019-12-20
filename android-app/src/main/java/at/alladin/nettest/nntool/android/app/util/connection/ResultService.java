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

package at.alladin.nettest.nntool.android.app.util.connection;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.disassociate.DisassociateResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface ResultService {

    @GET("/api/v1/measurement-agents/{agentUuid}/measurements")
    Call<ApiResponse<ApiPagination<BriefMeasurementResponse>>> getMeasurementsRequest(@Path("agentUuid") String agentUuid);

    @GET("/api/v1/measurement-agents/{agentUuid}/measurements/{measurementUuid}/details")
    Call<ApiResponse<DetailMeasurementResponse>> getGroupedDetailMeasurementRequest(@Path("agentUuid") String agentUuid, @Path("measurementUuid") String measurementUuid);

    @GET("/api/v1/measurement-agents/{agentUuid}/measurements/{measurementUuid}")
    Call<ApiResponse<FullMeasurementResponse>> getFullMeasurementRequest(@Path("agentUuid") String agentUuid, @Path("measurementUuid") String measurementUuid);

    @DELETE("/api/v1/measurement-agents/{agentUuid}/measurements/{measurementUuid}")
    Call<ApiResponse<DisassociateResponse>> deleteSingleMeasurement(@Path("agentUuid") String agentUuid, @Path("measurementUuid") String measurementUuid);

    @DELETE("/api/v1/measurement-agents/{agentUuid}/measurements")
    Call<ApiResponse<DisassociateResponse>> deleteAgent(@Path("agentUuid") String agentUuid);
}
