package at.alladin.nettest.service.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MeasurementAgentRegistrationTermsAndConditionsNotAcceptedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MeasurementAgentRegistrationTermsAndConditionsNotAcceptedException() {
		super();
	}

	public MeasurementAgentRegistrationTermsAndConditionsNotAcceptedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MeasurementAgentRegistrationTermsAndConditionsNotAcceptedException(String message, Throwable cause) {
		super(message, cause);
	}

	public MeasurementAgentRegistrationTermsAndConditionsNotAcceptedException(String message) {
		super(message);
	}

	public MeasurementAgentRegistrationTermsAndConditionsNotAcceptedException(Throwable cause) {
		super(cause);
	}
}
