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
