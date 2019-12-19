package at.alladin.nettest.service.map.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.map.domain.model.MapServiceOptions;
import at.alladin.nettest.service.map.domain.repository.SqlSettingsRepository;
import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationType;
import at.alladin.nettest.shared.server.model.ServerSettings.ColorThresholds;
import at.alladin.nettest.shared.server.model.ServerSettings.SpeedThresholds;

//TODO: Merge this w/the other classes that deal w/the ThresholdsPerTechnology
/**
 * Helper class to read technologies per technology
 * @author fk
 *
 */
@Service
public class ThresholdsPerTechnologyHelperService {
	
	@Autowired
	private SqlSettingsRepository sqlSettingsRepository;

	private SpeedThresholds speedThresholds = null;
	
    /**
     * The skewed thresholds are used to make the thresholds appear more similar to the displayed legend
     * The cutoff points are placed in between the existing thresholds
     * e.g: Unskewed map with:
     * 			 1000 - red; 2000 - pink; 10000 - blue; > 10000 - green and yellow;
     * 		Turns into skewed Map with:
     * 			 0 - red; 500 - red; 1500 - pink; 6000 - blue; 10000 - green and yellow; > 10000 - green and yellow
     * As the result is interpolated w/the two keys above and below the value, the results work better this way
     * For cutoffs, the edge colours are added twice to the map (maybe make this smoother in future versions (definitely before version 6.0)			
     */
	private SpeedThresholds skewedSpeedThresholds = null;
	
	private Map<String, Integer> groupNameToReferenceTechnology = null;
	
	/**
	 * 
	 * @return the ThresholdsPerTechnology object as it is in the postgre DB, or null if the connection failed
	 */
	public SpeedThresholds getThresholdsPerTechnology() {
//		if (thresholdsPerTechnology == null) {
//			initThresholdsPerTechnologyHelper();
//		}
		return speedThresholds;
	}
	
	/**
	 * 
	 * @return a different version of the thresholds per technology, which assigns values that are useful when interpolating for the heatmap
	 * (Every value is bumped down a notch)
	 */
	public SpeedThresholds getSkewedThresholdsPerTechnology() {
//		if (skewedThresholdsPerTechnology == null) {
//			initThresholdsPerTechnologyHelper();
//		}
		return skewedSpeedThresholds;
	}
	
	/**
	 * Method to look up the networktype ID of a technology/signal group; use for the ThresholdsPerTechnology
	 * @param technology
	 * @param signalGroup
	 * @return the network ID of a given technology/signal group; to be used in the ThresholdsPerTechnology
	 */
	public Integer getReferenceTechnologyId(final String technology, final MapServiceOptions.SignalGroup signalGroup) {
		if(groupNameToReferenceTechnology == null) {
			initGroupNameToReferenceTechnology();
		}
		return groupNameToReferenceTechnology.get(getTechnologyName(technology, signalGroup));
	}
	
	/**
	 * groupnames are the supported filters of the website (e.g. WLAN, 3, 234, MOBILE, ...)
	 * @return all supported groupnames
	 */
	public Set<String> getGroupnameSet() {
		if(groupNameToReferenceTechnology == null) {
			initGroupNameToReferenceTechnology();
		}
		return groupNameToReferenceTechnology.keySet();
	}
	
	public ColorThresholds getByClassificationType(final SpeedThresholds thresholds, final ClassificationType classificationType) {	
		if (thresholds == null) {
			return null;
		}
		switch(classificationType) {
		case UPLOAD:
			return thresholds.getUpload();
		case DOWNLOAD:
			return thresholds.getDownload();
		case SIGNAL:
			return thresholds.getSignal();
		case PING:
			return thresholds.getPing();
		default:
			return null;
		}
	}
	
