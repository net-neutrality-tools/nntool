package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.OutputStream;

import at.alladin.nntool.qos.testserver.ServerPreferences.TestServerServiceEnum;
import at.alladin.nntool.qos.testserver.tcp.TcpMultiClientServer;
import at.alladin.nntool.qos.testserver.util.TestServerConsole;

/**
 * 
 * @author lb
 *
 */
public class WaitAction implements Action {

	private final long sleepMillis;
	
	public WaitAction(final long sleepMillis) {
		this.sleepMillis = sleepMillis;
	}
	
	@Override
	public boolean execute(OutputStream os) {
		try {
			Thread.sleep(sleepMillis);
			return true;
		}
		catch (final Exception e) {
			TestServerConsole.error("WaitAction error (sleepMillis: " + sleepMillis + ")", e, 
					TcpMultiClientServer.VERBOSE_LEVEL_REQUEST_RESPONSE, TestServerServiceEnum.TCP_SERVICE);
		}
		
		return false;
	}

}
