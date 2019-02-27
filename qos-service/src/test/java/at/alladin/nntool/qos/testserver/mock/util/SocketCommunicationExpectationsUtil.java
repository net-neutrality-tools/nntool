package at.alladin.nntool.qos.testserver.mock.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.google.common.net.InetAddresses;

import mockit.Expectations;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
public class SocketCommunicationExpectationsUtil {
	
	public static Expectations createExpectationWithOutputStream(final Socket socket, 
			final OutputStream serverResponseOutputStream,
			final String message) throws IOException {
		
		return new Expectations() {
			{
				socket.getInetAddress(); 
				result = InetAddresses.forString("1.1.1.1");
				
				socket.getInputStream(); 
				result = new ByteArrayInputStream(message.getBytes());

				socket.getOutputStream();
				result = serverResponseOutputStream;
			}
		};
	}

	
	public static Expectations createExpectationWithThrownException(final Socket socket, 
			final Exception exceptionToThrow,
			final String message) throws IOException {
		
		return new Expectations() {
			{
				socket.getInetAddress(); 
				result = InetAddresses.forString("1.1.1.1");
				
				socket.getInputStream(); 
				result = new ByteArrayInputStream(message.getBytes());

				socket.getOutputStream();
				result = exceptionToThrow;
			}
		};
	}

	public static Expectations createExpectationWithThrownExceptionOnClose(final Socket socket, 
			final Exception exceptionToThrow) throws IOException {
		
		return new Expectations() {
			{
				socket.close();
				result = new IOException();
				result = null;
			}
		};
	}

}
