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
