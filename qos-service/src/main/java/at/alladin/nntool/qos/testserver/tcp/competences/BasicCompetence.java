package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.BufferedReader;

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
	public byte[] processRequest(String firstLine, BufferedReader br) {
		return ClientHandler.getBytesWithNewline(firstLine);
	}

}
