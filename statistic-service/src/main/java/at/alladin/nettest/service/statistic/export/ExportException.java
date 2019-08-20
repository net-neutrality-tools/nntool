package at.alladin.nettest.service.statistic.export;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ExportException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExportException() {
		super();
	}

	public ExportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExportException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExportException(String message) {
		super(message);
	}

	public ExportException(Throwable cause) {
		super(cause);
	}
}
