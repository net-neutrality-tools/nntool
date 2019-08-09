package at.alladin.nettest.nntool.android.app.workflow.measurement;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.nntool.android.speed.JniSpeedMeasurementResult;
import at.alladin.nettest.nntool.android.speed.SpeedTaskDesc;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.QoSMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SpeedMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ConnectionInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SpeedMeasurementRawDataItemDto;
import at.alladin.nntool.client.v2.task.result.QoSResultCollector;
import at.alladin.nntool.client.v2.task.result.QoSTestResult;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class SubMeasurementResultParseUtil {

    public static QoSMeasurementResult parseIntoQosMeasurementResult(final QoSResultCollector qoSResultCollector) {
        final QoSMeasurementResult ret = new QoSMeasurementResult();

        ret.setObjectiveResults(new ArrayList<>());
        for (final QoSTestResult qosResult : qoSResultCollector.getResults()) {
            ret.getObjectiveResults().add(qosResult.getResultMap());
        }

        return ret;
    }

    public static SpeedMeasurementResult parseIntoSpeedMeasurementResult(final JniSpeedMeasurementResult result, final SpeedTaskDesc speedTaskDesc) {
        final SpeedMeasurementResult ret = new SpeedMeasurementResult();

        final JniSpeedMeasurementResult.TimeInfo timeInfo = result.getTimeInfo();
        final ConnectionInfoDto connectionInfoDto = new ConnectionInfoDto();
        ret.setConnectionInfo(connectionInfoDto);

        final RttInfoDto rttInfoDto = new RttInfoDto();
        ret.setRttInfo(rttInfoDto);

        if (speedTaskDesc != null) {
            connectionInfoDto.setAddress(speedTaskDesc.getSpeedServerAddrV4());
            connectionInfoDto.setPort(speedTaskDesc.getSpeedServerPort());
            connectionInfoDto.setEncrypted(speedTaskDesc.isUseEncryption());
            connectionInfoDto.setRequestedNumStreamsDownload(speedTaskDesc.getDownloadStreams());
            connectionInfoDto.setRequestedNumStreamsUpload(speedTaskDesc.getUploadStreams());

            rttInfoDto.setRequestedNumPackets(speedTaskDesc.getRttCount());
        }

        if (result.getDownloadInfoList() != null && result.getDownloadInfoList().size() > 0) {
            final JniSpeedMeasurementResult.BandwidthResult lastDownloadEntry = result.getDownloadInfoList().get(result.getDownloadInfoList().size() - 1);
            ret.setBytesDownload(lastDownloadEntry.getBytes());
            ret.setBytesDownloadIncludingSlowStart(lastDownloadEntry.getBytesIncludingSlowStart());
            ret.setDurationDownloadNs(lastDownloadEntry.getDurationNsTotal());
            if (timeInfo != null && timeInfo.getDownloadStart() != null ) {
                ret.setRelativeStartTimeDownloadNs(timeInfo.getRttUdpStart() != null ? timeInfo.getDownloadStart() - timeInfo.getRttUdpStart() : 0);
            }

            connectionInfoDto.setActualNumStreamsDownload(lastDownloadEntry.getNumStreamsStart());

            final List<SpeedMeasurementRawDataItemDto> downloadResults = new ArrayList<>();
            for (JniSpeedMeasurementResult.BandwidthResult bandwidth : result.getDownloadInfoList()) {
                downloadResults.add(parseBandWidthResultIntoRawDataItem(bandwidth));
            }

            ret.setDownloadRawData(downloadResults);
        }

        if (result.getUploadInfoList() != null && result.getUploadInfoList().size() > 0) {
            final JniSpeedMeasurementResult.BandwidthResult lastUploadEntry = result.getUploadInfoList().get(result.getUploadInfoList().size() - 1);
            ret.setBytesUpload(lastUploadEntry.getBytes());
            ret.setBytesUploadIncludingSlowStart(lastUploadEntry.getBytesIncludingSlowStart());
            ret.setDurationUploadNs(lastUploadEntry.getDurationNsTotal());
            if (timeInfo != null && timeInfo.getUploadStart() != null ) {
                ret.setRelativeStartTimeUploadNs(timeInfo.getRttUdpStart() != null ? timeInfo.getUploadStart() - timeInfo.getRttUdpStart() :
                        timeInfo.getDownloadStart() != null ? timeInfo.getUploadStart() - timeInfo.getDownloadStart() : 0);
            }

            connectionInfoDto.setActualNumStreamsUpload(lastUploadEntry.getNumStreamsStart());

            final List<SpeedMeasurementRawDataItemDto> uploadResults = new ArrayList<>();
            for (JniSpeedMeasurementResult.BandwidthResult bandwidth : result.getUploadInfoList()) {
                uploadResults.add(parseBandWidthResultIntoRawDataItem(bandwidth));
            }

            ret.setUploadRawData(uploadResults);
        }

        if (result.getRttUdpResultList() != null && result.getRttUdpResultList().size() > 0) {
            final JniSpeedMeasurementResult.RttUdpResult lastRttResult = result.getRttUdpResultList().get(result.getRttUdpResultList().size() - 1);
            ret.setDurationRttNs(lastRttResult.getDurationNs());

            rttInfoDto.setNumSent(lastRttResult.getNumSent());
            rttInfoDto.setNumReceived(lastRttResult.getNumReceived());
            rttInfoDto.setNumError(lastRttResult.getNumError());
            rttInfoDto.setNumMissing(lastRttResult.getNumMissing());
            rttInfoDto.setPacketSize(lastRttResult.getPacketSize());
            rttInfoDto.setAddress(lastRttResult.getPeer());
            rttInfoDto.setAverageNs(lastRttResult.getAverageNs());
            rttInfoDto.setMaximumNs(lastRttResult.getMaxNs());
            rttInfoDto.setMedianNs(lastRttResult.getMedianNs());
            rttInfoDto.setMinimumNs(lastRttResult.getMinNs());
            rttInfoDto.setStandardDeviationNs(lastRttResult.getStandardDeviationNs());

            rttInfoDto.setRtts(parseRttListForSingleEntries(result.getRttUdpResultList()));

        }

        if (timeInfo != null) {
            ret.setRelativeStartTimeRttNs(0L);
        }

        if (result.getMeasurementServerIp() != null) {
            connectionInfoDto.setIpAddress(result.getMeasurementServerIp());
        }

        return ret;
    }

    private static SpeedMeasurementRawDataItemDto parseBandWidthResultIntoRawDataItem (JniSpeedMeasurementResult.BandwidthResult bandwidth) {
        final SpeedMeasurementRawDataItemDto ret = new SpeedMeasurementRawDataItemDto();
        ret.setBytes(bandwidth.getBytes());
        ret.setRelativeTimeNs(bandwidth.getDurationNs());
        ret.setBytesIncludingSlowStart(bandwidth.getBytesIncludingSlowStart());
        return ret;
    }

    private static List<RttDto> parseRttListForSingleEntries(final List<JniSpeedMeasurementResult.RttUdpResult> rttResults) {
        final List<RttDto> ret = new ArrayList<>();
        boolean foundFirstEntry = false;
        int lastCount = 0;
        long previousAvgSum = 0;
        for (JniSpeedMeasurementResult.RttUdpResult res : rttResults) {
            //if we have less than 4 received rtts, we can reconstruct single values
            if (!foundFirstEntry && res.getNumReceived() < 4) {
                foundFirstEntry = true;
                lastCount = res.getNumReceived();
                switch (res.getNumReceived()) {
                    case 1:
                    case 3: {
                        final RttDto rtt = new RttDto();
                        rtt.setRttNs(res.getAverageNs());
                        rtt.setRelativeTimeNs(res.getDurationNs());
                        ret.add(rtt);
                        if (res.getNumReceived() == 1) {
                            break;
                        }
                    }
                    case 2: {
                        RttDto rtt = new RttDto();
                        rtt.setRttNs(res.getMaxNs());
                        rtt.setRelativeTimeNs(res.getDurationNs());
                        ret.add(rtt);

                        rtt = new RttDto();
                        rtt.setRttNs(res.getMinNs());
                        rtt.setRelativeTimeNs(res.getDurationNs());
                        ret.add(rtt);
                        break;
                    }
                }
                previousAvgSum = res.getAverageNs();
            } else if (foundFirstEntry && lastCount != res.getNumReceived()) {

                int newRtts = res.getNumReceived() - lastCount;
                /*
                 * the newest (n-th) rtt is given as
                 * rtt_n = (CurrentAverage * CurrentNumReceived) - (PreviousAverage * PreviousNumReceived)
                 *
                 * depending on the number of newly received rtts, we divide to get the specific results
                 *
                 */
                long rttVal = (res.getAverageNs() * res.getNumReceived() - (previousAvgSum * lastCount)) / newRtts;
                for (int i = 0; i < newRtts; ++i) {
                    final RttDto rtt = new RttDto();
                    rtt.setRttNs(rttVal);
                    rtt.setRelativeTimeNs(res.getDurationNs());
                    ret.add(rtt);
                }

                lastCount = res.getNumReceived();
                previousAvgSum = res.getAverageNs();
            }
        }
        return ret;
    }
}