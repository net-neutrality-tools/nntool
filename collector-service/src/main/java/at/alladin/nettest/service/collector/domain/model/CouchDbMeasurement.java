package at.alladin.nettest.service.collector.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.server.domain.model.Measurement;
import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;
import at.alladin.nettest.spring.data.couchdb.core.mapping.Document;

@Document("Measurement")
public class CouchDbMeasurement extends Measurement {	
	
	@JsonProperty("_rev")
	@Expose
	@SerializedName("_rev")
	private String rev;
	
	@JsonProperty("docType")
	@Expose
	@SerializedName("docType") // TODO: rename to @docType
	private String docType;
	
	public CouchDbMeasurement() {
		docType = DocTypeHelper.getDocType(getClass());
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
}
