package at.alladin.nettest.qos.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import at.alladin.nettest.shared.model.Client;
//import at.alladin.nettest.shared.model.request.BasicRequest;
import at.alladin.rmbt.client.helper.RevisionHelper;

//TODO: rename
public class HelperFunctions {

    private static final String DEBUG_TAG = "HelperFunctions";

    private static final String PLATTFORM_NAME = "Android";

//    public static <T extends BasicRequest> T fillBasicInfo(Class<T> basicRequestClazz, Context ctx) {
//
//        final T basicRequest;
//        try {
//            basicRequest = basicRequestClazz.newInstance();
//
//            basicRequest.setPlatform(PLATTFORM_NAME);
//            basicRequest.setOsVersion(android.os.Build.VERSION.RELEASE + "(" + android.os.Build.VERSION.INCREMENTAL + ")");
//            basicRequest.setApiLevel(String.valueOf(android.os.Build.VERSION.SDK_INT));
//            basicRequest.setDevice(android.os.Build.DEVICE);
//            basicRequest.setModel(android.os.Build.MODEL);
//            basicRequest.setProduct(android.os.Build.PRODUCT);
//            basicRequest.setLanguage(Locale.getDefault().getLanguage());
//            basicRequest.setTimezone(TimeZone.getDefault().getID());
//            basicRequest.setSoftwareRevision(RevisionHelper.getVerboseRevision());
//
//            PackageInfo pInfo = null;
//
//            try {
//                pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
//            } catch (final PackageManager.NameNotFoundException e) {
//                // e1.printStackTrace();
//                Log.e(DEBUG_TAG, "version of the application cannot be found", e);
//            }
//
//            if (pInfo != null) {
//                basicRequest.setSoftwareVersionCode(pInfo.versionCode);
//                basicRequest.setSoftwareVersionName(pInfo.versionName);
//            }
//
//            basicRequest.setClientType(Client.ClientType.MOBILE);
//
//            return basicRequest;
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    public static String getUuid(final Context context) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString("qos_client_uuid", "");
    }

    public static void setUuid(final String uuid, final Context context) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString("qos_client_uuid", uuid).apply();
    }

    //TODO: is copy, remove one of them (android-app: Helperfunctions)
    /**
     * Obtains the default dns server for Android versions > LOLLIPOP
     * @param context
     * @return
     */
    public static List<InetAddress> getDnsServer (Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return null;
        }
        try {

            final List<InetAddress> priorityServersArrayList = new ArrayList<>();
            final List<InetAddress> serversArrayList = new ArrayList<>();

            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {

                // Iterate all networks
                // Notice that android LOLLIPOP or higher allow iterating multiple connected networks of SAME type
                for (Network network : connectivityManager.getAllNetworks()) {

                    final NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                    if (networkInfo.isConnected()) {

                        final LinkProperties linkProperties = connectivityManager.getLinkProperties(network);

                        boolean hasDefault = false;

                        for (RouteInfo route : linkProperties.getRoutes()) {
                            if (route.isDefaultRoute()) {
                                hasDefault = true;
                                break;
                            }
                        }

                        // Prioritize the DNS servers for link which have a default route
                        if (hasDefault) {
                            priorityServersArrayList.addAll(linkProperties.getDnsServers());
                        } else {
                            serversArrayList.addAll(linkProperties.getDnsServers());
                        }
                    }
                }
            }

            // Append secondary arrays only if priority is empty
            if (priorityServersArrayList.isEmpty()) {
                priorityServersArrayList.addAll(serversArrayList);
            }

            return priorityServersArrayList;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;

    }
}
