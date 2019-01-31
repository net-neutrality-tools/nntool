package at.alladin.nntool.qos.testserver.mock;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import mockit.Mock;
import mockit.MockUp;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
public class SocketWithCountDownLatchMockup extends MockUp<Socket> {
		
	final CountDownLatch latch;
	
	public SocketWithCountDownLatchMockup(final CountDownLatch latch) {
		this.latch = latch;
	}
	
	@Mock
	public synchronized void close() throws IOException {
		latch.countDown();
	}

}
