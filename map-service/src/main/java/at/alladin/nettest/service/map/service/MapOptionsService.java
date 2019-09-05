package at.alladin.nettest.service.map.service;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import at.alladin.nettest.service.map.config.MapCacheConfig;
import at.alladin.nettest.service.map.config.MapServiceSettingsConfig;
import at.alladin.nettest.service.map.domain.model.MapServiceOptions;
import at.alladin.nettest.service.map.domain.model.MapServiceSettings;
import at.alladin.nettest.service.map.domain.model.MapServiceOptions.SignalGroup;
import at.alladin.nettest.shared.server.helper.ClassificationHelper.ClassificationType;
import at.alladin.nettest.shared.server.model.ServerSettings;

@Service
public class MapOptionsService {
	
	private final Logger logger = LoggerFactory.getLogger(MapOptionsService.class);

    @Inject
    private SqlSettingsService sqlSettingsService;
    
    @Inject
    private MapServiceSettingsConfig mapServiceConfig;
    
    @Inject
    private ClassificationService classificationService;
    
    @Inject 
    private MapCacheConfig cacheconfig;

    private MapServiceSettings mapServiceSettings;

    @PostConstruct
    public void postConstruct() {
    	mapServiceSettings = new MapServiceSettings();
    	
	    //TODO: parse resultSet
	
    	setDefaultMapFilter();
    	setAccuracyMapFilter();
    	setMapFilterMap();
	    //TODO: read mapFilterMap from DB
	    generateMapFilterSettings();
    }

    public MapServiceOptions getMapOptionsForKey (final String key) {
        if (mapServiceSettings != null && mapServiceSettings.getMapFilterMap() != null
                && mapServiceSettings.getMapServiceOptions().containsKey(key)) {
            return mapServiceSettings.getMapServiceOptions().get(key);
        }
        return null;
    }

    public MapServiceSettings.MapFilter getMapFilterForKey (final String key) {
        if (mapServiceSettings != null && mapServiceSettings.getMapFilterMap() != null
                && mapServiceSettings.getMapFilterMap().containsKey(key)) {
            return mapServiceSettings.getMapFilterMap().get(key);
        }
        return null;
    }

    public List<MapServiceSettings.SQLFilter> getSqlFilterList (final Map<String, String> keyMap) {
        return getSqlFilterList(keyMap, false, false);
    }

    /**
     *
     * @param keyMap the map of to-be-used params
     * @param addDefaultFilters will add default parameters to the returned list
     * @param addAccuracyFilter will add accuracy filter to the returned list
     * @return all associated sqlfilters from the params
     */
    public List<MapServiceSettings.SQLFilter> getSqlFilterList (final Map<String, String> keyMap, final boolean addDefaultFilters, final boolean addAccuracyFilter) {
        final List<MapServiceSettings.SQLFilter> ret = new ArrayList<>();
        if (mapServiceSettings == null || mapServiceSettings.getMapFilterMap() == null) {
            return ret;
        }
        if (addDefaultFilters) {
            ret.addAll(getDefaultMapFilters());
        }
        if (addAccuracyFilter) {
            ret.add(getAccuracyMapFilter());
        }

        if (keyMap != null) {
            for (Map.Entry<String, String> entry : keyMap.entrySet()) {
                final MapServiceSettings.MapFilter fil = getMapFilterForKey(entry.getKey());
                if (fil != null) {
                    final MapServiceSettings.SQLFilter sqlFil = fil.getFilter(entry.getValue());
                    if (sqlFil != null) {
                        ret.add(sqlFil);
                    }
                }
            }
        }

        return ret;
    }
    
    public boolean isValidFilter (final String key) {
        return mapServiceSettings != null && mapServiceSettings.getMapFilterMap() != null
                && mapServiceSettings.getMapFilterMap().containsKey(key);
    }

    public MapServiceSettings.SQLFilter getAccuracyMapFilter () {
        return mapServiceSettings.getAccuracyMapFilter();
    }

    public List<MapServiceSettings.SQLFilter> getDefaultMapFilters () {
        return mapServiceSettings.getDefaultMapFilters();
    }

    public Set<String> getMapOptionKeys() {
        return mapServiceSettings.getMapServiceOptions().keySet();
    }

    public int getMaxZoomLevel() {
        return mapServiceConfig.getMaxZoomLevel();
    }

    public int getCacheIgnoreZoomLevel() {
        return cacheconfig.getCacheIgnoreZoomLevel();
    }

    public int getCacheStaleTimeInSeconds() {
        return cacheconfig.getCacheStaleSeconds();
    }

    public int getCacheExpiredTimeInSeconds() {
        return cacheconfig.getCacheExpireSeconds();
    }
    
    private void setDefaultMapFilter () {
	    //TODO: read default and accuracy filters from DB
	    mapServiceSettings.setDefaultMapFilters(Collections.unmodifiableList(new ArrayList<MapServiceSettings.SQLFilter>() {
	        {
	            add(new MapServiceSettings.SQLFilter(
	            		"(ias.implausible)::boolean = false AND ias.status = 'FINISHED'"));
	        }
	    }));
    }
    
    private void setAccuracyMapFilter () {
    	mapServiceSettings.setAccuracyMapFilter(new MapServiceSettings.SQLFilter("(t.geo_location_accuracy)::float < 2000"));
    }
    
