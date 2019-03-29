package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import java.util.UUID;

import org.mapstruct.Mapper;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Mapper(componentModel = "spring")
public interface UuidMapper {

	default UUID map(String s) {
		if (s == null) {
			return null;
		}
		
		return UUID.fromString(s);
	}
	
	default String map(UUID s) {
		if (s == null) {
			return null;
		}
		
		return s.toString();
	}
}
