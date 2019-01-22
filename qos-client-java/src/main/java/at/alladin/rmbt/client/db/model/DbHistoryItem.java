/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

package at.alladin.rmbt.client.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import at.alladin.nettest.shared.model.HistoryItem;
/**
 * Created by lb on 12.09.16.
 */
@DatabaseTable(tableName = "history")
public class DbHistoryItem {

    /**
     * test uuid
     */
    @DatabaseField(id = true)
    String uuid;

    /**
     * test timestamp
     */
    @DatabaseField
    Long timeStamp;

    /**
     * FILTER; the device name
     */
    @DatabaseField
    String device;

    /**
     * FILTER; the network type group name (WIFI, 3G, etc)
     */
    @DatabaseField
    String networkType;

    /**
     * stringified JSON, see {@link HistoryItem}
     *
     */
    @DatabaseField
    String historyItem;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getHistoryItem() {
        return historyItem;
    }

    public void setHistoryItem(String historyItem) {
        this.historyItem = historyItem;
    }

    @Override
    public String toString() {
        return "DbHistoryItem{" +
                "uuid='" + uuid + '\'' +
                ", timeStamp=" + timeStamp +
                ", device='" + device + '\'' +
                ", networkType='" + networkType + '\'' +
                ", historyItem='" + historyItem + '\'' +
                '}';
    }
}
