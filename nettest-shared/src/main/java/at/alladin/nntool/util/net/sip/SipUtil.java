package at.alladin.nntool.util.net.sip;

import java.util.Locale;

import at.alladin.nntool.util.net.sip.SipRequestMessage.SipRequestType;
import at.alladin.nntool.util.net.sip.SipResponseMessage.SipResponseType;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class SipUtil {

	/**
	 * generates INVITE request using From as Via header
	 * @param from
	 * @param to
	 * @return
	 */
	public static SipRequestMessage generateInviteMessage(final String from, final String to) {
		return generateInviteMessage(from, to, from);
	}
	

	/**
	 * 
	 * @param from
	 * @param to
	 * @param via
	 * @return
	 */
	public static SipRequestMessage generateInviteMessage(final String from, final String to, final String via) {
		final SipRequestMessage sip = new SipRequestMessage(SipRequestType.INVITE);
		sip.setFrom(from);
		sip.setTo(to);
		sip.setVia(via);
		return sip;
	}

	/**
	 *
	 * @param from
	 * @param to
	 * @param via
	 * @return
	 */
	public static SipRequestMessage generateByeMessage(final String from, final String to, final String via) {
		final SipRequestMessage sip = new SipRequestMessage(SipRequestType.BYE);
		sip.setFrom(from);
		sip.setTo(to);
		sip.setVia(via);
		return sip;
	}


	/**
	 * 
	 * @param data
	 * @return
	 */
	public static SipRequestMessage parseRequestData(final byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		
		return parseRequestData(new String(data));
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static SipRequestMessage parseRequestData(final String data) {
		if (data == null || data.length() == 0) {
			return null;
		}
		
		final String[] parts = data.split("\n");
		final String[] firstLine = parts[0].split(" ");
		if (firstLine.length < 3) {
			return null;
		}
		
		SipRequestType type = null;
		try {
			type = SipRequestMessage.SipRequestType.valueOf(firstLine[0].toUpperCase(Locale.US));
			if (type == null) {
				return null;
			}
		}
		catch (final IllegalArgumentException e) {
			return null;
		}
		
		final SipRequestMessage msg = new SipRequestMessage(type);
		
		if (parts.length > 1) {
			for (int i = 1; i < parts.length; i++) {
				final int pos = parts[i].indexOf(":"); 
				if (pos > 0) {
					try {
						msg.addHeader(parts[i].substring(0, pos).toUpperCase(Locale.US), parts[i].substring(pos+1).trim());
					}
					catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return msg;
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static SipResponseMessage parseResponseData(final byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		
		return parseResponseData(new String(data));
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static SipResponseMessage parseResponseData(final String data) {
		if (data == null || data.length() == 0) {
			return null;
		}
		
		final String[] parts = data.split("\n");
		final String[] firstLine = parts[0].split(" ");
		if (firstLine.length < 3) {
			return null;
		}
		
		SipResponseType type = null;
		try {
			final int code = Integer.parseInt(firstLine[1].toUpperCase(Locale.US));
			final String codeName = firstLine[2].toUpperCase(Locale.US);

			type = SipResponseType.getByCode(code);
			if (codeName == null || type == null || !codeName.equals(type.toString())) {
				return null;
			}
		}
		catch (final IllegalArgumentException e) {
			return null;
		}
		
		final SipResponseMessage msg = new SipResponseMessage(type);
		
		if (parts.length > 1) {
			for (int i = 1; i < parts.length; i++) {
				final int pos = parts[i].indexOf(":"); 
				if (pos > 0) {
					try {
						msg.addHeader(parts[i].substring(0, pos).toUpperCase(Locale.US), parts[i].substring(pos+1).trim());
					}
					catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return msg;
	}

}
