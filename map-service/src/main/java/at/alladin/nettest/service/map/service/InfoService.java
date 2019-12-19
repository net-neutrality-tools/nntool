package at.alladin.nettest.service.map.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.map.domain.model.MapServiceOptions;
import at.alladin.nettest.service.map.domain.model.MapTypes;
import at.alladin.nettest.service.map.domain.model.Provider;
import at.alladin.nettest.shared.server.helper.ClassificationHelper;
import at.alladin.nettest.shared.server.model.ServerSettings.ColorThresholds;
import at.alladin.nettest.shared.server.model.ServerSettings.SpeedThresholds;
import at.alladin.nntool.shared.map.info.MapAppearanceInfo;
import at.alladin.nntool.shared.map.info.MapFilters;
import at.alladin.nntool.shared.map.info.MapThresholdEntries;
import at.alladin.nntool.shared.map.info.TechnologyFilter;
import at.alladin.nntool.shared.map.info.TechnologyTypes;
import at.alladin.nntool.shared.map.info.option.AbstractOption;
import at.alladin.nntool.shared.map.info.option.OperatorOption;
import at.alladin.nntool.shared.map.info.option.ProviderOption;
import at.alladin.nntool.shared.map.info.option.StatisticalOption;
import at.alladin.nntool.shared.map.info.option.TechnologyOption;
import at.alladin.nntool.shared.map.info.option.TimePeriodOption;

@Service
public class InfoService {

    private final Logger logger = LoggerFactory.getLogger(InfoService.class);

    private static final NumberFormat format = NumberFormat.getNumberInstance();

    private final static String MOBILE_PROVIDER_SQL = "SELECT p.name as name, p.short_name as short_name FROM providers p WHERE " //(potential TODO:) readd filtering by some providers only? "p.map_filter=true"
            + " p.mcc_mnc_mappings IS NOT NULL ORDER BY p.short_name";  // allow mobile networks for wifi/browser

    private final static String GENERAL_PROVIDER_SQL = "SELECT p.name as name," + /*"mcc_mnc," + */ " p.short_name as short_name FROM providers p"
    		//mcc mnc was read from db but never used
    		//+ "WHERE p.map_filter=true"
            + " ORDER BY p.short_name";  // allow mobile networks for wifi/browser

    /**
     *
     */
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MapOptionsService mapOptionsService;
    
    @Autowired
	private ThresholdsPerTechnologyHelperService thresholdsHelperService;

    /**
     *
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     *
     * @return
     * @throws JSONException
     * @throws SQLException
     */
    public MapFilters getMapFilter(final Locale locale) {
        final MapFilters ret = new MapFilters();

        final TechnologyFilter statisticalMethodFilter = getStatisticalFilter(locale);
        final TechnologyFilter timePeriodFilter = getTimeFilter(locale);
        final TechnologyFilter operatorFilter = getOperatorFilter(locale);
        final TechnologyFilter providerFilter = getProviderFilter(locale);
        final TechnologyFilter mobileTechnologyFilter = getMobileTechnologyFilter(locale);

        List<TechnologyFilter> filterList = ret.getMobileTechnolgyFilter();
        filterList.add(statisticalMethodFilter);
        filterList.add(operatorFilter);
        filterList.add(timePeriodFilter);
        filterList.add(mobileTechnologyFilter);
        //mob.add(getDeviceFilter("mobile", locale);

        filterList = ret.getWifiTechnologyFilter();
        filterList.add(statisticalMethodFilter);
        filterList.add(providerFilter);
        filterList.add(timePeriodFilter);
        //wifi.add(getDeviceFilter("wifi", locale);

        filterList = ret.getBrowserTechnolgyFilter();
        filterList.add(statisticalMethodFilter);
        filterList.add(providerFilter);
        filterList.add(timePeriodFilter);
//      browser.add(getDeviceFilter("browser", locale));


        filterList = ret.getAllTechnolgyFilter();
        filterList.add(statisticalMethodFilter);
        filterList.add(timePeriodFilter);

        return ret;
    }

