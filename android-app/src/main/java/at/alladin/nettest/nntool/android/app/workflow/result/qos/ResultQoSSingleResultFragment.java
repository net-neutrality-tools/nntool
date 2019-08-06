package at.alladin.nettest.nntool.android.app.workflow.result.qos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.ActionBarFragment;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.EvaluatedQoSResult;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class ResultQoSSingleResultFragment extends ActionBarFragment {

    private final static String TAG = ResultQoSSingleResultFragment.class.getSimpleName();

    private TextView qosSummary;

    private TextView qosDescription;

    private LinearLayout resultList;

    private EvaluatedQoSResult evaluatedQoSResult;

    private Map<String, String> keyToTranslationMap;

    private int testNumber;

    public static ResultQoSSingleResultFragment newInstance (final EvaluatedQoSResult evaluatedQoSResult, final Map<String, String> keyToTranslationMap, final int testNumber) {
        final ResultQoSSingleResultFragment fragment = new ResultQoSSingleResultFragment();
        fragment.setEvaluatedQoSResult(evaluatedQoSResult);
        fragment.setKeyToTranslationMap(keyToTranslationMap);
        fragment.setTestNumber(testNumber);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_qos_single_result, container, false);

        qosSummary = v.findViewById(R.id.qos_result_single_summary);
        qosDescription = v.findViewById(R.id.qos_result_single_description);
        resultList = v.findViewById(R.id.qos_result_single_list);

        if (evaluatedQoSResult != null && evaluatedQoSResult.getSummary() != null) {
            qosSummary.setText(evaluatedQoSResult.getSummary());
        }

        if (keyToTranslationMap != null && evaluatedQoSResult.getResultKeyMap() != null) {
            for (Map.Entry<String, EvaluatedQoSResult.QoSResultOutcome> entry : evaluatedQoSResult.getResultKeyMap().entrySet()) {
                final View resultItem = inflater.inflate(R.layout.qos_single_result_item, null, false);
                final ImageView img = resultItem.findViewById(R.id.qos_result_single_results_color);
                switch (entry.getValue()) {
                    case OK:
                        img.setImageDrawable(getContext().getDrawable(R.drawable.rectangle_success));
                        break;
                    case INFO:
                        img.setImageDrawable(getContext().getDrawable(R.drawable.rectangle_info));
                        break;
                    case FAIL:
                        img.setImageDrawable(getContext().getDrawable(R.drawable.rectangle_failure));
                        break;
                }

                final TextView text = resultItem.findViewById(R.id.qos_result_single_results);
                text.setText(keyToTranslationMap.containsKey(entry.getKey()) ? keyToTranslationMap.get(entry.getKey()) : entry.getKey());
                resultList.addView(resultItem);
            }
        }

        if (evaluatedQoSResult != null && evaluatedQoSResult.getDescription() != null) {
            qosDescription.setText(evaluatedQoSResult.getDescription());
        }

        return v;
    }

    @Override
    public Integer getTitleStringId() {
        return R.string.result_qos_test_list_item_header;
    }

    @Override
    public Object[] getTitleArgs() {
        return new Object[] {testNumber};
    }

    @Override
    public Integer getHelpSectionStringId() {
        return R.string.help_link_qos_result_section;
    }

    public EvaluatedQoSResult getEvaluatedQoSResult() {
        return evaluatedQoSResult;
    }

    public void setEvaluatedQoSResult(EvaluatedQoSResult evaluatedQoSResult) {
        this.evaluatedQoSResult = evaluatedQoSResult;
    }

    public Map<String, String> getKeyToTranslationMap() {
        return keyToTranslationMap;
    }

    public void setKeyToTranslationMap(Map<String, String> keyToTranslationMap) {
        this.keyToTranslationMap = keyToTranslationMap;
    }

    public int getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(int testNumber) {
        this.testNumber = testNumber;
    }
}
