package at.alladin.nntool.util.net.sip;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class SipResponseMessage extends SipMessage {

	/**
	 * 
	 * @author lb@alladin.at
	 *
	 */
	public enum SipResponseType {
		TRYING(100),
		RINGING(180),
		OK(200);
		
		final int code;
		
		private SipResponseType(final int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
	}
	
	private final SipResponseType type;
	
	public SipResponseMessage(final SipResponseType type, final SipRequestMessage request) {
		this.type = type;
	}

	public SipResponseType getType() {
		return type;
	}

	@Override
	String getFirstLine() {
		return SIP_PROTOCOL_STRING + " " + type.getCode() + " " + type.toString();
	}
}
