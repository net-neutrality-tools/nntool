package at.alladin.nettest.nntool.android.app.util.info;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import at.alladin.nettest.nntool.android.app.support.telephony.CellInfoWrapper;
import at.alladin.nettest.nntool.android.app.util.info.gps.GeoLocationChangeEvent;
import at.alladin.nettest.nntool.android.app.util.info.gps.GeoLocationChangeListener;
import at.alladin.nettest.nntool.android.app.util.info.gps.GeoLocationGatherer;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkChangeEvent;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkChangeListener;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkGatherer;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalGatherer;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalStrengthChangeEvent;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalStrengthChangeListener;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class InformationCollector
        implements SignalStrengthChangeListener, NetworkChangeListener, GeoLocationChangeListener {

    private final InformationProvider informationProvider;

    private final AtomicBoolean isCollecting = new AtomicBoolean(false);

    private final List<CellInfoWrapper> cellInfoList = new ArrayList<>();
    private final List<GeoLocationDto> geoLocationList = new ArrayList<>();

    public InformationCollector(final InformationProvider informationProvider) {
        this.informationProvider = informationProvider;
    }

    public InformationProvider getInformationProvider() {
        return informationProvider;
    }

    public void start() {
        final SignalGatherer signalGatherer = this.informationProvider.getGatherer(SignalGatherer.class);
        final GeoLocationGatherer geoLocationGatherer = this.informationProvider.getGatherer(GeoLocationGatherer.class);
        final NetworkGatherer networkGatherer = this.informationProvider.getGatherer(NetworkGatherer.class);

        if (signalGatherer != null) {
            signalGatherer.addListener(this);
        }
        if (geoLocationGatherer != null) {
            geoLocationGatherer.addListener(this);
        }
        if (networkGatherer != null) {
            networkGatherer.addListener(this);
        }

        this.isCollecting.set(true);
    }

    public void stop() {
        this.isCollecting.set(false);

        final SignalGatherer signalGatherer = this.informationProvider.getGatherer(SignalGatherer.class);
        final GeoLocationGatherer geoLocationGatherer = this.informationProvider.getGatherer(GeoLocationGatherer.class);
        final NetworkGatherer networkGatherer = this.informationProvider.getGatherer(NetworkGatherer.class);

        if (signalGatherer != null) {
            signalGatherer.removeListener(this);
        }
        if (geoLocationGatherer != null) {
            geoLocationGatherer.removeListener(this);
        }
        if (networkGatherer != null) {
            networkGatherer.removeListener(this);
        }
    }

    public List<CellInfoWrapper> getCellInfoList() {
        return cellInfoList;
    }

    public List<GeoLocationDto> getGeoLocationList() {
        return geoLocationList;
    }

    @Override
    public void onLocationChanged(GeoLocationChangeEvent geoLocationChangeEvent) {
        if (this.isCollecting.get()
                && geoLocationChangeEvent != null
                && geoLocationChangeEvent.getGeoLocationDto() != null) {
            if (GeoLocationChangeEvent.GeoLocationChangeEventType.LOCATION_UPDATE.equals(geoLocationChangeEvent.getEventType())) {
                geoLocationList.add(geoLocationChangeEvent.getGeoLocationDto());
            }
        }
    }

    @Override
    public void onNetworkChange(NetworkChangeEvent event) {
        if (this.isCollecting.get() && event != null) {
            //do we want to collect information about network changes?
        }
    }

    @Override
    public void onSignalStrengthChange(SignalStrengthChangeEvent event) {
        if (this.isCollecting.get()
                && event != null
                && event.getCurrentSignalStrength() != null
                && event.getCurrentSignalStrength().getCellInfoWrapper() != null) {
            cellInfoList.add(event.getCurrentSignalStrength().getCellInfoWrapper());
        }
    }
}
