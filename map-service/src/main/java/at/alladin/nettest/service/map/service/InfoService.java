package at.alladin.nettest.service.map.service;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import at.alladin.nettest.service.map.domain.model.info.MapAppearanceInfo;
import at.alladin.nettest.service.map.domain.model.info.MapFilters;
import at.alladin.nettest.service.map.domain.model.MapServiceOptions;
import at.alladin.nettest.service.map.domain.model.info.MapThresholdEntries;
import at.alladin.nettest.service.map.domain.model.MapTypes;
import at.alladin.nettest.service.map.domain.model.info.TechnologyFilter;
import at.alladin.nettest.service.map.domain.model.info.TechnologyTypes;
import at.alladin.nettest.service.map.domain.model.info.option.AbstractOption;
import at.alladin.nettest.service.map.domain.model.info.option.DeviceOption;
import at.alladin.nettest.service.map.domain.model.info.option.OperatorOption;
import at.alladin.nettest.service.map.domain.model.info.option.ProviderOption;
import at.alladin.nettest.service.map.domain.model.info.option.StatisticalOption;
import at.alladin.nettest.service.map.domain.model.info.option.TechnologyOption;
import at.alladin.nettest.service.map.domain.model.info.option.TimePeriodOption;
//import at.alladin.nettest.shared.model.Device;
//import at.alladin.nettest.shared.model.Provider;
import at.alladin.nettest.shared.server.helper.ClassificationHelper;
import at.alladin.nettest.shared.server.model.ServerSettings.ColorThresholds;
import at.alladin.nettest.shared.server.model.ServerSettings.SpeedThresholds;

@Service
public class InfoService {

    private final Logger logger = LoggerFactory.getLogger(InfoService.class);

    private static final NumberFormat format = NumberFormat.getNumberInstance();

    private final static String MOBILE_PROVIDER_SQL = "SELECT name, shortname FROM ha_provider p WHERE p.map_filter=true"
            + " AND p.mcc_mnc IS NOT NULL ORDER BY shortname";  // allow mobile networks for wifi/browser

    private final static String GENERAL_PROVIDER_SQL = "SELECT name,mcc_mnc,shortname FROM ha_provider p WHERE p.map_filter=true"
            + " ORDER BY shortname";  // allow mobile networks for wifi/browser

    /**
     *
     */
    @Inject
    private MessageSource messageSource;

    @Inject
    private MapOptionsService mapOptionsService;
    
	@Inject
	private ThresholdsPerTechnologyHelperService thresholdsHelperService;

    /**
     *
     */
    @Inject
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

        technologyOptionList.add(new TechnologyOption("", getLocalizedMessage("MAP_FILTER_TECHNOLOGY_ANY", locale),
                getLocalizedMessage("MAP_FILTER_TECHNOLOGY_ANY", locale), true));
        technologyOptionList.add(new TechnologyOption("34", getLocalizedMessage("MAP_FILTER_TECHNOLOGY_3G_4G", locale),
                getLocalizedMessage("MAP_FILTER_TECHNOLOGY_3G_4G", locale)));
        technologyOptionList.add(new TechnologyOption("2", getLocalizedMessage("MAP_FILTER_TECHNOLOGY_2G", locale),
                getLocalizedMessage("MAP_FILTER_TECHNOLOGY_2G", locale)));
        technologyOptionList.add(new TechnologyOption("3", getLocalizedMessage("MAP_FILTER_TECHNOLOGY_3G", locale),
                getLocalizedMessage("MAP_FILTER_TECHNOLOGY_3G", locale)));
        technologyOptionList.add(new TechnologyOption("4", getLocalizedMessage("MAP_FILTER_TECHNOLOGY_4G", locale),
                getLocalizedMessage("MAP_FILTER_TECHNOLOGY_4G", locale)));

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

        /*
        final List<Provider> providerList = jdbcTemplate.query(MOBILE_PROVIDER_SQL, new ProviderRowMapper());
        providerList.forEach(p -> operatorOptions.add(new OperatorOption(p.getShortname(), p.getName(), p.getName())));
         */

