package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
	
	private static final String SHARE_TEXT_PLACEHOLDER = "{}";
	
	private static final String SHARE_TEXT_INTRO_TRANSLATION_KEY = "share_text_intro";

	@Autowired
	private MessageSource messageSource;

	/**
	 * Groups the results according to the groupStructure param
	 * Will move all values that are defined in the groupStructure from their current location into the location defined by the groupStructure
	 * If none of the defined values of a group within the groupStructure are valid, the groupStructure will NOT be added at all
	 * @param measurement, the measurement result to be grouped
	 * @param groupStructure, the structure to be applied to the measurement as obtained w/ServerResource.getSetting(speedtestDetailGroups) TODO: make this more specific
	 * @return a DetailMeasurementResponse with the grouped measurement result
	 */
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
		final Format format = new DecimalFormat("0.00", new DecimalFormatSymbols(locale));
		//final JsonArray groupedResultsJson = groupJsonResult(gson.toJson(measurement, Measurement.class), groupStructure).getAsJsonArray("groups");
    	
    	//Fill in the corresponding Response with translated and formatted values
		final DetailMeasurementResponse ret = new DetailMeasurementResponse();
		
		final PriorityQueue<ShareText> shareTextQueue = new PriorityQueue<>((textOne, textTwo) -> {
			return textOne.getPriority().compareTo(textTwo.getPriority());
		});
		
		final List<DetailMeasurementGroup> responseGroupList = new ArrayList<>();
		ret.setGroups(responseGroupList);
		
		final Format tzFormat = new DecimalFormat("+0.##;-0.##", new DecimalFormatSymbols(locale));
		
//			for(int i = 0; i < groupedResultsJson.size(); i++){
//				groups.add(gson.fromJson(groupedResultsJson.get(i), SpeedtestDetailGroup.class));
//			}
		
		for (final SpeedtestDetailGroup groupDefinition : groupStructure) {
			//create a corresponding responseGroup w/formatted and i18ed values
			final DetailMeasurementGroup responseGroup = new DetailMeasurementGroup();

			responseGroup.setTitle(messageSource.getMessage("key_" + groupDefinition.getKey(), null, locale));
			responseGroup.setDescription(messageSource.getMessage("description_" + groupDefinition.getKey(), null, locale));
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
				
				//if key starts with SPEED or QOS -> start w/different values
				final Object value;
				switch (keyPath[0].toLowerCase()) {
				case SPEED_PREFIX:
					value = getObjectAt(Arrays.copyOfRange(keyPath, 1, keyPath.length), measurement.getMeasurements().get(MeasurementTypeDto.SPEED), SpeedMeasurement.class);
					break;
				case QOS_PREFIX:
					value = getObjectAt(Arrays.copyOfRange(keyPath, 1, keyPath.length), measurement.getMeasurements().get(MeasurementTypeDto.QOS), QoSMeasurement.class);
					break;
				default:
					value = getObjectAt(keyPath, measurement, Measurement.class);
				}
				
				if (value == null) {
					continue;
				}
				
				String val = value.toString();
				
				//fill the item accordingly
				final DetailMeasurementGroupItem item = new DetailMeasurementGroupItem();
				
				item.setTitle(messageSource.getMessage(entry.getTranslationKey(), null, locale));
				
				if (includeKeys) {
					item.setKey(key);
				}
				
				//do formatting
				//special cases get their own formatting (not ideal...)
				if(key.endsWith("network_type")){
					item.setValue(Helperfunctions.getNetworkTypeName(Integer.parseInt(val)));
					//TODO: add specific rules for the items below
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
				//provide the values for the share text
				if (entry.getShareText() != null) {
					final ShareText share = new ShareText();
					////getLocalizedMessage(entry.getTranslationKey(), locale));
					if (entry.getShareText().contains(SHARE_TEXT_PLACEHOLDER)) { 
						share.setText(entry.getShareText().replace(SHARE_TEXT_PLACEHOLDER, item.getValue() + 
							(item.getUnit() != null ? " " + item.getUnit() : "") + "\n"));
					} else {
						share.setText(entry.getShareText() + "\n");
					}
					share.setPriority(entry.getSharePriority() != null ? entry.getSharePriority() : Integer.MAX_VALUE);
					shareTextQueue.add(share);
				}
				
				//and add that item to the responseGroup
				responseGroup.getItems().add(item);
			}
			//group specific exceptions land here
			if (groupDefinition.getKey().equals("device_information_group")) {
				final List<GeoLocation> locations = measurement.getGeoLocationInfo().getGeoLocations();
				
				if (locations != null && locations.size() > 0) {
					final DetailMeasurementGroupItem item = new DetailMeasurementGroupItem();
					
					item.setTitle(messageSource.getMessage("key_location", null, locale));
					
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
		//assemble share text
		if (!shareTextQueue.isEmpty()) {
			final StringBuilder builder = new StringBuilder();
			//getLocalizedMessage(entry.getTranslationKey(), locale));
			builder.append(SHARE_TEXT_INTRO_TRANSLATION_KEY).append("\n");
			
			for (ShareText s : shareTextQueue) {
				builder.append(s.getText());
			}
			
			ret.setShareMeasurementText(builder.toString());
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
				Field field = null;
				while (currentClass != Object.class) {
					try {
						field = currentClass.getDeclaredField(part);
					} catch (NoSuchFieldException ex) {
						currentClass = currentClass.getSuperclass();
						continue;
					}
					break;
				}
				if (field == null) {
					return null;
				}
				field.setAccessible(true);
				
				currentObject = field.get(currentObject);
				currentClass = field.getType();

			} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
				return null;
			}
			
		}
		
		return currentObject;
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

	private class ShareText {
		
		private String text;
		
		private Integer priority;

		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public Integer getPriority() {
			return priority;
		}
		public void setPriority(Integer priority) {
			this.priority = priority;
		}

		@Override
		public String toString() {
			return "ShareText [text=" + text + ", priority=" + priority + "]";
		}
		
		
	}

}
