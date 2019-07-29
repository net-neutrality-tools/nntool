package at.alladin.nntool.util.net.sip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import at.alladin.nntool.util.net.sip.SipRequestMessage.SipRequestType;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class SipUtilTest {
	
	@Test
	public void testSipRequestParsing() {
		final SipRequestMessage msg = SipUtil.parseRequestData("INVITE sip:bob@home SIP/2.0\n" + 
				"Via: SIP/2.0/TCP localhost:5060\n" + 
				"Max-Forwards: 70\n" + 
				"From: Alice <sip:alice@home>\n" + 
				"To: Bob <sip:bob@home>\n" + 
				"Call-ID: 1234@home\n" + 
				"Contact: <sip:alice@home>");
		
		assertNotNull("msg != null", msg);
		assertEquals("TO != Bob <sip:bob@home>", "Bob <sip:bob@home>", msg.getTo());
		assertEquals("FROM != Alice <sip:alice@home>", "Alice <sip:alice@home>", msg.getFrom());
		assertEquals("VIA != SIP/2.0/TCP localhost:5060", "SIP/2.0/TCP localhost:5060", msg.getVia());
		assertEquals("SIP Action != INVITE", SipRequestType.INVITE, msg.getType());
	}
}
