package at.alladin.nntool.qos.testserver.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.alladin.nntool.qos.testserver.ServerPreferences;
import at.alladin.nntool.qos.testserver.TestServer;
import at.alladin.nntool.qos.testserver.ServerPreferences.TestServerServiceEnum;
import at.alladin.nntool.qos.testserver.entity.TestCandidate;
import at.alladin.nntool.qos.testserver.mock.SocketWithCountDownLatchMockup;
import at.alladin.nntool.qos.testserver.mock.util.SocketCommunicationExpectationsUtil;
import at.alladin.nntool.qos.testserver.tcp.TcpClientHandler;
import at.alladin.nntool.qos.testserver.tcp.TcpMultiClientServer;
import at.alladin.nntool.qos.testserver.util.TestServerConsole;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
public class TcpClientHandlerIntegrationTest {

	@Mocked Socket socket;
	
	@Mocked TcpMultiClientServer tcpServer;
	
	CountDownLatch latch;
	
	SocketWithCountDownLatchMockup slm;
	
	@Before
	public void init() throws IOException {
		latch = new CountDownLatch(1);
		slm = new SocketWithCountDownLatchMockup(latch);
	}
	
	@After
	public void teardown() {
		TestServer.newInstance();
	}
	
	@Test
	public void testClientHandlerWithSingleLineRequestMessage() throws Exception {
		TestServer.getInstance().serverPreferences = new ServerPreferences();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();		
		SocketCommunicationExpectationsUtil.createExpectationWithOutputStream(socket, os, "MESSAGE");		
		TcpClientHandler tch = new TcpClientHandler(socket, tcpServer);

		tch.run();
		
		final boolean reachedZeroCountdown = latch.await(10L, TimeUnit.SECONDS);				
		assertTrue("CountDownLatch hasn't reached 0 and ran into a timeout", reachedZeroCountdown);
		assertEquals("TCP/NTP response != 'MESSAGE\n'", "MESSAGE\n", os.toString());		
	}
	
	@Test
	public void testClientHandlerWithSingleMultiLineRequestMessage() throws Exception {
		TestServer.getInstance().serverPreferences = new ServerPreferences();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		SocketCommunicationExpectationsUtil.createExpectationWithOutputStream(socket, os, "MULTILINE\nMESSAGE");		
		TcpClientHandler tch = new TcpClientHandler(socket, tcpServer);

		tch.run();
		
		final boolean reachedZeroCountdown = latch.await(10L, TimeUnit.SECONDS);				
		assertTrue("CountDownLatch hasn't reached 0 and ran into a timeout", reachedZeroCountdown);
		assertEquals("TCP/NTP response != 'MULTILINE\n'", "MULTILINE\n", os.toString());
	}
	
	@Test
	public void testClientHandlerWithIpCheckEnabledAndClientSocketContainingValidSocketAddress() throws Exception {
		TestServer.getInstance().serverPreferences = new ServerPreferences();
		TestServer.getInstance().serverPreferences.setIpCheck(true);
		
		new Expectations() {
			{
				tcpServer.getCandidateMap(); 
				result = new Delegate<Map<InetAddress, TestCandidate>>() {
					public Map<InetAddress, TestCandidate> delegate() {
						final Map<InetAddress, TestCandidate> map = new HashMap<InetAddress, TestCandidate>();
						map.put(socket.getInetAddress(), new TestCandidate());
						return map;
					}
				};
			}
		};
		
		final ByteArrayOutputStream os = new ByteArrayOutputStream();		
		SocketCommunicationExpectationsUtil.createExpectationWithOutputStream(socket, os, "MESSAGE");		
		TcpClientHandler tch = new TcpClientHandler(socket, tcpServer);

		tch.run();

		final boolean reachedZeroCountdown = latch.await(10L, TimeUnit.SECONDS);				
		assertTrue("CountDownLatch hasn't reached 0 and ran into a timeout", reachedZeroCountdown);	
		assertEquals("TCP/NTP response != 'MESSAGE\n'", "MESSAGE\n", os.toString());
	}

	@Test
	public void testClientHandlerWithIpCheckEnabledAndClientSocketContainingUnknownSocketAddress() throws Exception {
		TestServer.getInstance().serverPreferences = new ServerPreferences();
		TestServer.getInstance().serverPreferences.setIpCheck(true);

		List<String> logList = new ArrayList<>();
		
		new MockUp<TestServerConsole>() {
			
			@Mock
			public void log(String msg, int verboseLevelNeeded, TestServerServiceEnum service) {
				logList.add(msg);
			}
		};
		
		final ByteArrayOutputStream os = new ByteArrayOutputStream();		
		SocketCommunicationExpectationsUtil.createExpectationWithOutputStream(socket, os, "MESSAGE");		
		TcpClientHandler tch = new TcpClientHandler(socket, tcpServer);

		tch.run();

		final boolean reachedZeroCountdown = latch.await(10L, TimeUnit.SECONDS);				
		assertTrue("CountDownLatch hasn't reached 0 and ran into a timeout", reachedZeroCountdown);	
		
		assertEquals("Amount of logged messages != 2", 2, logList.size());
		
		assertEquals("Logged messages [1] != '/1.1.1.1: not a valid candidate for TCP/NTP'", 
				"/1.1.1.1: not a valid candidate for TCP/NTP", logList.get(1));
	}
	
	@Test
	public void testClientHandlerCatchingSocketTimeoutExceptionThenClosingSocket() throws Exception {
		TestServer.getInstance().serverPreferences = new ServerPreferences();
		SocketCommunicationExpectationsUtil.createExpectationWithThrownException(socket, new SocketTimeoutException(), "PING");		
		TcpClientHandler tch = new TcpClientHandler(socket, tcpServer);
		tch.run();
		final boolean reachedZeroCountdown = latch.await(10L, TimeUnit.SECONDS);				
		assertTrue("CountDownLatch hasn't reached 0 and ran into a timeout", reachedZeroCountdown);
	}
	
	@Test
	public void testClientHandlerCatchingIOExceptionThenClosingSocket() throws Exception {
		TestServer.getInstance().serverPreferences = new ServerPreferences();
		SocketCommunicationExpectationsUtil.createExpectationWithThrownException(socket, new IOException(), "PING");		
		TcpClientHandler tch = new TcpClientHandler(socket, tcpServer);
		tch.run();
		final boolean reachedZeroCountdown = latch.await(10L, TimeUnit.SECONDS);				
		assertTrue("CountDownLatch hasn't reached 0 and ran into a timeout", reachedZeroCountdown);
	}
	
	@Test
	public void testClientHandlerCatchingMultipleExceptionWhileClosingClientSocket() throws Exception {
		TestServer.getInstance().serverPreferences = new ServerPreferences();
		
		slm = new SocketWithCountDownLatchMockup(latch) {
			
			@Mock
			public synchronized void close() throws IOException {
				super.close();
				throw new IOException();
			}
		};
		
		SocketCommunicationExpectationsUtil.createExpectationWithThrownException(socket, new SocketTimeoutException(), "PING");
		//SocketCommunicationExpectationsUtil.createExpectationWithThrownExceptionOnClose(socket, new IOException());

		TcpClientHandler tch = new TcpClientHandler(socket, tcpServer);
		
		tch.run();
		final boolean reachedZeroCountdown = latch.await(10L, TimeUnit.SECONDS);				
		assertTrue("CountDownLatch hasn't reached 0 and ran into a timeout", reachedZeroCountdown);
	}
}