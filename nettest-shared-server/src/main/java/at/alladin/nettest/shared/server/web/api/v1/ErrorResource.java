package at.alladin.nettest.shared.server.web.api.v1;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiError;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;

/**
 * This controller handles all uncaught exceptions or errors and provides a JSON representation of the error.
 * The Java stack-trace is only added if the service runs in development mode.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
public class ErrorResource implements ErrorController {

	private final Logger logger = LoggerFactory.getLogger(ErrorResource.class);

	/**
	 *
	 */
	@Autowired
	private ErrorAttributes errorAttributes;

	/**
	 *
	 */
	@Autowired
	private Environment env;

	/**
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/error")
	public ResponseEntity<ApiResponse<?>> error(WebRequest webRequest, HttpServletResponse response) {
		final boolean includeStackTrace = env.acceptsProfiles(Profiles.of("dev"));

		final HttpStatus status = HttpStatus.valueOf(response.getStatus());

		final List<ApiError> errors = Arrays.asList(mapError(webRequest, includeStackTrace));
		logger.debug("Rendering error response: {}", errors);

		return ResponseHelper.error(null, errors, status);
	}

	/**
	 *
	 * @param errorMap
	 * @param includeStackTrace
	 * @return
	 */
	private ApiError mapError(WebRequest webRequest, boolean includeStackTrace) {
		final Map<String, Object> errorMap = errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
		final Throwable cause = errorAttributes.getError(webRequest);

		logger.info("{}", errorMap);

		//logger.info("{}", errorMap.get("timestamp"));
		//LocalDateTime.parse("", new DateTimeFormatterBuilder()..toFormatter());
		//Tue Sep 18 10:33:31 CEST 2018

		Object path 		= errorMap.get("path");
		Object status 		= errorMap.get("status");
		Object error		= errorMap.get("error");

		Object message 		= errorMap.get("message");
		Object exception 	= errorMap.get("exception");
		Object trace 		= includeStackTrace ? errorMap.get("trace") : null;

		if (cause != null) {
			message = cause.getMessage(); //cause.getLocalizedMessage()
			exception = cause.getClass().getCanonicalName();

			if (includeStackTrace) {
				try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
					cause.printStackTrace(pw);

					trace = sw.toString();
				} catch (Exception ex) { }
			}
		}

		return new ApiError(
			LocalDateTime.now(), // errorMap.get("timestamp"),
			path != null ? path.toString() : null,
			status != null ? Integer.parseInt(status.toString()) : null,
			error != null ? error.toString() : null,
			message != null ? message.toString() : null,
			exception != null ? exception.toString() : null,
			trace != null ? trace.toString() : null
		);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.boot.autoconfigure.web.ErrorController#getErrorPath()
	 */
	@Override
	public String getErrorPath() {
		return "/error";
	}
}
