package at.alladin.nettest.nntool.android.app.workflow;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Basic fragment that enables easy setting of web view fragments
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public abstract class WebViewFragment extends ActionBarFragment {

    private final static String TAG = WebViewFragment.class.getSimpleName();

    protected abstract String getWebviewUrl();

    protected abstract String getOnErrorHtmlPath();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final WebView webview = new WebView(getActivity())
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
                handleWebviewError(webview, errorCode);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                handleWebviewError(view, errorResponse == null ? -1 : errorResponse.getStatusCode());
                super.onReceivedHttpError(view, request, errorResponse);
            }
        });

        final String url = getWebviewUrl();
        Log.d(TAG, "Loading url:" + url);
        webview.loadUrl(url);

        return webview;
    }

    protected void handleWebviewError(final WebView webView, final int errorCode) {
        Log.e(TAG, "Received error code " + errorCode + " when trying to load webView. Rendering default page!");
        //on untimely navigation, the context may be lost just as the error is received (resulting in an illegal state exception)
        try {
            webView.loadUrl(getOnErrorHtmlPath());
        } catch (IllegalStateException ex) {
            Log.w(TAG, ex.getMessage());
            Log.w(TAG, "User navigated away from webView, ignoring exception");
        }
    }

}
