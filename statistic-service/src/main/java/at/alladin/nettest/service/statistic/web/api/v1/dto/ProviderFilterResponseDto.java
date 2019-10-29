package at.alladin.nettest.service.statistic.web.api.v1.dto;

import java.util.ArrayList;
import java.util.List;

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
public class ProviderFilterResponseDto {

	@JsonPropertyDescription("Contains the a list with pre-configured filter elements including default values.")
	@Expose
	@SerializedName("filter_list")
	@JsonProperty("filter_list")
	List<BasicFilterDto<?>> filterList = new ArrayList<>();

	public List<BasicFilterDto<?>> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<BasicFilterDto<?>> filterList) {
		this.filterList = filterList;
	}
	
}
