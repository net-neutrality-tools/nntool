package at.alladin.nettest.nntool.android.app.workflow.tc;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class TermsAndConditionsFragment extends DialogFragment {

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.tc_dialog, container, false);

        final Toolbar toolbar = v.findViewById(R.id.toolbar_tc);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(view -> getActivity().finishAffinity());
        toolbar.setTitle("Terms & Conditions");

        final Button b = v.findViewById(R.id.button_confirm_tc);
        b.setOnClickListener(view -> {
            PreferencesUtil.setTermsAndConditionsAccepted(getContext(), TERMS_AND_CONDITIONS_VERSION);
            dismiss();
        });

        final WebView web = v.findViewById(R.id.webview_tc);

        web.loadUrl("file:///android_asset/tc.html");

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        getDialog().setCancelable(false);
    }
}
