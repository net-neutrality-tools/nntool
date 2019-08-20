package at.alladin.nettest.nntool.android.app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class ObjectMapperUtil {

    public static ObjectMapper createBasicObjectMapper() {
        return new ObjectMapper().registerModule(new JodaModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
