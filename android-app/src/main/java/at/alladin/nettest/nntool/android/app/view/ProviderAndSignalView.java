package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.support.telephony.CellInfoWrapper;
import at.alladin.nettest.nntool.android.app.support.telephony.TechnologyType;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkChangeEvent;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkChangeListener;
import at.alladin.nettest.nntool.android.app.util.info.signal.CurrentSignalStrength;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalStrengthChangeEvent;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalStrengthChangeListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ProviderAndSignalView extends RelativeLayout implements NetworkChangeListener, SignalStrengthChangeListener {

    private final static String TAG = ProviderAndSignalView.class.getSimpleName();

    TextView providerText;
    TextView signalText;

    NetworkChangeEvent lastNetworkChangeEvent;

    public ProviderAndSignalView(Context context) {
        super(context);
        init();
    }

    public ProviderAndSignalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProviderAndSignalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.provider_signal_view, this);
        signalText = findViewById(R.id.text_title_signal);
        providerText = findViewById(R.id.text_title_provider);
    }

    @Override
    public void onNetworkChange(NetworkChangeEvent event) {
        lastNetworkChangeEvent = event;
        if (event != null) {
            Log.d(TAG, event.toString());
            if (NetworkChangeEvent.NetworkChangeEventType.NO_CONNECTION.equals(lastNetworkChangeEvent.getEventType())) {
                providerText.setText("-");
                signalText.setText("-");
            }
            else {
                providerText.setText(event.getOperatorName());
            }
        }
    }

    @Override
    public void onSignalStrengthChange(SignalStrengthChangeEvent event) {
        if (event != null) {
            Log.d(TAG, event.toString());
            if (lastNetworkChangeEvent != null
                    && NetworkChangeEvent.NetworkChangeEventType.NO_CONNECTION.equals(lastNetworkChangeEvent.getEventType())) {
                providerText.setText("-");
                signalText.setText("-");
            }
            else if (event.getCurrentSignalStrength() != null) {
                final CellInfoWrapper wrapper = event.getCurrentSignalStrength().getCellInfoWrapper();
                final String signalInfo = getResources().getString(R.string.signal_info,
                        getTechnologyString(event.getCurrentSignalStrength()),
                        event.getCurrentSignalStrength().getSignalDbm());

                signalText.setText(signalInfo);
            }
        }
    }

    private String getTechnologyString(final CurrentSignalStrength ss) {
        final CellInfoWrapper wrapper = ss.getCellInfoWrapper();
        if (wrapper != null && wrapper.getCellIdentityWrapper() != null
                && wrapper.getCellIdentityWrapper().getCellInfoType() != null) {
            final TechnologyType  technologyType = wrapper.getCellIdentityWrapper().getCellInfoType().getTechnologyType();
            if (technologyType != null) {
                Log.d(TAG,"CellInfoType: " + wrapper.getCellIdentityWrapper().getCellInfoType() + " techType: " + technologyType);
                switch (technologyType) {
                    case TECH_WLAN:
                        return getResources().getString(R.string.technology_wlan);
                    case TECH_4G:
                        return getResources().getString(R.string.technology_4g);
                    case TECH_3G:
                        return getResources().getString(R.string.technology_3g);
                    case TECH_2G:
                        return getResources().getString(R.string.technology_2g);
                }
            }
        }

        return "";
    }
}
