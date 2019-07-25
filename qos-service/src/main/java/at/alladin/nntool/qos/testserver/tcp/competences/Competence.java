package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.BufferedReader;

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
	 * Process request and return response.
	 * @param tcpClientHandler
	 * @return
	 */
	byte[] processRequest(final String firstLine, final BufferedReader br);
}
