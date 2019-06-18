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

    final Map<Class<?>, Gatherer> gathererMap = new HashMap<>();

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
        final Iterator<Map.Entry<Class<?>, Gatherer>> it = gathererMap.entrySet().iterator();
        while (it.hasNext()) {
            final Class<?> clazz = it.next().getKey();
            unregisterGatherer((Class<? extends Gatherer>) clazz, false, true);
            it.remove();
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

        for (final Map.Entry<Class<?>, Gatherer> e : gathererMap.entrySet()) {
            e.getValue().onStart();
            if (e.getValue() instanceof RunnableGatherer) {
                scheduleRunnableGatherer((RunnableGatherer) e.getValue(), (Class<RunnableGatherer>) e.getKey());
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
            return (T) gathererMap.get(clazz);
        }

        gatherer.setInformationProvider(this);
        gathererMap.put(clazz, gatherer);

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
            return (T) gathererMap.get(clazz);
        }

        gatherer.setInformationProvider(this);
        gathererMap.put(clazz, gatherer);

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
        if (interval != null && !scheduledFutureMap.containsKey(clazz)) {
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
        final T gatherer = (T) (remove ? gathererMap.remove(gathererClazz) : gathererMap.get(gathererClazz));
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
        return (T) gathererMap.get(gathererClazz);
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
        return informationProvider;
    }
}
