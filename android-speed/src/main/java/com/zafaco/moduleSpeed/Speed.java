package com.zafaco.moduleSpeed;

/*
 * *****************************************************************************
 *                                                                             *
 *       * * * * * *     ZAFACO GMBH     * * * * * *                           *
 *                                                                             *
 *       Website: http://www.zafaco.de                                         *
 *                                                                             *
 *       Copyright (C) 2018                                                    *
 *                                                                             *
 *       zafaco GmbH hereby disclaims all copyright interest in the program    *
 *       (which makes passes at compilers)                                     *
 *                                                                             *
 * *****************************************************************************
 */

import com.google.android.gms.location.LocationRequest;
import com.tns.JavaScriptImplementation;
import com.zafaco.moduleCommon.Common;
import com.zafaco.moduleCommon.Http;
import com.zafaco.moduleCommon.Tool;
import com.zafaco.moduleCommon.listener.ListenerWireless;
import com.zafaco.moduleCommon.interfaces.ModulesInterface;
import com.zafaco.moduleCommon.interfaces.GenericInterface;
import com.zafaco.moduleCommon.listener.ListenerGeoLocation;
import com.zafaco.moduleCommon.listener.ListenerNetwork;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

@JavaScriptImplementation(javaScriptFile = "app/index.mobile.js")
public class Speed
{
    /****************** Default Measurement Parameters ********************/
    private static String defaultPlatform                           = "mobile";

    private static boolean defaultPerformRttMeasurement             = true;
    private static boolean defaultPerformDownloadMeasuement         = true;
    private static boolean defaultPerformUploadMeasurement          = true;

    private static int defaultRouteToClientTargetPort               = 8080;

    /************************* Default Parameters *************************/

    private static String defaultIndexUrl                           = "";

    /***************************** Variables ******************************/

    private final Context ctx;

    //Module Objects
    private NativeScript nativeScript;
    private Common mCommon;
    private Tool mTool;
    private Http mHttp;
    private ListenerNetwork listenerNetwork;
    private ListenerGeoLocation listenerGeoLocation;
    private ListenerWireless listenerWireless;

    //Vars
    private boolean testRunning = false;
    private int min_time = 1000;
    private int min_distance = 1;

    private IntentFilter iFilter;
    private NSBroadcastReceiver nsReceiver;

    private boolean isDownloadRunning = false;
    private boolean isUploadRunning = false;

    /**************************** Public Functions ****************************/

    /**
     * Construtor
     * @param ctx Context of Speed
     */
    public Speed(Context ctx)
    {
        this.ctx = ctx;

        nativeScript = new NativeScript();

        mCommon = new Common(ctx);

        mTool = new Tool();

        mHttp = new Http();

        //Init Lib
        initSpeedParameter();
    }

    public void setDownloadStarted() throws Exception
    {
        isDownloadRunning = true;

        JSONObject jDataAdditional = Common.getJSONMTWSMeasurementAdditional();

        JSONObject jData = new JSONObject();
        jData.put("app_mode_download_start", jDataAdditional.getString("app_mode"));
        jData.put("app_access_id_download_start", jDataAdditional.getString("app_access_id"));
        jData.put("app_mode_download_changed", 0 );
        jData.put("app_access_id_download_changed", 0);
        jData.put("app_velocity_download_max", jDataAdditional.getInt("app_velocity") );

        Common.addToJSONMTWSMeasurementAdditional(jData);
    }

    public void setDownloadStopped()
    {
        isDownloadRunning = false;
    }

    public void setUploadStarted() throws Exception
    {
        isUploadRunning = true;

        JSONObject jDataAdditional = Common.getJSONMTWSMeasurementAdditional();

        JSONObject jData = new JSONObject();
        jData.put("app_mode_upload_start", jDataAdditional.getString("app_mode"));
        jData.put("app_access_id_upload_start", jDataAdditional.getString("app_access_id"));
        jData.put("app_mode_upload_changed", 0 );
        jData.put("app_access_id_upload_changed", 0);
        jData.put("app_velocity_upload_max", jDataAdditional.getInt("app_velocity") );

        Common.addToJSONMTWSMeasurementAdditional(jData);
    }

    public void setUploadStopped()
    {
        isUploadRunning = false;
    }

