package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowTarget;

public class MeasurementRecentResultSelectorView extends LinearLayout {

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
            ((MainActivity) getContext()).navigateTo(WorkflowTarget.HISTORY);
        });
    }

}
