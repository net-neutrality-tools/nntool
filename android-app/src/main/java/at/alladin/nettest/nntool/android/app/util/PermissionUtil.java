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
