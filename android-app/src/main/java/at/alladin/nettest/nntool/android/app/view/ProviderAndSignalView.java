package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkChangeEvent;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkChangeListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ProviderAndSignalView extends RelativeLayout implements NetworkChangeListener {

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
        Log.i(TAG, event.toString());

        if (event != null) {
            providerText.setText(event.getOperatorName());
        }
    }
}
