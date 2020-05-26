/*!
    \file JniSpeedMeasurementResult.java
    \author zafaco GmbH <info@zafaco.de>
    \author alladin-IT GmbH <info@alladin.at>
    \date Last update: 2020-05-26

    Copyright (C) 2016 - 2020 zafaco GmbH
    Copyright (C) 2019 alladin-IT GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.zafaco.speed;

import android.support.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

public class JniSpeedMeasurementResult {

    private List<BandwidthResult> downloadInfoList = new ArrayList<>();

    private List<BandwidthResult> uploadInfoList = new ArrayList<>();

    private RttUdpResult rttUdpResult;

    private TimeInfo timeInfo;

    private String measurementServerIp;

    private String clientIp;

    public RttUdpResult getRttUdpResult() {
        return rttUdpResult;
    }

    @Keep
    public void setRttUdpResult(RttUdpResult rttUdpResult) {
        this.rttUdpResult = rttUdpResult;
    }

    public List<BandwidthResult> getDownloadInfoList() {
        return downloadInfoList;
    }

    public void setDownloadInfoList(List<BandwidthResult> downloadInfoList) {
        this.downloadInfoList = downloadInfoList;
    }

    public List<BandwidthResult> getUploadInfoList() {
        return uploadInfoList;
    }

    public void setUploadInfoList(List<BandwidthResult> uploadInfoList) {
        this.uploadInfoList = uploadInfoList;
    }

    @Keep
    public void addDownloadInfo(final BandwidthResult downloadResult) {
        this.downloadInfoList.add(downloadResult);
    }

    @Keep
    public void addUploadInfo(final BandwidthResult uploadResult) {
        this.uploadInfoList.add(uploadResult);
    }

    public TimeInfo getTimeInfo() {
        return timeInfo;
    }

    @Keep
    public void setTimeInfo(TimeInfo timeInfo) {
        this.timeInfo = timeInfo;
    }

    public String getMeasurementServerIp() {
        return measurementServerIp;
    }

    @Keep
    public void setMeasurementServerIp(String measurementServerIp) {
        this.measurementServerIp = measurementServerIp;

        // @TODO only to set peer with something useful. It can probably be removed in a future refactor
        this.rttUdpResult.setPeer(measurementServerIp);
    }

    public String getClientIp() {
        return clientIp;
    }

    @Keep
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Override
    public String toString() {
        return "JniSpeedMeasurementResult{" +
                "downloadInfoList=" + downloadInfoList +
                ", uploadInfoList=" + uploadInfoList +
                ", rttUdpResult=" + rttUdpResult +
                ", timeInfo=" + timeInfo +
                ", measurementServerIp='" + measurementServerIp + '\'' +
                ", clientIp='" + clientIp + '\'' +
                '}';
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

        @Keep
        public void setBytes(Long bytes) {
            this.bytes = bytes;
        }

        public Long getBytesIncludingSlowStart() {
            return bytesIncludingSlowStart;
        }

        @Keep
        public void setBytesIncludingSlowStart(Long bytesIncludingSlowStart) {
            this.bytesIncludingSlowStart = bytesIncludingSlowStart;
        }

        public Long getDurationNs() {
            return durationNs;
        }

        @Keep
        public void setDurationNs(Long durationNs) {
            this.durationNs = durationNs;
        }

        public Long getDurationNsTotal() {
            return durationNsTotal;
        }

        @Keep
        public void setDurationNsTotal(Long durationNsTotal) {
            this.durationNsTotal = durationNsTotal;
        }

        public Integer getNumStreamsEnd() {
            return numStreamsEnd;
        }

        @Keep
        public void setNumStreamsEnd(Integer numStreamsEnd) {
            this.numStreamsEnd = numStreamsEnd;
        }

        public Integer getNumStreamsStart() {
            return numStreamsStart;
        }

        @Keep
        public void setNumStreamsStart(Integer numStreamsStart) {
            this.numStreamsStart = numStreamsStart;
        }

        public Float getProgress() {
            return progress;
        }

        @Keep
        public void setProgress(Float progress) {
            this.progress = progress;
        }

        public Long getThroughputAvgBps() {
            return throughputAvgBps;
        }

        @Keep
        public void setThroughputAvgBps(Long throughputAvgBps) {
            this.throughputAvgBps = throughputAvgBps;
        }

        @Override
        public String toString() {
            return "BandwidthResult{" +
                    "bytes=" + bytes +
                    ", bytesIncludingSlowStart=" + bytesIncludingSlowStart +
                    ", durationNs=" + durationNs +
                    ", durationNsTotal=" + durationNsTotal +
                    ", numStreamsEnd=" + numStreamsEnd +
                    ", numStreamsStart=" + numStreamsStart +
                    ", progress=" + progress +
                    ", throughputAvgBps=" + throughputAvgBps +
                    '}';
        }
    }

    public static class RttUdpResult {
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

        private Float progress;

        private Long standardDeviationNs;

        private List<SingleRtt> singleRtts = new ArrayList<>();

        @Keep
        public void addSingleRtt(Long rttNs, Long relativeTimeNs) {
            final SingleRtt toAdd = new SingleRtt();
            toAdd.setRttNs(rttNs);
            toAdd.setRelativeTimeNsMeasurementStart(relativeTimeNs);
            singleRtts.add(toAdd);
        }

        public List<SingleRtt> getSingleRtts() {
            return singleRtts;
        }

        @Keep
        public void setSingleRtts(List<SingleRtt> singleRtts) {
            this.singleRtts = singleRtts;
        }

        public Long getAverageNs() {
            return averageNs;
        }

        @Keep
        public void setAverageNs(Long averageNs) {
            this.averageNs = averageNs;
        }

        public Long getDurationNs() {
            return durationNs;
        }

        @Keep
        public void setDurationNs(Long durationNs) {
            this.durationNs = durationNs;
        }

        public Long getMaxNs() {
            return maxNs;
        }

        @Keep
        public void setMaxNs(Long maxNs) {
            this.maxNs = maxNs;
        }

        public Long getMedianNs() {
            return medianNs;
        }

        @Keep
        public void setMedianNs(Long medianNs) {
            this.medianNs = medianNs;
        }

        public Long getMinNs() {
            return minNs;
        }

        @Keep
        public void setMinNs(Long minNs) {
            this.minNs = minNs;
        }

        public Integer getNumError() {
            return numError;
        }

        @Keep
        public void setNumError(Integer numError) {
            this.numError = numError;
        }

        public Integer getNumMissing() {
            return numMissing;
        }

        @Keep
        public void setNumMissing(Integer numMissing) {
            this.numMissing = numMissing;
        }

        public Integer getNumReceived() {
            return numReceived;
        }

        @Keep
        public void setNumReceived(Integer numReceived) {
            this.numReceived = numReceived;
        }

        public Integer getNumSent() {
            return numSent;
        }

        @Keep
        public void setNumSent(Integer numSent) {
            this.numSent = numSent;
        }

        public Integer getPacketSize() {
            return packetSize;
        }

        @Keep
        public void setPacketSize(Integer packetSize) {
            this.packetSize = packetSize;
        }

        public String getPeer() {
            return peer;
        }

        @Keep
        public void setPeer(String peer) {
            this.peer = peer;
        }

        public Long getStandardDeviationNs() {
            return standardDeviationNs;
        }

        @Keep
        public void setStandardDeviationNs(Long standardDeviationNs) {
            this.standardDeviationNs = standardDeviationNs;
        }

        public Float getProgress() {
            return progress;
        }

        @Keep
        public void setProgress(Float progress) {
            this.progress = progress;
        }

        @Override
        public String toString() {
            return "RttUdpInfo{" +
                    "averageNs=" + averageNs +
                    ", durationNs=" + durationNs +
                    ", maxNs=" + maxNs +
                    ", medianNs=" + medianNs +
                    ", minNs=" + minNs +
                    ", numError=" + numError +
                    ", numMissing=" + numMissing +
                    ", numReceived=" + numReceived +
                    ", numSent=" + numSent +
                    ", packetSize=" + packetSize +
                    ", peer='" + peer + '\'' +
                    ", progress=" + progress +
                    ", standardDeviationNs=" + standardDeviationNs +
                    '}';
        }
    }

    public static class SingleRtt {
        private Long rttNs;

        private Long relativeTimeNsMeasurementStart;

        public SingleRtt() {

        }

        public SingleRtt(Long rttNs, Long relativeTimeNsMeasurementStart) {
            this.rttNs = rttNs;
            this.relativeTimeNsMeasurementStart = relativeTimeNsMeasurementStart;
        }

        public Long getRttNs() {
            return rttNs;
        }

        @Keep
        public void setRttNs(Long rttNs) {
            this.rttNs = rttNs;
        }

        public Long getRelativeTimeNsMeasurementStart() {
            return relativeTimeNsMeasurementStart;
        }

        @Keep
        public void setRelativeTimeNsMeasurementStart(Long relativeTimeNsMeasurementStart) {
            this.relativeTimeNsMeasurementStart = relativeTimeNsMeasurementStart;
        }

        @Override
        public String toString() {
            return "SingleRtt{" +
                    "rttNs=" + rttNs +
                    ", relativeTimeNsMeasurementStart=" + relativeTimeNsMeasurementStart +
                    '}';
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

        @Keep
        public void setDownloadStart(Long downloadStart) {
            this.downloadStart = downloadStart;
        }

        public Long getDownloadEnd() {
            return downloadEnd;
        }

        @Keep
        public void setDownloadEnd(Long downloadEnd) {
            this.downloadEnd = downloadEnd;
        }

        public Long getRttUdpStart() {
            return rttUdpStart;
        }

        @Keep
        public void setRttUdpStart(Long rttUdpStart) {
            this.rttUdpStart = rttUdpStart;
        }

        public Long getRttUdpEnd() {
            return rttUdpEnd;
        }

        @Keep
        public void setRttUdpEnd(Long rttUdpEnd) {
            this.rttUdpEnd = rttUdpEnd;
        }

        public Long getUploadStart() {
            return uploadStart;
        }

        @Keep
        public void setUploadStart(Long uploadStart) {
            this.uploadStart = uploadStart;
        }

        public Long getUploadEnd() {
            return uploadEnd;
        }

        @Keep
        public void setUploadEnd(Long uploadEnd) {
            this.uploadEnd = uploadEnd;
        }

        @Override
        public String toString() {
            return "TimeInfo{" +
                    "downloadStart=" + downloadStart +
                    ", downloadEnd=" + downloadEnd +
                    ", rttUdpStart=" + rttUdpStart +
                    ", rttUdpEnd=" + rttUdpEnd +
                    ", uploadStart=" + uploadStart +
                    ", uploadEnd=" + uploadEnd +
                    '}';
        }
    }

}
