package at.alladin.nettest.nntool.android.app.workflow.measurement.jni;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class JniSpeedMeasurementState {

    private JniSpeedState downloadMeasurement = new JniSpeedState();

    private JniSpeedState uploadMeasurement = new JniSpeedState();

    public JniSpeedState getDownloadMeasurement() {
        return downloadMeasurement;
    }

    public void setDownloadMeasurement(JniSpeedState downloadMeasurement) {
        this.downloadMeasurement = downloadMeasurement;
    }

    public JniSpeedState getUploadMeasurement() {
        return uploadMeasurement;
    }

    public void setUploadMeasurement(JniSpeedState uploadMeasurement) {
        this.uploadMeasurement = uploadMeasurement;
    }

    @Override
    public String toString() {
        return "JniSpeedMeasurementState{" +
                "downloadMeasurement=" + downloadMeasurement +
                ", uploadMeasurement=" + uploadMeasurement +
                '}';
    }

    public class JniSpeedState {
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
