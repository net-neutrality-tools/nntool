package at.alladin.nettest.service.search.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class that is used if the measurement was not found.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Is information hiding for open-data even necessary? (HttpStatus.FORBIDDEN)
public class MeasurementNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MeasurementNotFoundException() {
		super();
	}

	public MeasurementNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MeasurementNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public MeasurementNotFoundException(String message) {
		super(message);
	}

	public MeasurementNotFoundException(Throwable cause) {
		super(cause);
	}
}
