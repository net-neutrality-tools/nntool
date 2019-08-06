package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.BufferedReader;
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
	public boolean appliesTo(String incomingDataLine) {
		return true;
	}

	@Override
	public List<Action> processRequest(String firstLine, BufferedReader br) {
		final List<Action> result = new ArrayList<>();
		result.add(new ResponseAction(ClientHandler.getBytesWithNewline(firstLine)));
		return result;
	}
}
