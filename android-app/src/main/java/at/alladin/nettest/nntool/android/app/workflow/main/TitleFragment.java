package at.alladin.nettest.nntool.android.app.workflow.main;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import at.alladin.nettest.nntool.android.app.BuildConfig;
import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.OnTaskFinishedCallback;
import at.alladin.nettest.nntool.android.app.async.RequestMeasurementTask;
import at.alladin.nettest.nntool.android.app.util.LmapUtil;
import at.alladin.nettest.nntool.android.app.util.info.InformationService;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkGatherer;
import at.alladin.nettest.nntool.android.app.view.ProviderAndSignalView;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementService;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementType;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class TitleFragment extends Fragment implements ServiceConnection {

    private final static String TAG = TitleFragment.class.getSimpleName();

    private InformationService informationService;

    private ProviderAndSignalView providerSignalView;

    /**
     *
     * @return
     */
    public static TitleFragment newInstance() {
        final TitleFragment fragment = new TitleFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_title, container, false);

        final View startButton = v.findViewById(R.id.title_page_start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BuildConfig.MEASUREMENT_SHOW_DATA_CONSUMPTION_WARNING) {
                    final String message = getResources().getString(R.string.measurement_data_consumption_warning);
                    AlertDialog alert = new AlertDialog.Builder(getActivity())
                            .setPositiveButton(android.R.string.ok,
                                    (dialog, w) -> startMeasurement())
                            .setNegativeButton(android.R.string.cancel,
                                    (dialog, w) -> Log.d(TAG, "Data consumption warning declined"))
                            .setMessage(message)
                            .setCancelable(false)
                            .create();

                    alert.show();
                }
                else {
                    startMeasurement();
                }
            }
        });


        providerSignalView = v.findViewById(R.id.view_provider_signal);

        return v;
    }

    private void startMeasurement() {
        final RequestMeasurementTask task = new RequestMeasurementTask(getContext(),
                new OnTaskFinishedCallback<LmapUtil.LmapTaskDescWrapper>() {
                    @Override
                    public void onTaskFinished(LmapUtil.LmapTaskDescWrapper result) {
                        if (result != null && result.getTaskDescList() != null && result.getTaskDescList().size() > 0) {
                            final Bundle bundle = new Bundle();
                            bundle.putSerializable(MeasurementService.EXTRAS_KEY_QOS_TASK_DESK_LIST,
                                    (Serializable) result.getTaskDescList());
                            bundle.putSerializable(MeasurementService.EXTRAS_KEY_QOS_TASK_COLLECTOR_URL,
                                    result.getCollectorUrl());
                            ((MainActivity) getActivity()).startMeasurement(MeasurementType.QOS, bundle);
                        }
                    }
                });

        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        final Intent serviceIntent = new Intent(getContext(), InformationService.class);
        getContext().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        getContext().unbindService(this);
        super.onPause();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        informationService = ((InformationService.InformationServiceBinder) service).getService();
        final NetworkGatherer networkGatherer = informationService.getInformationProvider().getGatherer(NetworkGatherer.class);
        if (networkGatherer != null && providerSignalView != null) {
            networkGatherer.addNetworkChangeListener(providerSignalView);
        }
        Log.d(TAG, "InformationService connected");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        final NetworkGatherer networkGatherer = informationService.getInformationProvider().getGatherer(NetworkGatherer.class);
        if (networkGatherer != null && providerSignalView != null) {
            networkGatherer.removeNetworkCHangeListener(providerSignalView);
        }
        informationService = null;
        Log.d(TAG, "InformationService disconnected");
    }
}
