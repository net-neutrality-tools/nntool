package at.alladin.nettest.service.search.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class that is used if there was a bad export request.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadExportRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BadExportRequestException() {
		super();
	}

	public BadExportRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BadExportRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadExportRequestException(String message) {
		super(message);
	}

	public BadExportRequestException(Throwable cause) {
		super(cause);
	}
}
