package at.alladin.nettest.nntool.android.app.workflow.statistics;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.WebViewFragment;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class StatisticsFragment extends WebViewFragment {

    public static StatisticsFragment newInstance() {
        final StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }

    @Override
    protected String getWebviewUrl() {
        return getString(R.string.default_statistics_page_link);
    }

    @Override
    protected String getOnErrorHtmlPath() {
        return getString(R.string.default_statistics_page_error_html_path);
    }

    @Override
    public Integer getTitleStringId() {
        return R.string.title_statistics;
    }

    @Override
    public Integer getHelpSectionStringId() {
        return R.string.help_link_statistics_section;
    }
}
