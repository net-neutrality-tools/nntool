package at.alladin.nettest.nntool.android.app.util.info;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import at.alladin.nettest.nntool.android.app.util.info.gps.GeoLocationGatherer;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkGatherer;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalGatherer;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class InformationService extends Service {

    public static String ACTION_START_INFORMATION_SERVICE = "at.alladin.nettest.nntool.android.app.startInformationService";

    private final static String TAG = InformationService.class.getSimpleName();

    private final InformationServiceBinder informationServiceBinder = new InformationServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return informationServiceBinder;
    }

    public class InformationServiceBinder extends Binder {
        public InformationService getService() {
            return InformationService.this;
        }
    }

    private InformationProvider informationProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        informationProvider = new InformationProvider(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        informationProvider.onStop();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.d(TAG, "Got intent with action: '" + intent.getAction() + "'");
            if (ACTION_START_INFORMATION_SERVICE.equals(intent.getAction())) {
                registerGatherer(NetworkGatherer.class);
                registerGatherer(SignalGatherer.class);
                registerGatherer(GeoLocationGatherer.class);
                informationProvider.onStart();
            }
        }

        return START_STICKY;
    }

    public <T extends Gatherer> T registerGatherer(final Class<T> gathererClazz) {
        return informationProvider.registerGatherer(gathererClazz);
    }

    public <T extends Gatherer> T unregisterGatherer(final Class<T> gathererClazz) {
        return informationProvider.unregisterGatherer(gathererClazz);
    }

    public InformationProvider getInformationProvider() {
        return informationProvider;
    }
}
