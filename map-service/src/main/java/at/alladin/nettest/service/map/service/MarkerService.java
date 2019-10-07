package at.alladin.nettest.service.map.service;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.inject.Inject;

import at.alladin.nettest.service.map.domain.model.MapServiceOptions;
import at.alladin.nettest.service.map.domain.model.MapServiceSettings;
import at.alladin.nettest.service.map.util.GeographyHelper;
import at.alladin.nettest.shared.nntool.Helperfunctions;
import at.alladin.nettest.shared.server.helper.ClassificationHelper;
import at.alladin.nntool.shared.map.MapCoordinate;
import at.alladin.nntool.shared.map.MapMarkerRequest;
import at.alladin.nntool.shared.map.MapMarkerResponse;
import at.alladin.nntool.shared.map.MapMarkerResponse.MapMarker;
import at.alladin.nntool.shared.map.MapMarkerResponse.MarkerItem;

@Service
public class MarkerService {

	private final Logger logger = LoggerFactory.getLogger(MarkerService.class);
	
    /**
     *
     */
    private static int MAX_PROVIDER_LENGTH = 22;

    /**
     *
     */
    private static int CLICK_RADIUS = 10;

    @Inject
    private ClassificationService classificationService;

    @Inject
    private MapOptionsService mapOptionsService;

    //TODO: @Inject message source
    private MessageSource messageSource = new MessageSource() {
		
		@Override
		public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
			// TODO Auto-generated method stub
			return code;
		}
		
