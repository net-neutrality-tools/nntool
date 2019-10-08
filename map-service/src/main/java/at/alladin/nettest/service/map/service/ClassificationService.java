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

package at.alladin.nettest.service.map.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationItem;
import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationType;
import at.alladin.nettest.shared.server.model.ServerSettings.ColorThresholds;
import at.alladin.nettest.shared.server.model.ServerSettings.SpeedThresholds;


//TODO: merge this class with the ClassificationHelper from server-shared (as far as possible)

/**
 * 
 * @author alladin-IT GmbH (?@alladin.at)
 *
 */
@Service
public class ClassificationService {
	
	@Autowired
	private ThresholdsPerTechnologyHelperService thresholdsHelperService;
	
    //classification thresholds for new classification
	private SpeedThresholds thresholdsPerTechnology;
	
    /**
     * TODO obtain info elsewhere
     */
    @PostConstruct
    public void init() {
        
        this.thresholdsPerTechnology = thresholdsHelperService.getThresholdsPerTechnology();
        
    }

    private static String classifyColor(final ColorThresholds colorThresholds, final long value) {
    	final Long ceilingKey = colorThresholds.getColorMap().ceilingKey(value);
    	return ceilingKey == null ? colorThresholds.getDefaultColor() : colorThresholds.getColorMap().get(ceilingKey);
    }
    
    public ClassificationItem classifyColor(final ClassificationType type, final long value, final Integer networkType) {
    	final ClassificationItem ret = new ClassificationItem();
    	if (thresholdsPerTechnology != null) {
    		final ColorThresholds thresholds = thresholdsHelperService.getByClassificationType(thresholdsPerTechnology, type);
    		if (thresholds != null) {
    			ret.setClassificationColor(classifyColor(thresholds, value));
    		}
    	}
    	
    	return ret;
    }
    
}
