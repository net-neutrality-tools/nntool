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

/**
 * Class Speed
 */
@JavaScriptImplementation(javaScriptFile = "app/index.mobile.js")
public class Speed
{
    /****************** Default Measurement Parameters ********************/
    private static String defaultPlatform                           = "mobile";

    private static boolean defaultPerformRttMeasurement             = true;
    private static boolean defaultPerformDownloadMeasuement         = true;
    private static boolean defaultPerformUploadMeasurement          = true;

    private static int defaultRouteToClientTargetPort               = 8080;

    private String wsTargets						                = "peer-ias-de-01";
    private String wsTargetsRtt						                = "peer-ias-de-01";
    private String wsTLD 							                = "net-neutrality.tools";

    private int defaultWsParallelStreamsDownload                    = 4;
    private int defaultWsParallelStreamsUpload                      = 4;
    private int defaultWsFrameSizeDownload                          = 32768;
    private int defaultWsFrameSizeUpload                            = 65535;

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

    /**
     * Method setDownloadStarted
     * @throws Exception
     */
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

    /**
     * Method setDownloadStopped
     */
    public void setDownloadStopped()
    {
        isDownloadRunning = false;
    }

    /**
     * Method setUploadStarted
     * @throws Exception
     */
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

    /**
     * Method setUploadStopped
     */
    public void setUploadStopped()
    {
        isUploadRunning = false;
    }

    /**
     * Method initSpeedParameter
     */
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
            jsonMTWSMeasurement.put("wsTargets", new JSONArray().put(wsTargets));
            jsonMTWSMeasurement.put("wsTargetsRtt", new JSONArray().put(wsTargetsRtt));
            jsonMTWSMeasurement.put("wsTLD", wsTLD);
            jsonMTWSMeasurement.put("wsTargetPort", 80);
            jsonMTWSMeasurement.put("wsWss", 0);
            jsonMTWSMeasurement.put("wsAuthToken", "placeholderToken");
            jsonMTWSMeasurement.put("wsAuthTimestamp", "placeholderTimestamp");

            jsonMTWSMeasurement.put("performRttMeasurement", defaultPerformRttMeasurement);
            jsonMTWSMeasurement.put("performDownloadMeasurement", defaultPerformDownloadMeasuement);
            jsonMTWSMeasurement.put("performUploadMeasurement", defaultPerformUploadMeasurement);

            jsonMTWSMeasurement.put("wsParallelStreamsDownload", defaultWsParallelStreamsDownload);
            jsonMTWSMeasurement.put("wsParallelStreamsUpload", defaultWsParallelStreamsUpload);
            jsonMTWSMeasurement.put("wsFrameSizeDownload", defaultWsFrameSizeDownload);
            jsonMTWSMeasurement.put("wsFrameSizeUpload", defaultWsFrameSizeUpload);

            jsonMTWSMeasurement.put("cookieId", false);

