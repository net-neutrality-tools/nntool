/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.nntool.android.app.util.info;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public abstract class Gatherer<T> {

    InformationProvider informationProvider;

    final AtomicReference<T> currentValue = new AtomicReference<>();

    public InformationProvider getInformationProvider() {
        return informationProvider;
    }

    public void setInformationProvider(final InformationProvider informationProvider) {
        this.informationProvider = informationProvider;
    }

    public T getCurrentValue() {
        return currentValue.get();
    }

    public void setCurrentValue(final T currentValue) {
        this.currentValue.set(currentValue);
    }

    public ConnectivityManager getConnectivityManager() {
        if (informationProvider != null) {
            return informationProvider.getConnectivityManager();
        }
        return null;
    }

    public TelephonyManager getTelephonyManager() {
        if (informationProvider != null) {
            return informationProvider.getTelephonyManager();
        }
        return null;
    }

    public WifiManager getWifiManager() {
        if (informationProvider != null) {
            return informationProvider.getWifiManager();
        }
        return null;
    }

    public LocationManager getLocationManager() {
        if (informationProvider != null) {
            return informationProvider.getLocationManager();
        }
        return null;
    }

    public abstract void onStart();

    public abstract void onStop();
}
