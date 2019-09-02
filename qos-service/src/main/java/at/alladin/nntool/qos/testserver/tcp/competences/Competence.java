package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @author lb@alladin.at
 *
 */
public interface Competence {

	/**
	 * Check if the incoming data (first line) applies to this specific competence.
	 * @param firstLine
	 * @return
	 */
	boolean appliesTo(final String firstLine);
	
	/**
	 * Read full request   
	 * @param firstLine
	 * @return
	 */
	String readFullRequest(final String firstLine, final BufferedReader br) throws IOException;
	
	/**
	 * Process request and return action(s).
	 * @param tcpClientHandler
	 * @return
	 */
	List<Action> processRequest(final String data);
}
