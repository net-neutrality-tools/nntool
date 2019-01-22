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

/**
 * Created by lb on 12.09.16.
 */
@DatabaseTable(tableName="measurement")
public class DbMeasurementItem {

    @DatabaseField(id=true)
    private String uuid;

    @DatabaseField
    private String results;

    @DatabaseField
    private String detailedResults;

    @DatabaseField
    private String qosResults;

    public DbMeasurementItem() { }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getDetailedResults() {
        return detailedResults;
    }

    public void setDetailedResults(String detailedResults) {
        this.detailedResults = detailedResults;
    }

    public String getQosResults() {
        return qosResults;
    }

    public void setQosResults(String qosResults) {
        this.qosResults = qosResults;
    }

    @Override
    public String toString() {
        return "DbMeasurementItem{" +
                "uuid='" + uuid + '\'' +
                ", results='" + results + '\'' +
                ", detailedResults='" + detailedResults + '\'' +
                ", qosResults='" + qosResults + '\'' +
                '}';
    }
}
