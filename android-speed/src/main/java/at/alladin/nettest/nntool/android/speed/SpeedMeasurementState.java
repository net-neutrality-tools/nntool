package at.alladin.nettest.nntool.android.speed;

import android.support.annotation.Keep;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class SpeedMeasurementState {

    private SpeedPhaseState downloadMeasurement = new SpeedPhaseState();

    private SpeedPhaseState uploadMeasurement = new SpeedPhaseState();

    private PingPhaseState pingMeasurement = new PingPhaseState();

    private float progress;

    private MeasurementPhase measurementPhase = MeasurementPhase.INIT;

    public SpeedPhaseState getDownloadMeasurement() {
        return downloadMeasurement;
    }

    public void setDownloadMeasurement(SpeedPhaseState downloadMeasurement) {
        this.downloadMeasurement = downloadMeasurement;
    }

    public SpeedPhaseState getUploadMeasurement() {
        return uploadMeasurement;
    }

    public void setUploadMeasurement(SpeedPhaseState uploadMeasurement) {
        this.uploadMeasurement = uploadMeasurement;
    }

    public PingPhaseState getPingMeasurement() {
        return pingMeasurement;
    }

    public void setPingMeasurement(PingPhaseState pingMeasurement) {
        this.pingMeasurement = pingMeasurement;
    }

    @Keep
    public void setMeasurementPhaseByStringValue(final String state) {
        if (state == null) {
            return;
        }
        try {
            measurementPhase = MeasurementPhase.valueOf(state);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public MeasurementPhase getMeasurementPhase() {
        return measurementPhase;
    }

    public void setMeasurementPhase(MeasurementPhase measurementPhase) {
        this.measurementPhase = measurementPhase;
    }

    @Override
    public String toString() {
        return "JniSpeedMeasurementState{" +
                "downloadMeasurement=" + downloadMeasurement +
                ", uploadMeasurement=" + uploadMeasurement +
                '}';
    }

    public enum MeasurementPhase {
        INIT,
        PING,
        DOWNLOAD,
        UPLOAD,
        END
    }

    public class PingPhaseState {
        private long averageMs;

        public long getAverageMs() {
            return averageMs;
        }

        public void setAverageMs(long averageMs) {
            this.averageMs = averageMs;
        }
    }

    public class SpeedPhaseState {
        private long bytes;

        private long bytesIncludingSlowStart;

        private long durationMs;

        private long durationMsTotal;

        private int numStreamsEnd;

        private int numStreamsStart;

        private long throughputAvgBps;

        public long getBytes() {
            return bytes;
        }

        public void setBytes(long bytes) {
            this.bytes = bytes;
        }

        public long getBytesIncludingSlowStart() {
            return bytesIncludingSlowStart;
        }

        public void setBytesIncludingSlowStart(long bytesIncludingSlowStart) {
            this.bytesIncludingSlowStart = bytesIncludingSlowStart;
        }

        public long getDurationMs() {
            return durationMs;
        }

        public void setDurationMs(long durationMs) {
            this.durationMs = durationMs;
        }

        public long getDurationMsTotal() {
            return durationMsTotal;
        }

        public void setDurationMsTotal(long durationMsTotal) {
            this.durationMsTotal = durationMsTotal;
        }

        public int getNumStreamsEnd() {
            return numStreamsEnd;
        }

        public void setNumStreamsEnd(int numStreamsEnd) {
            this.numStreamsEnd = numStreamsEnd;
        }

        public int getNumStreamsStart() {
            return numStreamsStart;
        }

        public void setNumStreamsStart(int numStreamsStart) {
            this.numStreamsStart = numStreamsStart;
        }

        public long getThroughputAvgBps() {
            return throughputAvgBps;
        }

        public void setThroughputAvgBps(long throughputAvgBps) {
            this.throughputAvgBps = throughputAvgBps;
        }

        @Override
        public String toString() {
            return "JniSpeedState{" +
                    "bytes=" + bytes +
                    ", bytesIncludingSlowStart=" + bytesIncludingSlowStart +
                    ", durationMs=" + durationMs +
                    ", durationMsTotal=" + durationMsTotal +
                    ", numStreamsEnd=" + numStreamsEnd +
                    ", numStreamsStart=" + numStreamsStart +
                    ", throughputAvgBps=" + throughputAvgBps +
                    '}';
        }
    }
}
