package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.MeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementTypeParameters;
import at.alladin.nettest.spring.data.couchdb.core.mapping.Document;

@Document("TaskConfiguration")
public class TaskConfigurationSpeed extends TaskConfiguration {
	
	@Expose
	@SerializedName("measurement_parameters")
	private SpeedMeasurementTypeParameters measurementParameters;
	
	@Override
	public MeasurementTypeParameters getMeasurementParameters() {
		return measurementParameters;
	}

	public void setMeasurementParameters(SpeedMeasurementTypeParameters measurementParameters) {
		this.measurementParameters = measurementParameters;
	}
	
}
