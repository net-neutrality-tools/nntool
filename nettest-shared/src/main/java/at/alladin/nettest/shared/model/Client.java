/*******************************************************************************
 * Copyright 2017-2019 alladin-IT GmbH
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

package at.alladin.nettest.shared.model;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.joda.time.DateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.UuidAwareEntity;

/**
 * 
 * @author lb
 *
 */
@Generated("org.jsonschema2pojo")
public class Client extends UuidAwareEntity {

	private static final long serialVersionUID = -7749051308376567533L;

	/**
     * Type of client used
     * 
     * (Required)
     * 
     */
    @Expose
    private Client.ClientType clientType;
    
    /**
     * 
     * (Required)
     * 
     */
    @SerializedName("time")
    @Expose
    private DateTime time;
    
    /**
     * no sync_group table anymore. sync_group_id renamed to sync_group_uuid. if a client wants to sync either a new sync_group_uuid and sync_code is generated or the current sync_code is being transmitted.
     * 
     */
    @Expose
    private String syncGroupUuid;
    
    /**
     * Sync code
     */
    @Expose
    private String syncCode;
    
    /**
     * Has the client accept the term and conditions
     * 
     * (Required)
     * 
     */
    @Expose
    private boolean termsAndConditionsAccepted;

    
    /**
     * Version of t&c the client accepted
     * 
     * (Required)
     * 
     */
    @Expose
    private int termsAndConditionsAcceptedVersion;

    /**
     * 
     * (Required)
     * 
     * @return
     *     The clientType
     */
    public Client.ClientType getClientType() {
        return clientType;
    }

    /**
     * 
     * (Required)
     * 
     * @param clientType
     *     The client_type
     */
    public void setClientType(Client.ClientType clientType) {
        this.clientType = clientType;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The time
     */
    public DateTime getTime() {
        return time;
    }

    /**
     * 
     * (Required)
     * 
     * @param time
     *     The time
     */
    public void setTime(DateTime time) {
        this.time = time;
    }

    /**
     * no sync_group table anymore. sync_group_id renamed to sync_group_uuid. if a client wants to sync either a new sync_group_uuid and sync_code is generated or the current sync_code is being transmitted.
     * 
     * @return
     *     The syncGroupUuid
     */
    public String getSyncGroupUuid() {
        return syncGroupUuid;
    }

    /**
     * no sync_group table anymore. sync_group_id renamed to sync_group_uuid. if a client wants to sync either a new sync_group_uuid and sync_code is generated or the current sync_code is being transmitted.
     * 
     * @param syncGroupUuid
     *     The sync_group_uuid
     */
    public void setSyncGroupUuid(String syncGroupUuid) {
        this.syncGroupUuid = syncGroupUuid;
    }

    /**
     * 
     * @return
     *     The syncCode
     */
    public String getSyncCode() {
        return syncCode;
    }

    /**
     * 
     * @param syncCode
     *     The sync_code
     */
    public void setSyncCode(String syncCode) {
        this.syncCode = syncCode;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The termsAndConditionsAccepted
     */
    public boolean getTermsAndConditionsAccepted() {
        return termsAndConditionsAccepted;
    }

    /**
     * 
     * (Required)
     * 
     * @param termsAndConditionsAccepted
     *     The terms_and_conditions_accepted
     */
    public void setTermsAndConditionsAccepted(boolean termsAndConditionsAccepted) {
        this.termsAndConditionsAccepted = termsAndConditionsAccepted;
    }

    public int getTermsAndConditionsAcceptedVersion() {
		return termsAndConditionsAcceptedVersion;
	}

	public void setTermsAndConditionsAcceptedVersion(int termsAndConditionsAcceptedVersion) {
		this.termsAndConditionsAcceptedVersion = termsAndConditionsAcceptedVersion;
	}

	@Override
	public String toString() {
		return "Client [clientType=" + clientType + ", time=" + time + ", syncGroupUuid=" + syncGroupUuid
				+ ", syncCode=" + syncCode + ", termsAndConditionsAccepted=" + termsAndConditionsAccepted
				+ ", termsAndConditionsAcceptedVersion=" + termsAndConditionsAcceptedVersion + "]";
	}

	/**
     * 
     * @author lb
     *
     */
    @Generated("org.jsonschema2pojo")
    public static enum ClientType {

    	/**
    	 * 
    	 */
        @SerializedName("MOBILE")
        MOBILE("MOBILE"),
        
        /**
         * 
         */
        @SerializedName("DESKTOP")
        DESKTOP("DESKTOP");
        
        /**
         * 
         */
        private final String value;
        
        /**
         * 
         */
        private final static Map<String, ClientType> CONSTANTS = new HashMap<>();

        /**
         * 
         */
        static {
            for (ClientType c : values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        /**
         * 
         * @param value
         */
        private ClientType(String value) {
            this.value = value;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return this.value;
        }

        /**
         * 
         * @param value
         * @return
         */
        public static ClientType fromValue(String value) {
            final ClientType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }
}