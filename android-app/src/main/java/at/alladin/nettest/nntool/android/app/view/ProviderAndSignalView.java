package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkChangeEvent;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkChangeListener;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalStrengthChangeEvent;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalStrengthChangeListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ProviderAndSignalView extends RelativeLayout implements NetworkChangeListener, SignalStrengthChangeListener {

    private final static String TAG = ProviderAndSignalView.class.getSimpleName();

    TextView providerText;
    TextView signalText;

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
        if (event != null) {
            Log.i(TAG, event.toString());
            providerText.setText(event.getOperatorName());
        }
    }

    @Override
    public void onSignalStrengthChange(SignalStrengthChangeEvent event) {
        if (event != null) {
            signalText.setText(event.getCurrentSignalStrength().toString());
        }
    }
}
