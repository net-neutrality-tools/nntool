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

import java.util.concurrent.TimeUnit;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface RunnableGatherer extends Runnable {

    public static class Interval {
        private int duration;
        private TimeUnit timeUnit;

        public Interval(final int amount, final TimeUnit timeUnit) {
            this.duration = amount;
            this.timeUnit = timeUnit;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public void setTimeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public long toMillis() {
            return TimeUnit.MILLISECONDS.convert(duration, timeUnit);
        }
    }

    Interval getInterval();
}
