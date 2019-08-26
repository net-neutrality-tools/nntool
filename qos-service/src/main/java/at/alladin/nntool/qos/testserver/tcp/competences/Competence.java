package at.alladin.nntool.qos.testserver.tcp.competences;

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
	boolean appliesTo(final byte[] data);
	
	/**
	 * Process request and return action(s).
	 * @param tcpClientHandler
	 * @return
	 */
	List<Action> processRequest(final byte[] data);
}
