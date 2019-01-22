package at.alladin.nettest.shared.qos;

public class UdpPayload {

    private int communicationFlag;

    private int packetNumber;

    private String uuid;

    private Long timestamp;

    public int getCommunicationFlag() {
        return communicationFlag;
    }

    public void setCommunicationFlag(int communicationFlag) {
        this.communicationFlag = communicationFlag;
    }

    public int getPacketNumber() {
        return packetNumber;
    }

    public void setPacketNumber(int packetNumber) {
        this.packetNumber = packetNumber;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

	@Override
	public String toString() {
		return "UdpPayload [communicationFlag=" + communicationFlag + ", packetNumber=" + packetNumber + ", uuid="
				+ uuid + ", timestamp=" + timestamp + "]";
	}
}
