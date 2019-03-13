package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class AlladinTextView extends AppCompatTextView {
    public AlladinTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public AlladinTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlladinTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        /*Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "alladin_icons.ttf");
        setTypeface(tf);*/
    }
}
