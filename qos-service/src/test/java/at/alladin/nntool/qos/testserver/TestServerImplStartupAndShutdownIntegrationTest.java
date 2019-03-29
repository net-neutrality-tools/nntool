package at.alladin.nntool.qos.testserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import at.alladin.nntool.qos.testserver.mock.ServerSocketMockup;
import mockit.Mock;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
public class TestServerImplStartupAndShutdownIntegrationTest {
	
	/**
	 * 
	 * @author Lukasz Budryk (lb@alladin.at)
	 *
	 */
	public class ServerSocketAcceptCounterMockup extends ServerSocketMockup {
		
		public long acceptCounter = 0;
		
		public ServerSocketAcceptCounterMockup(final TestServerImpl ts) throws IOException {
			super(ts);
		}
		
		@Mock
		public Socket accept() throws IOException {
			acceptCounter += 1;
			return null;
		}		
	}

	@Test
	public void testTestServerStartUpWithShutdownAfterOneIncomingConnection() throws Exception {
		final TestServerImpl ts = new TestServerImpl();
		ts.setShutdownHookEnabled(false);
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		//mock server socket
		final ServerSocketAcceptCounterMockup ssm = new ServerSocketAcceptCounterMockup(ts) {
			
			@Mock
			public Socket accept() throws IOException {
				super.accept();
				testServer.shutdown();
				latch.countDown();
				return null;
			}
		};
		
		ts.run(new ServerPreferences(getClass().getResourceAsStream("config.properties")));
		final boolean reachedZeroCountdown = latch.await(10L, TimeUnit.SECONDS);
		
		assertTrue("CountDownLatch hasn't reached 0 and ran into a timeout", reachedZeroCountdown);
		assertFalse("QoSService is running", ts.getQoSService().isRunning());
		assertEquals("ServerSocket.accept() has been called != 1 time", 1, ssm.acceptCounter);
	}
}
