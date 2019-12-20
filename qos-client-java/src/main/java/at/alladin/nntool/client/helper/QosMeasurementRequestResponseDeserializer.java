/*******************************************************************************
 * Copyright 2018-2019 alladin-IT GmbH
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

package at.alladin.nntool.client.helper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nettest.shared.model.response.QosMeasurementRequestResponse;

/**
 * Custom deserializer for QosMeaseurementRequestResponse objs
 * The only difference is that unknown QosMeasurementType enums for the "objectives" Map don't crash the system if sent by the server
 * Instead they are simply ignored
 * Usage: register them w/your GSON of choice by calling
 * .registerTypeAdapter(QosMeasurementRequestResponse.class, new QosMeasurementRequestResponseDeserializer()) on your GsonBuilder
 */
public class QosMeasurementRequestResponseDeserializer implements JsonDeserializer<QosMeasurementRequestResponse>{

    final private TypeToken<?> typeToken = new TypeToken<List<Map<String, Object>>>(){};

    public QosMeasurementRequestResponseDeserializer() {
    }

    @Override
    public QosMeasurementRequestResponse deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            final QosMeasurementRequestResponse ret = new QosMeasurementRequestResponse();
            if (json != null && json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                if (obj.has("test_token")) {
                    ret.setTestToken(obj.get("test_token").getAsString());
                }
                if (obj.has("test_uuid")) {
                    ret.setTestUuid(obj.get("test_uuid").getAsString());
                }
                obj = obj.getAsJsonObject("objectives");
                if (obj != null) {

                    final Set<Entry<String, QosMeasurementType>> qosEntrySet = QosMeasurementType.CONSTANTS.entrySet();
                    for (Entry<String, QosMeasurementType> entry : qosEntrySet) {
                        if (obj.has(entry.getKey())) {
                            ret.getObjectives().put(entry.getValue(), (List<Map<String, Object>>) context.deserialize(obj.get(entry.getKey()), typeToken.getType()));
                        }
                    }
                }
                return ret;
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
