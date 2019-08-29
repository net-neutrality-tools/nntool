package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.FilterOutputStream;

import at.alladin.nntool.qos.testserver.tcp.TcpClientHandler;

public interface Action {

	public boolean execute(final TcpClientHandler tcpClientHandler, final byte[] requestData, final FilterOutputStream fos);
}
