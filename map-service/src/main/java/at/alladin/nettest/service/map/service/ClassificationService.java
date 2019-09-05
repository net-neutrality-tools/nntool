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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.map.domain.model.MapServiceOptions.SignalGroup;
import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationItem;
import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationType;
import at.alladin.nettest.shared.server.model.ServerSettings;
import at.alladin.nettest.shared.server.model.ServerSettings.ColorThresholds;
import at.alladin.nettest.shared.server.model.ServerSettings.SpeedThresholds;


//TODO: merge this class with the ClassificationHelper from server-shared (as far as possible)

/**
 * 
 * @author alladin-IT GmbH (?@alladin.at)
 *
 */
@Service
public final class ClassificationService {
	
	@Autowired
	private SqlSettingsService sqlSettingsService;
	
	@Autowired
	private ThresholdsPerTechnologyHelperService thresholdsHelperService;
	
    //classification thresholds for new classification
	private SpeedThresholds thresholdsPerTechnology;
    private final Set<Integer> NETWORK_IDS_WIFI = new HashSet<>(Arrays.asList(99));
    private final Set<Integer> NETWORK_IDS_RSRP = new HashSet<>(Arrays.asList(13));
    private final Set<Integer> NETWORK_IDS_MOBILE = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 105));
	
    /**
     * TODO obtain info elsewhere
     */
    @PostConstruct
    public void init() {
        final ServerSettings settings = sqlSettingsService.getSettings();
        
        this.thresholdsPerTechnology = thresholdsHelperService.getThresholdsPerTechnology();
        
    }

    /**
     * 
     * @param values
     * @return
     */
    private static List<String> getCaptions(int[] values) {
        final List<String> result = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            result.add(String.format(Locale.US, "%.1f", ((double)values[i]) / 1000));
        }
        return result;
    }

    private static String classifyColor(final ColorThresholds colorThresholds, final long value) {
    	final Long ceilingKey = colorThresholds.getColorMap().ceilingKey(value);
    	return ceilingKey == null ? colorThresholds.getDefaultColor() : colorThresholds.getColorMap().get(ceilingKey);
    }
    
    public ClassificationItem classifyColor(final ClassificationType type, final long value, final Integer networkType) {
    	final ClassificationItem ret = new ClassificationItem();
    	switch (type) {
    	case DOWNLOAD:
    		if(thresholdsPerTechnology != null && networkType != null && thresholdsPerTechnology.getDownload() != null) {
    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getDownload(), value));
    		}
    		break;
    	case UPLOAD:
       		if(thresholdsPerTechnology != null && networkType != null && thresholdsPerTechnology.getUpload() != null) {
    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getUpload(), value));
    		}
    		break;
    	case PING:
    		if(thresholdsPerTechnology != null && networkType != null && thresholdsPerTechnology.getPing() != null) {
    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getPing(), value));
    		}
    		break;
    	case SIGNAL:
    		if(thresholdsPerTechnology != null && networkType != null && thresholdsPerTechnology.getSignal() != null) {
    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getSignal(), value));
    		}
//    	case SIGNAL_MOBILE:
//    		ret.setClassificationNumber(classify(THRESHOLD_SIGNAL_MOBILE, value));
//    		if(thresholdsPerTechnology != null) {
//    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getSignalMobile(), value));
//    		}
//    		break;
//    	case SIGNAL_RSRP:
//    		ret.setClassificationNumber(classify(THRESHOLD_SIGNAL_RSRP, value));
//    		if(thresholdsPerTechnology != null) {
//    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getSignalRsrp(), value));
//    		}
//    		break;
//    	case SIGNAL_WIFI:
//    		ret.setClassificationNumber(classify(THRESHOLD_SIGNAL_WIFI, value));
//    		if(thresholdsPerTechnology != null) {
//    			ret.setClassificationColor(classifyColor(thresholdsPerTechnology.getSignalWifi(), value));
//    		}
//			break;
    	default:
        	//the old default return was 1
        	ret.setClassificationNumber(1);
        	//TODO: default color?
    	}
    	
    	return ret;
    }

	public SpeedThresholds getThresholdsPerTechnology() {
		return thresholdsPerTechnology;
	}
	
	public ColorThresholds getThresholdsByClassificationType (final ClassificationType type) {
		switch (type) {
		case DOWNLOAD:
			return thresholdsPerTechnology.getDownload();
		case UPLOAD:
			return thresholdsPerTechnology.getUpload();
		case PING:
			return thresholdsPerTechnology.getPing();
		case SIGNAL:
			return thresholdsPerTechnology.getSignal();
		default:
			return null;
		}
	}
	/*

	public int[] getClassificationForType(final ClassificationType classificationType, final SignalGroup signalGroup) {
		switch (classificationType) {
		case DOWNLOAD:
			return THRESHOLD_DOWNLOAD;
		case UPLOAD:
			return THRESHOLD_UPLOAD;
		case PING:
			return THRESHOLD_PING;
		case SIGNAL:
			switch (signalGroup) {
				case WIFI:
					return THRESHOLD_SIGNAL_WIFI;
				default:
					return THRESHOLD_SIGNAL_MOBILE;
			}
		default:
			return null;
		}
	}

	public List<String> getClassificationCaptionsForType(final ClassificationType classificationType, final SignalGroup signalGroup) {
		switch (classificationType) {
		case DOWNLOAD:
			return THRESHOLD_DOWNLOAD_CAPTIONS;
		case UPLOAD:
			return THRESHOLD_UPLOAD_CAPTIONS;
		case PING:
			return THRESHOLD_PING_CAPTIONS;
		case SIGNAL:
			switch (signalGroup) {
				case WIFI:
					return THRESHOLD_SIGNAL_WIFI_CAPTIONS;
				default:
					return THRESHOLD_SIGNAL_MOBILE_CAPTIONS;
			}
		default:
			return null;
		}
	}
	*/
    
}
