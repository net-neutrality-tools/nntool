package at.alladin.nntool.util.net.sip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import at.alladin.nntool.util.net.sip.SipRequestMessage.SipRequestType;
import at.alladin.nntool.util.net.sip.SipResponseMessage.SipResponseType;

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

	@Test
	public void testSipRequestParsingWithError() {
		final SipRequestMessage msg = SipUtil.parseRequestData("UNKNOWN_ACTION sip:bob@home SIP/2.0\n");
		assertNull("msg == null", msg);
	}

	@Test
	public void testSipResponseParsing() {
		final SipResponseMessage msg = SipUtil.parseResponseData("SIP/2.0 180 Ringing\n" + 
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
		assertEquals("SIP Response != Ringing", SipResponseType.RINGING, msg.getType());		
		assertEquals("SIP Response Code != 180", SipResponseType.RINGING.getCode(), msg.getType().getCode());
	}
	
	@Test
	public void testSipResponseParsingWithError() {
		final SipResponseMessage msg = SipUtil.parseResponseData("SIP/2.0 280 Ringing\n");
		assertNull("msg == null", msg);
	}
}
