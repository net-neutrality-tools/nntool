package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.main.MeasurementServerSelectionFragment;
import at.alladin.nettest.nntool.android.app.workflow.tc.TermsAndConditionsFragment;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class MeasurementServerSelectionView extends RelativeLayout {

    private final static String TAG = ProviderAndSignalView.class.getSimpleName();

    TextView serverName;

    public MeasurementServerSelectionView(Context context) {
        super(context);
        init();
    }

    public MeasurementServerSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MeasurementServerSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.measurement_server_selection_view, this);
        final View container = findViewById(R.id.measurement_server_selection_container);
        container.setOnClickListener(v -> {
            MeasurementServerSelectionFragment f = MeasurementServerSelectionFragment.newInstance();
            final FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
            f.show(ft, "MSSF");
        });

        serverName = findViewById(R.id.text_measurement_server_selection_name);
        /*
        serverName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasurementServerSelectionFragment f = MeasurementServerSelectionFragment.newInstance();
                final FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                f.show(ft, "MSSF");
            }
        });
        */
    }
}
