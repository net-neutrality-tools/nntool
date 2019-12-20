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

package at.alladin.nettest.shared.server.helper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiError;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;

/**
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface ResponseHelper {

	/**
	 *
	 * @param data
	 * @return
	 */
	public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
		return ResponseEntity.ok(new ApiResponse<>(data));
	}

	/**
	 *
	 * @param data
	 * @return
	 */
	public static <T> ResponseEntity<ApiResponse<ApiPagination<T>>> ok(Page<T> page) {
		return ResponseEntity.ok(new ApiResponse<>(new ApiPagination<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements())));
	}

	/**
	 *
	 * @param errors
	 * @return
	 */
	public static <T> ResponseEntity<ApiResponse<?>> internalServerError(List<ApiError> errors) {
		return internalServerError(null, errors);
	}

	/**
	 *
	 * @param data
	 * @param errors
	 * @return
	 */
	public static <T> ResponseEntity<ApiResponse<?>> internalServerError(T data, List<ApiError> errors) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(data, errors));
	}

	/**
	 *
	 * @param data
	 * @param errors
	 * @param status
	 * @return
	 */
	public static <T> ResponseEntity<ApiResponse<?>> error(T data, List<ApiError> errors, HttpStatus status) {
		return ResponseEntity.status(status).body(new ApiResponse<>(data, errors));
	}
}
