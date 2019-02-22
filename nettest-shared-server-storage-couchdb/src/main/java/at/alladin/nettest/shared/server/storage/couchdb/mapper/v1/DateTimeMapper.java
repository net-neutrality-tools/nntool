package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import java.time.ZoneOffset;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface DateTimeMapper {

	default org.joda.time.LocalDateTime map(java.time.LocalDateTime ldt) {
		if (ldt == null) {
			return null;
		}
		
		return new org.joda.time.LocalDateTime(ldt.toInstant(ZoneOffset.UTC).toEpochMilli());
	}
	
	default java.time.LocalDateTime map(org.joda.time.LocalDateTime ldt) {
		if (ldt == null) {
			return null;
		}
		
		return java.time.LocalDateTime.of(
			ldt.getYear(),
			ldt.getMonthOfYear(),
			ldt.getDayOfMonth(),
			ldt.getHourOfDay(),
			ldt.getMinuteOfHour(),
			ldt.getSecondOfMinute(),
			ldt.getMillisOfSecond() * 1000000
		);
	}
}
