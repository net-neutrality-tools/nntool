package at.alladin.nettest.nntool.android.app.workflow.help;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.WebViewFragment;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class HelpFragment extends WebViewFragment {

    private final static String TAG = HelpFragment.class.getSimpleName();

    private String helpUrlSection;

    @Override
    protected String getOnErrorHtmlPath() {
        return "file:///android_asset/help_unavailable.html";
    }

    @Override
    protected String getWebviewUrl() {
        return getString(R.string.default_help_page_link) + (helpUrlSection == null ? "" : '#' + helpUrlSection);
    }

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

    @Override
    public Integer getTitleStringId() {
        return R.string.title_help;
    }

    public String getHelpUrlSection() {
        return helpUrlSection;
    }

    public void setHelpUrlSection(String helpUrlSection) {
        this.helpUrlSection = helpUrlSection;
    }
}
