package at.alladin.nettest.nntool.android.app.workflow;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.help.HelpFragment;

/**
 * Basic fragment that enables easy setting of correct titles and forwarding to correct help-sections
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public abstract class ActionBarFragment extends Fragment {

    private final static String TAG = ActionBarFragment.class.getSimpleName();

    /**
     * The title string id to be displayed in the action bar (e.g. R.string.title_history)
     * if Null the title will be empty
     * @return
     */
    public abstract Integer getTitleStringId();

    public Object[] getTitleArgs() {
        return null;
    }

    /**
     * The help section string id to be appended to the help link (e.g. R.string.help_link_history_section)
     * If null, no section will be appended
     * @return
     */
    public Integer getHelpSectionStringId() {
        return null;
    }

    public Object[] getHelpSectionArgs() {
        return null;
    }

    /**
     * @return true if the help button should be displayed, false otherwise
     */
    public boolean showHelpButton() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Integer helpSectionStringId = getHelpSectionStringId();
        final Object[] helpArgs = getHelpSectionArgs();
        if (R.id.action_bar_show_help_action == item.getItemId()) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_layout, HelpFragment.newInstance(helpSectionStringId == null ?
                            null : helpArgs == null ? getString(helpSectionStringId) : getString(helpSectionStringId, helpArgs)))
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        final Integer titleStringId = getTitleStringId();
        if (titleStringId != null) {
            final Object[] titleArgs = getTitleArgs();
            if (titleArgs != null) {
                ((MainActivity) getActivity()).updateActionBar(getString(titleStringId, titleArgs));
            } else {
                ((MainActivity) getActivity()).updateActionBar(getString(titleStringId));
            }
        } else {
            ((MainActivity) getActivity()).updateActionBar(new String());
        }
        setHasOptionsMenu(true);
        super.onResume();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (!showHelpButton()) {
            menu.findItem(R.id.action_bar_show_help_action).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

}
