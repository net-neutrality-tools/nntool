package at.alladin.nntool.shared.qos.util;

import at.alladin.nettest.shared.model.qos.QosMeasurementObjective;

public class QosObjectiveHelper {

	public static void preProcess(final QosMeasurementObjective objective) {
		if (objective != null && objective.getType() != null) {
			switch (objective.getType()) {
			case SIP:
				SipTaskHelper.preProcess(objective);
				break;
			default:
				break;
			}
		}
	}
}
