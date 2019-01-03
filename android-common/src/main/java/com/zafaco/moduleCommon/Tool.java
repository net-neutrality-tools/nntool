package com.zafaco.moduleCommon;

/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2019                                                          *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2019-01-03
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static android.telephony.TelephonyManager.SIM_STATE_ABSENT;
import static android.telephony.TelephonyManager.SIM_STATE_NETWORK_LOCKED;
import static android.telephony.TelephonyManager.SIM_STATE_PIN_REQUIRED;
import static android.telephony.TelephonyManager.SIM_STATE_PUK_REQUIRED;
import static android.telephony.TelephonyManager.SIM_STATE_READY;
import static android.telephony.TelephonyManager.SIM_STATE_UNKNOWN;

public class Tool
{
    /**
     *
     * @param msg String to Print
     */
    public void printOutput(String msg)
    {
        Date date = new Date();

        if(Common.DEBUG)
        {
            System.out.println("DEBUG: [" + new SimpleDateFormat("HH:mm:ss", Locale.GERMAN).format(date.getTime()) + "]: " + msg);
        }
    }

    /**
     *
     * @param map Map to Print
     */
    public void printHashMap(HashMap<String,String> map)
    {
        printOutput("===============================================");

        for (Map.Entry<String, String> entry : map.entrySet())
        {
            printOutput(entry.getKey() + " = " + entry.getValue());
        }

        printOutput("===============================================");
    }

    /**
     *
     * @param map Map to Print
     */
    public void printTreeMap(TreeMap<String,HashMap<String,String>> map)
    {
        printOutput("===============================================");

        for (Map.Entry<String, HashMap<String,String>> entry : map.entrySet())
        {
            printOutput(entry.getKey() + " => ");
            printHashMap(entry.getValue());
        }

        printOutput("===============================================");
    }

    /**
     *
     * @param ex Exception to Print
     */
    public void printTrace(Exception ex)
    {
        if(Common.DEBUG)
            ex.printStackTrace();
    }

    /**
     *
     * @param obj Map to Print
     */
    public void printJSONObject(JSONObject obj)
    {
        printOutput("===============================================");

        try
        {
            printOutput(obj.toString(3));
        }
        catch(Exception ex)
        {
            printTrace(ex);
        }

        printOutput("===============================================");
    }

    public void printDatabase(Cursor cData)
    {
        DatabaseUtils.dumpCursor(cData);
    }

    public JSONObject mergeJSON(JSONObject object1, JSONObject object2)
    {
        try
        {
            for (Iterator<String> iter = object2.keys(); iter.hasNext(); )
            {
                String key = iter.next();
                object1.put(key,object2.get(key));
            }
        }
        catch(Exception ex) {}

        return object1;
    }

    public String getCursorValue(Cursor cCursor, String sColumn)
    {
        return cCursor.getString(cCursor.getColumnIndexOrThrow(sColumn));
    }

    public TreeMap<String, HashMap<String, String>> dumpCursorToMap(Cursor cCursor)
    {
        TreeMap map = new TreeMap<>();

        while (cCursor.moveToNext())
        {
            LinkedHashMap<String, String> columns = new LinkedHashMap<>();

            for (int i = 0; i < cCursor.getColumnCount(); i++)
            {
                //printOutput("DB["+getCursorValue(cCursor,"timestamp")+"]: " + cCursor.getColumnName(i) + ":" + cCursor.getString(i));
                columns.put(cCursor.getColumnName(i), cCursor.getString(i));
            }
            map.put(getCursorValue(cCursor,"timestamp"),columns);
        }

        printTreeMap(map);

        return map;
    }

    public String generateInstallationId()
    {
        String clientInstallationId = null;

        String sTimestamp = String.valueOf(System.currentTimeMillis());

        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] encodedhash = digest.digest(sTimestamp.getBytes(StandardCharsets.UTF_8));

