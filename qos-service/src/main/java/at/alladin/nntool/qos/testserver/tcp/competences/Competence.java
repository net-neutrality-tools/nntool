package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.BufferedReader;
import java.util.List;

/**
 * 
 * @author lb@alladin.at
 *
 */
public interface Competence {

	/**
	 * Check if the incoming data applies to this specific competence.
	 * @param firstLine
	 * @return
	 */
	boolean appliesTo(final String firstLine);
	
	/**
	 * Process request and return action(s).
	 * @param tcpClientHandler
	 * @return
	 */
	List<Action> processRequest(final String firstLine, final BufferedReader br);
}
