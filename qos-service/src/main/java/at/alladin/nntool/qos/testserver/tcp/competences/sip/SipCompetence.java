package at.alladin.nntool.qos.testserver.tcp.competences.sip;

import java.io.BufferedReader;

import at.alladin.nntool.qos.testserver.ClientHandler;
import at.alladin.nntool.qos.testserver.tcp.competences.Competence;

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
