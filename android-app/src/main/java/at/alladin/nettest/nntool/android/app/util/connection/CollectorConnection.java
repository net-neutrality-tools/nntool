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

import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class CollectorConnection extends AbstractConnection<CollectorService> {

    public CollectorConnection(final String url) {
        super(url, null, null, CollectorService.class);
    }

    public CollectorConnection(final boolean isEncrypted, final String hostname, final int port, final String pathPrefix) {
        super(isEncrypted, hostname, null,  null, port, pathPrefix, CollectorService.class);
    }

    public MeasurementResultResponse sendMeasurementReport(final LmapReportDto reportDto) {
        try {
            return getControllerService().postMeasurementRequest(reportDto, "").execute().body().getData();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
