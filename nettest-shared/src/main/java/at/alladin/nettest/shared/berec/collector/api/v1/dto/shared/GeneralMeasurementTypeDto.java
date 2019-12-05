package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * General Measurement type DTO.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "General Measurement type DTO.")
@JsonClassDescription("General Measurement type DTO.")
public enum GeneralMeasurementTypeDto {
	SPEED,
	
	TCP,
    UDP,
    DNS,
    NON_TRANSPARENT_PROXY,
    HTTP_PROXY,
    VOIP,
    TRACEROUTE,
    WEBSITE,
    SIP,

    MKIT_DASH,
    MKIT_WEB_CONNECTIVITY;
}
