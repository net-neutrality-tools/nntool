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
import android.widget.LinearLayout;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowTarget;
import at.alladin.nettest.nntool.android.app.workflow.result.WorkflowResultParameter;

public class MeasurementRecentResultSelectorView extends LinearLayout {

    private String measurementUuid;

    public MeasurementRecentResultSelectorView(Context context) {
        super(context);
        init();
    }

    public MeasurementRecentResultSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MeasurementRecentResultSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.measurement_recent_result_view, this);
        this.setOnClickListener( v -> {
            WorkflowResultParameter param = new WorkflowResultParameter();
            param.setMeasurementUuid(measurementUuid);
            ((MainActivity) getContext()).navigateTo(WorkflowTarget.RESULT, param);
        });
    }

    public String getMeasurementUuid() {
        return measurementUuid;
    }

    public void setMeasurementUuid(String measurementUuid) {
        this.measurementUuid = measurementUuid;
    }
}
