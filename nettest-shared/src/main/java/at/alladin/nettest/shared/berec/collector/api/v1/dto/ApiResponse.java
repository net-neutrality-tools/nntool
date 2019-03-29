package at.alladin.nettest.shared.berec.collector.api.v1.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Object that is used as wrapper for every response.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Object that is used as wrapper for every response.")
@JsonClassDescription("Object that is used as wrapper for every response.")
public class ApiResponse<T> extends ApiBase<T> {

	/**
	 * Optional list of errors that occurred during request processing.
	 */
	@io.swagger.annotations.ApiModelProperty("Optional list of errors that occurred during request processing.")
	@JsonPropertyDescription("Optional list of errors that occurred during request processing.")
	@Expose
	@SerializedName("errors")
	@JsonProperty("errors")
	private final List<ApiError> errors;

	/**
	 *
	 * @param data
	 */
	public ApiResponse(T data) {
		this(data, null);
	}

	/**
	 *
	 * @param data
	 * @param errors
	 */
	@JsonCreator
	public ApiResponse(@JsonProperty("data") T data, @JsonProperty("errors") List<ApiError> errors) {
		super(data);

		this.errors = errors != null ? new ArrayList<>(errors) : null;
	}

	/**
	 *
	 * @return
	 */
	public List<ApiError> getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return "ApiResponse{" +
				"errors=" + errors +
				"} " + super.toString();
	}
}
