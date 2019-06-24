package at.alladin.nettest.nntool.android.app.workflow.main;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.OnTaskFinishedCallback;
import at.alladin.nettest.nntool.android.app.async.RequestMeasurementTask;
import at.alladin.nettest.nntool.android.app.util.LmapUtil;
import at.alladin.nettest.nntool.android.app.util.info.InformationProvider;
import at.alladin.nettest.nntool.android.app.util.info.gps.GeoLocationGatherer;
import at.alladin.nettest.nntool.android.app.util.info.interfaces.TrafficGatherer;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkGatherer;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalGatherer;
import at.alladin.nettest.nntool.android.app.view.GeoLocationView;
import at.alladin.nettest.nntool.android.app.view.ProviderAndSignalView;
import at.alladin.nettest.nntool.android.app.view.InterfaceTrafficView;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementService;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementType;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class TitleFragment extends Fragment {

    private final static String TAG = TitleFragment.class.getSimpleName();

    private ProviderAndSignalView providerSignalView;

    private GeoLocationView geoLocationView;

    private InformationProvider informationProvider;

    private InterfaceTrafficView interfaceTrafficView;

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
                if (getResources().getBoolean(R.bool.functionality_data_consumption_warning_enabled)) {
                    final String message = getResources().getString(R.string.functionality_data_consumption_warning_message);
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

        geoLocationView = v.findViewById(R.id.view_geo_location);

        interfaceTrafficView = v.findViewById(R.id.view_interface_traffic);

        //Log.i(TAG, "onCreateView");
        return v;
    }

    private void startMeasurement() {
        final RequestMeasurementTask task = new RequestMeasurementTask(getContext(),
                new OnTaskFinishedCallback<LmapUtil.LmapTaskWrapper>() {
                    @Override
                    public void onTaskFinished(LmapUtil.LmapTaskWrapper result) {
                        if (result != null && result.getTaskDescList() != null && result.getTaskDescList().size() > 0) {
                            final Bundle bundle = new Bundle();
                            bundle.putSerializable(MeasurementService.EXTRAS_KEY_QOS_TASK_DESC_LIST,
                                    (Serializable) result.getTaskDescList());
                            bundle.putSerializable(MeasurementService.EXTRAS_KEY_QOS_TASK_COLLECTOR_URL,
                                    result.getCollectorUrl());
                            bundle.putSerializable(MeasurementService.EXTRAS_KEY_SPEED_TASK_COLLECTOR_URL,
                                    result.getSpeedCollectorUrl());
                            bundle.putSerializable(MeasurementService.EXTRAS_KEY_SPEED_TASK_DESC,
                                    result.getSpeedTaskDesc());
                            ((MainActivity) getActivity()).startMeasurement(MeasurementType.SPEED, bundle);
                            //((MainActivity) getActivity()).startMeasurement(MeasurementType.QOS, bundle);
                        }
                    }
                });

        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        startInformationProvider();
        //Log.i(TAG, "onResume");
        //final Intent serviceIntent = new Intent(getContext(), InformationService.class);
        //getContext().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        //Log.i(TAG, "onPause");
        //getContext().unbindService(this);
        stopInformationProvider();
        super.onPause();
    }

    public void startInformationProvider() {
        informationProvider = InformationProvider.createDefault(getContext());

        informationProvider.start();

        final NetworkGatherer networkGatherer = informationProvider.getGatherer(NetworkGatherer.class);
        final SignalGatherer signalGatherer = informationProvider.getGatherer(SignalGatherer.class);
        final GeoLocationGatherer geoLocationGatherer = informationProvider.getGatherer(GeoLocationGatherer.class);
        final TrafficGatherer trafficGatherer = informationProvider.getGatherer(TrafficGatherer.class);

        if (networkGatherer != null && providerSignalView != null) {
            networkGatherer.addListener(providerSignalView);
        }

        if (signalGatherer != null && providerSignalView != null) {
            signalGatherer.addListener(providerSignalView);
        }

        if (geoLocationGatherer != null && geoLocationView != null) {
            geoLocationGatherer.addListener(geoLocationView);
        }

        if (trafficGatherer != null && interfaceTrafficView != null) {
            trafficGatherer.addListener(interfaceTrafficView);
        }
    }

    public void stopInformationProvider() {
        if (informationProvider == null) {
            return;
        }

        informationProvider.stop();

        final NetworkGatherer networkGatherer = informationProvider.getGatherer(NetworkGatherer.class);
        if (networkGatherer != null && providerSignalView != null) {
            networkGatherer.removeListener(providerSignalView);
        }

        final SignalGatherer signalGatherer = informationProvider.getGatherer(SignalGatherer.class);
        if (signalGatherer != null && providerSignalView != null) {
            signalGatherer.removeListener(providerSignalView);
        }

        final GeoLocationGatherer geoLocationGatherer = informationProvider.getGatherer(GeoLocationGatherer.class);
        if (geoLocationGatherer != null && geoLocationView != null) {
            geoLocationGatherer.removeListener(geoLocationView);
        }

        final TrafficGatherer trafficGatherer = informationProvider.getGatherer(TrafficGatherer.class);
        if (trafficGatherer != null && interfaceTrafficView != null) {
            trafficGatherer.removeListener(interfaceTrafficView);
        }
    }
}
