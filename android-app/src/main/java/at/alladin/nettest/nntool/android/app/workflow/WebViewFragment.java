/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;

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

        //set user agent string
        final WebSettings webSettings = webview.getSettings();
        final String userAgent = PreferencesUtil.getUserAgentString(getActivity());
        if (userAgent != null) {
            webSettings.setUserAgentString(userAgent);
        }

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
