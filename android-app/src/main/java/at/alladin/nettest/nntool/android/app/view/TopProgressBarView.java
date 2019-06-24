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

    private TextView leftText;
    private TextView rightText;
    private AlladinTextView leftIcon;
    private AlladinTextView rightIcon;

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
        leftIcon = findViewById(R.id.top_progress_bar_view_left_icon);
        rightIcon = findViewById(R.id.top_progress_bar_view_right_icon);
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

    public void setLeftIcon(final String text) {
        if (leftIcon != null) {
            leftIcon.setText(text);
        }
    }

    public void setRightIcon(final String text) {
        if (rightIcon != null) {
            rightIcon.setText(text);
        }
    }
}
