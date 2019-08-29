package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.FilterOutputStream;

import at.alladin.nntool.qos.testserver.ServerPreferences.TestServerServiceEnum;
import at.alladin.nntool.qos.testserver.tcp.TcpClientHandler;
import at.alladin.nntool.qos.testserver.tcp.TcpMultiClientServer;
import at.alladin.nntool.qos.testserver.util.TestServerConsole;

/**
 * 
 * @author lb
 *
 */
public class SleepAction implements Action {

	private final long sleepMillis;
	
	public SleepAction(final long sleepMillis) {
		this.sleepMillis = sleepMillis;
	}
	
	public long getSleepMillis() {
		return sleepMillis;
	}

	@Override
	public boolean execute(final TcpClientHandler tcpClientHandler, final byte[] requestData, FilterOutputStream os) {
		try {
			Thread.sleep(sleepMillis);
			return true;
		}
		catch (final Exception e) {
			TestServerConsole.error("SleepAction error (sleepMillis: " + sleepMillis + ")", e, 
					TcpMultiClientServer.VERBOSE_LEVEL_REQUEST_RESPONSE, TestServerServiceEnum.TCP_SERVICE);
		}
		
		return false;
	}

}
