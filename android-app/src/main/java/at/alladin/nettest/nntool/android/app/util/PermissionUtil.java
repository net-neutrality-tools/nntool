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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class PermissionUtil {

    public final static int REQUEST_CODE_LOCATION = 1;

    public final static String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public static boolean isLocationPermissionGranted(final Context context) {
        return isPermissionGranted(context, LOCATION_PERMISSIONS);
    }

    public static void requestLocationPermission(final Activity activity) {
        ActivityCompat.requestPermissions(activity, LOCATION_PERMISSIONS, REQUEST_CODE_LOCATION);
    }

    private static boolean isPermissionGranted(final Context context, final String[] requestedPermissions) {
        if (requestedPermissions == null || requestedPermissions.length == 0) {
            return true;
        }

        for (final String permission : requestedPermissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}