    private void initSpeedParameter()
    {
        try
        {
            JSONObject jsonMTWSMeasurement = new JSONObject();

            JSONArray jArray = new JSONArray();
            String jTmp = "peer-ias-de-01";
            jArray.put(jTmp);

            jsonMTWSMeasurement.put("cmd", "start");
            jsonMTWSMeasurement.put("platform", defaultPlatform);
            jsonMTWSMeasurement.put("wsTargets", jArray);
            jsonMTWSMeasurement.put("wsTargetsV4", jArray);
            jsonMTWSMeasurement.put("wsTargetsRtt", jArray);
            jsonMTWSMeasurement.put("wsTLD", "net-neutrality.tools");
            jsonMTWSMeasurement.put("wsTargetPort", 80);
            jsonMTWSMeasurement.put("wsWss", 0);
            jsonMTWSMeasurement.put("wsAuthToken", "placeholderToken");
            jsonMTWSMeasurement.put("wsAuthTimestamp", "placeholderTimestamp");

            jsonMTWSMeasurement.put("performRttMeasurement", defaultPerformRttMeasurement);
            jsonMTWSMeasurement.put("performDownloadMeasurement", defaultPerformDownloadMeasuement);
            jsonMTWSMeasurement.put("performUploadMeasurement", defaultPerformUploadMeasurement);

            jsonMTWSMeasurement.put("cookieId", false);

            Common.addToJSONMTWSMeasurement(jsonMTWSMeasurement);
        }
        catch(JSONException ex) { mTool.printTrace(ex); }
    }

    //Measurement Start/Stop -----------------------------------------------------------------------

    public void startMeasurement( Application app, GenericInterface interfaceCallback)
    {
        try
        {
            defaultIndexUrl = Common.getJSONMTWSMeasurement().getString("indexUrl");
        }
        catch (JSONException ex)
        {
            mTool.printTrace(ex);
        }

        //Settings ---------------------------------------------------------------------------------
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(app.getApplicationContext());

        //If "caching! = true, use cache - if false request new file and download it
        //if( !prefs.getBoolean("caching",false) )
        //{
        //    nativeScript.setDefaultIndexUrl(defaultIndexUrl);
        //}

        //------------------------------------------------------------------------------------------
        //Get Device Data
        mCommon.deviceData();

        //------------------------------------------------------------------------------------------
        //Start Network and GeoListener
        startListener();

        //------------------------------------------------------------------------------------------
        //LocalBroadcastReceiver
        iFilter = new IntentFilter();
        nsReceiver = new NSBroadcastReceiver(interfaceCallback);

        iFilter.addAction("tnsReportCallback");
        iFilter.addAction("tnsConsoleLogCallback");
        LocalBroadcastManager.getInstance(ctx).registerReceiver(nsReceiver, iFilter);

        //------------------------------------------------------------------------------------------
        //Write Settings to File -> Native Script needs it before calling
        writeToFile();

        //------------------------------------------------------------------------------------------
        //Native Script
        nativeScript.startMeasurement(app);

        //------------------------------------------------------------------------------------------
    }

    public void stopMeasurement()
    {
        //Init Main JSON Object
        initSpeedParameter();

        //Stop Measurement
        nativeScript.stopMeasurement();

        LocalBroadcastManager.getInstance(ctx).unregisterReceiver(nsReceiver);

        //Start Network and GeoListener
        stopListener();
    }


    //Listener -------------------------------------------------------------------------------------