	//TODO: read this from settings somewhere
	private void initGroupNameToReferenceTechnology() {
    	final Map<String, Integer> groupNameToRefTechnology = new HashMap<>();
    	//groupNameToRefTechnology.put(null, 3);	//the null key is equivalent to all mobile technologies (the website sends no param if 2G/3G/4G is selected)
    	groupNameToRefTechnology.put("2", 2);
    	groupNameToRefTechnology.put("3", 15);
    	groupNameToRefTechnology.put("4", 13);
    	groupNameToRefTechnology.put("234", 13);
    	groupNameToRefTechnology.put("34", 13);
    	groupNameToRefTechnology.put("WIFI", 99);
    	groupNameToRefTechnology.put("LAN", 98);
    	groupNameToRefTechnology.put("CLI", 97);
    	groupNameToRefTechnology.put("ALL", 13);
    	groupNameToRefTechnology.put("BROWSER", 98);
    	//groupNameToRefTechnology.put("MOBILE", 3);
    	groupNameToReferenceTechnology = groupNameToRefTechnology;
	}
	
	/**
	 * Grab settings from postgesql and json it into a ThresholdsPerTechnology object
	 */
	@PostConstruct
	private void initThresholdsPerTechnologyHelper() {
		speedThresholds = sqlSettingsRepository.getSettings().getSpeedThresholds();
        
        //provide skewed values
        if (speedThresholds != null) {
        	skewedSpeedThresholds = new SpeedThresholds();
        	skewedSpeedThresholds.setDownload(new ColorThresholds());
        	skewedSpeedThresholds.setUpload(new ColorThresholds());
        	skewedSpeedThresholds.setPing(new ColorThresholds());
        	skewedSpeedThresholds.setSignal(new ColorThresholds());
        	addSkewedEntries(ClassificationType.DOWNLOAD, true);
        	addSkewedEntries(ClassificationType.UPLOAD, true);
        	addSkewedEntries(ClassificationType.PING, false);
        	addSkewedEntries(ClassificationType.SIGNAL, false);
        	
        }
	}
	
	/**
	 * Helper method to parse both technology and signal group into the respective technology
	 * Useful as only the MOBILE SignalGroup really uses the technology string, all others are returned with their fixed values
	 * Needed for the groupNameToReferenceTechnology Map
	 * @param technology
	 * @param signalGroup
	 * @return
	 */
    private String getTechnologyName(final String technology, final MapServiceOptions.SignalGroup signalGroup) {
    	switch(signalGroup) {
    	case WIFI:
    		return "WIFI";
    	case BROWSER:
    		return "BROWSER";
    	case MOBILE:
    		if(technology == null) {
    			return "234";
    		} else {
    			return technology;
    		}
    	case ALL:
    		return "ALL";
    	default:
    		return "ALL";
    	}
    }
    
    /**
     * Helper method to construct the skewed thresholdsPerTechnology
     * @param type the classificationType to add to the skewedThresholds
     * @param zeroFirst if the zero is to be added to the worst (TRUE) or best (FALSE) value 
     */
    private void addSkewedEntries (final ClassificationType type, final boolean zeroFirst) {
    	
		final ColorThresholds thresh = getByClassificationType(speedThresholds, type);
		final ColorThresholds skewedThresh = new ColorThresholds();
		skewedThresh.setColorMap(new TreeMap<>());
		
		Long lastKey = 0L;
		boolean first = true;
		Set<Long> keySet = null;
		if (type == ClassificationType.SIGNAL) {
			keySet = thresh.getColorMap().descendingKeySet();
		} else {
			keySet = thresh.getColorMap().keySet();
		}
		for (Long key : keySet) {
			if (first) {
				if (type == ClassificationType.SIGNAL) {
					skewedThresh.getColorMap().put(lastKey, thresh.getDefaultColor());
				} else {
					skewedThresh.getColorMap().put(lastKey, thresh.getColorMap().get(key));
				}
				first = false;
			}
			skewedThresh.getColorMap().put((lastKey + key) / 2 , thresh.getColorMap().get(key));
			lastKey = key;
		}
		if (type == ClassificationType.SIGNAL) {
			skewedThresh.getColorMap().put(lastKey, thresh.getColorMap().get(lastKey));
			skewedThresh.setDefaultColor(thresh.getDefaultColor());    			
		} else {
			skewedThresh.getColorMap().put(lastKey, thresh.getDefaultColor());
			skewedThresh.setDefaultColor(thresh.getDefaultColor());    			
		}
		
		getByClassificationType(skewedSpeedThresholds, type).setColorMap(skewedThresh.getColorMap());
		getByClassificationType(skewedSpeedThresholds, type).setDefaultColor(skewedThresh.getDefaultColor());
	}
	
}