        ret.setOptions(operatorOptions);
        return ret;
    }

    public TechnologyFilter getProviderFilter(final Locale locale) {
    	//The provider here is using the same name as the operator filter (above), but provides a different key (this one is only used for browsers, instead of the operatorFilter)
        final TechnologyFilter ret = new TechnologyFilter(getLocalizedMessage("MAP_FILTER_CARRIER", locale), "provider");

        final List<AbstractOption> providerOptions = new ArrayList<>();

        providerOptions.add(new ProviderOption(getLocalizedMessage("MAP_FILTER_ALL_OPERATORS", locale), "", "", true));

        /*
        final List<Provider> providerList = jdbcTemplate.query(GENERAL_PROVIDER_SQL, new ProviderRowMapper());
        providerList.forEach((Provider p) -> providerOptions.add(new ProviderOption(p.getShortname(), p.getName(), p.getName())));
        */

        ret.setOptions(providerOptions);
        return ret;

    }

    /**
     * Function still has plenty of TODO entries left, however it is currently not in use
     * These TODO entries need be resolved however, before someone uses the device filter
     * @param type
     * @param locale
     * @return
     */
    public TechnologyFilter getDeviceFilter(final String type, final Locale locale){
        final TechnologyFilter ret = new TechnologyFilter(getLocalizedMessage("MAP_FILTER_DEVICE", locale) ,"device");

        final List<AbstractOption> deviceOptions = new ArrayList<>();

        deviceOptions.add(new DeviceOption(getLocalizedMessage("MAP_FILTER_ALL_DEVICES", locale), "", "", true));

    	/*
        String typeFilter = "";
        if (type != null)
            if ("mobile".equals(type))
                typeFilter = " AND network_type not in (0, 97, 98, 99)";
            else if ("wifi".equals(type))
                typeFilter = " AND network_type = 99";
            else if ("browser".equals(type))
                typeFilter = " AND network_type = 98";

        final PreparedStatement ps = conn
                .prepareStatement(String
                        .format("SELECT string_agg(DISTINCT s.model,';') keys," +
                        		" COALESCE(adm.fullname, s.model) val" +
                        		" FROM" +
                        		" (SELECT DISTINCT model FROM test t " +
                        		" WHERE t.deleted = false AND t.implausible = false " +
                        		" AND t.status = 'FINISHED'" +
                        		" AND t.model IS NOT NULL" +
                        		" %s) s" +
                        		" LEFT JOIN device_map adm ON adm.codename=s.model" +
                        		" GROUP BY val ORDER BY val ASC",
                                typeFilter));

        */

        //TODO: joins happening, repo access more involved
        /*
        final List<Device> deviceList = new ArrayList<>();	//TODO: obtain this list from some repo

        final String summary = getLocalizedMessage("MAP_FILTER_DEVICE_SUMMARY", locale);
        for (Device d : deviceList) {
            //TODO: code the coalesces (or use them during DB access)
            deviceOptions.add(new DeviceOption(d.getFullname(), String.format("%s %s", summary, d.getFullname()), d.getId()));
        }
        */

    	/*
        final String summary = labels.getString("MAP_FILTER_DEVICE_SUMMARY");
        while (rs.next()) {
            final JSONObject obj2 = new JSONObject();
            options.put(obj2);
            final String modelValue = rs.getString("val");
            obj2.put("title", modelValue);
            obj2.put("summary", String.format("%s %s", summary, modelValue));
            obj2.put("device", rs.getString("keys"));
        }
        */

        ret.setOptions(deviceOptions);

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
    /*
    private class ProviderRowMapper implements RowMapper<Provider> {

        @Override
        public Provider mapRow(ResultSet rs, int rowNum) {
            Provider prov = new Provider();
            try {
                prov.setShortname(rs.getString("shortname"));
                prov.setName(rs.getString("name"));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return prov;
        }
    }
    */
}
