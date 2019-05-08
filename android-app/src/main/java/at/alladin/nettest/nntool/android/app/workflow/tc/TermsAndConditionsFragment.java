package at.alladin.nettest.nntool.android.app.workflow.tc;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.workflow.AbstractFullScreenDialogFragment;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class TermsAndConditionsFragment extends AbstractFullScreenDialogFragment {

    public final static int TERMS_AND_CONDITIONS_VERSION = 1;

    /**
     *
     * @return
     */
    public static TermsAndConditionsFragment newInstance() {
        final TermsAndConditionsFragment fragment = new TermsAndConditionsFragment();
        return fragment;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    public int getViewId() {
        return R.layout.tc_dialog;
    }

    @Override
    public void onViewCreated(View v) {
        final Activity activity = getActivity();

        setOnConfirmListener(() -> {
            PreferencesUtil.setTermsAndConditionsAccepted(getContext(), TERMS_AND_CONDITIONS_VERSION);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) activity).registerMeasurementAgent();
                }
            });
            dismiss();
        });

        setOnCloseListener(() -> activity.finishAffinity());

        setToolbarTitle("Terms & Conditions", true);

        final WebView web = v.findViewById(R.id.webview_tc);
        web.loadUrl("file:///android_asset/tc.html");
    }
}
