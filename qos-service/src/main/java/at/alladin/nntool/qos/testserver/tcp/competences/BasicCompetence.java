package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.BufferedReader;
import java.io.IOException;
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
	public boolean appliesTo(final String data) {
		return true;
	}

	@Override
	public List<Action> processRequest(final String data) {
		final List<Action> result = new ArrayList<>();
		result.add(new ResponseAction(ClientHandler.getBytesWithNewline(new String(data))));
		return result;
	}

	@Override
	public String readFullRequest(String firstLine, BufferedReader br) throws IOException {
		return firstLine;
	}
}
