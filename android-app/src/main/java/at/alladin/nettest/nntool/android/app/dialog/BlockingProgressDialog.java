package at.alladin.nettest.nntool.android.app.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 * @author roughly based on https://github.com/d-max/spots-dialog.git by Maxim Dybarsky
 */
public class BlockingProgressDialog extends AlertDialog {

    public static class Builder {

        private Context context;
        private String message;
        private int messageId;
        private int themeId;
        private boolean cancelable = true;
        private OnCancelListener cancelListener;

        public Builder(final Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(@StringRes int messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder setTheme(@StyleRes int themeId) {
            this.themeId = themeId;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setCancelListener(OnCancelListener cancelListener) {
            this.cancelListener = cancelListener;
            return this;
        }

        public BlockingProgressDialog build() {
            return new BlockingProgressDialog(context,
                    messageId != 0 ? context.getString(messageId) : message, themeId, cancelable,
                    cancelListener
            );
        }
    }

    private class ProgressLayout extends FrameLayout {

        public ProgressLayout(Context context) {
            super(context);
            init();
        }

        public ProgressLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ProgressLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private TextView textView;

        private void init() {
            final LinearLayout ll = new LinearLayout(getContext());
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            ll.setLayoutParams(layoutParams);
            ll.setGravity(Gravity.CENTER);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            addView(ll);

            final ProgressBar progress = new ProgressBar(getContext());
            progress.setIndeterminate(true);
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(30,10,10,10);
            progress.setLayoutParams(layoutParams);
            ll.addView(progress);

            textView = new TextView(getContext());
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(30,10,10,10);
            textView.setLayoutParams(layoutParams);

            ll.addView(textView);
        }

        public void setMessage(final String text) {
            textView.setText(text);
        }
    }

    private final String message;

    private ProgressLayout progressLayout;

    private BlockingProgressDialog(final Context context, final String message, final int theme,
                                   final boolean cancelable, final OnCancelListener cancelListener) {
        super(context, theme);
        this.message = message;
        setCancelable(cancelable);
        if (cancelListener != null) {
            setOnCancelListener(cancelListener);
        }
    }

    public static int argb(float alpha, float red, float green, float blue) {
        return ((int) (alpha * 255.0f + 0.5f) << 24) |
                ((int) (red   * 255.0f + 0.5f) << 16) |
                ((int) (green * 255.0f + 0.5f) <<  8) |
                (int) (blue  * 255.0f + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        progressLayout = new ProgressLayout(getContext());
        setContentView(progressLayout);
        progressLayout.setMessage(message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setBackgroundDrawable(new ColorDrawable(argb(.9f,1f,1f,1f)));
    }
}
