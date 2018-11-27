package at.alladin.nettest.shared.berec.collector.api.v1.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Wrapper for paginated responses (e.g. list of measurements).
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Wrapper for paginated responses (e.g. list of measurements).")
@JsonClassDescription("Wrapper for paginated responses (e.g. list of measurements).")
public class ApiPagination<T> {

	/**
	 * Paginated list of objects.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Paginated list of objects.")
	@JsonPropertyDescription("Paginated list of objects.")
	@Expose
	@SerializedName("content")
	@JsonProperty(required = true, value = "content")
	private final List<T> content;
	
	/**
	 * Current page number (>= 0).
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Current page number (>= 0).")
	@JsonPropertyDescription("Current page number (>= 0).")
	@Expose
	@SerializedName("page_number")
	@JsonProperty(required = true, value = "page_number")
	private final int pageNumber;

	/**
	 * Current page size (> 0).
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Current page size (> 0).")
	@JsonPropertyDescription("Current page size (> 0).")
	@Expose
	@SerializedName("page_size")
	@JsonProperty(required = true, value = "page_size")
	private final int pageSize;

	/**
	 * Total amount of pages (>= 0).
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Total amount of pages (>= 0).")
	@JsonPropertyDescription("Total amount of pages (>= 0).")
	@Expose
	@SerializedName("total_pages")
	@JsonProperty(required = true, value = "total_pages")
	private final int totalPages;
	
	/**
	 * Total amount of objects (>= 0).
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Total amount of objects (>= 0).")
	@JsonPropertyDescription("Total amount of objects (>= 0).")
	@Expose
	@SerializedName("total_elements")
	@JsonProperty(required = true, value = "total_elements")
	private final long totalElements;

	/**
	 * 
	 * @param page
	 */
	public ApiPagination(List<T> content, int pageNumber, int pageSize, int totalPages, long totalElements) {
		this.content = content;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.totalPages = totalPages;
		this.totalElements = totalElements;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<T> getContent() {
		return content;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * 
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 
	 * @return
	 */
	public int getTotalPages() {
		return totalPages;
	}

	/**
	 * 
	 * @return
	 */
	public long getTotalElements() {
		return totalElements;
	}
}
