package at.alladin.nntool.qos.testserver.tcp.competences.sip;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nntool.qos.testserver.tcp.competences.Action;
import at.alladin.nntool.qos.testserver.tcp.competences.Competence;
import at.alladin.nntool.qos.testserver.tcp.competences.RepeatAction;
import at.alladin.nntool.qos.testserver.tcp.competences.ResponseAction;
import at.alladin.nntool.qos.testserver.tcp.competences.SleepAction;
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
	public boolean appliesTo(final byte[] data) {
		//try to parse first line
		return (SipUtil.parseRequestData(data) != null || SipUtil.parseResponseData(data) != null);
	}

	@Override
	public List<Action> processRequest(final byte[] data) {
		try {
			final List<Action> resultList = new ArrayList<>();
			
			final String sipData = new String(data);
			SipMessage msg = SipUtil.parseResponseData(sipData);
			
			if (msg == null) {
				msg = SipUtil.parseRequestData(sipData);
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
						resultList.add(new SleepAction(100));
						resultList.add(new ResponseAction(
								new SipResponseMessage(SipResponseType.RINGING, (SipRequestMessage) msg).getData()));
						resultList.add(new RepeatAction());
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
