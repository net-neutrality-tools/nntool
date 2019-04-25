package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementGroup;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementGroupItem;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.nntool.Helperfunctions;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.GeoLocation;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedtestDetailGroup;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedtestDetailGroup.SpeedtestDetailGroupEntry;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedtestDetailGroup.SpeedtestDetailGroupEntry.FormatEnum;


/**
 * 
 * @author fk
 *
 */
@Service
public class DetailMeasurementService {

	private static final Locale STRING_FORMAT_LOCALE = Locale.US;
	
	private static final String SPEED_PREFIX = "speed";
	
	private static final String QOS_PREFIX = "qos";
	
	/**
	 * Groups the results according to the groupStructure param
	 * Will move all values that are defined in the groupStructure from their current location into the location defined by the groupStructure
	 * If none of the defined values of a group within the groupStructure are valid, the groupStructure will NOT be added at all
	 * If an exception is thrown during this process, the original json String is returned
	 * @param json, the JSON result to be grouped
	 * @param groupStructure, the structure to be applied to the json String as obtained w/ServerResource.getSetting(speedtestDetailGroups) TODO: make this more specific
	 * @return a JSONObject that has all key/value pairs that are defined in the groupStructure moved to the correct place, all other values remain unchanged
	 */
	public DetailMeasurementResponse groupJsonResult(final Measurement measurement, final List<SpeedtestDetailGroup> groupStructure){
		final DetailMeasurementResponse ret = new DetailMeasurementResponse();
		ret.setGroups(new ArrayList<>());
		groupStructure.forEach(group -> ret.getGroups().add(extractGroupValues(measurement, group)));
		
		return ret;
	}
	
	public DetailMeasurementResponse groupResult(final Measurement measurement, final List<SpeedtestDetailGroup> groupStructure, final Locale locale, 
			final int geoAccuracyDetailLimit) {
		return groupResult(measurement, groupStructure, locale, geoAccuracyDetailLimit, false);
	}
	
	/**
	 * Gets the grouped results according to the groupStructure param
	 * Items still need to be translated afterwards (the titles of the entries will be the translation keys to look for)
	 * If a unit is present, it still needs to be translated and appended to the value
	 * If the value starts with "key_" it needs be translated
	 * The key_location element needs custom handling! (used for the geo location)
	 * @param measurement the object whose results need be grouped
	 * @param groupStructure the structure of the groups as in couchdb speedtestDetailGroups
	 * @param locale needed for number formatting
	 * @param geoAccuracyDetailLimit the corresponding setting (in rmbt.geoAccuracyDetailLimit) needed for the geolocation entries
	 * @param includeKeys will include the internal keys in the result (e.g. speed_parameter_group)
	 * @return
	 */
	public DetailMeasurementResponse groupResult(final Measurement measurement, final List<SpeedtestDetailGroup> groupStructure, final Locale locale, 
			final int geoAccuracyDetailLimit, final boolean includeKeys) {
		final Format format = new DecimalFormat("0.00");
		//final JsonArray groupedResultsJson = groupJsonResult(gson.toJson(measurement, Measurement.class), groupStructure).getAsJsonArray("groups");
    	
    	//Fill in the corresponding Response with translated and formatted values
		final DetailMeasurementResponse ret = new DetailMeasurementResponse();
		
		final List<DetailMeasurementGroup> responseGroupList = new ArrayList<>();
		ret.setGroups(responseGroupList);
		
		final Format tzFormat = new DecimalFormat("+0.##;-0.##", new DecimalFormatSymbols(locale));
		
//			for(int i = 0; i < groupedResultsJson.size(); i++){
//				groups.add(gson.fromJson(groupedResultsJson.get(i), SpeedtestDetailGroup.class));
//			}
		
		for (final SpeedtestDetailGroup groupDefinition : groupStructure) {
			//create a corresponding responseGroup w/formatted and i18ed values
			final DetailMeasurementGroup responseGroup = new DetailMeasurementGroup();

			responseGroup.setTitle("key_" + groupDefinition.getKey());
			responseGroup.setDescription("description_" + groupDefinition.getKey());
			responseGroup.setIconCharacter(groupDefinition.getIcon());
			
			responseGroup.setItems(new ArrayList<>());
			
			for (final SpeedtestDetailGroupEntry entry : groupDefinition.getValues()) {
				final String key = entry.getKey();
				final FormatEnum formatEnum = entry.getFormat();
				final String unit = entry.getUnit();
				
				final String[] keyPath = entry.getKey().split("\\.");
				if(keyPath.length == 0){
					continue;
				}
				
				//TODO: if key starts with SPEED or QOS -> start w/different values
				final Object value = getObjectAt(keyPath, measurement, Measurement.class);
				if (value == null) {
					continue;
				}
				
				String val = value.toString();
				
				//fill the item accordingly
				final DetailMeasurementGroupItem item = new DetailMeasurementGroupItem();
				
				item.setTitle(entry.getTranslationKey());//getLocalizedMessage(entry.getTranslationKey(), locale));
				
				if (includeKeys) {
					item.setKey(key);
				}
				
				//do formatting
				//special cases get their own formatting (not ideal...)
				if(key.endsWith("network_type")){
					item.setValue(Helperfunctions.getNetworkTypeName(Integer.parseInt(val)));
				} else {
					//default formatting
					if(formatEnum != null){
						val = formatResultValueString(val, formatEnum, format);
					}
					if(unit != null) {
						item.setUnit(unit);
					}
				}
				
				if(item.getValue() == null){
					item.setValue(val);
				}
				//and add that item to the responseGroup
				responseGroup.getItems().add(item);
			}
			//group specific exceptions land here
			if (groupDefinition.getKey().equals("device_information_group")) {
				final List<GeoLocation> locations = measurement.getGeoLocationInfo().getGeoLocations();
				
				if (locations != null && locations.size() > 0) {
					final DetailMeasurementGroupItem item = new DetailMeasurementGroupItem();
					
					item.setTitle("key_location");//getLocalizedMessage("key_location", locale));
					
					try{
						item.setValue(getGeoLocation(geoAccuracyDetailLimit, locale, locations.get(0)));
					} catch(JSONException ex) {
						ex.printStackTrace();
					}
					
					responseGroup.getItems().add(item);
				}
			}
			if (responseGroup.getItems().size() > 0) {
				ret.getGroups().add(responseGroup);
			}
		} 
		return ret;
	}
	
