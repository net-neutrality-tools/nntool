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

package at.alladin.nettest.qos;

import java.util.List;

import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nntool.client.v2.task.result.QoSResultCollector;

public abstract class QoSMeasurementClientControlAdapter implements QoSMeasurementClientControlListener {

    @Override
    public void onMeasurementStarted(List<QosMeasurementType> testsToBeExecuted) {

    }

    @Override
    public void onMeasurementStopped() {

    }

    @Override
    public void onMeasurementError(Exception e) {

    }

    @Override
    public void onMeasurementFinished(String qosTestUuid, QoSResultCollector qoSResultCollector) {

    }
}
