package at.alladin.nettest.service.statistic.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import at.alladin.nettest.service.statistic.dto.filter.BasicFilterDto;
import at.alladin.nettest.service.statistic.dto.filter.ConnectionTypeFilterDto;
import at.alladin.nettest.service.statistic.dto.filter.FilterEntry;
import at.alladin.nettest.service.statistic.dto.filter.TimePeriodFilterDto;

/**
 * 
 * @author lb@alladin.at
 *
 */
@Service
public class FilterService {

	/**
	 * 
	 * @return
	 */
	public List<BasicFilterDto<?>> getFiltersForProviderStatistics() {
		final List<BasicFilterDto<?>> filters = new ArrayList<>();
		
		final TimePeriodFilterDto timeFilter = new TimePeriodFilterDto();
		timeFilter.setKey("period");
		//TODO: translations
		timeFilter.getOptions().add(new FilterEntry<>("24 Hours", (long) (24*60)));
		timeFilter.getOptions().add(new FilterEntry<>("1 Week", (long) (7*24*60)));
		timeFilter.getOptions().add(new FilterEntry<>("1 Month", (long) (30*7*24*60)));
		timeFilter.getOptions().add(new FilterEntry<>("3 Months", (long) (3*30*7*24*60)));
		timeFilter.getOptions().add(new FilterEntry<>("6 Months", (long) (6*30*7*24*60)));
		timeFilter.getOptions().add(new FilterEntry<>("12 Months", (long) (12*30*7*24*60)));
		timeFilter.setDefaultValue((long) (7*24*60));
		filters.add(timeFilter);
		
		final ConnectionTypeFilterDto connectionFiler = new ConnectionTypeFilterDto();
		connectionFiler.setKey("measurement_type");
		//TODO: translations
		connectionFiler.getOptions().add(new FilterEntry<>("Mobile", 1));
		connectionFiler.getOptions().add(new FilterEntry<>("Browser", 2));
		connectionFiler.getOptions().add(new FilterEntry<>("WIFI", 3));
		connectionFiler.setDefaultValue(1);
		filters.add(connectionFiler);
		
		return filters;
	}
}
