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

package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class BottomMeasurementResultSummaryView extends RelativeLayout {

    private TextView pingText;
    private TextView downloadText;
    private TextView uploadText;

    public BottomMeasurementResultSummaryView(Context context) {
        super(context);
        init();
    }

    public BottomMeasurementResultSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomMeasurementResultSummaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.bottom_measurement_result_summary, this);
        pingText = findViewById(R.id.bottom_measurement_result_summary_ping_result);
        downloadText = findViewById(R.id.bottom_measurement_result_summary_download_result);
        uploadText = findViewById(R.id.bottom_measurement_result_summary_upload_result);
    }

    public void setPingText(final String text) {
        if (pingText != null) {
            pingText.setText(text);
        }
    }

    public void setDownloadText(final String text) {
        if (downloadText != null) {
            downloadText.setText(text);
        }
    }

    public void setUploadText(final String text) {
        if (uploadText != null) {
            uploadText.setText(text);
        }
    }
}
