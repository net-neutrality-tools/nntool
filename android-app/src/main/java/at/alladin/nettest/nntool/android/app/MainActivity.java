package at.alladin.nettest.nntool.android.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import at.alladin.nettest.nntool.android.app.async.RegisterMeasurementAgentTask;
import at.alladin.nettest.nntool.android.app.dialog.BlockingProgressDialog;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowTarget;
import at.alladin.nettest.nntool.android.app.workflow.about.AboutFragment;
import at.alladin.nettest.nntool.android.app.workflow.main.TitleFragment;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementService;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementType;
import at.alladin.nettest.nntool.android.app.workflow.measurement.QosFragment;
import at.alladin.nettest.nntool.android.app.workflow.tc.TermsAndConditionsFragment;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    navigateTo(WorkflowTarget.TITLE);
                    return true;
                case R.id.navigation_dashboard:
                    navigateTo(WorkflowTarget.ABOUT);
                    return true;
                case R.id.navigation_notifications:
                    navigateTo(WorkflowTarget.ABOUT);
                    return true;
            }
            return false;
        }
    };

    public void navigateTo(final WorkflowTarget target) {
        Fragment targetFragment = null;
        boolean isBottomNavigationVisible = true;

        switch (target) {
            case TITLE:
                targetFragment = TitleFragment.newInstance();
                break;
            case MEASUREMENT_QOS:
                isBottomNavigationVisible = false;
                targetFragment = QosFragment.newInstance();
                break;
            case ABOUT:
                targetFragment = AboutFragment.newInstance();
        }

        setBottomNavigationVisible(isBottomNavigationVisible);

        if (targetFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_layout, targetFragment)
                    .commit();
        }
    }

    public void startMeasurement(final MeasurementType measurementType) {
        switch (measurementType) {
            case QOS:
                navigateTo(WorkflowTarget.MEASUREMENT_QOS);
                final Intent intent = new Intent(MeasurementService.ACTION_START_QOS_MEASUREMENT,
                        null, this, MeasurementService.class);
                startService(intent);
                break;
        }
    }

    public void setBottomNavigationVisible(final boolean isVisible) {
        final boolean wasVisible = navigation.getVisibility() == View.VISIBLE;
        if (navigation != null && isVisible != wasVisible) {
            navigation.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            navigation.setEnabled(isVisible);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigateTo(WorkflowTarget.TITLE);

        if (!PreferencesUtil.isTermsAndConditionsAccepted(this, TermsAndConditionsFragment.TERMS_AND_CONDITIONS_VERSION)) {
            TermsAndConditionsFragment f = TermsAndConditionsFragment.newInstance();
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            f.show(ft, "TC");
        }
        else {
            registerMeasurementAgent();
        }

        getSupportActionBar().setElevation(0f);
    }

    public void registerMeasurementAgent() {
        RegisterMeasurementAgentTask task = new RegisterMeasurementAgentTask(this, null);
        task.execute();
    }
}
