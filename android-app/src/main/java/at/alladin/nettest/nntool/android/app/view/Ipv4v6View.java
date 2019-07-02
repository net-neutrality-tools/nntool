package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.info.system.CurrentSystemInfo;
import at.alladin.nettest.nntool.android.app.util.info.system.SystemInfoEvent;
import at.alladin.nettest.nntool.android.app.util.info.system.SystemInfoListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class Ipv4v6View extends RelativeLayout {

    private final static String TAG = Ipv4v6View.class.getSimpleName();

    TextView ipv4Text;
    TextView ipv6Text;

    public Ipv4v6View(Context context) {
        super(context);
        init();
    }

    public Ipv4v6View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Ipv4v6View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.ipv4v6_view, this);
        ipv4Text = findViewById(R.id.ipv4_status_text);
        ipv6Text = findViewById(R.id.ipv6_status_text);
    }
}
