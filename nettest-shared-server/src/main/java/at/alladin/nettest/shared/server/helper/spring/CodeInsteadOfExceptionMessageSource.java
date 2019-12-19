/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
