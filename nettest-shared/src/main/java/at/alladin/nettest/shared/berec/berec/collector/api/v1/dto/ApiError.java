package at.alladin.nettest.shared.berec.collector.api.v1.dto;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * DTO that wraps server errors and/or exceptions.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "DTO that wraps server errors and/or exceptions.")
@JsonClassDescription("DTO that wraps server errors and/or exceptions.")
public class ApiError {

	/**
	 * Date and time at which the error occurred.
	 */
	@io.swagger.annotations.ApiModelProperty("Date and time at which the error occurred.")
	@JsonPropertyDescription("Date and time at which the error occurred.")
	@Expose
	@SerializedName("time")
	@JsonProperty("time")
	private final LocalDateTime time;

	/**
	 * URI path/resource that caused the error.
	 */
	@io.swagger.annotations.ApiModelProperty("URI path/resource that caused the error.")
	@JsonPropertyDescription("URI path/resource that caused the error.")
	@Expose
	@SerializedName("path")
	@JsonProperty("path")
	private final String path;

	/**
	 * Status code for the error. Example: 400, 404, 500, ...
	 */
	@io.swagger.annotations.ApiModelProperty(value = "Status code for the error.", example = "400, 404, 500, ...")
	@JsonPropertyDescription("Status code for the error. Example: 400, 404, 500, ...")
	@Expose
	@SerializedName("status")
	@JsonProperty("status")
	private final Integer status;

	/**
	 * String representation of the status. Example: "Internal Server Error, "Not Found", ...
	 */
	@io.swagger.annotations.ApiModelProperty(value = "String representation of the status.", example = "'Internal Server Error', 'Not Found', ...")
	@JsonPropertyDescription("String representation of the status. Example: \"Internal Server Error, \"Not Found\", ...")
	@Expose
	@SerializedName("error")
	@JsonProperty("error")
	private final String error;

	/**
	 * The error or exception message. Example: "java.lang.RuntimeException".
	 */
	@io.swagger.annotations.ApiModelProperty(value = "The error or exception message.", example = "java.lang.RuntimeException")
	@JsonPropertyDescription("The error or exception message. Example: \"java.lang.RuntimeException\".")
	@Expose
	@SerializedName("message")
	@JsonProperty("message")
	private final String message;

	/**
	 * Exception class name.
	 */
	@io.swagger.annotations.ApiModelProperty("Exception class name.")
	@JsonPropertyDescription("Exception class name.")
	@Expose
	@SerializedName("exception")
	@JsonProperty("exception")
	private final String exception;

	/**
	 * Exception stack trace.
	 */
	@io.swagger.annotations.ApiModelProperty("Exception stack trace.")
	@JsonPropertyDescription("Exception stack trace.")
	@Expose
	@SerializedName("trace")
	@JsonProperty("trace")
	private final String trace;

	/**
	 *
	 * @param title
	 * @param message
	 */
	public ApiError(LocalDateTime times, String path, Integer status, String error, String message, String exception, String trace) {
		this.time = times;
		this.path = path;
		this.status = status;
		this.error = error;
		this.message = message;
		this.exception = exception;
		this.trace = trace;
	}

	/**
	 *
	 * @return
	 */
	public LocalDateTime getTime() {
		return time;
	}

	/**
	 *
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 *
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 *
	 * @return
	 */
	public String getError() {
		return error;
	}

	/**
	 *
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 *
	 * @return
	 */
	public String getException() {
		return exception;
	}

	/**
	 *
	 * @return
	 */
	public String getTrace() {
		return trace;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServerError [time=" + time + ", path=" + path + ", status=" + status + ", error=" + error
				+ ", message=" + message + ", exception=" + exception + "]";
	}
}