    private void startListener()
    {
        listenerNetwork = new ListenerNetwork(ctx, new ModulesInterface()
        {
            @Override
            public void receiveString(String message) {

            }

            @Override
            public void receiveData(JSONObject message)
            {
                JSONObject jData = new JSONObject();
                JSONObject jDataAdditional = Common.getJSONMTWSMeasurementAdditional();

                try
                {
                    //------------------------------------------------------------------------------------------

                    //While download measurement is running
                    if( isDownloadRunning )
                    {
                        if(jDataAdditional.getInt("app_mode_download_changed") == 0 && !message.getString("app_mode").equals(jDataAdditional.getString("app_mode_download_start")))
                        {
                            jData.put("app_mode_download_changed", 1 );

                        }
                        if(jDataAdditional.getInt("app_access_id_download_changed") == 0 && !message.getString("app_access_id").equals(jDataAdditional.getString("app_access_id_download_start")))
                        {
                            jData.put("app_access_id_download_changed", 1);
                        }

                        Common.addToJSONMTWSMeasurementAdditional(jData);
                    }

                    //While upload measurement is running
                    if( isUploadRunning )
                    {
                        if(jDataAdditional.getInt("app_mode_upload_changed") == 0 && !message.getString("app_mode").equals(jDataAdditional.getString("app_mode_upload_start")))
                        {
                            jData.put("app_mode_upload_changed", 1 );

                        }
                        if(jDataAdditional.getInt("app_access_id_upload_changed") == 0 && !message.getString("app_access_id").equals(jDataAdditional.getString("app_access_id_upload_start")))
                        {
                            jData.put("app_access_id_upload_changed", 1);
                        }

                        Common.addToJSONMTWSMeasurementAdditional(jData);
                    }

                    //------------------------------------------------------------------------------------------

                }
                catch (Exception ex) { mTool.printTrace(ex); }

                Common.addToJSONMTWSMeasurementAdditional(message);
            }
        });
        listenerNetwork.getState();

        listenerGeoLocation = new ListenerGeoLocation(ctx, new ModulesInterface()
        {
            @Override
            public void receiveString(String message) {

            }

            @Override
            public void receiveData(JSONObject message)
            {
                JSONObject jData = new JSONObject();
                JSONObject jDataAdditional = Common.getJSONMTWSMeasurementAdditional();

                try
                {
                    //------------------------------------------------------------------------------------------

                    //While download measurement is running
                    if( isDownloadRunning )
                    {
                        if( message.getInt("app_velocity") > jDataAdditional.getInt("app_velocity") )
                        {
                            jData.put("app_velocity_download_max", message.getInt("app_velocity") );
                        }

                        Common.addToJSONMTWSMeasurementAdditional(jData);
                    }

                    //While upload measurement is running
                    if( isUploadRunning )
                    {

                        if( message.getInt("app_velocity") > jDataAdditional.getInt("app_velocity") )
                        {
                            jData.put("app_velocity_upload_max", message.getInt("app_velocity") );
                        }

                        Common.addToJSONMTWSMeasurementAdditional(jData);
                    }

                    //------------------------------------------------------------------------------------------

                }
                catch (Exception ex) { mTool.printTrace(ex); }

                Common.addToJSONMTWSMeasurementAdditional(message);
            }
        });
        listenerGeoLocation.getPosition(min_time, min_distance, LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void stopListener()
    {
        listenerNetwork.stopUpdates();
        listenerGeoLocation.stopUpdates();
    }

    //Getter/Setter --------------------------------------------------------------------------------

    public void setMeasurementRunning(boolean flag) {
        testRunning = flag;
    }

    public boolean getMeasurementRunning() {
        return testRunning;
    }

    private void writeToFile()
    {
        JSONObject jData = Common.getJSONMTWSMeasurement();

        File path = ctx.getFilesDir();
        File file = new File(path, "app/index.js");

        try
        {
            FileOutputStream stream = new FileOutputStream(file);

            stream.write("global.require('common-mobile.js');".getBytes());
            stream.write("var params = ".getBytes());

            stream.write(jData.toString(2).getBytes());

            stream.write(";".getBytes());
            stream.write("global.measurementStart(params);".getBytes());

            stream.flush();
            stream.close();
        }
        catch (Exception ex) { mTool.printTrace(ex); }

        mTool.printOutput("NativeScript JavaScript File: "+file.getPath());

        mTool.printJSONObject(jData);

    }

    public void performRouteToClientLookup( JSONObject report )
    {
        JSONObject jData = new JSONObject();

        try
        {
            String server = report.getString("server_v6");

            if(server.equals("-"))
                server = report.getString("server_");

            String url = "http://"+server+":"+defaultRouteToClientTargetPort;

            HashMap<String, String> hHeader = new HashMap<>();
            hHeader.put("Content-Type","application/json");
            hHeader.put("Origin","");

            jData.put("cmd","traceroute");

            JSONObject jResult = mHttp.genericHTTPRequest(url, "POST", hHeader, jData.toString());

            JSONObject jTmp = new JSONObject(jResult.getString("sResponse"));
            JSONArray jHops = jTmp.getJSONArray("hops");
            JSONObject jHop = jHops.getJSONObject(jHops.length()-1);
            String hops = jHop.getString("id");

            JSONObject jAdd = new JSONObject();
            jAdd.put("server_client_route_hops",hops);
            jAdd.put("server_client_route",jHops.toString());

            Common.addToJSONMTWSMeasurementAdditional(jAdd);
        }
        catch (Exception ex) { mTool.printTrace(ex); }


    }
}
