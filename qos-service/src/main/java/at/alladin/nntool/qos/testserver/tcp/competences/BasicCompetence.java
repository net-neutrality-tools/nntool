package at.alladin.nntool.qos.testserver.tcp.competences;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nntool.qos.testserver.ClientHandler;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class BasicCompetence implements Competence {

	@Override
	public boolean appliesTo(final byte[] data) {
		return true;
	}

	@Override
	public List<Action> processRequest(final byte[] data) {
		final List<Action> result = new ArrayList<>();
		result.add(new ResponseAction(ClientHandler.getBytesWithNewline(new String(data))));
		return result;
	}
}
