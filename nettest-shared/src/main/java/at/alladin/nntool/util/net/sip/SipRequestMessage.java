package at.alladin.nntool.util.net.sip;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class SipRequestMessage extends SipMessage {

	public enum SipRequestType {
		INVITE,
		ACK,
		BYE
	}

	private SipRequestType type;
	
	public SipRequestMessage(final SipRequestType type) {
		this.type = type;
	}
	
	public SipRequestType getType() {
		return type;
	}

	public void setType(SipRequestType type) {
		this.type = type;
	}

	@Override
	String getFirstLine() {
		return type.toString() + " " + getTo() + " " + SIP_PROTOCOL_STRING;
	}

	@Override
	public String toString() {
		return "SipRequestMessage [type=" + type + ", toString()=" + super.toString() + "]";
	}
}
