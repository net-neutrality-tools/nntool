package at.alladin.nettest.nntool.android.app.workflow.measurement;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.info.InformationServiceListener;
import at.alladin.nettest.nntool.android.app.util.info.interfaces.CurrentInterfaceTraffic;
import at.alladin.nettest.nntool.android.app.util.info.interfaces.InterfaceTrafficUpdateListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class InterfaceTrafficView extends LinearLayout implements InterfaceTrafficUpdateListener {

    private final static String TAG = InterfaceTrafficView.class.getSimpleName();

    private TextView[] trafficInView;
    private TextView[] trafficOutView;

    public InterfaceTrafficView(Context context) {
        super(context);
        init();
    }

    public InterfaceTrafficView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InterfaceTrafficView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.interface_traffic_view, this);

        trafficInView = new TextView[3];
        trafficOutView = new TextView[3];
        for (int i = 0; i < 3; i++) {
            final int inId = this.getResources().getIdentifier("nerdmode_traffic_in_arrow_" + i, "id", getContext().getPackageName());
            final int outId = this.getResources().getIdentifier("nerdmode_traffic_out_arrow_" + i, "id", getContext().getPackageName());
            trafficInView[i] = findViewById(inId);
            trafficOutView[i] = findViewById(outId);
        }

    }

    @Override
    public void onTrafficUpdate(CurrentInterfaceTraffic currentInterfaceTraffic) {

    }
}
