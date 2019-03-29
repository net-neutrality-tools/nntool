package at.alladin.nntool.qos.testserver.mock;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import at.alladin.nntool.qos.testserver.TestServerImpl;
import mockit.Mock;
import mockit.MockUp;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
public class ServerSocketMockup extends MockUp<ServerSocket> {
	
	public final TestServerImpl testServer;
	
	public SocketAddress endpoint;
	
	public ServerSocketMockup(final TestServerImpl ts) throws IOException {
		this.testServer = ts;
	}
	
	@Mock
	public boolean isBound() {
		return true;
	}
	
	@Mock
	public void bind(SocketAddress endpoint) throws IOException {
		this.endpoint = endpoint;
	}
	
	@Mock
    public boolean isClosed() {
		return false;
	}
	
	@Mock
	public Socket accept() throws IOException {
		return new Socket();
	}
}