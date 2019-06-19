package at.alladin.nettest.nntool.android.app.workflow.measurement;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.nntool.android.speed.JniSpeedMeasurementResult;
import at.alladin.nettest.nntool.android.speed.SpeedTaskDesc;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SpeedMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ConnectionInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SpeedMeasurementRawDataItemDto;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class ResultParseUtil {

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

        if (result.getRttUdpResultList() != null && result.getRttUdpResultList().size() > 0) {
            final JniSpeedMeasurementResult.RttUdpResult lastRttResult = result.getRttUdpResultList().get(result.getRttUdpResultList().size() - 1);
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

            final List<RttDto> rttList = new ArrayList<>();
            rttInfoDto.setRtts(rttList);

            for (JniSpeedMeasurementResult.RttUdpResult rtt : result.getRttUdpResultList()) {
                rttList.add(parseRttUdpResultIntoRttDto(rtt));
            }
        }

        if (timeInfo != null) {
            ret.setRelativeStartTimeRttNs(0L);
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

    private static RttDto parseRttUdpResultIntoRttDto (JniSpeedMeasurementResult.RttUdpResult rtt) {
        final RttDto ret = new RttDto();

        ret.setRelativeTimeNs(rtt.getDurationNs());
        ret.setNumSent(rtt.getNumSent());
        ret.setNumReceived(rtt.getNumReceived());
        ret.setNumError(rtt.getNumError());
        ret.setNumMissing(rtt.getNumMissing());
        ret.setAverageNs(rtt.getAverageNs());
        ret.setMaximumNs(rtt.getMaxNs());
        ret.setMedianNs(rtt.getMedianNs());
        ret.setMinimumNs(rtt.getMinNs());
        ret.setStandardDeviationNs(rtt.getStandardDeviationNs());
        ret.setProgress(rtt.getProgress());

        return ret;
    }
}
