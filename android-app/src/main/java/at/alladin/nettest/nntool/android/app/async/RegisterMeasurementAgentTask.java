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

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.dialog.BlockingProgressDialog;
import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.connection.ControllerConnection;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.util.RequestUtil;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class RegisterMeasurementAgentTask extends AsyncTask<Void, Void, RegistrationResponse> {

    final OnTaskFinishedCallback<RegistrationResponse> callback;

    final Context context;

    BlockingProgressDialog progressDialog;

    public RegisterMeasurementAgentTask(final Context context, final OnTaskFinishedCallback<RegistrationResponse> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
       progressDialog = new BlockingProgressDialog.Builder(context)
               .setCancelable(false)
               .setMessage(R.string.task_register_agent_dialog_info)
               .build();
       progressDialog.show();
    }

    @Override
    protected RegistrationResponse doInBackground(Void... voids) {
        final ControllerConnection controllerConnection = ConnectionUtil.createControllerConnection(context, false);
        final RegistrationResponse response = controllerConnection.registerMeasurementAgent(
                RequestUtil.prepareApiRegistrationRequest(context));
        return response;
    }

    @Override
    protected void onPostExecute(RegistrationResponse result) {
        if (result != null) {
            PreferencesUtil.setAgentUuid(context, result.getAgentUuid());
            if (result.getSettings() != null && result.getSettings().getQosTypeInfo() != null && result.getSettings().getQosTypeInfo().size() > 0) {
                PreferencesUtil.setQoSTypeInfo(context, result.getSettings().getQosTypeInfo());
            }
            if (result.getSettings() != null && result.getSettings().getUrls() != null) {
                PreferencesUtil.setSettingsUrls(context, result.getSettings().getUrls());
            }
        }

        if (callback != null) {
            callback.onTaskFinished(result);
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