    public MapTypes getMapTypes(final Locale locale) {
        final MapTypes ret = new MapTypes();


        final SpeedThresholds thresholds = thresholdsHelperService.getThresholdsPerTechnology();
        final Set<String> groupnameKeys = thresholdsHelperService.getGroupnameSet();

        String lastType = null;
        List<MapAppearanceInfo> appearanceInfo = null;

        for (final String key : mapOptionsService.getMapOptionKeys()) {
            final MapServiceOptions mapOption = mapOptionsService.getMapOptionsForKey(key);
            final String[] split = key.split("/");
            if (lastType == null || !lastType.equals(split[0])) {
                lastType = split[0];
                final TechnologyTypes techType = new TechnologyTypes();
                techType.setTitle(getLocalizedMessage(String.format("MAP_%s", lastType.toUpperCase()), locale));
                ret.getTechnolgyTypeList().add(techType);
                appearanceInfo = techType.getAppearanceInfoList();
            }

            final MapAppearanceInfo info = new MapAppearanceInfo();
            appearanceInfo.add(info);

            info.setMapOptions(key);
            final String type = split[1].toUpperCase();
            info.setSummary(getLocalizedMessage(String.format("MAP_%s_SUMMARY", type), locale));
            info.setTitle(getLocalizedMessage(String.format("RESULT_%s", type), locale));
            info.setUnit(getLocalizedMessage(String.format("RESULT_%s_UNIT", type), locale));
            info.setOverlayType(mapOption.getOverlayType());


            if(thresholds != null) {

                for(String groupname : groupnameKeys) {
                    //don't show groups that are not needed
                    if (Character.isDigit(groupname.charAt(0))) {
                        if(!key.toLowerCase().startsWith("mobile")) {
                            continue;
                        }
                    } else {
                        if(!groupname.toLowerCase().equals(key.substring(0, key.indexOf("/")).toLowerCase())) {
                            continue;
                        }
                    }

                    final ColorThresholds colors = thresholdsHelperService.getByClassificationType(thresholds, mapOption.getClassificationType());

                    if(colors == null || colors.getColorMap() == null || colors.getDefaultColor() == null) {
                        continue;
                    }

                    final MapThresholdEntries thresholdEntries = new MapThresholdEntries();

                    Set<Long> keySet = null;
                    if (mapOption.getClassificationType() == ClassificationHelper.ClassificationType.PING ) { // mapOption.classificationType == ClassificationType.SIGNAL) {
                        thresholdEntries.getThresholdColors().add(colors.getDefaultColor());
                        //elements with reverse ordering
                        keySet = colors.getColorMap().descendingKeySet();
                    } else {
                        keySet = colors.getColorMap().keySet();
                    }
                    if (mapOption.getClassificationType() != ClassificationHelper.ClassificationType.PING && mapOption.getClassificationType() != ClassificationHelper.ClassificationType.SIGNAL) {
                        //this is the lowest displayed value
                        thresholdEntries.getThresholdValues().add("0");
                    }

                    for (final Long colorKey : keySet) {
                        if (mapOption.getClassificationType() == ClassificationHelper.ClassificationType.PING || mapOption.getClassificationType() == ClassificationHelper.ClassificationType.SIGNAL) {
                            thresholdEntries.getThresholdValues().add(format.format(colorKey));
                        } else {
                            thresholdEntries.getThresholdValues().add(format.format(colorKey / 1000.0f));
                        }
                        thresholdEntries.getThresholdColors().add(colors.getColorMap().get(colorKey));
                    }

                    if (mapOption.getClassificationType() != ClassificationHelper.ClassificationType.PING) { // && mapOption.classificationType != ClassificationType.SIGNAL) {
                        thresholdEntries.getThresholdColors().add(colors.getDefaultColor());
                    }

                    if (mapOption.getClassificationType() == ClassificationHelper.ClassificationType.PING || mapOption.getClassificationType() == ClassificationHelper.ClassificationType.SIGNAL) {
                        //this is the lowest displayed value
                        thresholdEntries.getThresholdValues().add("0");
                    }

                    info.getThresholds().put(groupname == null ? "" : groupname, thresholdEntries);
                }

            }

        }

        return ret;
    }


    /**
     *
     * @param locale
     * @return
     */
    public TechnologyFilter getTimeFilter(final Locale locale) {
        final TechnologyFilter ret = new TechnologyFilter(getLocalizedMessage("MAP_FILTER_PERIOD", locale), "period");

        final List<AbstractOption> timePeriodOptionList = new ArrayList<>();

        timePeriodOptionList.add(new TimePeriodOption(getLocalizedMessage("MAP_FILTER_PERIOD_7_DAYS", locale),
                getLocalizedMessage("MAP_FILTER_PERIOD_7_DAYS", locale), 7));
        timePeriodOptionList.add(new TimePeriodOption(getLocalizedMessage("MAP_FILTER_PERIOD_30_DAYS", locale),
                getLocalizedMessage("MAP_FILTER_PERIOD_30_DAYS", locale), 30));
        timePeriodOptionList.add(new TimePeriodOption(getLocalizedMessage("MAP_FILTER_PERIOD_90_DAYS", locale),
                getLocalizedMessage("MAP_FILTER_PERIOD_90_DAYS", locale), 90));
        timePeriodOptionList.add(new TimePeriodOption(getLocalizedMessage("MAP_FILTER_PERIOD_180_DAYS", locale),
                getLocalizedMessage("MAP_FILTER_PERIOD_180_DAYS", locale), 180, true)); //180 days is the default option
        timePeriodOptionList.add(new TimePeriodOption(getLocalizedMessage("MAP_FILTER_PERIOD_365_DAYS", locale),
                getLocalizedMessage("MAP_FILTER_PERIOD_365_DAYS", locale), 365));
        timePeriodOptionList.add(new TimePeriodOption(getLocalizedMessage("MAP_FILTER_PERIOD_730_DAYS", locale),
                getLocalizedMessage("MAP_FILTER_PERIOD_730_DAYS", locale), 730));

        ret.setOptions(timePeriodOptionList);

        return ret;
    }

