package at.alladin.nettest.nntool.android.speed;

import java.io.Serializable;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class SpeedTaskDesc implements Serializable {

    private String speedServerAddrV4;

    private String speedServerAddrV6;

    private int speedServerPort = 80;

    private int rttCount;

    private int downloadStreams;

    private int uploadStreams;

    private boolean performDownload = true;

    private boolean performUpload = true;

    private boolean performRtt = true;

    private boolean useEncryption = false;

    public int getRttCount() {
        return rttCount;
    }

    public void setRttCount(int rttCount) {
        this.rttCount = rttCount;
    }

    public int getDownloadStreams() {
        return downloadStreams;
    }

    public void setDownloadStreams(int downloadStreams) {
        this.downloadStreams = downloadStreams;
    }

    public int getUploadStreams() {
        return uploadStreams;
    }

    public void setUploadStreams(int uploadStreams) {
        this.uploadStreams = uploadStreams;
    }

    public String getSpeedServerAddrV4() {
        return speedServerAddrV4;
    }

    public void setSpeedServerAddrV4(String speedServerAddrV4) {
        this.speedServerAddrV4 = speedServerAddrV4;
    }

    public String getSpeedServerAddrV6() {
        return speedServerAddrV6;
    }

    public void setSpeedServerAddrV6(String speedServerAddrV6) {
        this.speedServerAddrV6 = speedServerAddrV6;
    }

    public int getSpeedServerPort() {
        return speedServerPort;
    }

    public void setSpeedServerPort(int speedServerPort) {
        this.speedServerPort = speedServerPort;
    }

    public boolean isPerformDownload() {
        return performDownload;
    }

    public void setPerformDownload(boolean performDownload) {
        this.performDownload = performDownload;
    }

    public boolean isPerformUpload() {
        return performUpload;
    }

    public void setPerformUpload(boolean performUpload) {
        this.performUpload = performUpload;
    }

    public boolean isPerformRtt() {
        return performRtt;
    }

    public void setPerformRtt(boolean performRtt) {
        this.performRtt = performRtt;
    }

    public boolean isUseEncryption() {
        return useEncryption;
    }

    public void setUseEncryption(boolean useEncryption) {
        this.useEncryption = useEncryption;
    }

    @Override
    public String toString() {
        return "SpeedTaskDesc{" +
                "speedServerAddrV4='" + speedServerAddrV4 + '\'' +
                ", speedServerAddrV6='" + speedServerAddrV6 + '\'' +
                ", speedServerPort=" + speedServerPort +
                ", rttCount=" + rttCount +
                ", downloadStreams=" + downloadStreams +
                ", uploadStreams=" + uploadStreams +
                ", performDownload=" + performDownload +
                ", performUpload=" + performUpload +
                ", performRtt=" + performRtt +
                ", useEncryption=" + useEncryption +
                '}';
    }
}