package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;

/**
 * Contains information about the network type.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the network type.")
public class EmbeddedNetworkType {
	
	@Expose
	@SerializedName("_id")
	@JsonProperty("_id")
	private String id;
	
	@Expose
	@SerializedName("_rev")
	@JsonProperty("_rev")
	private String rev;
	
	@JsonProperty("docType")
	@Expose
	@SerializedName("docType") // TODO: rename to @docType
	private String docType;
	
	public EmbeddedNetworkType() {
		docType = DocTypeHelper.getDocType(getClass());
	}
	
	/**
	 * Network type id as it gets returned by the Android API.
	 */
	@JsonPropertyDescription("Network type id as it gets returned by the Android API.")
	@Expose
	@SerializedName("network_type_id")
	@JsonProperty(required = true, value = "network_type_id")
	private Integer networkTypeId;
	
	/**
	 * Network type name.
	 */
	@JsonPropertyDescription("Network type name.")
	@Expose
	@SerializedName("name")
	@JsonProperty("name")
	private String name;
	
	/**
	 * Network group name (e.g. 2G, 3G, LAN).
	 */
	@JsonPropertyDescription("Network group name (e.g. 2G, 3G, LAN).")
	@Expose
	@SerializedName("group_name")
	@JsonProperty("group_name")
	private String groupName;

	/**
	 * @see NetworkTypeCategory
	 */
	@JsonPropertyDescription("Contains the different network categories.")
	@Expose
	@SerializedName("category")
	@JsonProperty("category")
	private NetworkTypeCategory category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public NetworkTypeCategory getCategory() {
		return category;
	}

	public void setCategory(NetworkTypeCategory category) {
		this.category = category;
	}

	public Integer getNetworkTypeId() {
		return networkTypeId;
	}

	public void setNetworkTypeId(Integer networkTypeId) {
		this.networkTypeId = networkTypeId;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}
	
}
