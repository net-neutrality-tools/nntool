package at.alladin.nettest.nntool.android.app.workflow.measurement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.nntool.android.speed.JniSpeedMeasurementResult;
import at.alladin.nettest.nntool.android.speed.SpeedTaskDesc;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SpeedMeasurementResult;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class ResultParseUtilTest {

    private JniSpeedMeasurementResult jniSpeedResult;

    private SpeedTaskDesc taskDesc;

    private static final long lastBytesDownload = 158549920L;
    private static final long lastBytesDownloadIncludingSlowStart = 341843580L;
    private static final long lastBytesUpload = 128773020L;
    private static final long lastBytesUploadIncludingSlowStart = 312066680L;
    private static final long lastDurationRttNs = 6691659000000L;
    private static final long lastDurationDownloadNs = 2500000000L;
    private static final long lastDurationUploadNs = 2000000000L;
    private static final long durationUploadTotal = 5676805000L;
    private static final long durationDownloadTotal = 6150238000L;
    private static final long rttUdpStart = 1561554378264499000L;
    private static final long uploadStart = 1561554404912793000L;
    private static final long downloadStart = 1561554390079342000L;

    @Before
    public void init() {
        jniSpeedResult = new JniSpeedMeasurementResult();
        final JniSpeedMeasurementResult.TimeInfo timeInfo = new JniSpeedMeasurementResult.TimeInfo();
        jniSpeedResult.setTimeInfo(timeInfo);

        timeInfo.setDownloadStart(1561554390079342000L);
        timeInfo.setDownloadEnd(1561554403818863000L);
        timeInfo.setRttUdpStart(1561554378264499000L);
        timeInfo.setRttUdpEnd(1561554389964947000L);
        timeInfo.setUploadStart(1561554404912793000L);
        timeInfo.setUploadEnd(1561554418577800000L);

        final List<JniSpeedMeasurementResult.BandwidthResult> downloadResultList = new ArrayList<>();
        jniSpeedResult.setDownloadInfoList(downloadResultList);

        JniSpeedMeasurementResult.BandwidthResult down = new JniSpeedMeasurementResult.BandwidthResult();
        down.setBytes(32480120L);
        down.setBytesIncludingSlowStart(215783920L);
        down.setDurationNs(500000000L);
        down.setDurationNsTotal(4148195000L);
        down.setNumStreamsEnd(0);
        down.setNumStreamsStart(4);
        down.setProgress(0.2f);
        down.setThroughputAvgBps(519681920L);
        downloadResultList.add(down);

        down = new JniSpeedMeasurementResult.BandwidthResult();
        down.setBytes(64327200L);
        down.setBytesIncludingSlowStart(247620860L);
        down.setDurationNs(1000000000L);
        down.setDurationNsTotal(4655557000L);
        down.setNumStreamsEnd(0);
        down.setNumStreamsStart(4);
        down.setProgress(0.4f);
        down.setThroughputAvgBps(514617600L);
        downloadResultList.add(down);

        down = new JniSpeedMeasurementResult.BandwidthResult();
        down.setBytes(96509540L);
        down.setBytesIncludingSlowStart(279803200L);
        down.setDurationNs(1500000000L);
        down.setDurationNsTotal(5165506000L);
        down.setNumStreamsEnd(0);
        down.setNumStreamsStart(4);
        down.setProgress(0.6f);
        down.setThroughputAvgBps(514717546L);
        downloadResultList.add(down);

        down = new JniSpeedMeasurementResult.BandwidthResult();
        down.setBytes(128773020L);
        down.setBytesIncludingSlowStart(312066680L);
        down.setDurationNs(2000000000L);
        down.setDurationNsTotal(5676805000L);
        down.setNumStreamsEnd(0);
        down.setNumStreamsStart(4);
        down.setProgress(0.8f);
        down.setThroughputAvgBps(515092080L);
        downloadResultList.add(down);

        down = new JniSpeedMeasurementResult.BandwidthResult();
        down.setBytes(lastBytesDownload);
        down.setBytesIncludingSlowStart(lastBytesDownloadIncludingSlowStart);
        down.setDurationNs(lastDurationDownloadNs);
        down.setDurationNsTotal(durationDownloadTotal);
        down.setNumStreamsEnd(4);
        down.setNumStreamsStart(4);
        down.setProgress(1.0f);
        down.setThroughputAvgBps(507359744L);
        downloadResultList.add(down);


        final List<JniSpeedMeasurementResult.BandwidthResult> uploadResultList = new ArrayList<>();
        jniSpeedResult.setUploadInfoList(uploadResultList);

        JniSpeedMeasurementResult.BandwidthResult up = new JniSpeedMeasurementResult.BandwidthResult();
        up.setBytes(32480120L);
        up.setBytesIncludingSlowStart(215783920L);
        up.setDurationNs(500000000L);
        up.setDurationNsTotal(4148195000L);
        up.setNumStreamsEnd(0);
        up.setNumStreamsStart(4);
        up.setProgress(0.25f);
        up.setThroughputAvgBps(519681920L);
        uploadResultList.add(up);

        up = new JniSpeedMeasurementResult.BandwidthResult();
        up.setBytes(64327200L);
        up.setBytesIncludingSlowStart(247620860L);
        up.setDurationNs(1000000000L);
        up.setDurationNsTotal(4655557000L);
        up.setNumStreamsEnd(0);
        up.setNumStreamsStart(4);
        up.setProgress(0.5f);
        up.setThroughputAvgBps(514617600L);
        uploadResultList.add(up);

        up = new JniSpeedMeasurementResult.BandwidthResult();
        up.setBytes(96509540L);
        up.setBytesIncludingSlowStart(279803200L);
        up.setDurationNs(1500000000L);
        up.setDurationNsTotal(5165506000L);
        up.setNumStreamsEnd(0);
        up.setNumStreamsStart(4);
        up.setProgress(0.75f);
        up.setThroughputAvgBps(514717546L);
        uploadResultList.add(up);

        up = new JniSpeedMeasurementResult.BandwidthResult();
        up.setBytes(lastBytesUpload);
        up.setBytesIncludingSlowStart(lastBytesUploadIncludingSlowStart);
        up.setDurationNs(lastDurationUploadNs);
        up.setDurationNsTotal(durationUploadTotal);
        up.setNumStreamsEnd(4);
        up.setNumStreamsStart(4);
        up.setProgress(1.0f);
        up.setThroughputAvgBps(515092080L);
        uploadResultList.add(up);

        final List<JniSpeedMeasurementResult.RttUdpResult> rttResultList = new ArrayList<>();
        jniSpeedResult.setRttUdpResultList(rttResultList);

        JniSpeedMeasurementResult.RttUdpResult rtt = new JniSpeedMeasurementResult.RttUdpResult();
        rtt.setAverageNs(23294000000L);
        rtt.setDurationNs(3686543000000L);
        rtt.setMaxNs(24296000000L);
        rtt.setMedianNs(23526000000L);
        rtt.setMinNs(22062000000L);
        rtt.setNumError(0);
        rtt.setNumMissing(0);
        rtt.setNumReceived(3);
        rtt.setNumSent(3);
        rtt.setPacketSize(0);
        rtt.setPeer("peer-ias-de-01.net-neutrality.tools");
        rtt.setProgress(0.5f);
        rtt.setStandardDeviationNs(926579852L);
        rttResultList.add(rtt);

        rtt = new JniSpeedMeasurementResult.RttUdpResult();
        rtt.setAverageNs(23294000000L);
        rtt.setDurationNs(4195873000000L);
        rtt.setMaxNs(24296000000L);
        rtt.setMedianNs(23526000000L);
        rtt.setMinNs(22062000000L);
        rtt.setNumError(0);
        rtt.setNumMissing(0);
        rtt.setNumReceived(3);
        rtt.setNumSent(3);
        rtt.setPacketSize(0);
        rtt.setPeer("peer-ias-de-01.net-neutrality.tools");
        rtt.setProgress(0.5f);
        rtt.setStandardDeviationNs(926579852L);
        rttResultList.add(rtt);

        rtt = new JniSpeedMeasurementResult.RttUdpResult();
        rtt.setAverageNs(23076000000L);
        rtt.setDurationNs(4684975000000L);
        rtt.setMaxNs(24296000000L);
        rtt.setMedianNs(22974000000L);
        rtt.setMinNs(22062000000L);
        rtt.setNumError(0);
        rtt.setNumMissing(0);
        rtt.setNumReceived(4);
        rtt.setNumSent(4);
        rtt.setPacketSize(0);
        rtt.setPeer("peer-ias-de-01.net-neutrality.tools");
        rtt.setProgress(0.66666f);
        rtt.setStandardDeviationNs(886962654L);
        rttResultList.add(rtt);

        rtt = new JniSpeedMeasurementResult.RttUdpResult();
        rtt.setAverageNs(23076000000L);
        rtt.setDurationNs(4684975000000L);
        rtt.setMaxNs(24296000000L);
        rtt.setMedianNs(22974000000L);
        rtt.setMinNs(22062000000L);
        rtt.setNumError(0);
        rtt.setNumMissing(0);
        rtt.setNumReceived(4);
        rtt.setNumSent(4);
        rtt.setPacketSize(0);
        rtt.setPeer("peer-ias-de-01.net-neutrality.tools");
        rtt.setProgress(0.66666f);
        rtt.setStandardDeviationNs(886962654L);
        rttResultList.add(rtt);

        rtt = new JniSpeedMeasurementResult.RttUdpResult();
        rtt.setAverageNs(23076000000L);
        rtt.setDurationNs(4684975000000L);
        rtt.setMaxNs(24296000000L);
        rtt.setMedianNs(22974000000L);
        rtt.setMinNs(22062000000L);
        rtt.setNumError(0);
        rtt.setNumMissing(1);
        rtt.setNumReceived(4);
        rtt.setNumSent(5);
        rtt.setPacketSize(0);
        rtt.setPeer("peer-ias-de-01.net-neutrality.tools");
        rtt.setProgress(0.83333f);
        rtt.setStandardDeviationNs(799526459L);
        rttResultList.add(rtt);

        rtt = new JniSpeedMeasurementResult.RttUdpResult();
        rtt.setAverageNs(22796000000L);
        rtt.setDurationNs(lastDurationRttNs);
        rtt.setMaxNs(24296000000L);
        rtt.setMedianNs(22625000000L);
        rtt.setMinNs(22062000000L);
        rtt.setNumError(0);
        rtt.setNumMissing(1);
        rtt.setNumReceived(5);
        rtt.setNumSent(6);
        rtt.setPacketSize(0);
        rtt.setPeer("peer-ias-de-01.net-neutrality.tools");
        rtt.setProgress(1.0f);
        rtt.setStandardDeviationNs(893248332L);
        rttResultList.add(rtt);

        taskDesc = new SpeedTaskDesc();

        taskDesc.setSpeedServerAddrV4("peer-ias-de-01.net-neutrality.tools");
        taskDesc.setSpeedServerPort(80);
        taskDesc.setUseEncryption(false);
        taskDesc.setDownloadStreams(4);
        taskDesc.setUploadStreams(4);
        taskDesc.setRttCount(6);
        taskDesc.setPerformUpload(true);
        taskDesc.setPerformDownload(true);
        taskDesc.setPerformRtt(true);

    }

    @Test
    public void parseBasicCompleteResult () {
        final SpeedMeasurementResult result = ResultParseUtil.parseIntoSpeedMeasurementResult(jniSpeedResult, taskDesc);

        Assert.assertEquals("unexpected task description", "peer-ias-de-01.net-neutrality.tools", result.getConnectionInfo().getAddress());
        Assert.assertEquals("unexpected task description", 80, result.getConnectionInfo().getPort().intValue());
        Assert.assertEquals("unexpected task description", 4, result.getConnectionInfo().getRequestedNumStreamsDownload().intValue());
        Assert.assertEquals("unexpected task description", 4, result.getConnectionInfo().getRequestedNumStreamsUpload().intValue());
        Assert.assertEquals("unexpected task description", 4, result.getConnectionInfo().getActualNumStreamsDownload().intValue());
        Assert.assertEquals("unexpected task description", 4, result.getConnectionInfo().getActualNumStreamsUpload().intValue());
        Assert.assertFalse("unexpected task description", result.getConnectionInfo().isEncrypted());


        Assert.assertEquals("unexpected download bytes", lastBytesDownload, result.getBytesDownload().longValue());
        Assert.assertEquals("unexpected download bytes", lastBytesDownloadIncludingSlowStart, result.getBytesDownloadIncludingSlowStart().longValue());

        Assert.assertEquals("unexpected upload bytes", lastBytesUpload, result.getBytesUpload().longValue());
        Assert.assertEquals("unexpected upload bytes", lastBytesUploadIncludingSlowStart, result.getBytesUploadIncludingSlowStart().longValue());

        Assert.assertEquals("unexpected duration", lastDurationRttNs, result.getDurationRttNs().longValue());
        Assert.assertEquals("unexpected duration", durationDownloadTotal, result.getDurationDownloadNs().longValue());
        Assert.assertEquals("unexpected duration", durationUploadTotal, result.getDurationUploadNs().longValue());

        Assert.assertEquals("unexpected relative start time", 0, result.getRelativeStartTimeRttNs().longValue());
        Assert.assertEquals("unexpected relative start time", downloadStart - rttUdpStart, result.getRelativeStartTimeDownloadNs().longValue());
        Assert.assertEquals("unexpected relative start time", uploadStart - rttUdpStart, result.getRelativeStartTimeUploadNs().longValue());

        Assert.assertEquals("unexpected number of rtts", 5, result.getRttInfo().getRtts().size());
        Assert.assertEquals("unexpected number of download entries", 5, result.getDownloadRawData().size());
        Assert.assertEquals("unexpected number of upload entries", 4, result.getUploadRawData().size());

    }

    @Test
    public void parsePartialResultContainingAvailableResults () {
        jniSpeedResult.setDownloadInfoList(null);
        jniSpeedResult.setRttUdpResultList(null);
        SpeedMeasurementResult result = ResultParseUtil.parseIntoSpeedMeasurementResult(jniSpeedResult, taskDesc);

        Assert.assertEquals("unexpected task description", "peer-ias-de-01.net-neutrality.tools", result.getConnectionInfo().getAddress());
        Assert.assertEquals("unexpected task description", 80, result.getConnectionInfo().getPort().intValue());
        Assert.assertEquals("unexpected task description", 4, result.getConnectionInfo().getRequestedNumStreamsDownload().intValue());
        Assert.assertEquals("unexpected task description", 4, result.getConnectionInfo().getRequestedNumStreamsUpload().intValue());
        Assert.assertNull("unexpected task description", result.getConnectionInfo().getActualNumStreamsDownload());
        Assert.assertEquals("unexpected task description", 4, result.getConnectionInfo().getActualNumStreamsUpload().intValue());
        Assert.assertFalse("unexpected task description", result.getConnectionInfo().isEncrypted());


        Assert.assertNull("unexpected download bytes", result.getBytesDownload());
        Assert.assertNull("unexpected download bytes", result.getBytesDownloadIncludingSlowStart());

        Assert.assertEquals("unexpected upload bytes", lastBytesUpload, result.getBytesUpload().longValue());
        Assert.assertEquals("unexpected upload bytes", lastBytesUploadIncludingSlowStart, result.getBytesUploadIncludingSlowStart().longValue());

        Assert.assertNull("unexpected duration", result.getDurationRttNs());
        Assert.assertNull("unexpected duration", result.getDurationDownloadNs());
        Assert.assertEquals("unexpected duration", durationUploadTotal, result.getDurationUploadNs().longValue());

        Assert.assertEquals("unexpected relative start time", 0, result.getRelativeStartTimeRttNs().longValue());
        Assert.assertNull("unexpected relative start time", result.getRelativeStartTimeDownloadNs());
        Assert.assertEquals("unexpected relative start time", uploadStart - rttUdpStart, result.getRelativeStartTimeUploadNs().longValue());

        Assert.assertNull("unexpected number of rtts", result.getRttInfo().getRtts());
        Assert.assertNull("unexpected number of download entries", result.getDownloadRawData());
        Assert.assertEquals("unexpected number of upload entries", 4, result.getUploadRawData().size());
    }
}
