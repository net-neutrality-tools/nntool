package at.alladin.nntool.qos.testserver.mock;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;

import mockit.Mock;
import mockit.MockUp;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
public class RecordingFilterOutputStreamMock extends MockUp<FilterOutputStream> {
	
	final ByteArrayOutputStream baos;
	
	public RecordingFilterOutputStreamMock() {
		 this.baos = new ByteArrayOutputStream();
	}
	
	@Mock
    public void write(byte b[]) throws IOException {
    	baos.write(b);
    }

	public String getContentWithoutReset() {
		return baos.toString();
	}

	public String getContent() {
		final String content = baos.toString();
		baos.reset();
		return content;
	}

	public ByteArrayOutputStream getStream() {
		return baos;
	}
}
