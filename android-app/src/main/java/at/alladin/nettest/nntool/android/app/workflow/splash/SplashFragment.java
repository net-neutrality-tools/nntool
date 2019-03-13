package at.alladin.nettest.nntool.android.app.workflow.splash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SplashFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.splash_view, container, false);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getView().setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
   }
}
