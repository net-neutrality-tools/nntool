package at.alladin.nntool.qos.testserver.mock;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nntool.qos.testserver.ServerPreferences.TestServerServiceEnum;
import at.alladin.nntool.qos.testserver.util.TestServerConsole;
import mockit.Mock;
import mockit.MockUp;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class TestServerConsoleMockup extends MockUp<TestServerConsole> {

	public static class ConsoleLog {
		final String msg;
		final int verboseLevel;
		final TestServerServiceEnum service;
		final long timeMs = System.currentTimeMillis();
		
		public ConsoleLog(final String msg, final int verboseLevel, final TestServerServiceEnum service) {
			this.msg = msg;
			this.verboseLevel = verboseLevel;
			this.service = service;
		}

		public String getMsg() {
			return msg;
		}

		public int getVerboseLevel() {
			return verboseLevel;
		}

		public TestServerServiceEnum getService() {
			return service;
		}

		public long getTimeMs() {
			return timeMs;
		}

		@Override
		public String toString() {
			return "LogClass [msg=" + msg + ", verboseLevel=" + verboseLevel + ", service=" + service + ", timeMs="
					+ timeMs + "]";
		}
	}
	
	public final List<ConsoleLog> logList = new ArrayList<>();
	
	@Mock
	public void log(String msg, int verboseLevelNeeded, TestServerServiceEnum service) {
		logList.add(new ConsoleLog(msg, verboseLevelNeeded, service));
	}
	
	public List<ConsoleLog> getLogList() {
		return logList;
	}
}
