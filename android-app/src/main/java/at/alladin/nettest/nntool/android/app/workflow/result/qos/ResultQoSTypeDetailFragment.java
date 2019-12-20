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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.ActionBarFragment;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.EvaluatedQoSResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.QoSTypeDescription;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class ResultQoSTypeDetailFragment extends ActionBarFragment {

    private final static String TAG = ResultQoSTypeDetailFragment.class.getSimpleName();

    private ListView qosResultDetailsView;

    private TextView qosTypeView;

    private TextView qosTypeDescription;

    private FullQoSMeasurement qoSMeasurement;

    private QoSBoxResult qoSBoxResult;

    private QoSMeasurementTypeDto qosType;

    public static ResultQoSTypeDetailFragment newInstance (final FullQoSMeasurement qoSMeasurement, final QoSBoxResult qoSBoxResult) {
        final ResultQoSTypeDetailFragment fragment = new ResultQoSTypeDetailFragment();
        fragment.setQoSMeasurement(qoSMeasurement);
        fragment.setQoSBoxResult(qoSBoxResult);
        fragment.setQosType(qoSBoxResult.getType());
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_qos_detail_result, container, false);

        qosResultDetailsView = v.findViewById(R.id.qos_result_detail_list_view);

        qosTypeView = v.findViewById(R.id.qos_result_detail_qos_type);
        qosTypeDescription = v.findViewById(R.id.qos_result_detail_qos_type_description);

        final QoSTypeDescription typeDescription = qoSMeasurement.getQosTypeToDescriptionMap().get(qosType);

        if (qosTypeView != null && typeDescription != null) {
            qosTypeView.setText(typeDescription.getName());
        }
        if (qosTypeDescription != null && typeDescription != null) {
            qosTypeDescription.setText(typeDescription.getDescription());
        }
        final List<EvaluatedQoSResult> evaluatedResultsOfType = new ArrayList<>();
        for (EvaluatedQoSResult eq : qoSMeasurement.getResults()) {
            if (qosType == eq.getType()) {
                evaluatedResultsOfType.add(eq);
            }
        }

        qosResultDetailsView.setAdapter(new ResultQoSTypeDetailAdapter(getContext(), evaluatedResultsOfType));
        qosResultDetailsView.setOnItemClickListener((parent, view, position, id) -> {
            final EvaluatedQoSResult item = (EvaluatedQoSResult) qosResultDetailsView.getItemAtPosition(position);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_layout, ResultQoSSingleResultFragment.newInstance(item, qoSMeasurement.getKeyToTranslationMap(), position + 1))
                    .addToBackStack(null)
                    .commit();
        });

        return v;
    }

    @Override
    public Integer getTitleStringId() {
        return R.string.title_qos_result;
    }

    @Override
    public Integer getHelpSectionStringId() {
        return R.string.help_link_qos_result_section;
    }

    public FullQoSMeasurement getQoSMeasurement() {
        return qoSMeasurement;
    }

    public void setQoSMeasurement(FullQoSMeasurement qoSMeasurement) {
        this.qoSMeasurement = qoSMeasurement;
    }

    public QoSBoxResult getQoSBoxResult() {
        return qoSBoxResult;
    }

    public void setQoSBoxResult(QoSBoxResult qoSBoxResult) {
        this.qoSBoxResult = qoSBoxResult;
    }

    public QoSMeasurementTypeDto getQosType() {
        return qosType;
    }

    public void setQosType(QoSMeasurementTypeDto qosType) {
        this.qosType = qosType;
    }
}
