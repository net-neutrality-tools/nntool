package at.alladin.nettest.nntool.android.app.workflow.measurement;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.view.CanvasArcDoubleGaugeWithLabels;
import at.alladin.nettest.nntool.android.app.view.TopProgressBarView;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class SpeedFragment  extends Fragment implements ServiceConnection {

    private final static String TAG = "SPEED_FRAGMENT";

    private TopProgressBarView topProgressBarView;

    private CanvasArcDoubleGaugeWithLabels cadlabprogView;

    public static SpeedFragment newInstance() {
        final SpeedFragment fragment = new SpeedFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_speed, container, false);
        topProgressBarView = view.findViewById(R.id.top_progress_bar_view);
        cadlabprogView = view.findViewById(R.id.canvas_arc_double_gauge_with_labels);
        return view;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