	private Object getObjectAt (final String[] keyPath, final Object startingObject, final Class<?> startingClass) {
		Class<?> currentClass = startingClass;
		Object currentObject = startingObject;
		
		for (String part : keyPath) {
			if (currentObject == null) {
				return null;
			}
			try {
				final Field field = currentClass.getDeclaredField(part);
				field.setAccessible(true);
				
				currentObject = field.get(currentObject);
				currentClass = field.getType();

			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				return null;
			}
			
		}
		
		return currentObject;
	}
	
	/**
	 * Helper method to extract all values specified in the groupDefinition from the json JSONObject
	 * places them into the defined groups instead
	 * @param json the JSONObject containing all the information to be grouped
	 * @param groupDefinition the definition of a single group to be extracted by this method
	 * @return the same json with the specified values moved into the specified groups
	 */
	private DetailMeasurementGroup extractGroupValues(final Measurement measurement, final SpeedtestDetailGroup groupDefinition){
		final DetailMeasurementGroup ret = new DetailMeasurementGroup();
		
		ret.setIconCharacter(groupDefinition.getIcon());
		//TODO: translate + add suffix/prefix
		ret.setDescription(groupDefinition.getKey());
		ret.setTitle(groupDefinition.getKey());
		ret.setItems(new ArrayList<>());
		
		
		for (SpeedtestDetailGroupEntry entry : groupDefinition.getValues()) {
			final String[] keyPath = entry.getKey().split("\\.");
			if(keyPath.length == 0){
				continue;
			}
			
			final Object value;
			if (SPEED_PREFIX.equals(keyPath[0].toLowerCase())) {
				value = getObjectAt(keyPath, measurement.getMeasurements().get(MeasurementTypeDto.SPEED), SpeedMeasurement.class);
			} else if (QOS_PREFIX.equals(keyPath[0].toLowerCase())) {
				value = getObjectAt(keyPath, measurement.getMeasurements().get(MeasurementTypeDto.QOS), QoSMeasurement.class);
			} else {
				value = getObjectAt(keyPath, measurement, Measurement.class);
			}
			
			if (value == null) {
				continue;
			}
			
			//TODO: specific rules for the items added below
			
			final DetailMeasurementGroupItem item = new DetailMeasurementGroupItem();
			item.setKey(entry.getKey());
			if (entry.getTranslationKey() == null) {
				item.setTitle("key_" + entry.getKey());
			} else {
				item.setTitle(entry.getTranslationKey()); //TODO: translate
			}
			item.setUnit(entry.getUnit());
			item.setValue(value.toString());
			
			ret.getItems().add(item);
			
		}
		
		return ret;
	}
	
	private String getGeoLocation(final int geoAccuracyDetailLimit, final Locale locale, final GeoLocation location) throws JSONException {
        // geo-location
    	
    	final Double latitude = location.getLatitude();
    	final Double longitude = location.getLongitude();
    	final Double accuracy = location.getAccuracy();
    	final Double altitude = location.getAltitude();
    	
        if (location != null && latitude != null && longitude != null && accuracy != null) {
            if (accuracy < geoAccuracyDetailLimit) {//	settings.getRmbt().getGeoAccuracyDetailLimit()) {
                
            	final StringBuilder geoString = new StringBuilder(Helperfunctions.geoToString(latitude, longitude));
                geoString.append(" (");
                
                String provider = location.getProvider();
                
                if (provider != null) {
                	
                	provider = provider.toUpperCase();
                	String key = null;
                	
                	switch(provider) {
	                	case "GPS":
	            			key  = "key_geo_source_gps";
	            			break;
	                	case "BROWSER":
	            			key  = "key_geo_source_browser";
	            			break;
	            		case "NETWORK":
                		default:
                			key = "key_geo_source_network";
                	}
                	
                    geoString.append(key);
                    geoString.append(", ");
                }
                
                geoString.append(String.format(STRING_FORMAT_LOCALE, "+/- %.0f m", accuracy));

                if (altitude != null) {
                	geoString.append(String.format(STRING_FORMAT_LOCALE, ", alt: %.0f m", altitude));
                }

                geoString.append(")");
                
                return geoString.toString();                
            }
            
        }
        
        return null;
    }
	
	private String formatResultValueString(final String value, final FormatEnum formatEnum, final Format format) {
		try {
			return format.format(Double.parseDouble(value) / formatEnum.getDivider());
		} catch (NumberFormatException | NullPointerException ex) {
			ex.printStackTrace();
			// do nothing with it, just return original value
		}
		
		return value;
	}


}
