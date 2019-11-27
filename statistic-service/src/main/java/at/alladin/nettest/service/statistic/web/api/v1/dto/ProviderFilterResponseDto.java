package at.alladin.nettest.service.statistic.web.api.v1.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.service.statistic.web.api.v1.dto.filter.BasicFilterDto;

/**
 * 
 * @author lb
 *
 */
@io.swagger.annotations.ApiModel(description = "Holds provider statistics filter.")
@JsonClassDescription("Holds provider statistics filter.")
public class ProviderFilterResponseDto {

	@JsonPropertyDescription("Contains a list with pre-configured filter elements including default values.")
	@Expose
	@SerializedName("filters")
	@JsonProperty("filters")
	List<BasicFilterDto<?>> filters = new ArrayList<>();

	public List<BasicFilterDto<?>> getFilters() {
		return filters;
	}

	public void setFilters(List<BasicFilterDto<?>> filters) {
		this.filters = filters;
	}
	
}
