package at.alladin.nettest.nntool.android.app.workflow.help;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.Serializable;
import java.util.ArrayList;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.OnTaskFinishedCallback;
import at.alladin.nettest.nntool.android.app.async.RequestAgentIpTask;
import at.alladin.nettest.nntool.android.app.async.RequestMeasurementTask;
import at.alladin.nettest.nntool.android.app.async.RequestSpeedMeasurementPeersTask;
import at.alladin.nettest.nntool.android.app.util.AlertDialogUtil;
import at.alladin.nettest.nntool.android.app.util.LmapUtil;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.util.info.InformationProvider;
import at.alladin.nettest.nntool.android.app.util.info.gps.GeoLocationGatherer;
import at.alladin.nettest.nntool.android.app.util.info.interfaces.TrafficGatherer;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkGatherer;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalGatherer;
import at.alladin.nettest.nntool.android.app.util.info.system.SystemInfoGatherer;
import at.alladin.nettest.nntool.android.app.view.CpuAndRamView;
import at.alladin.nettest.nntool.android.app.view.GeoLocationView;
import at.alladin.nettest.nntool.android.app.view.InterfaceTrafficView;
import at.alladin.nettest.nntool.android.app.view.Ipv4v6View;
import at.alladin.nettest.nntool.android.app.view.MeasurementServerSelectionView;
import at.alladin.nettest.nntool.android.app.view.ProviderAndSignalView;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementService;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementType;
import at.alladin.nettest.nntool.android.app.workflow.result.ResultFragment;

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
        ((MainActivity) activity).setActionBarTitle(getString(R.string.title_help));

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
                Log.e(TAG, "Failed to load help with error: " + description);
                //TODO: show error page
            }
        });

        webview.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
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