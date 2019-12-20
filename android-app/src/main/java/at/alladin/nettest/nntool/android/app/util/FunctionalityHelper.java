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

package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nntool.shared.qos.QosMeasurementType;

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
