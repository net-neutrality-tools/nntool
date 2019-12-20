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

import java.util.UUID;

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
	UuidMapperImpl.class
})
public class UuidMapperTest {

	@Autowired
	private UuidMapper uuidMapper;
	
	@Test
	public void testMapNullStringToUuid() {
		assertNull(uuidMapper.map((String) null));
	}
	
	@Test
	public void testMapStringToUuid() {
		final String uuidV4 = "134cf52c-fcd5-41dc-bfad-59aaa6f0f1fb";
		
		final UUID uuid = uuidMapper.map(uuidV4);
		
		assertEquals(uuidV4, uuid.toString());
	}
	
	@Test
	public void testMapNullUuidToString() {
		assertNull(uuidMapper.map((UUID) null));
	}
	
	@Test
	public void testMapJodaDateTimeToJdk8DateTime() {
		final String uuidV4 = "157e139a-2680-4472-a781-a11014131056";
		
		final UUID uuid = UUID.fromString(uuidV4); 
		
		final String mappedUuidV4 = uuidMapper.map(uuid);
		
		assertEquals(uuidV4, mappedUuidV4);
	}
}