            Common.addToJSONMTWSMeasurement(jsonMTWSMeasurement);
        }
        catch(JSONException ex) { mTool.printTrace(ex); }
    }

    //Measurement Start/Stop -----------------------------------------------------------------------

    /**
     * Method startMeasurement
     * @param app
     * @param interfaceCallback
     */
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

    /**
     * Method stopMeasurement
     */
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

    /**
     * Method startListener
     */
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
            /**
             * Method receiveString
             * @param message
             */
            @Override
            public void receiveString(String message) {

            }

            /**
             * Method receiveData
             * @param message
             */
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

    /**
     * Method stopListener
     */
    private void stopListener()
    {
        listenerNetwork.stopUpdates();
        listenerGeoLocation.stopUpdates();
    }

    //Getter/Setter --------------------------------------------------------------------------------

    /**
     * Method setMeasurementRunning
     * @param flag
     */
    public void setMeasurementRunning(boolean flag) {
        testRunning = flag;
    }

    /**
     * Method getMeasurementRunning
     * @return
     */
    public boolean getMeasurementRunning() {
        return testRunning;
    }

    /**
     * Method writeToFile
     */
    private void writeToFile()
    {
        JSONObject jData = Common.getJSONMTWSMeasurement();

        File path = ctx.getFilesDir();
        File file = new File(path, "index.js");

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

    /**
     * Method performRouteToClientLookup
     * @param report
     */
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

    //Profiles -------------------------------------------------------------------------------------

    /**
     * Method setDownloadProfileLow
     */
    public void setDownloadProfileLow()
    {
        defaultWsParallelStreamsDownload = 4;
        defaultWsFrameSizeDownload = 2048;
    }

    /**
     * Method setDownloadProfileMiddle
     */
    public void setDownloadProfileMiddle()
    {
        defaultWsParallelStreamsDownload = 4;
        defaultWsFrameSizeDownload = 32768;
    }

    /**
     * Method setDownloadProfileHigh
     */
    public void setDownloadProfileHigh()
    {
        defaultWsParallelStreamsDownload = 4;
        defaultWsFrameSizeDownload = 524288;
    }

    /**
     * Method setDownloadProfileVeryHigh
     */
    public void setDownloadProfileVeryHigh()
    {
        defaultWsParallelStreamsDownload = 8;
        defaultWsFrameSizeDownload = 524288;
    }

    /**
     * Method setUploadProfileLow
     */
    public void setUploadProfileLow()
    {
        defaultWsParallelStreamsUpload = 4;
        defaultWsFrameSizeUpload = 2048;
    }

    /**
     * Method setUploadProfileMiddle
     */
    public void setUploadProfileMiddle()
    {
        defaultWsParallelStreamsUpload = 4;
        defaultWsFrameSizeUpload = 32768;
    }

    /**
     * Method setUploadProfileHigh
     */
    public void setUploadProfileHigh()
    {
        defaultWsParallelStreamsUpload = 4;
        defaultWsFrameSizeUpload = 65535;
    }

    /**
     * Method setUploadProfileVeryHigh
     */
    public void setUploadProfileVeryHigh()
    {
        defaultWsParallelStreamsUpload = 20;
        defaultWsFrameSizeUpload = 65535;
    }

    /**
     * Method setIPAuto
     */
    public void setIPAuto()
    {
        if(wsTargets.contains("ipv"))
        {
            wsTargets = wsTargets.substring(0, wsTargets.lastIndexOf("-"));
        }
        if(wsTargetsRtt.contains("ipv"))
        {
            wsTargetsRtt = wsTargetsRtt.substring(0, wsTargetsRtt.lastIndexOf("-"));
        }
    }

    /**
     * Method setIPV4
     */
    public void setIPV4()
    {
        setIPAuto();

        wsTargets = wsTargets+"-ipv4";
        wsTargetsRtt = wsTargetsRtt+"-ipv4";
    }

    /**
     * Method setIPV6
     */
    public void setIPV6()
    {
        setIPAuto();

        wsTargets = wsTargets+"-ipv6";
        wsTargetsRtt = wsTargetsRtt+"-ipv6";
    }

    /**
     * Method setUploadProfileVeryHigh
     */
    public void setSingleStreamOff()
    {
        defaultWsFrameSizeDownload = 32768;
        defaultWsParallelStreamsDownload = 4;

        defaultWsFrameSizeUpload = 32768;
        defaultWsParallelStreamsUpload = 4;
    }

    /**
     * Method setUploadProfileVeryHigh
     */
    public void setSingleStreamOn()
    {
        defaultWsFrameSizeDownload = defaultWsFrameSizeDownload * defaultWsParallelStreamsDownload;
        defaultWsParallelStreamsDownload = 1;

        defaultWsFrameSizeUpload = defaultWsFrameSizeUpload * defaultWsParallelStreamsUpload;
        defaultWsParallelStreamsUpload = 1;

        if (defaultWsFrameSizeUpload > 65535)
        {
            defaultWsFrameSizeUpload = 65535;
        }

    }

    //TestCases ------------------------------------------------------------------------------------

    /**
     * Method setTestcaseAll
     */
    public void setTestcaseAll()
    {
        defaultPerformRttMeasurement             = true;
        defaultPerformDownloadMeasuement         = true;
        defaultPerformUploadMeasurement          = true;
    }

    /**
     * Method setTestcaseAll
     */
    public void setTestcaseRTT()
    {
        defaultPerformRttMeasurement             = true;
        defaultPerformDownloadMeasuement         = false;
        defaultPerformUploadMeasurement          = false;
    }

    /**
     * Method setTestcaseAll
     */
    public void setTestcaseDownload()
    {
        defaultPerformRttMeasurement             = false;
        defaultPerformDownloadMeasuement         = true;
        defaultPerformUploadMeasurement          = false;
    }

    /**
     * Method setTestcaseAll
     */
    public void setTestcaseUpload()
    {
        defaultPerformRttMeasurement             = false;
        defaultPerformDownloadMeasuement         = false;
        defaultPerformUploadMeasurement          = true;
    }
}
