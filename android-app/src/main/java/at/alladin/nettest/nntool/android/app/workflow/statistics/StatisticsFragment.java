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