    public TechnologyFilter getStatisticalFilter (final Locale locale) {
        final TechnologyFilter ret = new TechnologyFilter(getLocalizedMessage("MAP_FILTER_STATISTICAL_METHOD", locale),
                "statistical_method");

        final List<AbstractOption> statisticalOptionList = new ArrayList<>();

        final double[] statisticalMethodArray = { 0.8, 0.5, 0.2 };
        for (int stat = 1; stat <= statisticalMethodArray.length; stat++) {
            final StatisticalOption opt = new StatisticalOption(getLocalizedMessage("MAP_FILTER_STATISTICAL_METHOD_" + stat + "_TITLE", locale),
                    getLocalizedMessage("MAP_FILTER_STATISTICAL_METHOD_" + stat + "_SUMMARY", locale), statisticalMethodArray[stat - 1]);
            if (stat == 2) { //2nd list entry is default (median)
                opt.setIsDefault(true);
            }
            statisticalOptionList.add(opt);
        }

        ret.setOptions(statisticalOptionList);

        return ret;
    }

    public TechnologyFilter getMobileTechnologyFilter(final Locale locale) {
        final TechnologyFilter ret = new TechnologyFilter(getLocalizedMessage("MAP_FILTER_TECHNOLOGY", locale), "technology");

        final List<AbstractOption> technologyOptionList = new ArrayList<>();

        technologyOptionList.add(new TechnologyOption(getLocalizedMessage("MAP_FILTER_TECHNOLOGY_ANY", locale),
                getLocalizedMessage("MAP_FILTER_TECHNOLOGY_ANY", locale), "",  true));
        technologyOptionList.add(new TechnologyOption(getLocalizedMessage("MAP_FILTER_TECHNOLOGY_3G_4G", locale), 
                getLocalizedMessage("MAP_FILTER_TECHNOLOGY_3G_4G", locale), "34"));
        technologyOptionList.add(new TechnologyOption(getLocalizedMessage("MAP_FILTER_TECHNOLOGY_2G", locale), 
                getLocalizedMessage("MAP_FILTER_TECHNOLOGY_2G", locale), "2"));
        technologyOptionList.add(new TechnologyOption(getLocalizedMessage("MAP_FILTER_TECHNOLOGY_3G", locale),
                getLocalizedMessage("MAP_FILTER_TECHNOLOGY_3G", locale), "3"));
        technologyOptionList.add(new TechnologyOption(getLocalizedMessage("MAP_FILTER_TECHNOLOGY_4G", locale),
                getLocalizedMessage("MAP_FILTER_TECHNOLOGY_4G", locale), "4"));

        ret.setOptions(technologyOptionList);

        return ret;
    }

    /**
     *
     * @param locale
     * @return
     * @throws SQLException
     */
    public TechnologyFilter getOperatorFilter(final Locale locale) {
        final TechnologyFilter ret = new TechnologyFilter(getLocalizedMessage("MAP_FILTER_CARRIER", locale), "operator");

        final List<AbstractOption> operatorOptions = new ArrayList<>();

        operatorOptions.add(new OperatorOption(getLocalizedMessage("MAP_FILTER_ALL_OPERATORS", locale), "", "", true));

        final List<Provider> providerList = jdbcTemplate.query(MOBILE_PROVIDER_SQL, new ProviderRowMapper());
        providerList.forEach(p -> operatorOptions.add(new OperatorOption(p.getShortname(), p.getName(), p.getName())));

        ret.setOptions(operatorOptions);
        return ret;
    }

    public TechnologyFilter getProviderFilter(final Locale locale) {
    	//The provider here is using the same name as the operator filter (above), but provides a different key (this one is only used for browsers, instead of the operatorFilter)
        final TechnologyFilter ret = new TechnologyFilter(getLocalizedMessage("MAP_FILTER_CARRIER", locale), "provider");

        final List<AbstractOption> providerOptions = new ArrayList<>();

        providerOptions.add(new ProviderOption(getLocalizedMessage("MAP_FILTER_ALL_OPERATORS", locale), "", "", true));

        
        final List<Provider> providerList = jdbcTemplate.query(GENERAL_PROVIDER_SQL, new ProviderRowMapper());
        providerList.forEach((Provider p) -> providerOptions.add(new ProviderOption(p.getShortname(), p.getName(), p.getName())));
        

        ret.setOptions(providerOptions);
        return ret;

    }

    /**
     *
     * @param key
     * @param locale
     * @return
     */
    private String getLocalizedMessage(final String key, final Locale locale) {
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (NoSuchMessageException ex) {
            logger.error("Could not find message with key {}", key, ex);
            return key;
        }
    }

    /**
     * helper class to allow easy db access for reading providers
     */
    
    private class ProviderRowMapper implements RowMapper<Provider> {

        @Override
        public Provider mapRow(ResultSet rs, int rowNum) {
            Provider prov = new Provider();
            try {
                prov.setShortname(rs.getString("short_name"));
                prov.setName(rs.getString("name"));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return prov;
        }
    }
    
}
