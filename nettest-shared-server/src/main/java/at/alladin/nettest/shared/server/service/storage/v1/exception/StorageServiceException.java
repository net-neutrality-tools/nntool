package at.alladin.nettest.shared.server.service.storage.v1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class StorageServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public StorageServiceException() {
		super();
	}

	public StorageServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public StorageServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public StorageServiceException(String message) {
		super(message);
	}

	public StorageServiceException(Throwable cause) {
		super(cause);
	}
}
