/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
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

package at.alladin.nettest.service.statistic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.statistic.web.api.v1.dto.ProviderStatisticsRequestParams;
import at.alladin.nettest.service.statistic.web.api.v1.dto.ProviderStatisticsRequestParams.MeasurementType;
import at.alladin.nettest.service.statistic.web.api.v1.dto.filter.BasicFilterDto;
import at.alladin.nettest.service.statistic.web.api.v1.dto.filter.ConnectionTypeFilterDto;
import at.alladin.nettest.service.statistic.web.api.v1.dto.filter.FilterEntry;
import at.alladin.nettest.service.statistic.web.api.v1.dto.filter.TimePeriodFilterDto;

/**
 * 
 * @author lb@alladin.at
 *
 */
@Service
public class FilterService {
	
	@Autowired
	private MessageSource messageSource;

	/**
	 * 
	 * @return
	 */
	public List<BasicFilterDto<?>> getFiltersForProviderStatistics(final ProviderStatisticsRequestParams request, final Locale locale) {
		final List<BasicFilterDto<?>> filters = new ArrayList<>();
		
		if (request == null) {
			//if request is null we need to provide default filters
			final TimePeriodFilterDto timeFilter = new TimePeriodFilterDto();
			timeFilter.setKey("period");
			//TODO: translations
			timeFilter.getOptions().add(new FilterEntry<>(messageSource.getMessage("statistics.filter.period.24hours", null, locale), (long) (24*60)));
			timeFilter.getOptions().add(new FilterEntry<>("1 Week", (long) (7*24*60)));
			timeFilter.getOptions().add(new FilterEntry<>("1 Month", (long) (30*7*24*60)));
			timeFilter.getOptions().add(new FilterEntry<>("3 Months", (long) (3*30*7*24*60)));
			timeFilter.getOptions().add(new FilterEntry<>("6 Months", (long) (6*30*7*24*60)));
			timeFilter.getOptions().add(new FilterEntry<>("12 Months", (long) (12*30*7*24*60)));
			timeFilter.setDefaultValue((long) (7*24*60));
			filters.add(timeFilter);
			
			final ConnectionTypeFilterDto connectionFiler = new ConnectionTypeFilterDto();
			connectionFiler.setKey("measurementType");
			//TODO: translations
			connectionFiler.getOptions().add(new FilterEntry<>("Mobile", MeasurementType.MOBILE));
			connectionFiler.getOptions().add(new FilterEntry<>("Browser", MeasurementType.BROWSER));
			connectionFiler.getOptions().add(new FilterEntry<>("WIFI", MeasurementType.WIFI));
			connectionFiler.setDefaultValue(MeasurementType.MOBILE);
			
			filters.add(connectionFiler);
		}
		else {
			//TODO: update filters
			final TimePeriodFilterDto timeFilter = new TimePeriodFilterDto();
			timeFilter.setKey("period");
			timeFilter.getOptions().add(new FilterEntry<>("24 Hours", (long) (24*60)));
			timeFilter.getOptions().add(new FilterEntry<>("1 Week", (long) (7*24*60)));
			timeFilter.getOptions().add(new FilterEntry<>("1 Month", (long) (30*7*24*60)));
			timeFilter.getOptions().add(new FilterEntry<>("3 Months", (long) (3*30*7*24*60)));
			timeFilter.getOptions().add(new FilterEntry<>("6 Months", (long) (6*30*7*24*60)));
			timeFilter.getOptions().add(new FilterEntry<>("12 Months", (long) (12*30*7*24*60)));
			timeFilter.setDefaultValue((long) (7*24*60));
			filters.add(timeFilter);
			
			final ConnectionTypeFilterDto connectionFiler = new ConnectionTypeFilterDto();
			connectionFiler.setKey("measurementType");
			connectionFiler.getOptions().add(new FilterEntry<>("Mobile", MeasurementType.MOBILE));
			connectionFiler.getOptions().add(new FilterEntry<>("Browser", MeasurementType.BROWSER));
			connectionFiler.getOptions().add(new FilterEntry<>("WIFI", MeasurementType.WIFI));
			connectionFiler.setDefaultValue(MeasurementType.MOBILE);
			filters.add(connectionFiler);
		}
		
		return filters;
	}
}
