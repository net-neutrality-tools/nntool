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
