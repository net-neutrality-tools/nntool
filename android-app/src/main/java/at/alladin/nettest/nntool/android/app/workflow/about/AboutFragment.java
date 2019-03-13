package at.alladin.nettest.nntool.android.app.workflow.about;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class AboutFragment extends Fragment {

    public static AboutFragment newInstance() {
        final AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.about_fragment, container, false);
        return v;
    }
}
