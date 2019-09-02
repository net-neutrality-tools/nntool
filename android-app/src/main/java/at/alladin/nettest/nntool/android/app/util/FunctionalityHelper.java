package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class FunctionalityHelper {

    static Set<QosMeasurementType> unavailableQoSTypes = null;

    /**
     *
     * @param isAvailableType
     * @param context
     * @return true if the given type is allowed to be executed on this android device
     */
    public static boolean isQoSTypeAvailable(final QosMeasurementType isAvailableType, final Context context) {
        if (unavailableQoSTypes == null) {
            unavailableQoSTypes = new HashSet<>();
            final String[] unavailableQosTypeStrings = context.getResources().getStringArray(R.array.functionality_unavailable_qos_types);
            for (String type : unavailableQosTypeStrings) {
                try {
                    unavailableQoSTypes.add(QosMeasurementType.fromValue(type.toLowerCase()));
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return !unavailableQoSTypes.contains(isAvailableType);
    }

    public static boolean isQoSTypeAvailable(final QoSMeasurementTypeDto isAvailableType, final Context context) {
        try {
            return isQoSTypeAvailable(QosMeasurementType.fromValue(isAvailableType.name().toLowerCase()), context);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
