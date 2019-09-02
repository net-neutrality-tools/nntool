package at.alladin.nntool.qos.testserver.mock;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import mockit.Mock;
import mockit.MockUp;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
public class DataInputStreamMockup extends MockUp<DataInputStream> {
	
	private final String[] messages;
	
	private final AtomicInteger indexAvailable = new AtomicInteger(0);
	
	private final AtomicInteger index = new AtomicInteger(0);
	
	public DataInputStreamMockup(final String[] messages) throws IOException {
		this.messages = messages;
	}
	
	@Mock
    public int available() throws IOException {
		if (indexAvailable.get() >= messages.length) {
			return -1;
		}
		final String msg = messages[indexAvailable.getAndAdd(1)];
		return msg.length();
    }

    @Mock
    public int read(byte b[]) throws IOException {
		if (index.get() >= messages.length) {
			return -1;
		}						
		final byte[] msg = messages[index.getAndAdd(1)].getBytes();
		System.arraycopy(msg, 0, b, 0, msg.length);
		return msg.length;
    }

}