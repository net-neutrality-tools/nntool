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

package at.alladin.nettest.shared;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.annotation.ExcludeFromRest;

/**
 * 
 * @author bp
 * @author lb
 *
 */
public abstract class CouchDbEntity implements Serializable {

	private static final long serialVersionUID = -8058183094519107803L;

	/**
	 * 
	 */
	@SerializedName("_id")
	@Expose
	@ExcludeFromRest
    protected String id;
	
	/**
	 * 
	 */
	@SerializedName("_rev")
	@Expose
	@ExcludeFromRest
	protected String rev;
	
	/**
	 * 
	 */
	@Expose
	@ExcludeFromRest
	protected String doctype;
	
	/**
	 * 
	 */
	@Expose
	@ExcludeFromRest
	protected List<String> profiles;
	
	/**
	 * 
	 */
	public CouchDbEntity() {
		doctype = getClass().getSimpleName(); // set doctype based on implementing class
	}
	
	/**
	 * 
	 * @return
	 */
    public String getId() {
    	return id;
    }
    
    /**
     * 
     * @param id
     */
    public void setId(String id) {
    	this.id = id;
    }
    
    /**
     * 
     * @return
     *     The _rev
     */
    public String getRev() {
        return rev;
    }

    /**
     * 
     * @param rev
     *     The _rev
     */
    public void setRev(String rev) {
        this.rev = rev;
    }
    
	/**
     * 
     * @return
     *     The doctype
     */
    public String getDoctype() {
        return doctype;
    }

    /**
     * 
     * @param doctype
     *     The doctype
     */
    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    /**
     * 
     * @return
     */
	public List<String> getProfiles() {
		return profiles;
	}

	/**
	 * 
	 * @param profiles
	 */
	public void setProfiles(List<String> profiles) {
		this.profiles = profiles;
	}
}
