package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import org.mapstruct.Mapper;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Mapper(componentModel = "spring")
public interface DateTimeMapper {

	default org.joda.time.LocalDateTime map(java.time.LocalDateTime ldt) {
		if (ldt == null) {
			return null;
		}
	
		return new org.joda.time.LocalDateTime(
			ldt.getYear(),
			ldt.getMonthValue(),
			ldt.getDayOfMonth(),
			ldt.getHour(),
			ldt.getMinute(),
			ldt.getSecond(),
			ldt.getNano() / 1000000 // ?
		);
		
		//return new org.joda.time.LocalDateTime(ldt.toInstant(ZoneOffset.UTC).toEpochMilli());
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
			ldt.getMillisOfSecond() * 1000000 // ?
		);
	}
}
