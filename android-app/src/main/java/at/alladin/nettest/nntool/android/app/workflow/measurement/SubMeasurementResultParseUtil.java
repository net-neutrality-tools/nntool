package at.alladin.nettest.nntool.android.app.workflow.measurement;

import java.util.ArrayList;
import java.util.List;

import com.zafaco.speed.JniSpeedMeasurementResult;
import com.zafaco.speed.SpeedTaskDesc;
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
            //we sent the duration (not the duration_total), as the mbps are calculated from the duration => the total duration can be calculated from the relative starttimes
            ret.setDurationDownloadNs(lastDownloadEntry.getDurationNs());
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
            ret.setDurationUploadNs(lastUploadEntry.getDurationNs());
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

        if (result.getRttUdpResult() != null) {
            final JniSpeedMeasurementResult.RttUdpResult lastRttResult = result.getRttUdpResult();
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
            if (lastRttResult.getSingleRtts() != null && lastRttResult.getSingleRtts().size() > 0) {
                final List<RttDto> rttList = new ArrayList<>();
                RttDto toAdd;
                for (JniSpeedMeasurementResult.SingleRtt rtt : lastRttResult.getSingleRtts()) {
                    toAdd = new RttDto();
                    toAdd.setRttNs(rtt.getRttNs());
                    toAdd.setRelativeTimeNs(rtt.getRelativeTimeNsMeasurementStart());
                    rttList.add(toAdd);
                }
                rttInfoDto.setRtts(rttList);
            }

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

    private static List<RttDto> parseIntoRttDto(final List<Long> singleRttList) {
        final List<RttDto> ret = new ArrayList<>();
        for (Long rtt : singleRttList) {
            RttDto dto = new RttDto();
            dto.setRttNs(rtt);
            ret.add(dto);
        }
        return ret;
    }
}
