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

package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
	DateTimeMapperImpl.class
})
public class DateTimeMapperTest {

	@Autowired
	private DateTimeMapper dateTimeMapper;
	
	@Test
	public void testMapNullJdk8DateTimeToJodaDateTime() {
		assertNull(dateTimeMapper.map((java.time.LocalDateTime) null));
	}
	
	@Test
	public void testMapJdk8DateTimeToJodaDateTime() {
		final int year = 2019;
		final int month = 2;
		final int dayOfMonth = 23;
		final int hour = 19;
		final int minute = 17;
		final int second = 37;
		final int nanoOfSecond = 600000000;
		
		final java.time.LocalDateTime jdk8LocalDateTime = java.time.LocalDateTime.of(
			year, month, dayOfMonth, hour, minute, second, nanoOfSecond
		);
		
		final org.joda.time.LocalDateTime jodaLocalDateTime = dateTimeMapper.map(jdk8LocalDateTime);

		assertEquals(year, 		   jodaLocalDateTime.getYear());
		assertEquals(month, 	   jodaLocalDateTime.getMonthOfYear());
		assertEquals(dayOfMonth,   jodaLocalDateTime.getDayOfMonth());
		assertEquals(hour, 		   jodaLocalDateTime.getHourOfDay());
		assertEquals(minute, 	   jodaLocalDateTime.getMinuteOfHour());
		assertEquals(second, 	   jodaLocalDateTime.getSecondOfMinute());
		assertEquals(nanoOfSecond, jodaLocalDateTime.getMillisOfSecond() * 1000000);
	}
	
	@Test
	public void testMapNullJodaDateTimeToJdk8DateTime() {
		assertNull(dateTimeMapper.map((org.joda.time.LocalDateTime) null));
	}
	
	@Test
	public void testMapJodaDateTimeToJdk8DateTime() {
		final int year = 2019;
		final int month = 2;
		final int dayOfMonth = 23;
		final int hour = 19;
		final int minute = 17;
		final int second = 37;
		final int nanoOfSecond = 600000000;
		
		final org.joda.time.LocalDateTime jodaLocalDateTime = new org.joda.time.LocalDateTime(
			year, month, dayOfMonth, hour, minute, second, nanoOfSecond / 1000000
		);
		
		final java.time.LocalDateTime jdk8LocalDateTime = dateTimeMapper.map(jodaLocalDateTime);
		
		assertEquals(year,	 	   jdk8LocalDateTime.getYear());
		assertEquals(month,	 	   jdk8LocalDateTime.getMonthValue());
		assertEquals(dayOfMonth,   jdk8LocalDateTime.getDayOfMonth());
		assertEquals(hour, 		   jdk8LocalDateTime.getHour());
		assertEquals(minute, 	   jdk8LocalDateTime.getMinute());
		assertEquals(second, 	   jdk8LocalDateTime.getSecond());
		assertEquals(nanoOfSecond, jdk8LocalDateTime.getNano());
	}
}
