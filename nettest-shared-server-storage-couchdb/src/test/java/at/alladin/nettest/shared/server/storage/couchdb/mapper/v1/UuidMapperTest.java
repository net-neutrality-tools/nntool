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
