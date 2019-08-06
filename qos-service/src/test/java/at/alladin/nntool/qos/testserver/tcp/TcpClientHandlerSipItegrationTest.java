package at.alladin.nntool.qos.testserver.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.alladin.nntool.qos.testserver.ServerPreferences;
import at.alladin.nntool.qos.testserver.TestServer;
import at.alladin.nntool.qos.testserver.mock.SocketWithCountDownLatchMockup;
import at.alladin.nntool.qos.testserver.mock.TestServerConsoleMockup;
import at.alladin.nntool.qos.testserver.mock.TestServerConsoleMockup.ConsoleLog;
import at.alladin.nntool.qos.testserver.mock.util.SocketCommunicationExpectationsUtil;
import at.alladin.nntool.qos.testserver.tcp.competences.BasicCompetence;
import at.alladin.nntool.qos.testserver.tcp.competences.Competence;
import at.alladin.nntool.qos.testserver.tcp.competences.sip.SipCompetence;
import at.alladin.nntool.util.net.sip.SipResponseMessage;
import at.alladin.nntool.util.net.sip.SipResponseMessage.SipResponseType;
import at.alladin.nntool.util.net.sip.SipUtil;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;

/**
 * 
 * @author lb
 *
 */
public class TcpClientHandlerSipItegrationTest {

	@Mocked Socket socket;
	
	@Mocked TcpMultiClientServer tcpServer;
	
	CountDownLatch latch;
	
	SocketWithCountDownLatchMockup slm;
	
	TestServerConsoleMockup tscm; 
	
	@Before
	public void init() throws IOException {
		latch = new CountDownLatch(1);
		slm = new SocketWithCountDownLatchMockup(latch);
		tscm = new TestServerConsoleMockup();
	}
	
	@After
	public void teardown() {
		TestServer.newInstance();
	}

	@Test
	public void testClientHandlerWithSimpleSipWorkflowIncludingInviteAndTrying(@Mocked FilterOutputStream fos) throws Exception {
		TestServer.getInstance().serverPreferences = new ServerPreferences();
		final AtomicReference<String[]> results = new AtomicReference<>(new String[2]);
		SocketCommunicationExpectationsUtil.createExpectationWithMultipleResultStrings(socket, fos, results,
						"INVITE sip:bob@home SIP/2.0\n" + 
						"Via: SIP/2.0/TCP localhost:5060\n" + 
						"Max-Forwards: 70\n" + 
						"From: Alice <sip:alice@home>\n" + 
						"To: Bob <sip:bob@home>\n\n");
		
		TcpClientHandler tch = new TcpClientHandler(socket, tcpServer);

		new Expectations() {
			{
				tcpServer.getCompetences();
				result = new Delegate<Deque<Competence>>() {
					public Deque<Competence> delegate() {
						final Deque<Competence> r = new ArrayDeque<>();
						r.addFirst(new BasicCompetence());
						r.addFirst(new SipCompetence());
						return r;
					}
				};
			}
		};
		
		tch.run();
		
		final boolean reachedZeroCountdown = latch.await(10L, TimeUnit.SECONDS);
				
		assertTrue("CountDownLatch hasn't reached 0 and ran into a timeout", reachedZeroCountdown);

		final SipResponseMessage resTrying = SipUtil.parseResponseData(results.get()[0]);
		assertNotNull("SIP response == null", resTrying);
		assertEquals("SIP response != TRYING", SipResponseType.TRYING, resTrying.getType());
		assertEquals("FROM != Bob <sip:bob@home>", "Bob <sip:bob@home>", resTrying.getFrom());
		assertEquals("TO != Alice <sip:alice@home>", "Alice <sip:alice@home>", resTrying.getTo());
		assertEquals("VIA != SIP/2.0/TCP localhost:5060", "SIP/2.0/TCP localhost:5060", resTrying.getVia());
		
		final SipResponseMessage resRinging = SipUtil.parseResponseData(results.get()[1]);
		assertNotNull("SIP response == null", resRinging);
		assertEquals("SIP response != RINGING", SipResponseType.RINGING, resRinging.getType());
		assertEquals("FROM != Bob <sip:bob@home>", "Bob <sip:bob@home>", resRinging.getFrom());
		assertEquals("TO != Alice <sip:alice@home>", "Alice <sip:alice@home>", resRinging.getTo());
		assertEquals("VIA != SIP/2.0/TCP localhost:5060", "SIP/2.0/TCP localhost:5060", resRinging.getVia());
	}
	
	@Test
	public void testClientHandlerWithSimpleSipWorkflowIncludingInviteAndTryingAndCheckDelayBetweenPackets(@Mocked FilterOutputStream fos) throws Exception {
		TestServer.getInstance().serverPreferences = new ServerPreferences();
		final AtomicReference<String[]> results = new AtomicReference<>(new String[2]);
		SocketCommunicationExpectationsUtil.createExpectationWithMultipleResultStrings(socket, fos, results,
						"INVITE sip:bob@home SIP/2.0\n" + 
						"Via: SIP/2.0/TCP localhost:5060\n" + 
						"Max-Forwards: 70\n" + 
						"From: Alice <sip:alice@home>\n" + 
						"To: Bob <sip:bob@home>\n\n");
		
		TcpClientHandler tch = new TcpClientHandler(socket, tcpServer);

		new Expectations() {
			{
				tcpServer.getCompetences();
				result = new Delegate<Deque<Competence>>() {
					public Deque<Competence> delegate() {
						final Deque<Competence> r = new ArrayDeque<>();
						r.addFirst(new BasicCompetence());
						r.addFirst(new SipCompetence());
						return r;
					}
				};
			}
		};
		
		tch.run();
		
		final boolean reachedZeroCountdown = latch.await(10L, TimeUnit.SECONDS);
				
		assertTrue("CountDownLatch hasn't reached 0 and ran into a timeout", reachedZeroCountdown);

		final List<ConsoleLog> lc = tscm.getLogList();
		assertNotNull("Log is null", lc);
		assertTrue("Log size < 2", lc.size() >= 2);
		
		final ConsoleLog tryingLog = lc.get(lc.size()-2);
		final ConsoleLog ringingLog = lc.get(lc.size()-1);
		assertNotNull("tryingLog is null", tryingLog);
		assertNotNull("ringingLog is null", ringingLog);
		
		assertTrue("tryingLog not after ringingLog", ringingLog.getTimeMs() > tryingLog.getTimeMs());
		assertTrue("tryingLog and ringingLog time diff < 100", (ringingLog.getTimeMs() - tryingLog.getTimeMs()) >= 100);
	}
}