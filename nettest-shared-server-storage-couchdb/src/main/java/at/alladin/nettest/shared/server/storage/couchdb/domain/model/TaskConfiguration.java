package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.MeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementTypeParameters;
import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;
import at.alladin.nettest.spring.data.couchdb.core.mapping.Document;

@Document("TaskConfiguration")
public abstract class TaskConfiguration {
	
	@Expose
	@SerializedName("_id")
	@JsonProperty("_id")
	protected String id;
	
	@Expose
	@SerializedName("_rev")
	@JsonProperty("_rev")
	protected String rev;
	
	@JsonProperty("docType")
	@Expose
	@SerializedName("docType") // TODO: rename to @docType
	protected String docType;
	
	public abstract MeasurementTypeParameters getMeasurementParameters();
	
	@Expose
	private String name;
	
	@Expose
	private String version;
	
	@Expose
	@SerializedName("option_list")
	private List<Option> optionList;
	
	@Expose
	@SerializedName("tag_list")
	private List<String> tagList;
	
	public TaskConfiguration() {
		docType = DocTypeHelper.getDocType(this.getClass());
	}
	
	public static class Option {
		@Expose
		private String name;
		
		@Expose
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<Option> getOptionList() {
		return optionList;
	}

	public void setOptionList(List<Option> optionList) {
		this.optionList = optionList;
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}

}
