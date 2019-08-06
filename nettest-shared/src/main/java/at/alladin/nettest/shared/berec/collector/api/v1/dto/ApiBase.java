package at.alladin.nettest.shared.berec.collector.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Abstract wrapper for every request and response.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Abstract wrapper for every request and response.")
@JsonClassDescription("Abstract wrapper for every request and response.")
public abstract class ApiBase<T> {

	/**
	 * Actual data that is returned for the request/response.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Actual data that is returned for the request/response.")
	@JsonPropertyDescription("Actual data that is returned for the request/response.")
	@Expose
	@SerializedName("data")
	@JsonProperty(required = true, value = "data")
	@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="deserialize_type")
	private T data;
	
	/**
	 * 
	 */
	public ApiBase() {
		
	}
	
	/**
	 * 
	 * @param data
	 */
	public ApiBase(T data) {
		this.data = data;
	}
	
	/**
	 * 
	 * @return
	 */
	public T getData() {
		return data;
	}
	
	/**
	 * 
	 * @param data
	 */
	public void setData(T data) {
		this.data = data;
	}
}
