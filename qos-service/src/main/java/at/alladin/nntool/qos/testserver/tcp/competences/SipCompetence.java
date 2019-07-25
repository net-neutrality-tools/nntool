package at.alladin.nntool.qos.testserver.tcp.competences;

import java.io.BufferedReader;

import at.alladin.nntool.qos.testserver.ClientHandler;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class SipCompetence implements Competence {

	@Override
	public boolean appliesTo(String firstLine) {
		return false;
	}

	@Override
	public byte[] processRequest(String firstLine, BufferedReader br) {
		return ClientHandler.getBytesWithNewline(firstLine);
	}
}
