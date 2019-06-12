package at.alladin.nettest.nntool.android.speed;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 * Complete result as reported by ias-cpp
 */
public class SpeedMeasurementResult {

    private List<BandwidthResult> downloadInfo = new ArrayList<>();

    private List<BandwidthResult> uploadInfo = new ArrayList<>();

    private RttUdpInfo rttUdpInfo;

    private TimeInfo timeInfo;

    public List<BandwidthResult> getDownloadInfo() {
        return downloadInfo;
    }

    public void setDownloadInfo(List<BandwidthResult> downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    public List<BandwidthResult> getUploadInfo() {
        return uploadInfo;
    }

    public void setUploadInfo(List<BandwidthResult> uploadInfo) {
        this.uploadInfo = uploadInfo;
    }

    public RttUdpInfo getRttUdpInfo() {
        return rttUdpInfo;
    }

    public void setRttUdpInfo(RttUdpInfo rttUdpInfo) {
        this.rttUdpInfo = rttUdpInfo;
    }

    public TimeInfo getTimeInfo() {
        return timeInfo;
    }

    public void setTimeInfo(TimeInfo timeInfo) {
        this.timeInfo = timeInfo;
    }

    public static class BandwidthResult {

        private Long bytes;

        private Long bytesIncludingSlowStart;

        private Long durationNs;

        private Long durationNsTotal;

        private Integer numStreamsEnd;

        private Integer numStreamsStart;

        private Float progress;

        private Long throughputAvgBps;

        public Long getBytes() {
            return bytes;
        }

        public void setBytes(Long bytes) {
            this.bytes = bytes;
        }

        public Long getBytesIncludingSlowStart() {
            return bytesIncludingSlowStart;
        }

        public void setBytesIncludingSlowStart(Long bytesIncludingSlowStart) {
            this.bytesIncludingSlowStart = bytesIncludingSlowStart;
        }

        public Long getDurationNs() {
            return durationNs;
        }

        public void setDurationNs(Long durationNs) {
            this.durationNs = durationNs;
        }

        public Long getDurationNsTotal() {
            return durationNsTotal;
        }

        public void setDurationNsTotal(Long durationNsTotal) {
            this.durationNsTotal = durationNsTotal;
        }

        public Integer getNumStreamsEnd() {
            return numStreamsEnd;
        }

        public void setNumStreamsEnd(Integer numStreamsEnd) {
            this.numStreamsEnd = numStreamsEnd;
        }

        public Integer getNumStreamsStart() {
            return numStreamsStart;
        }

        public void setNumStreamsStart(Integer numStreamsStart) {
            this.numStreamsStart = numStreamsStart;
        }

        public Float getProgress() {
            return progress;
        }

        public void setProgress(Float progress) {
            this.progress = progress;
        }

        public Long getThroughputAvgBps() {
            return throughputAvgBps;
        }

        public void setThroughputAvgBps(Long throughputAvgBps) {
            this.throughputAvgBps = throughputAvgBps;
        }
    }

    public static class RttUdpInfo {
        private Long averageNs;

        private Long durationNs;

        private Long maxNs;

        private Long medianNs;

        private Long minNs;

        private Integer numError;

        private Integer numMissing;

        private Integer numReceived;

        private Integer numSent;

        private Integer packetSize;

        private String peer;

        private Long standardDeviationNs;

        public Long getAverageNs() {
            return averageNs;
        }

        public void setAverageNs(Long averageNs) {
            this.averageNs = averageNs;
        }

        public Long getDurationNs() {
            return durationNs;
        }

        public void setDurationNs(Long durationNs) {
            this.durationNs = durationNs;
        }

        public Long getMaxNs() {
            return maxNs;
        }

        public void setMaxNs(Long maxNs) {
            this.maxNs = maxNs;
        }

        public Long getMedianNs() {
            return medianNs;
        }

        public void setMedianNs(Long medianNs) {
            this.medianNs = medianNs;
        }

        public Long getMinNs() {
            return minNs;
        }

        public void setMinNs(Long minNs) {
            this.minNs = minNs;
        }

        public Integer getNumError() {
            return numError;
        }

        public void setNumError(Integer numError) {
            this.numError = numError;
        }

        public Integer getNumMissing() {
            return numMissing;
        }

        public void setNumMissing(Integer numMissing) {
            this.numMissing = numMissing;
        }

        public Integer getNumReceived() {
            return numReceived;
        }

        public void setNumReceived(Integer numReceived) {
            this.numReceived = numReceived;
        }

        public Integer getNumSent() {
            return numSent;
        }

        public void setNumSent(Integer numSent) {
            this.numSent = numSent;
        }

        public Integer getPacketSize() {
            return packetSize;
        }

        public void setPacketSize(Integer packetSize) {
            this.packetSize = packetSize;
        }

        public String getPeer() {
            return peer;
        }

        public void setPeer(String peer) {
            this.peer = peer;
        }

        public Long getStandardDeviationNs() {
            return standardDeviationNs;
        }

        public void setStandardDeviationNs(Long standardDeviationNs) {
            this.standardDeviationNs = standardDeviationNs;
        }
    }

    public static class TimeInfo {
        private Long downloadStart;

        private Long downloadEnd;

        private Long rttUdpStart;

        private Long rttUdpEnd;

        private Long uploadStart;

        private Long uploadEnd;

        public Long getDownloadStart() {
            return downloadStart;
        }

        public void setDownloadStart(Long downloadStart) {
            this.downloadStart = downloadStart;
        }

        public Long getDownloadEnd() {
            return downloadEnd;
        }

        public void setDownloadEnd(Long downloadEnd) {
            this.downloadEnd = downloadEnd;
        }

        public Long getRttUdpStart() {
            return rttUdpStart;
        }

        public void setRttUdpStart(Long rttUdpStart) {
            this.rttUdpStart = rttUdpStart;
        }

        public Long getRttUdpEnd() {
            return rttUdpEnd;
        }

        public void setRttUdpEnd(Long rttUdpEnd) {
            this.rttUdpEnd = rttUdpEnd;
        }

        public Long getUploadStart() {
            return uploadStart;
        }

        public void setUploadStart(Long uploadStart) {
            this.uploadStart = uploadStart;
        }

        public Long getUploadEnd() {
            return uploadEnd;
        }

        public void setUploadEnd(Long uploadEnd) {
            this.uploadEnd = uploadEnd;
        }
    }

}
