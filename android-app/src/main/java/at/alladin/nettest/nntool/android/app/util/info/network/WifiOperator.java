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

package at.alladin.nettest.nntool.android.app.util.info.network;

import android.net.wifi.WifiInfo;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public final class WifiOperator implements OperatorInfo {
    final String ssid;

    final String bssid;

    final int networkId;

    final int frequency;

    public WifiOperator(final WifiInfo wifiInfo) {
        this.ssid = NetworkGatherer.removeQuotationsInCurrentSSIDForJellyBean(wifiInfo.getSSID());
        this.bssid = wifiInfo.getBSSID();
        this.networkId = wifiInfo.getNetworkId();
        this.frequency = wifiInfo.getFrequency();
    }

    public String getSsid() {
        return ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public int getNetworkId() {
        return networkId;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public String getOperatorName() {
        return ssid;
    }

    @Override
    public String toString() {
        return "WifiOperator{" +
                "ssid='" + ssid + '\'' +
                ", bssid='" + bssid + '\'' +
                ", networkId=" + networkId +
                ", frequency=" + frequency +
                '}';
    }
}
