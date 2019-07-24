package at.alladin.nettest.nntool.android.app.util.info;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import at.alladin.nettest.nntool.android.app.util.info.gps.GeoLocationGatherer;
import at.alladin.nettest.nntool.android.app.util.info.interfaces.TrafficGatherer;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkGatherer;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalGatherer;
import at.alladin.nettest.nntool.android.app.util.info.system.SystemInfoGatherer;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 *
 * Information Provder is used to manage gatherer (data/information collectors)
 */
public class InformationProvider {

    private final static String TAG = InformationProvider.class.getSimpleName();

    final ConnectivityManager connectivityManager;

    final TelephonyManager telephonyManager;

    final WifiManager wifiManager;

    final LocationManager locationManager;

    final Context context;

    final Map<Class<?>, GathererHolder> gathererMap = new HashMap<>();

    final Map<Class<?>, Boolean> runnableMap = new HashMap<>();

    final Map<Class<?>, ScheduledFuture<?>> scheduledFutureMap = new HashMap<>();

    final AtomicBoolean isRunning = new AtomicBoolean(false);

    final AtomicBoolean isStopped = new AtomicBoolean(false);

    ScheduledExecutorService executorService = null;

    public InformationProvider(final Context context) {
        this.context = context;
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void stop() {
        if (isStopped.getAndSet(true)) {
            Log.e(TAG, "Cannot stop InformationProvider that has already been stopped!");
            return;
        }

        isRunning.set(false);
        final Iterator<Map.Entry<Class<?>, GathererHolder>> it = gathererMap.entrySet().iterator();
        while (it.hasNext()) {
            final Class<?> clazz = it.next().getKey();
            unregisterGatherer((Class<? extends Gatherer>) clazz, false, true);
            //it.remove();
            scheduledFutureMap.remove(clazz);
        }

        try {
            executorService.shutdownNow();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (isStopped.get()) {
            Log.e(TAG, "Cannot start InformationProvider that has already been stopped!");
            return;
        }

        isRunning.set(true);
        executorService = Executors.newScheduledThreadPool(5);

        for (final Map.Entry<Class<?>, GathererHolder> e : gathererMap.entrySet()) {
            e.getValue().getGatherer().onStart();
            if (e.getValue().getGatherer() instanceof RunnableGatherer) {
                scheduleRunnableGatherer((RunnableGatherer) e.getValue().getGatherer(), (Class<RunnableGatherer>) e.getKey());
            }
        }
    }

    public <T extends Gatherer> T registerGatherer(final Class<T> clazz) {
        try {
            final T gatherer = clazz.newInstance();
            return registerGatherer(gatherer, clazz);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T extends Gatherer> T registerGatherer(final T gatherer, final Class<T> clazz) {
        if (gathererMap.containsKey(clazz)) {
            return (T) gathererMap.get(clazz).getGatherer();
        }

        gatherer.setInformationProvider(this);
        gathererMap.put(clazz, new GathererHolder(gatherer, clazz));

        if (isRunning.get()) {
            try {
                gatherer.onStart();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }

        return gatherer;
    }

    public <T extends Gatherer & RunnableGatherer> T registerRunnableGatherer(final Class<T> clazz) {
        try {
            final T gatherer = clazz.newInstance();
            return registerRunnableGatherer(gatherer, clazz);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T extends Gatherer & RunnableGatherer> T registerRunnableGatherer(final T gatherer, final Class<T> clazz) {
        if (gathererMap.containsKey(clazz)) {
            return (T) gathererMap.get(clazz).getGatherer();
        }

        gatherer.setInformationProvider(this);
        gathererMap.put(clazz, new GathererHolder(gatherer, clazz, true));

        if (isRunning.get()) {
            try {
                gatherer.onStart();
                scheduleRunnableGatherer(gatherer, clazz);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }

        return gatherer;
    }

    private <T extends RunnableGatherer> ScheduledFuture<?> scheduleRunnableGatherer(final T gatherer, final Class<T> clazz) {
        final RunnableGatherer.Interval interval = gatherer.getInterval();
        final GathererHolder holder = gathererMap.get(clazz);
        if (holder != null && holder.isRunnable() && interval != null && !scheduledFutureMap.containsKey(clazz)) {
            final ScheduledFuture<?> scheduledFuture
                    = executorService.scheduleAtFixedRate(gatherer, interval.getDuration(), interval.getDuration(), interval.getTimeUnit());
            if (scheduledFuture != null) {
                scheduledFutureMap.put(clazz, scheduledFuture);
            }

            return scheduledFuture;
        }

        return null;
    }

    public <T extends Gatherer> T unregisterGatherer(final Class<T> gathererClazz) {
        return unregisterGatherer(gathererClazz, true, isRunning.get());
    }

    private <T extends Gatherer> T unregisterGatherer(final Class<T> gathererClazz, final boolean remove, final boolean isRunning) {
        final GathererHolder holder = remove ? gathererMap.remove(gathererClazz) : gathererMap.get(gathererClazz);
        final T gatherer = (T) (holder != null ? holder.getGatherer() : null);
        if (gatherer != null && isRunning) {
            gatherer.onStop();
            final ScheduledFuture<?> scheduledFuture =
                    remove ? scheduledFutureMap.remove(gathererClazz) : scheduledFutureMap.get(gathererClazz);
            if (scheduledFuture != null) {
                try {
                    scheduledFuture.cancel(true);
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return gatherer;
    }

    public <T extends Gatherer> T getGatherer(final Class<T> gathererClazz) {
        final GathererHolder holder = gathererMap.get(gathererClazz);
        return (T) (holder != null ? holder.getGatherer() : null);
    }

    public Context getContext() {
        return context;
    }

    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    public TelephonyManager getTelephonyManager() {
        return telephonyManager;
    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public static InformationProvider createDefault(final Context context) {
        final InformationProvider informationProvider = new InformationProvider(context);
        informationProvider.registerGatherer(SignalGatherer.class);
        informationProvider.registerGatherer(NetworkGatherer.class);
        informationProvider.registerGatherer(GeoLocationGatherer.class);
        informationProvider.registerRunnableGatherer(TrafficGatherer.class);
        informationProvider.registerRunnableGatherer(SystemInfoGatherer.class);
        return informationProvider;
    }

    public static InformationProvider createMeasurementDefault(final Context context) {
        final InformationProvider informationProvider = new InformationProvider(context);
        informationProvider.registerGatherer(SignalGatherer.class);
        informationProvider.registerGatherer(NetworkGatherer.class);
        informationProvider.registerGatherer(GeoLocationGatherer.class);
        //register TrafficGatherer as simple Gatherer as we do not need intermediate results.
        informationProvider.registerGatherer(TrafficGatherer.class);
        return informationProvider;
    }

    private class GathererHolder {
        private final Gatherer gatherer;
        private final Class<? extends Gatherer> clazz;
        private boolean isRunnable = false;

        public GathererHolder(final Gatherer gatherer, final Class<? extends Gatherer> clazz) {
            this.gatherer = gatherer;
            this.clazz = clazz;
        }

        public GathererHolder(final Gatherer gatherer, final Class<? extends Gatherer> clazz, final boolean isRunnable) {
            this.gatherer = gatherer;
            this.clazz = clazz;
            this.isRunnable = isRunnable;
        }

        public Gatherer getGatherer() {
            return gatherer;
        }

        public Class<? extends Gatherer> getClazz() {
            return clazz;
        }

        public boolean isRunnable() {
            return isRunnable;
        }

        public void setRunnable(boolean runnable) {
            isRunnable = runnable;
        }
    }
}
