package at.alladin.nettest.nntool.android.app;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import at.alladin.nettest.nntool.android.app.async.RegisterMeasurementAgentTask;
import at.alladin.nettest.nntool.android.app.util.PermissionUtil;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.util.info.InformationService;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowTarget;
import at.alladin.nettest.nntool.android.app.workflow.history.HistoryFragment;
import at.alladin.nettest.nntool.android.app.workflow.map.MapFragment;
import at.alladin.nettest.nntool.android.app.workflow.measurement.SpeedFragment;
import at.alladin.nettest.nntool.android.app.workflow.measurement.TitleWithRecentResultFragment;
import at.alladin.nettest.nntool.android.app.workflow.settings.SettingsFragment;
import at.alladin.nettest.nntool.android.app.workflow.main.TitleFragment;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementService;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementType;
import at.alladin.nettest.nntool.android.app.workflow.measurement.QosFragment;
import at.alladin.nettest.nntool.android.app.workflow.statistics.StatisticsFragment;
import at.alladin.nettest.nntool.android.app.workflow.tc.TermsAndConditionsFragment;
import at.alladin.nettest.nntool.android.speed.SpeedMeasurementState;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    navigateToTarget(WorkflowTarget.TITLE);
                    return true;
                case R.id.navigation_history:
                    navigateToTarget(WorkflowTarget.HISTORY);
                    return true;
                case R.id.navigation_map:
                    navigateToTarget(WorkflowTarget.MAP);
                    return true;
                case R.id.navigation_settings:
                    navigateToTarget(WorkflowTarget.SETTINGS);
                    return true;
                case R.id.navigation_statistics:
                    navigateToTarget(WorkflowTarget.STATISTICS);
                    return true;
            }
            return false;
        }
    };

    public void navigateTo(final WorkflowTarget target) {
        switch (target) {
            case TITLE:
                navigation.setSelectedItemId(R.id.navigation_home);
                break;
            //no need to select the item from the measurement speed or measurement qos target (they can only be started from the title anyway)
            case MEASUREMENT_SPEED:
            case MEASUREMENT_QOS:
            case MEASUREMENT_RECENT_RESULT:
                navigateToTarget(target);
                break;
            case SETTINGS:
                navigation.setSelectedItemId(R.id.navigation_settings);
                break;
            case MAP:
                navigation.setSelectedItemId(R.id.navigation_map);
                break;
            case HISTORY:
                navigation.setSelectedItemId(R.id.navigation_history);
                break;
            case STATISTICS:
                navigation.setSelectedItemId(R.id.navigation_statistics);
                break;
        }
    }

    private void navigateToTarget(final WorkflowTarget target) {
        Fragment targetFragment = null;
        boolean isBottomNavigationVisible = true;

        switch (target) {
            case TITLE:
                targetFragment = TitleFragment.newInstance();
                break;
            case MEASUREMENT_SPEED:
                isBottomNavigationVisible = false;
                targetFragment = SpeedFragment.newInstance();
                break;
            case MEASUREMENT_QOS:
                isBottomNavigationVisible = false;
                targetFragment = QosFragment.newInstance();
                break;
            case MEASUREMENT_RECENT_RESULT:
                targetFragment = TitleWithRecentResultFragment.newInstance();
                break;
            case SETTINGS:
                targetFragment = SettingsFragment.newInstance();
                break;
            case MAP:
                targetFragment = MapFragment.newInstance();
                break;
            case HISTORY:
                targetFragment = HistoryFragment.newInstance();
                break;
            case STATISTICS:
                targetFragment = StatisticsFragment.newInstance();
                break;
        }

        setBottomNavigationVisible(isBottomNavigationVisible);

        if (targetFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_layout, targetFragment)
                    .commit();
        }
    }

    public void startMeasurement(final MeasurementType measurementType, final Bundle options) {
        switch (measurementType) {
            case SPEED:
                navigateTo(WorkflowTarget.MEASUREMENT_SPEED);
                final Intent speedIntent = new Intent(MeasurementService.ACTION_START_SPEED_MEASUREMENT,
                        null, this, MeasurementService.class);
                speedIntent.putExtras(options);
                startService(speedIntent);
                break;
            case QOS:
                navigateTo(WorkflowTarget.MEASUREMENT_QOS);
                final Intent intent = new Intent(MeasurementService.ACTION_START_QOS_MEASUREMENT,
                        null, this, MeasurementService.class);
                intent.putExtras(options);
                startService(intent);
                break;
        }
    }

    public void startInformationService() {
        final Intent intent = new Intent(InformationService.ACTION_START_INFORMATION_SERVICE,
                null, this, InformationService.class);
        startService(intent);
    }

    public void stopInformationService() {
        final Intent intent = new Intent(this, InformationService.class);
        stopService(intent);
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
            PermissionUtil.requestLocationPermission(this);
        }

        getSupportActionBar().setElevation(0f);

    }

    @Override
    protected void onPause() {
        stopInformationService();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startInformationService();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtil.REQUEST_CODE_LOCATION:
                Log.d(TAG, "Granted FINE LOCATION permission!");
                break;
            default:
                break;
        }
    }

    public void registerMeasurementAgent() {
        RegisterMeasurementAgentTask task = new RegisterMeasurementAgentTask(this, r -> {
            if (r == null && getResources().getBoolean(R.bool.debug_functionality_reset_uuid_if_not_in_database_and_retry)) {
                Log.d(TAG, "Measurement agent registration request failed. Deleting agent uuid and retrying.");
                PreferencesUtil.setAgentUuid(MainActivity.this, null);
                final RegisterMeasurementAgentTask retryTask = new RegisterMeasurementAgentTask(MainActivity.this, null);
                retryTask.execute();
            }
        });
        task.execute();
    }

}
