/*!
    \file SpeedTaskDesc.java
    \author zafaco GmbH <info@zafaco.de>
    \author alladin-IT GmbH <info@alladin.at>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH
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

import java.io.Serializable;

public class SpeedTaskDesc implements Serializable {

    private String speedServerAddrV4;

    private String speedServerAddrV6;

    private int speedServerPort = 443;

    private int rttCount = 10;

    private int downloadStreams;

    private int uploadStreams;

    private boolean performDownload = true;

    private boolean performUpload = true;

    private boolean performRtt = true;

    private boolean useEncryption = true;

    private boolean useIpV6 = false;

    private String clientIp;

    private String clientIpPublic;

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

    public boolean isUseIpV6() {
        return useIpV6;
    }

    public void setUseIpV6(boolean useIpV6) {
        this.useIpV6 = useIpV6;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getClientIpPublic() {
        return clientIpPublic;
    }

    public void setClientIpPublic(String clientIpPublic) {
        this.clientIpPublic = clientIpPublic;
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
                ", useIpV6=" + useIpV6 +
                ", clientIp='" + clientIp + '\'' +
                ", clientIpPublic='" + clientIpPublic + '\'' +
                '}';
    }
}
