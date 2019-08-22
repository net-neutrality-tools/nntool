package at.alladin.nettest.shared.server.storage.couchdb.util;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class ReturnCodeMessageSource implements MessageSource {

	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		return code;
	}
	
	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		return code;
	}
	
	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return resolvable.getCodes()[0];
	}
}
