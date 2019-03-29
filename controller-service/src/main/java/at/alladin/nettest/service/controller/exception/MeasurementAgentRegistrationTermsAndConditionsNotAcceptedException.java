package at.alladin.nettest.service.controller.exception;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
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
