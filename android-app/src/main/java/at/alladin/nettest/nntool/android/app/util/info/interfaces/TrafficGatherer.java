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

package at.alladin.nettest.nntool.android.app.util.info.interfaces;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import at.alladin.nettest.nntool.android.app.util.info.ListenableGatherer;
import at.alladin.nettest.nntool.android.app.util.info.RunnableGatherer;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class TrafficGatherer
        extends ListenableGatherer<CurrentInterfaceTraffic, InterfaceTrafficUpdateListener>
        implements RunnableGatherer {

    private final static String TAG = TrafficGatherer.class.getSimpleName();

    private final static Interval INTERVAL = new Interval(2, TimeUnit.SECONDS);

    private final TrafficServiceImpl trafficService = new TrafficServiceImpl();

    @Override
    public void onStart() {
        Log.d(TAG, "Start TrafficGatherer");
        if (trafficService != null) {
            trafficService.start();
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Stop TrafficGatherer");
        if (trafficService != null) {
            trafficService.stop();
            calculateIntermediateValueAndEmitEvent();
        }
    }

    @Override
    public Interval getInterval() {
        return INTERVAL;
    }

    @Override
    public void run() {
        if (trafficService != null) {
            trafficService.stop();
            calculateIntermediateValueAndEmitEvent();
            trafficService.start();
        }
    }

    private void calculateIntermediateValueAndEmitEvent() {
        final CurrentInterfaceTraffic currentInterfaceTraffic =
                new CurrentInterfaceTraffic(trafficService.getRxBytes(),
                        trafficService.getTxBytes(), trafficService.getDurationNs());
        setCurrentValue(currentInterfaceTraffic);
        emitUpdateEvent(currentInterfaceTraffic);
    }

    protected void emitUpdateEvent(final CurrentInterfaceTraffic currentInterfaceTraffic) {
        Log.d(TAG, currentInterfaceTraffic.toString());

        for (final InterfaceTrafficUpdateListener listener : getListenerList()) {
            listener.onTrafficUpdate(currentInterfaceTraffic);
        }
    }
}
