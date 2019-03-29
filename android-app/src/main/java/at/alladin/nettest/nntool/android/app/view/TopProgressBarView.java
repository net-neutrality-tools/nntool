package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class TopProgressBarView extends RelativeLayout {

    TextView leftText;
    TextView rightText;

    public TopProgressBarView(Context context) {
        super(context);
        init();
    }

    public TopProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.top_progress_bar_view, this);
        leftText = findViewById(R.id.top_progress_bar_view_left_text);
        rightText = findViewById(R.id.top_progress_bar_view_right_text);
    }

    public void setLeftText(final String text) {
        if (leftText != null) {
            leftText.setText(text);
        }
    }

    public void setRightText(final String text) {
        if (rightText != null) {
            rightText.setText(text);
        }
    }
}
