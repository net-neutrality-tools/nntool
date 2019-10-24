package at.alladin.nettest.nntool.android.app.workflow.tc;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.PermissionUtil;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.dialog.AbstractFullScreenDialogFragment;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class TermsAndConditionsFragment extends AbstractFullScreenDialogFragment {

    //the name to be used when the TermsAndConditionFragment is added via fragmentTransaction
    public final static String TERMS_FRAGMENT_TAG = "TC_FRAGMENT";

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
    public int getLayoutId() {
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
                    PermissionUtil.requestLocationPermission(activity);
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