            clientInstallationId = bytesToHex(encodedhash);
        }
        catch(Exception ex) { printTrace(ex); }

        return clientInstallationId;
    }

    public String getNetType(int netType)
    {
        switch (netType) 
        {
            case TelephonyManager.NETWORK_TYPE_GPRS:    return "GPRS";                              //1
            case TelephonyManager.NETWORK_TYPE_EDGE:    return "EDGE";                              //2
            case TelephonyManager.NETWORK_TYPE_UMTS:    return "UMTS";                              //3
            case TelephonyManager.NETWORK_TYPE_CDMA:    return "CDMA";                              //4
            case TelephonyManager.NETWORK_TYPE_EVDO_0:  return "EVDO revision 0";                   //5
            case TelephonyManager.NETWORK_TYPE_EVDO_A:  return "EVDO revision A";                   //6
            case TelephonyManager.NETWORK_TYPE_1xRTT:   return "1xRTT";                             //7
            case TelephonyManager.NETWORK_TYPE_HSDPA:   return "HSDPA";                             //8
            case TelephonyManager.NETWORK_TYPE_HSUPA:   return "HSUPA";                             //9
            case TelephonyManager.NETWORK_TYPE_HSPA:    return "HSPA";                              //10
            case TelephonyManager.NETWORK_TYPE_IDEN:    return "iDen";                              //11
            case TelephonyManager.NETWORK_TYPE_EVDO_B:  return "EVDO revision B";                   //12
            case TelephonyManager.NETWORK_TYPE_LTE:     return "LTE";                               //13
            case TelephonyManager.NETWORK_TYPE_EHRPD:   return "eHRPD";                             //14
            case TelephonyManager.NETWORK_TYPE_HSPAP:   return "HSPA+";                             //15
            case TelephonyManager.NETWORK_TYPE_GSM:     return "GSM";                               //16
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:return "TD_SCDMA";                          //17
            case TelephonyManager.NETWORK_TYPE_IWLAN:   return "IWLAN";                             //18
            case TelephonyManager.NETWORK_TYPE_UNKNOWN: return "unknown";                           //0
            default: return "unknown";
        }
    }

    public String getVoicetype(int netType)
    {
        switch (netType)
        {
            case ServiceState.STATE_OUT_OF_SERVICE:     return "STATE_OUT_OF_SERVICE";
            case ServiceState.STATE_EMERGENCY_ONLY:     return "STATE_EMERGENCY_ONLY";
            case ServiceState.STATE_IN_SERVICE:         return "STATE_IN_SERVICE";
            case ServiceState.STATE_POWER_OFF:          return "STATE_POWER_OFF";
            default: return "STATE_UNKNOWN";
        }
    }

    public String getPhoneType(int phoneType)
    {
        switch (phoneType) {
            case TelephonyManager.PHONE_TYPE_NONE:
                return "None";
            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM";
            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.PHONE_TYPE_SIP:
                return "SIP";
            default:
                return "Unknown";
        }
    }

    public String setCategory(int id)
    {
        switch(id)
        {
            case 0:
                return "unknown";
            case 1:
            case 2:
            case 4:
                return "2G";
            case 3:
            case 8:
            case 9:
            case 10:
            case 15:
                return "3G";
            case 13:
                return "4G";
            case 18:
                return "WLAN";
        }

        return null;
    }

    public String mappingProvider(String app_operator_sim_mnc)
    {
        if( app_operator_sim_mnc == null )
            return "-";

        switch(app_operator_sim_mnc)
        {
            case "01":
            case "06": return "Telekom.de";
            case "02":
            case "04":
            case "09": return "Vodafone.de";
            case "03":
            case "05":
            case "07":
            case "08":
            case "11":
            case "77": return "o2 - de";
            default: return "-";
        }
    }

    /**
     * Check if User is on Wlan
     * @return true if online, else false
     */
    public boolean isWifi(Context ctx)
    {
        ConnectivityManager connMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mWifi = Objects.requireNonNull(connMan).getActiveNetworkInfo();

        if( mWifi != null )
            return mWifi.getType() == ConnectivityManager.TYPE_WIFI && mWifi.isConnected();
        else
            return false;
    }

    /**
     * Check if User is on Mobile
     * @return true if online, else false
     */
    public boolean isMobile(Context ctx)
    {
        ConnectivityManager connMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mMobile = Objects.requireNonNull(connMan).getActiveNetworkInfo();

        if( mMobile != null )
            return mMobile.getType() == ConnectivityManager.TYPE_MOBILE && mMobile.isConnected();
        else
            return false;

    }

    /**
     * Check if User is on Mobile
     * @return true if online, else false
     */
    public boolean isEthernet(Context ctx)
    {
        ConnectivityManager connMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mEthernet = Objects.requireNonNull(connMan).getActiveNetworkInfo();

        if( mEthernet != null )
            return mEthernet.getType() == ConnectivityManager.TYPE_ETHERNET && mEthernet.isConnected();
        else
            return false;

    }

    /**
     * Check if User is on Roaming
     * @return true if online, else false
     */
    public boolean isRoaming(Context ctx)
    {
        ConnectivityManager connMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mMobile = Objects.requireNonNull(connMan).getActiveNetworkInfo();

        return mMobile.getType() == ConnectivityManager.TYPE_MOBILE && mMobile.isRoaming();

    }

    /**
     * Check if User is online
     * @return true if online, else false
     */
    public boolean isOnline(Context ctx)
    {
        ConnectivityManager connMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = Objects.requireNonNull(connMan).getActiveNetworkInfo();

        return networkInfo.isConnected();

    }

    public int isSimReady(Context ctx)
    {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        int SIM_State = -1;

        int simState = tm.getSimState();

        switch (simState)
        {
            case SIM_STATE_ABSENT:
                SIM_State = SIM_STATE_ABSENT;
                // do something
                break;
            case SIM_STATE_NETWORK_LOCKED:
                SIM_State = SIM_STATE_NETWORK_LOCKED;
                // do something
                break;
            case SIM_STATE_PIN_REQUIRED:
                SIM_State = SIM_STATE_PIN_REQUIRED;
                // do something
                break;
            case SIM_STATE_PUK_REQUIRED:
                SIM_State = SIM_STATE_PUK_REQUIRED;
                // do something
                break;
            case SIM_STATE_READY:
                SIM_State = SIM_STATE_READY;
                // do something
                break;
            case SIM_STATE_UNKNOWN:
                SIM_State = SIM_STATE_UNKNOWN;
                // do something
                break;
        }

        return SIM_State;
    }

    public boolean isCalling(Context context)
    {
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        assert manager != null;
        return manager.getMode() == AudioManager.MODE_IN_CALL;
    }

    public boolean isAirplane(Context context)
    {
        return Settings.Global.getInt(context.getContentResolver(),Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public boolean isServiceRunning(Class<?> serviceClass, Context ctx)
    {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean existsFile(Context context, String dbName)
    {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    private HashMap<String, String> getCpuStats(HashMap<String, String> cpuload)
    {
        try
        {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" ");

            long idle = Long.parseLong(toks[5]);
            long cpu = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            if(cpuload.size() != 0)
            {
                long idlebefore = Long.parseLong(cpuload.get("idle"));
                long cpubefore = Long.parseLong(cpuload.get("cpu"));

                cpuload.put("usage","" + (float) (cpu - cpubefore) / ((cpu + idle) - (cpubefore + idlebefore)));

                return cpuload;
            }

            cpuload.put("cpu",""+cpu);
            cpuload.put("idle",""+idle);

        }
        catch (IOException ex) {}

        return cpuload;
    }


    private static String getStorage(Context ctx, String sDevice, String sType)
    {
        Environment4.Device test[];

        test = Environment4.getDevices(ctx);

        for (Environment4.Device d : test)
        {
            if(sDevice.equals("internal") && d.isPrimary())
            {
                switch (sType)
                {
                    case "total": return ""+d.getTotalSpace();
                    case "free": return ""+d.getFreeSpace();
                    case "used": return ""+(d.getTotalSpace()-d.getFreeSpace());
                }
            }
            if(sDevice.equals("external") && d.isRemovable())
            {
                switch (sType)
                {
                    case "total": return ""+d.getTotalSpace();
                    case "free": return ""+d.getFreeSpace();
                    case "used": return ""+(d.getTotalSpace()-d.getFreeSpace());
                }

            }
        }

        return "0";
    }

    public float getUptimeStats()
    {
        try
        {
            RandomAccessFile reader = new RandomAccessFile("/proc/uptime", "r");
            String load = reader.readLine();

            String[] toks = load.split(" ");

            return Float.parseFloat(toks[0]);
        }
        catch (IOException ex) { printTrace(ex); }

        return 0;
    }

    public float getBatteryStats(Context ctx)
    {
        Intent batteryIntent = ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        if( batteryIntent == null )
        {
            return 0;
        }

        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level==-1||scale==-1)

        {
            return 50.0f;
        }

        return((float)level/(float)scale)*100.0f;
    }

    /**
     * getWifiManagerData
     */
    public JSONObject getWifiManagerData(Context ctx)
    {
        JSONObject jData = new JSONObject();

        WifiManager wifiMan = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = Objects.requireNonNull(wifiMan).getConnectionInfo();

        try
        {
            jData.put("getBSSID", wifiInf.getBSSID());
            jData.put("getSSID", wifiInf.getSSID());
         }
        catch (Exception ex)
        {
            printTrace(ex);
        }

        return jData;
    }

    /**
     * Get installed Apps in a HashMap
     */
    public HashMap<String, String> checkInstalledApps(Context ctx)
    {
        HashMap<String, String> map = new HashMap<>();

        // Get list of installed apps
        PackageManager pm = ctx.getPackageManager();
        List<ApplicationInfo> installedApplications = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for(ApplicationInfo installedApplication : installedApplications)
        {
            map.put(installedApplication.loadLabel(pm).toString(),installedApplication.packageName);
        }

        return map;
    }

    /**
     * getTelephonyManagerData
     */
    JSONObject getTelephonyManagerData(Context ctx)
    {
        JSONObject object = new JSONObject();

        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        try
        {
            //object.put("app_imei", tm.getDeviceId());
            //object.put("app_imsi", tm.getSubscriberId());
            //object.put("app_getDeviceSoftwareVersion", tm.getDeviceSoftwareVersion());
            //object.put("app_getNetworkCountryIso", tm.getNetworkCountryIso());
            //object.put("app_getNetworkOperator", tm.getNetworkOperator());
            //object.put("app_getSimCountryIso", tm.getSimCountryIso());
            //object.put("app_getSimOperator", tm.getSimOperator());
            //object.put("app_getSimSerialNumber", tm.getSimSerialNumber());
            //object.put("app_getCallState", "" + tm.getCallState());
            //object.put("app_getDataState", "" + tm.getDataState());
            //object.put("app_getPhoneType", "" + tm.getPhoneType());
            //object.put("app_getSimState", "" + tm.getSimState());

            object.put("app_mode", (isMobile(ctx) ? "WWAN" : "WIFI" ));
            object.put("app_access", getNetType(tm.getNetworkType()));
            object.put("app_access_id", "" + tm.getNetworkType());
            //object.put("app_operator_net", tm.getNetworkOperatorName());
            //object.put("app_operator_sim", tm.getSimOperatorName());

        }
        catch (Exception ex)
        {
            printTrace(ex);
        }

        return object;
    }

    /**
     * getDeviceData
     */
    JSONObject getDeviceData(Context ctx)
    {
        JSONObject object = new JSONObject();

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        String android_id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);

        try
        {
            HashMap<String,String> cpuload = new HashMap<>();

            cpuload = getCpuStats(cpuload);

            Thread.sleep(1000);

            cpuload = getCpuStats(cpuload);

            /*
            object.put("client_system", Build.VERSION.RELEASE);
            object.put("client_api", Build.VERSION.SDK_INT);
            object.put("client_memory_total", "" + mi.totalMem);
            object.put("client_memory_available", "" + mi.availMem);
            object.put("client_memory_used", "" + (mi.totalMem - mi.availMem));
            object.put("client_disk_total", getStorage(ctx, "internal","total") );
            object.put("client_disk_available", getStorage(ctx, "internal","free") );
            object.put("client_disk_used", getStorage(ctx, "internal","used") );
            object.put("client_uptime", getUptimeStats());
            object.put("client_battery", getBatteryStats(ctx));
            object.put("client_disk_ext_total", getStorage(ctx, "external","total"));
            object.put("client_disk_ext_available", getStorage(ctx, "external","free"));
            object.put("client_disk_ext_used", getStorage(ctx, "external","used") );
            */

            object.put("app_manufacturer", Build.MANUFACTURER);
            object.put("app_manufacturer_id", android_id);
            object.put("app_manufacturer_version", Build.MODEL);

            object.put("app_cpu", cpuload.get("usage"));
            object.put("app_memory", Double.parseDouble(""+(mi.totalMem - mi.availMem)) / Double.parseDouble(""+mi.totalMem) );
            object.put("app_storage", Double.parseDouble(getStorage(ctx, "internal","used")) / Double.parseDouble(getStorage(ctx, "internal","total")) );

            object.put("app_country", Locale.getDefault().toString());

        }
        catch (Exception ex)
        {
            printTrace(ex);
        }

        return object;
    }

    public String tokb(String value, String type)
    {
        try
        {
            switch (type)
            {
                case "kB":
                    return "" + (int) Double.parseDouble(value);
                case "MB":
                    return "" + (int) (Double.parseDouble(value) * 1000);
                case "GB":
                    return "" + (int) (Double.parseDouble(value) * 1000000);

                case "Mbit/s":
                    return "" + Integer.parseInt(value) * 1000;
            }
        }
        catch(NumberFormatException nfe)
        {
            return "-1";
        }

        return "-1";
    }

    /**
     * get the sim card status
     */
    public boolean getSimCardState(Context ctx)
    {
        boolean simReady = false;

        try
        {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

            assert tm != null;

            if (tm.getSimState() == SIM_STATE_READY)
            {
                simReady = true;
            }
        }
        catch (Exception ex)
        {
            simReady = false;
        }

        return simReady;
    }

    private String bytesToHex(byte[] hash)
    {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void copyFiles(File src, File dst) throws IOException
    {
        try (InputStream in = new FileInputStream(src))
        {
            try (OutputStream out = new FileOutputStream(dst))
            {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0)
                {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public String formatStringToGUI(String value, int factor)
    {
        DecimalFormat df = new DecimalFormat("#0.00");

        Double d = Double.parseDouble(value)/factor;

        return ""+ df.format(d);
    }

    public String formatStringToGUI(String value, int factor, String unit)
    {
        DecimalFormat df = new DecimalFormat("#0.00");

        Double d = Double.parseDouble(value)/factor;

        return ""+ df.format(d) + " "+ unit;
    }

    public String formatStringToGUI(Double value, int factor, String unit)
    {
        DecimalFormat df = new DecimalFormat("#0.00");

        Double d = value/factor;

        return ""+ df.format(d) + " "+ unit;
    }

    public Object getObject(String s, Class<?> cls)
    {
        Gson gson = new Gson();

        return gson.fromJson(s, cls);
    }

    public String saveObject(Object o)
    {
        Gson gson = new Gson();

        return gson.toJson(o);
    }
}
