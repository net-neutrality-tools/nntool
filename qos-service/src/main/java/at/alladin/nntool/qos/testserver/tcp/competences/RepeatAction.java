package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.FilterOutputStream;

import at.alladin.nntool.qos.testserver.tcp.TcpClientHandler;

public class RepeatAction implements Action {

	@Override
	public boolean execute(final TcpClientHandler tcpClientHandler, final byte[] requestData, FilterOutputStream os) {
		tcpClientHandler.getRepeat().set(true);
		return true;
	}

}