		@Override
		public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
			// TODO Auto-generated method stub
			return code;
		}
		
		@Override
		public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
			// TODO Auto-generated method stub
			return "resolvable";
		}
	};

    @Inject
    private JdbcTemplate jdbcTemplate;

    public MapMarkerResponse obtainMarker(final MapMarkerRequest request, final Locale locale) {

        final MapMarkerResponse answer = new MapMarkerResponse();

        try {
        	
        	String clientUuidString = null;
        	if (request.getOptions() != null && request.getOptions().containsKey("client_uuid")) {
        		clientUuidString = request.getOptions().get("client_uuid");
        	}

            boolean useXY = false;
            boolean useLatLon = false;
            
            final MapCoordinate coordinates = request.getCoordinates();

            if (coordinates.getX() != null && coordinates.getY() != null) {
                useXY = true;
            } else if (coordinates.getLat() != null && coordinates.getLon() != null) {
                useLatLon = true;
            }

            if (coordinates.getZoom() != null && (useLatLon || useXY)) {
            	double geoX = 0;
                double geoY = 0;
                
            	final int zoom = coordinates.getZoom();
                if (useXY) {
                    geoX = coordinates.getX();
                    geoY = coordinates.getY();
                } else if (useLatLon) {
                    geoX = GeographyHelper.lonToMeters(coordinates.getLon());
                    geoY = GeographyHelper.latToMeters(coordinates.getLat());
                }

                final int size;
                if (coordinates.getSize() != null) {
                    size = coordinates.getSize();
                } else {
                	size = 0;
                }

                if (zoom != 0 && geoX != 0 && geoY != 0) {
                    double radius = 0;
                    if (size > 0) {
                        radius = size * GeographyHelper.getResFromZoom(256, zoom); // TODO use real tile size
                    } else {
                        radius = CLICK_RADIUS * GeographyHelper.getResFromZoom(256, zoom);  // TODO use real tile size
                    }
                    final double geoXMin = geoX - radius;
                    final double geoXMax = geoX + radius;
                    final double geoYMin = geoY - radius;
                    final double geoYMax = geoY + radius;

                    String prioritizeUUIDString = null;
                    String highlightUuidString = null;
                    UUID prioritizeUUID = null;

                    String optionStr = null;
                    if (request.getOptions() != null && request.getOptions().containsKey("map_options")) {
                        optionStr = request.getOptions().get("map_options");
                    } else {//if (optionStr == null || optionStr.length() == 0) { // set default
                        optionStr = "mobile/download";
                    }

                    final MapServiceOptions mo = mapOptionsService.getMapOptionsForKey(optionStr); //mso.getMapOptionMap().get(optionStr);

                    final List<MapServiceSettings.SQLFilter> filters = new ArrayList<>(mapOptionsService.getDefaultMapFilters());
                    filters.add(mapOptionsService.getAccuracyMapFilter());

                    if (request.getMapFilter() != null) {
	                    final Iterator<String> keys = request.getMapFilter().keySet().iterator();
	
	                    while (keys.hasNext()) {
	                        final String key = /*(String)*/ keys.next();
	                        if (request.getMapFilter().get(key) != null) {
	                            if (key.equals("highlight")) {
	                                if (highlightUuidString == null) {
	                                    highlightUuidString = request.getMapFilter().get(key);
	                                }
	                            } else if (key.equals("highlight_uuid")) {
	                                highlightUuidString = request.getMapFilter().get(key);
	                            } else if ("prioritize".equals(key)) {
	                                prioritizeUUIDString = request.getMapFilter().get(key);
	                            } else {
	                                final MapServiceSettings.MapFilter f = mapOptionsService.getMapFilterForKey(key);
	                                if (f != null) {
	                                    //filters.add(mapFilter.getFilter(mapFilterObj.getString(key)));
	                                    filters.add(f.getFilter("" + request.getMapFilter().get(key)));
	                                }
	                            }
	                        }
	                    }
                    }

                    if (prioritizeUUIDString != null) {
                        try {
                            prioritizeUUID = UUID.fromString(prioritizeUUIDString);
                        } catch (final Exception e) {
                            prioritizeUUID = null;
                        }
                    }

                    final StringBuilder whereSQL = new StringBuilder(mo.getSqlFilter());
                    for (final MapServiceSettings.SQLFilter sf : filters) {
                        whereSQL.append(" AND ").append(sf.getWhereClause());
                    }

                    final String sql = String.format(
                            "SELECT"
                                    + (useLatLon
	                                    ? " t.geo_location_latitude as lat, t.geo_location_longitude as lon"
	                                    : " ST_X(t.geo_location_geometry) x, ST_Y(t.geo_location_geometry) y"
                                		)
                                    + ", (t.start_time)::timestamp as time, t.agent_timezone as timezone, ias.throughput_avg_download_bps as speed_download, ias.throughput_avg_upload_bps as speed_upload"
                                    + ", ias.rtt_median_ns as ping_median, t.initial_network_type_id as network_type"
                                    + ", t.mobile_network_signal_strength_2g3g_dbm as signal_strength"
                                    + ", t.mobile_network_lte_rsrp_dbm as lte_rsrp"
                                    + ", t.wifi_initial_ssid as wifi_ssid"
                                    + ", t.mobile_network_operator_name as network_operator"
                                    + ", t.mobile_sim_operator_name as network_sim_operator"
                                    + ", t.provider_public_ip_as_name as public_ip_as_name"
                                    + ", t.open_data_uuid as open_test_uuid"
                                    + ", t.mobile_roaming_type::int as roaming_type"
                                    //TODO: there is only one provider name left, right?
//                                    + ", COALESCE(json->'network_info'->'provider'->>'shortname', json->'network_info'->'provider'->>'name') as provider_text"
//                                    + ", COALESCE(json->'mobile_network_info'->'mobile_provider'->>'shortname', json->'mobile_network_info'->'mobile_provider'->>'name') as mobile_network_name"
									+ ", COALESCE(t.provider_shortname, t.provider_name) as provider_name"
									+ ", t.mobile_sim_operator_name as mobile_sim_name"
                                    + ", t.initial_network_type_id as network_type_id"
                                    + ", t.network_signal_info as signals"
//                                    + ", t.uuid as uuid, t.client_uuid as client_uuid"
									+ ", t.agent_uuid as client_uuid"
									+ ", t.os_name as os_name"
									+ ", t.agent_type as agent_type"
                                    + " FROM measurements t"
                                    + " LEFT JOIN ias_measurements ias ON t.open_data_uuid = ias.measurement_open_data_uuid"
                                    + " WHERE"
                                    //+ ( clientUuid == null ? "" : " t.client_uuid = ?::uuid AND")
                                    + " %s"
//                                    + " AND location && ST_SetSRID(ST_MakeBox2D(ST_Point(?,?), ST_Point(?,?)), 900913)"
                                    + " AND t.geo_location_geometry && ST_SetSRID(ST_MakeBox2D(ST_Point(?,?), ST_Point(?,?)), 900913)"
                                    + " ORDER BY"
                                    + (prioritizeUUID == null ? "" : " CASE t.agent_uuid WHEN ?::uuid THEN 1 ELSE 2 END, ")
                                    + " t.open_data_uuid DESC" + " LIMIT 5", whereSQL);

                    //make some things final for the lambda compatibility
                    final UUID finalPrioritizeUuid = prioritizeUUID;
                    final String finalClientUuidString = clientUuidString;
                    final String finalHighlightUuidString = highlightUuidString;

                    final List<MapMarker> resultObjects = jdbcTemplate.query(sql, ps -> {
                        int i = 1;
                                /*
                                if (clientUuid != null) {
                                    ps.setObject(i++, clientUuid.toString());
                                }
                                */

                        for (final MapServiceSettings.SQLFilter sf : filters) {
                            i = sf.fillParams(i, ps);
                        }

                        ps.setDouble(i++, geoXMin);
                        ps.setDouble(i++, geoYMin);
                        ps.setDouble(i++, geoXMax);
                        ps.setDouble(i++, geoYMax);

                        if (finalPrioritizeUuid != null) {
                            ps.setObject(i++, finalPrioritizeUuid.toString());
                        }

                    }, (ResultSet rs, int rowNum) -> parseResultSet(rs, finalClientUuidString, finalHighlightUuidString, locale));

                    logger.info("Fetching marker info. #Results: " + (resultObjects == null ? 0 : resultObjects.size()));

                    answer.setMapMarkers(resultObjects);
                }
            } else {
                logger.error("Expected request is missing.");
            }
        } catch (final JSONException e) {
            logger.error("Error parsing JSON Data " + e.toString());
        }

        return answer;
    }

    private MapMarker parseResultSet(final ResultSet rs, final String clientUuidString, final String highlightUuidString, final Locale locale) throws SQLException {
        final NumberFormat format = NumberFormat.getInstance(locale);
        format.setRoundingMode(RoundingMode.HALF_UP);
        
        final MapMarker ret = new MapMarker();
        final List<MarkerItem> markerResultList = new ArrayList<>();
        ret.setResultItems(markerResultList);

        UUID highlightUuid = null;
        if (highlightUuidString != null) {
            try {
                highlightUuid = UUID.fromString(highlightUuidString);
            } catch (final Exception e) {
                highlightUuid = null;
            }
        }

        final String dbClientUuidString = rs.getString("client_uuid");
        //final String measurementUuid = rs.getString("uuid");
        final String measurementUuid = "0" + rs.getString("open_test_uuid");
        
        // RMBTClient Info

        if ((clientUuidString != null && clientUuidString.equals(dbClientUuidString)) ||
                (highlightUuidString != null && highlightUuidString.equals(dbClientUuidString))) {
            // highlight uses both the new highlight_uuid syntax and the old client_uuid syntax
            // TODO: The client_uuid should only return your own measurement markers
        	ret.setHighlight(true);

            // put measurement_uuid if measurement belongs to given client_uuid
        	ret.setOpenTestUuid(measurementUuid); // give measurement_uuid for in-app view of own results
        } else {
        	ret.setHighlight(false);
        }

        final double res_x = rs.getDouble(1);
        final double res_y = rs.getDouble(2);
        final String openTestUUID = rs.getString("open_test_uuid");

        ret.setLatitude(res_x);
        ret.setLongitude(res_y);

        ret.setOpenTestUuid("O" + openTestUUID);
        // marker.put("uid", uid);

        final Date date = rs.getTimestamp("time");
        final String tzString = rs.getString("timezone");
        final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        if (!Strings.isNullOrEmpty(tzString)) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(tzString));
        }
        markerResultList.add(generateMeasurementItem("MARKER_TIME", dateFormat.format(date)));
        ret.setTimestamp(date.getTime());
        
    	markerResultList.add(generateMeasurementItem("MARKER_AGENT", "AGENT_" + rs.getString("agent_type")));
        markerResultList.add(generateMeasurementItem("MARKER_OS", rs.getString("os_name")));

        //get the first networkType for classification
        final String signalString = rs.getString("signals");
        final JSONArray signalArr;
        if (signalString != null) {
        	signalArr = new JSONArray(signalString);
        } else {
        	signalArr = new JSONArray();
        }
        final Integer networkTypeId = signalArr.length() > 0 ? signalArr.getJSONObject(0).getInt("network_type_id") : rs.getInt("network_type_id");

        final List<MarkerItem> measurementResultList = new ArrayList<>();
        
        final int fieldDown = rs.getInt("speed_download");
        final String downloadString = String.format("%s %s", format.format(fieldDown / 1000000d), messageSource.getMessage("MARKER_DOWNLOAD_UNIT", null, locale));
        ClassificationHelper.ClassificationItem classificationItem = classificationService.classifyColor(ClassificationHelper.ClassificationType.DOWNLOAD, fieldDown, networkTypeId);
        MarkerItem markerItem = generateMeasurementItem(messageSource.getMessage("MARKER_DOWNLOAD", null, locale), downloadString, classificationItem);
        measurementResultList.add(markerItem);
        markerResultList.add(0, markerItem);

        final int fieldUp = rs.getInt("speed_upload");
        final String uploadString = String.format("%s %s", format.format(fieldUp / 1000000d),messageSource.getMessage("MARKER_UPLOAD_UNIT", null, locale));
        classificationItem = classificationService.classifyColor(ClassificationHelper.ClassificationType.UPLOAD, fieldUp, networkTypeId);
        markerItem = generateMeasurementItem(messageSource.getMessage("MARKER_UPLOAD", null, locale), uploadString, classificationItem);
        measurementResultList.add(markerItem);
        markerResultList.add(1, markerItem);

        final int pingValue = (int) Math.round(rs.getDouble("ping_median") / 1000000d);
        final String pingString = String.format("%s %s", format.format(pingValue),
                messageSource.getMessage("MARKER_PING_UNIT", null, locale));
        classificationItem = classificationService.classifyColor(ClassificationHelper.ClassificationType.PING, pingValue, networkTypeId);
        markerItem = generateMeasurementItem(messageSource.getMessage("MARKER_PING", null, locale), pingString, classificationItem);
        measurementResultList.add(markerItem);
        markerResultList.add(2, markerItem);

        final int networkType = rs.getInt("network_type");

        final String signalField = rs.getString("signal_strength");
        if (signalField != null && signalField.length() != 0) {
            final int signalValue = rs.getInt("signal_strength");
            classificationItem = classificationService.classifyColor(ClassificationHelper.ClassificationType.SIGNAL, signalValue, networkType);
            markerItem = generateMeasurementItem(messageSource.getMessage("MARKER_SIGNAL", null, locale), signalValue + " " + messageSource.getMessage("MARKER_SIGNAL_UNIT", null, locale), classificationItem);
            measurementResultList.add(markerItem);
            markerResultList.add(markerItem);
        }

        final String lteRsrpField = rs.getString("lte_rsrp");
        if (lteRsrpField != null && lteRsrpField.length() != 0) {
            final int lteRsrpValue = rs.getInt("lte_rsrp");
            classificationItem = classificationService.classifyColor(ClassificationHelper.ClassificationType.SIGNAL, lteRsrpValue, networkType);
            markerItem = generateMeasurementItem(messageSource.getMessage("MARKER_LTE_RSRP", null, locale), lteRsrpValue + " " + messageSource.getMessage("MARKER_LTE_RSRP_UNIT", null, locale), classificationItem);
            measurementResultList.add(markerItem);
            markerResultList.add(markerItem);
        }


        ret.setMeasurementResults(measurementResultList);

        final List<MarkerItem> networkResult = new ArrayList<>();

        markerItem = generateMeasurementItem(messageSource.getMessage("MARKER_NETWORK_TYPE", null, locale), Helperfunctions.getNetworkTypeName(networkType));
        networkResult.add(markerItem);
        markerResultList.add(0, markerItem);

        if (networkType == 98 || networkType == 99) { // mobile wifi or browser

            String providerText = null;

            try {
                providerText = MoreObjects.firstNonNull(rs.getString("provider_name"), rs.getString("public_ip_as_name"));
            } catch (NullPointerException ex) {
                // no provider
            }

            if (!Strings.isNullOrEmpty(providerText)) {
                if (providerText.length() > (MAX_PROVIDER_LENGTH + 3)) {
                    providerText = providerText.substring(0, MAX_PROVIDER_LENGTH) + "...";
                }
                markerItem = generateMeasurementItem(messageSource.getMessage("MARKER_PROVIDER", null, locale), providerText);
                networkResult.add(markerItem);
                markerResultList.add(0, markerItem);
            }

            if (networkType == 99) { // mobile wifi
                if (highlightUuid != null && rs.getString("uuid") != null) { // own test
                    final String ssid = rs.getString("wifi_ssid");
                    if (ssid != null && ssid.length() != 0) {
                    	networkResult.add(generateMeasurementItem(messageSource.getMessage("MARKER_WIFI_SSID", null, locale), ssid.toString()));
                    }
                }
            }
        } else { // mobile
            final String networkOperator = rs.getString("network_operator");
            final String mobileNetworkName = rs.getString("provider_name");
            final String simOperator = rs.getString("network_sim_operator");
            final String mobileSimName = rs.getString("mobile_sim_name");
            final int roamingType = rs.getInt("roaming_type");
            //network
            if (!Strings.isNullOrEmpty(networkOperator)) {
                final String mobileNetworkString;
                if (roamingType != 2) { //not international roaming - display name of home network
                    if (Strings.isNullOrEmpty(mobileSimName)) {
                        mobileNetworkString = networkOperator;
                    } else {
                        mobileNetworkString = String.format("%s (%s)", mobileSimName, networkOperator);
                    }
                } else { //international roaming - display name of network
                    if (Strings.isNullOrEmpty(mobileSimName)) {
                        mobileNetworkString = networkOperator;
                    } else {
                        mobileNetworkString = String.format("%s (%s)", mobileNetworkName, networkOperator);
                    }
                }

                networkResult.add(generateMeasurementItem(messageSource.getMessage("MARKER_MOBILE_NETWORK", null, locale), mobileNetworkString));
            } else if (!Strings.isNullOrEmpty(simOperator)) { //home network (sim)
                final String mobileNetworkString;

                if (Strings.isNullOrEmpty(mobileSimName)) {
                    mobileNetworkString = simOperator;
                } else {
                    mobileNetworkString = String.format("%s (%s)", mobileSimName, simOperator);
                }

            	/*
            	if (!Strings.isNullOrEmpty(mobileProviderName)) {
            		mobileNetworkString = mobileProviderName;
            	} else {
            		mobileNetworkString = simOperator;
            	}
            	*/

                markerItem = generateMeasurementItem(messageSource.getMessage("MARKER_HOME_NETWORK", null, locale), mobileNetworkString);
                networkResult.add(markerItem);
                markerResultList.add(markerItem);
            }

            if (roamingType > 0) {
            	networkResult.add(generateMeasurementItem(messageSource.getMessage("MARKER_ROAMING", null, locale), messageSource.getMessage(Helperfunctions.getRoamingTypeKey(roamingType), null, locale)));
            }
        }

        ret.setNetworkResult(networkResult);
        return ret;
    }

    /**
     * Adds a new JSONObject to the list, filled w/the provided values
     *
     * @param title
     * @param value
     * @param classificationItem
     */
    private MarkerItem generateMeasurementItem(final String title, final String value, final ClassificationHelper.ClassificationItem classificationItem) {
        final MarkerItem singleItem = new MarkerItem();
        if (title != null) {
            singleItem.setTitle(title);
        }
        if (value != null) {
            singleItem.setValue(value);
        }
        if (classificationItem != null) {
            singleItem.setClassification(classificationItem.getClassificationNumber());
        }
        if (classificationItem != null && classificationItem.getClassificationColor() != null) {
            singleItem.setClassificationColor(classificationItem.getClassificationColor());
        }
        return singleItem;
    }

    private MarkerItem generateMeasurementItem(final String title, final String value) {
        return generateMeasurementItem(title, value, null);
    }
}
