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

package at.alladin.nettest.nntool.android.app.workflow.result.qos;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.RequestFulllMeasurementTask;
import at.alladin.nettest.nntool.android.app.workflow.ActionBarFragment;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowParameter;
import at.alladin.nettest.nntool.android.app.workflow.result.WorkflowResultParameter;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.EvaluatedQoSResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.QoSTypeDescription;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class ResultQoSFragment extends ActionBarFragment {

    private final static String TAG = ResultQoSFragment.class.getSimpleName();

    private WorkflowResultParameter workflowResultParameter;

    private ProgressBar loadingProgressBar;

    private TextView errorText;

    private GridView qosResultGridView;

    private FullMeasurementResponse measurementResponse;

    public static ResultQoSFragment newInstance (WorkflowParameter workflowParameter) {
        final ResultQoSFragment fragment = new ResultQoSFragment();
        if (workflowParameter instanceof WorkflowResultParameter) {
            fragment.setWorkflowResultParameter((WorkflowResultParameter) workflowParameter);
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_qos_result, container, false);

        loadingProgressBar = v.findViewById(R.id.qos_result_loading_progress_bar);
        errorText = v.findViewById(R.id.qos_result_loading_error_text);
        qosResultGridView = v.findViewById(R.id.qos_result_grid_view);

        if (workflowResultParameter == null) {
            if (errorText != null) {
                errorText.setVisibility(View.VISIBLE);
            }
            return v;
        }

        final RequestFulllMeasurementTask measurementTask = new RequestFulllMeasurementTask(workflowResultParameter.getMeasurementUuid(), getContext(), result -> {
            if (loadingProgressBar != null) {
                loadingProgressBar.setVisibility(View.GONE);
            }

            if (result == null
                    || result.getData() == null
                    || result.getData().getMeasurements() == null
                    || !result.getData().getMeasurements().containsKey(MeasurementTypeDto.QOS)
                    || result.getData().getMeasurements().get(MeasurementTypeDto.QOS) == null) {
                if (errorText != null) {
                    errorText.setVisibility(View.VISIBLE);
                }
            } else {
                measurementResponse = result.getData();
                final Context context = getContext();
                if (context != null) {
                    final FullQoSMeasurement qoSMeasurement = (FullQoSMeasurement) measurementResponse.getMeasurements().get(MeasurementTypeDto.QOS);
                    qosResultGridView.setAdapter(new ResultQoSAdapter(context, new ArrayList<>(parseIntoBoxResults(qoSMeasurement)), qoSMeasurement));
                    qosResultGridView.setOnItemClickListener((parent, view, position, id) -> {
                        final QoSBoxResult item = (QoSBoxResult) qosResultGridView.getItemAtPosition(position);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_fragment_layout, ResultQoSTypeDetailFragment.newInstance(qoSMeasurement, item))
                                .addToBackStack(null)
                                .commit();
                    });

                }
            }

        });
        measurementTask.execute();

        return v;
    }

    private List<QoSBoxResult> parseIntoBoxResults (final FullQoSMeasurement qoSMeasurement) {
        final Map<QoSMeasurementTypeDto, QoSBoxResult> typeToBoxResult = new LinkedHashMap<>();
        for (EvaluatedQoSResult r : qoSMeasurement.getResults()) {
            QoSBoxResult boxResult = typeToBoxResult.get(r.getType());
            if (boxResult == null) {
                boxResult = new QoSBoxResult();
                boxResult.setType(r.getType());
                final QoSTypeDescription typeDescription = qoSMeasurement.getQosTypeToDescriptionMap().get(r.getType());
                if (typeDescription != null) {
                    boxResult.setName(typeDescription.getName());
                    boxResult.setIcon(typeDescription.getIcon());
                }
                boxResult.setSuccessCount(0);
                boxResult.setEvaluationCount(0);
                typeToBoxResult.put(r.getType(), boxResult);
            }
            if (r.getSuccessCount() != null && r.getEvaluationCount() != null) {
                boxResult.setSuccessCount(boxResult.getSuccessCount() + (r.getSuccessCount() == r.getEvaluationCount() ? 1 : 0));
                boxResult.setEvaluationCount(boxResult.getEvaluationCount() + 1);
            }
        }
        return new ArrayList<>(typeToBoxResult.values());
    }

    @Override
    public Integer getTitleStringId() {
        return R.string.title_qos_result;
    }

    @Override
    public Integer getHelpSectionStringId() {
        return R.string.help_link_qos_result_section;
    }

    public WorkflowResultParameter getWorkflowResultParameter() {
        return workflowResultParameter;
    }

    public void setWorkflowResultParameter(WorkflowResultParameter workflowResultParameter) {
        this.workflowResultParameter = workflowResultParameter;
    }

}
