package at.alladin.nntool.qos.testserver.tcp.competences.sip;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import at.alladin.nntool.qos.testserver.tcp.competences.Action;
import at.alladin.nntool.qos.testserver.tcp.competences.Competence;
import at.alladin.nntool.qos.testserver.tcp.competences.ResponseAction;
import at.alladin.nntool.qos.testserver.tcp.competences.WaitAction;
import at.alladin.nntool.util.net.sip.SipMessage;
import at.alladin.nntool.util.net.sip.SipRequestMessage;
import at.alladin.nntool.util.net.sip.SipResponseMessage;
import at.alladin.nntool.util.net.sip.SipResponseMessage.SipResponseType;
import at.alladin.nntool.util.net.sip.SipUtil;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class SipCompetence implements Competence {

	@Override
	public boolean appliesTo(String firstLine) {
		//try to parse first line
		return (SipUtil.parseRequestData(firstLine) != null || SipUtil.parseResponseData(firstLine) != null);
	}

	@Override
	public List<Action> processRequest(String firstLine, BufferedReader br) {
		try {
			final List<Action> resultList = new ArrayList<>();
			
			final StringBuilder sb = new StringBuilder();
			sb.append(firstLine).append("\n");
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			
			final byte[] data = sb.toString().getBytes();
			SipMessage msg = SipUtil.parseResponseData(data);
			
			if (msg == null) {
				msg = SipUtil.parseRequestData(data);
			}
			
			if (msg != null) {
				if (msg instanceof SipResponseMessage) {
					//we have a SIP response message
					switch (((SipResponseMessage) msg).getType()) {
					case OK:
						break;
					case RINGING:
						break;
					case TRYING:
						break;
					}
				}
				else if (msg instanceof SipRequestMessage) {
					//we have a SIP request message
					switch (((SipRequestMessage) msg).getType()) {
					case ACK:
						//ACK does not demand a response
						break;
					case BYE:
						resultList.add(new ResponseAction(
								new SipResponseMessage(SipResponseType.OK, (SipRequestMessage) msg).getData()));
						break;
					case INVITE:
						resultList.add(new ResponseAction(
								new SipResponseMessage(SipResponseType.TRYING, (SipRequestMessage) msg).getData()));
						resultList.add(new WaitAction(100));
						resultList.add(new ResponseAction(
								new SipResponseMessage(SipResponseType.RINGING, (SipRequestMessage) msg).getData()));
						break;
					}
				}
			}

			return resultList;
		}
		catch (final Exception e) {
			e.printStackTrace();
			return null;
		}		
	}	
}
