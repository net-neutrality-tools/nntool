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

package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.dialog.BlockingProgressDialog;
import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.ObjectMapperUtil;
import at.alladin.nettest.nntool.android.app.util.connection.CollectorConnection;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SendReportTask extends AsyncTask<Void, Void, MeasurementResultResponse> {

    private final static String TAG = SendReportTask.class.getSimpleName();

    private final Context context;
    private final OnTaskFinishedCallback<MeasurementResultResponse> callback;
    private BlockingProgressDialog progressDialog;
    private LmapReportDto reportDto;
    private String collectorUrl;

    public SendReportTask(final Context context, final LmapReportDto reportDto,
                          final String collectorUrl, final OnTaskFinishedCallback<MeasurementResultResponse> callback) {
        this.context = context;
        this.callback = callback;
        this.collectorUrl = collectorUrl;
        this.reportDto = reportDto;
        try {
            final String json = ObjectMapperUtil.createBasicObjectMapper()
                    .writeValueAsString(reportDto);
            Log.d(TAG, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPreExecute() {
        progressDialog = new BlockingProgressDialog.Builder(context)
                .setCancelable(false)
                .setMessage(R.string.task_send_report_dialog_info)
                .build();
        progressDialog.show();
    }

    @Override
    protected MeasurementResultResponse doInBackground(Void... voids) {
        final CollectorConnection collectorConnection = ConnectionUtil.createCollectorConnection(context, collectorUrl);
        return collectorConnection.sendMeasurementReport(reportDto);
    }

    @Override
    protected void onPostExecute(MeasurementResultResponse result) {
        if (callback != null) {
            callback.onTaskFinished(result);
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
