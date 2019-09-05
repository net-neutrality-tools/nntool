/*******************************************************************************
 * Copyright 2013-2017 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

package at.alladin.nettest.service.map.web.api.v0;

import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.service.map.domain.model.info.MapInfo;
import at.alladin.nettest.service.map.service.InfoService;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 
 * @author alladin-IT GmbH (fk@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v0/tiles/info")
public class InfoResource{
	
	private final Logger logger = LoggerFactory.getLogger(InfoResource.class);

	@Inject
	private InfoService infoService;
    
	//TODO: BIG! Restrict CrossOrigin requests if possible
	@CrossOrigin
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> obtainMapInfo (@ApiIgnore Locale locale) {
    	final MapInfo ret = new MapInfo();
    	ret.setMapFilters(infoService.getMapFilter(locale));
    	ret.setMapTechnologyTypeList(infoService.getMapTypes(locale).getTechnolgyTypeList());
    	return ResponseEntity.ok(new MapInfoWrapper(ret));
    }
    
    /**
     * Quite necessary wrapper class to provide same naming scheme as in previous versions
     * @author fk
     *
     */
    private class MapInfoWrapper {
    	@Expose
    	@SerializedName("mapfilter")
    	@JsonProperty("mapfilter")
    	private final MapInfo wrappedInfo;
    	
    	private MapInfoWrapper(final MapInfo info) {
    		this.wrappedInfo = info;
    	}

		public MapInfo getWrappedInfo() {
			return wrappedInfo;
		}
    }
}
