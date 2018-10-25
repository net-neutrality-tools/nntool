package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * QoS measurement request options sent by the client.
 * For example, these could contain information about what QoS measurement types are available on the client. 
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "QoS measurement request options sent by the client. For example, these could contain information about what QoS measurement types are available on the client.")
@JsonClassDescription("QoS measurement request options sent by the client. For example, these could contain information about what QoS measurement types are available on the client.")
public class QoSMeasurementRequestOptions extends MeasurementTypeRequestOptions {

}
