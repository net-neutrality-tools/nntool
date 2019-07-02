package at.alladin.nettest.nntool.android.speed;

import android.support.annotation.Keep;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class SpeedMeasurementState {

    private String measurementUuid;

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

    public String getMeasurementUuid() {
        return measurementUuid;
    }

    public void setMeasurementUuid(String measurementUuid) {
        this.measurementUuid = measurementUuid;
    }

    @Override
    public String toString() {
        return "SpeedMeasurementState{" +
                "measurementUuid='" + measurementUuid + '\'' +
                ", downloadMeasurement=" + downloadMeasurement +
                ", uploadMeasurement=" + uploadMeasurement +
                ", pingMeasurement=" + pingMeasurement +
                ", progress=" + progress +
                ", measurementPhase=" + measurementPhase +
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
        private long throughputAvgBps;

        public long getThroughputAvgBps() {
            return throughputAvgBps;
        }

        public void setThroughputAvgBps(long throughputAvgBps) {
            this.throughputAvgBps = throughputAvgBps;
        }

        @Override
        public String toString() {
            return "JniSpeedState{" +
                    "throughputAvgBps=" + throughputAvgBps +
                    '}';
        }
    }
}
