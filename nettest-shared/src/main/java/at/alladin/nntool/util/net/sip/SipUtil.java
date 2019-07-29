package at.alladin.nntool.util.net.sip;

import java.util.Locale;

import at.alladin.nntool.util.net.sip.SipRequestMessage.SipRequestType;

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
		return sip;
	}
	
	public static SipRequestMessage parseRequestData(final byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		
		return parseRequestData(new String(data));
	}
	
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
}
