package at.alladin.nettest.shared.server.helper.spring;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CodeInsteadOfExceptionMessageSource implements MessageSource {

	private static final Logger logger = LoggerFactory.getLogger(CodeInsteadOfExceptionMessageSource.class);
	
	final MessageSource messageSource;
	
	public CodeInsteadOfExceptionMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		return messageSource.getMessage(code, args, defaultMessage, locale);
	}

	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		try {
			return messageSource.getMessage(code, args, locale);
		} catch (NoSuchMessageException ex) {
			logger.warn(ex.getMessage());
			
			return code;
		}
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		try {
			return messageSource.getMessage(resolvable, locale);
		} catch (NoSuchMessageException ex) {
			logger.warn(ex.getMessage());
			
			return String.join(",", resolvable.getCodes());
		}
	}
}
