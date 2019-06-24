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
            final int inId = this.getResources().getIdentifier("interface_traffic_in_arrow_" + i, "id", getContext().getPackageName());
            final int outId = this.getResources().getIdentifier("interface_traffic_out_arrow_" + i, "id", getContext().getPackageName());
            trafficInView[i] = findViewById(inId);
            trafficOutView[i] = findViewById(outId);
        }

    }

    @Override
    public void onTrafficUpdate(CurrentInterfaceTraffic currentInterfaceTraffic) {
        if (currentInterfaceTraffic != null) {
            InterfaceTrafficClassificationEnum inClassification = InterfaceTrafficClassificationEnum.classify(currentInterfaceTraffic.getRxBps());
            InterfaceTrafficClassificationEnum outClassification = InterfaceTrafficClassificationEnum.classify(currentInterfaceTraffic.getTxBps());

            updateTrafficView(inClassification, trafficInView);
            updateTrafficView(outClassification, trafficOutView);
        }
    }

    private void updateTrafficView(InterfaceTrafficClassificationEnum value, TextView[] trafficViews) {
        for (int i = 0; i < 3; i++) {
            trafficViews[i].setTextColor(getResources().getColor(R.color.interface_traffic_arrow_inactive));
        }
        switch(value) {
            case HIGH:
                trafficViews[2].setTextColor(getResources().getColor(R.color.interface_traffic_arrow_active));
            case MID:
                trafficViews[1].setTextColor(getResources().getColor(R.color.interface_traffic_arrow_active));
            case LOW:
                trafficViews[0].setTextColor(getResources().getColor(R.color.interface_traffic_arrow_active));
            default:
                break;
        }
    }

    public enum InterfaceTrafficClassificationEnum {
        UNKNOWN(Long.MIN_VALUE, Long.MIN_VALUE),
        NONE(0,10000),  // 0 < x < 10kBit/s
        LOW(10000,(long)1e6),   // 10k < x < 100 kBit/s
        MID((long)1e6, (long)1e9),      // 100k < x  < 1 MBit/s
        HIGH((long)1e9, Long.MAX_VALUE);

        protected long minBps;
        protected long maxBps;

        InterfaceTrafficClassificationEnum(long minBps, long maxBps) {
            this.minBps = minBps;
            this.maxBps = maxBps;
        }

        public long getMinBps() {
            return this.minBps;
        }

        public long getMaxBps() {
            return this.maxBps;
        }

        /**
         *
         * @param bitPerSecond
         * @return
         */
        public static InterfaceTrafficClassificationEnum classify(long bitPerSecond) {
            for (InterfaceTrafficClassificationEnum e : InterfaceTrafficClassificationEnum.values()) {
                if (e.getMinBps() <= bitPerSecond && e.getMaxBps() >= bitPerSecond) {
                    return e;
                }
            }

            return UNKNOWN;
        }
    }

}
