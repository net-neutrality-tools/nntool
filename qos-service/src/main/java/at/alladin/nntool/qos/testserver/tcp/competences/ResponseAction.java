package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.FilterOutputStream;

import at.alladin.nntool.qos.testserver.ServerPreferences.TestServerServiceEnum;
import at.alladin.nntool.qos.testserver.tcp.TcpClientHandler;
import at.alladin.nntool.qos.testserver.tcp.TcpMultiClientServer;
import at.alladin.nntool.qos.testserver.util.TestServerConsole;

public class ResponseAction implements Action {

	private final byte[] data;
	
	public ResponseAction(final byte[] data) {
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}
	
	@Override
	public boolean execute(final TcpClientHandler tcpClientHandler, final byte[] requestData, FilterOutputStream os) {
		if (data != null) {
			try {
				os.write(data);
				os.flush();
				return true;
			}
			catch (final Exception e) {
				TestServerConsole.error("ResponseAction error!", e, 
						TcpMultiClientServer.VERBOSE_LEVEL_REQUEST_RESPONSE, TestServerServiceEnum.TCP_SERVICE);
			}
		}
		return false;
	}
}