    private void setMapFilterMap () {
    	//TODO: read mapFilterMap from DB
	    mapServiceSettings.setMapFilterMap(Collections.unmodifiableMap(new LinkedHashMap<String, MapServiceSettings.MapFilter>() {
	        {
	            put("operator", new MapServiceSettings.MapFilter()
	            {
	                @Override
	                public MapServiceSettings.SQLFilter getFilter(final String input)
	                {
	                    if (Strings.isNullOrEmpty(input))
	                        return null;
	                    if (input.equals("other"))
	                        return new MapServiceSettings.SQLFilter("t.mobile_sim_operator_name IS NULL");
	                    else
	                        return new MapServiceSettings.SQLFilter("t.mobile_sim_operator_name=?")
	                        {
	                            @Override
	                            public int fillParams(int i, final PreparedStatement ps) throws SQLException
	                            {
	                                ps.setString(i++, input);
	                                return i;
	                            }
	                        };
	                }
	            });
	
	            put("provider", new MapServiceSettings.MapFilter()
	            {
	                @Override
	                public MapServiceSettings.SQLFilter getFilter(final String input)
	                {
	                    if (Strings.isNullOrEmpty(input))
	                        return null;
	                    return new MapServiceSettings.SQLFilter("t.provider_name=?")
	                    {
	                        @Override
	                        public int fillParams(int i, final PreparedStatement ps) throws SQLException
	                        {
	                            ps.setString(i++, input);
	                            return i;
	                        }
	                    };
	                }
	            });
	
	            put("technology", new MapServiceSettings.MapFilter()
	            {
	                @Override
	                public MapServiceSettings.SQLFilter getFilter(final String input)
	                { // do not filter if empty
	                    if (Strings.isNullOrEmpty(input))
	                        return null;
	                    try
	                    {
	                        final int technology = Integer.parseInt(input);
	                        // use old numeric network type (replicate network_type_table here)
	                        if (technology == 2) {     // 2G
	                            return new MapServiceSettings.SQLFilter("(t.initial_network_type_id)::int in (1,2,4,5,6,7,11,12,14)");
	                        } else if (technology == 3) { // 3G
	                            return new MapServiceSettings.SQLFilter("(t.initial_network_type_id)::int in (3,8,9,10,15)");
	                        } else if (technology == 4) {// 4G
	                            return new MapServiceSettings.SQLFilter("(t.initial_network_type_id)::int = 13");
	                        } else if (technology == 34) {// 3G or 4G
	                            return new MapServiceSettings.SQLFilter("(t.initial_network_type_id)::int in (3,8,9,10,13,15)");
	                        } else {
	                            return null;
	                        }
	
				/* //alternative: use network_group_name
				return new SQLFilter("network_group_name=?")
				{
					@Override
					int fillParams(int i, final PreparedStatement ps) throws SQLException
					{ // convert 2 => '2G'
						ps.setString(i++, String.format("%dG", technology));
						return i;
					}
				};
				*/
	                    }
	                    catch (NumberFormatException e)
	                    {
	                        return null;
	                    }
	                }
	            });
	
	            put("period", new MapServiceSettings.MapFilter()
	            {
	                @Override
	                public MapServiceSettings.SQLFilter getFilter(final String input)
	                {
	                    if (Strings.isNullOrEmpty(input))
	                        return null;
	                    try
	                    {
	                        final int period = Integer.parseInt(input);
	                        if (period <= 0 || period > 730)
	                            return null;
	
	                        return new MapServiceSettings.SQLFilter("(t.start_time)::timestamp > NOW() - CAST(? AS INTERVAL)")
	                        {
	                            @Override
	                            public int fillParams(int i, final PreparedStatement ps) throws SQLException
	                            {
	                                ps.setString(i++, String.format("%d days", period));
	                                return i;
	                            }
	                        };
	                    }
	                    catch (NumberFormatException e)
	                    {
	                        return null;
	                    }
	                }
	            });
                    }
	    }));
    }
    
    private void generateMapFilterSettings () {
    	final ServerSettings settings = sqlSettingsService.getSettings();
    	final Map<String, MapServiceOptions> mapOptionList = new LinkedHashMap<String, MapServiceOptions>();
    	mapServiceConfig.getMapServiceOptions().forEach(opt -> {
    		try {
				mapOptionList.put(opt.getGroupKey() + "/" + opt.getTypeKey(), fillMapServiceOption(settings, opt));
			} catch (IllegalArgumentException | SQLException e) {
				logger.error("Invalid argument from config. Is skipped.");
				e.printStackTrace();
			}
    	}
    		);
            	
    	this.mapServiceSettings.setMapServiceOptions(mapOptionList); 
    }
    
    private MapServiceOptions fillMapServiceOption (final ServerSettings settings, final MapServiceOptions mapServiceOption) throws NumberFormatException, IllegalArgumentException, SQLException {
    	mapServiceOption.setClassificationType(ClassificationType.valueOf(mapServiceOption.getTypeKey().toUpperCase()));
		mapServiceOption.setSignalGroup(SignalGroup.valueOf(mapServiceOption.getGroupKey().toUpperCase()));
    	return mapServiceOption;
    }

}
