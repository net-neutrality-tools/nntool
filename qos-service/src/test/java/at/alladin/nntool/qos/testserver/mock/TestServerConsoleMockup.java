package at.alladin.nntool.qos.testserver.mock;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nntool.qos.testserver.ServerPreferences.TestServerServiceEnum;
import at.alladin.nntool.qos.testserver.util.TestServerConsole;
import mockit.Mock;
import mockit.MockUp;

/**
 * 
 * @author Lukasz Budryk (lb@alladin.at)
 *
 */
public class TestServerConsoleMockup extends MockUp<TestServerConsole> {

	public static class ConsoleLog {
		final String msg;
		final int verboseLevel;
		final TestServerServiceEnum service;
		final long timeMs = System.currentTimeMillis();
		final Throwable t;
		
		public ConsoleLog(final String msg, final int verboseLevel, final TestServerServiceEnum service) {
			this(msg, null, verboseLevel, service);
		}
		
		public ConsoleLog(final String msg, final Throwable t, final int verboseLevel, final TestServerServiceEnum service) {
			this.msg = msg;
			this.t = t;
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

		public Throwable getT() {
			return t;
		}

		@Override
		public String toString() {
			return "LogClass [msg=" + msg + ", verboseLevel=" + verboseLevel + ", service=" + service + ", timeMs="
					+ timeMs + (t != null ? ", t=" + t.getCause() : "") + "]";
		}
	}
	
	public final List<ConsoleLog> logList = new ArrayList<>();
	public final List<ConsoleLog> errorList = new ArrayList<>();
	
	@Mock
	public void log(String msg, int verboseLevelNeeded, TestServerServiceEnum service) {
		logList.add(new ConsoleLog(msg, verboseLevelNeeded, service));
	}
	
	@Mock
	public void error(String info, Throwable t, int verboseLevelNeeded, TestServerServiceEnum service) {
		errorList.add(new ConsoleLog(info, t, verboseLevelNeeded, service));
	}
	
	public List<ConsoleLog> getLogList() {
		return logList;
	}
	
	public List<ConsoleLog> getErrorList() {
		return errorList;
	}
}
