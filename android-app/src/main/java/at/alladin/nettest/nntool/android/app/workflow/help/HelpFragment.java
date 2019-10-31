package at.alladin.nettest.nntool.android.app.workflow.help;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class HelpFragment extends Fragment {

    private final static String TAG = HelpFragment.class.getSimpleName();

    private String helpUrlSection;

    public static HelpFragment newInstance() {
        return newInstance(null);
    }

    /**
     * Returns a new help-fragment with @section appended to the url
     * @param section (without the #)
     * @return
     */
    public static HelpFragment newInstance (final String section) {
        final HelpFragment fragment = new HelpFragment();
        fragment.setHelpUrlSection(section);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.action_bar_show_help_action).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        final Activity activity = getActivity();
        ((MainActivity) activity).updateActionBar(getString(R.string.title_help));

        final WebView webview = new WebView(activity)
        {
            @Override
            public boolean onKeyDown(final int keyCode, final KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK && canGoBack())
                {
                    goBack();
                    return true;
                }
                return super.onKeyDown(keyCode, event);
            }

        };

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onReceivedError(final WebView view, final int errorCode, final String description,
                                        final String failingUrl)
            {
                Log.e(TAG, "Received http error code " + errorCode + " when trying to load help. Rendering default page!");
                webview.loadUrl("file:///android_asset/help_unavailable.html");
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.e(TAG, "Received http error code " + errorResponse.getStatusCode() + " when trying to load help. Rendering default page!");
                webview.loadUrl("file:///android_asset/help_unavailable.html");
                super.onReceivedHttpError(view, request, errorResponse);
            }
        });

        final String url = getString(R.string.default_help_page_link) + (helpUrlSection == null ? "" : '#' + helpUrlSection);
        Log.d(TAG, "Loading url:" + url);
        webview.loadUrl(url);

        return webview;
    }

    public String getHelpUrlSection() {
        return helpUrlSection;
    }

    public void setHelpUrlSection(String helpUrlSection) {
        this.helpUrlSection = helpUrlSection;
    }
}
