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

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ResultConnection extends AbstractConnection<ResultService> {

    public ResultConnection(final String url) {
        super(url, null, ResultService.class);
    }

    public ResultConnection(final boolean isEncrypted, final String hostname, final int port, final String pathPrefix) {
        super(isEncrypted, hostname, null, port, pathPrefix, ResultService.class);
    }

    public ApiResponse<ApiPagination<BriefMeasurementResponse>> requestHistory(final String agentUuid) {
        try {
            return getControllerService().getMeasurementsRequest(agentUuid).execute().body();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ApiResponse<DetailMeasurementResponse> requestGroupedDetails (final String agentUuid, final String measurementUuid) {
        try {
            return getControllerService().getGroupedDetailMeasurementRequest(agentUuid, measurementUuid).execute().body();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public ApiResponse<DisassociateResponse> disassociateMeasurement (final String agentUuid, final String measurementUuid) {
        try {
            return getControllerService().deleteSingleMeasurement(agentUuid, measurementUuid).execute().body();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ApiResponse<DisassociateResponse> disassociateAgent (final String agentUuid) {
        try {
            return getControllerService().deleteAgent(agentUuid).execute().body();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ApiResponse<FullMeasurementResponse> requestFullMeasurement (final String agentUuid, final String measurementUuid) {
        try {
            return getControllerService().getFullMeasurementRequest(agentUuid, measurementUuid).execute().body();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
