package at.alladin.nettest.service.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GeneralBadRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GeneralBadRequestException() {
		super();
	}

	public GeneralBadRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GeneralBadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public GeneralBadRequestException(String message) {
		super(message);
	}

	public GeneralBadRequestException(Throwable cause) {
		super(cause);
	}
}
