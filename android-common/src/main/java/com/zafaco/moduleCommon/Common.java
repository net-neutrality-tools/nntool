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

import android.content.Context;

import org.json.JSONObject;

import java.util.Iterator;

public class Common
{
    /**************************** Loging Level ****************************/
    public static boolean DEBUG = false;
    public static boolean DEBUG_CONSOLE = false;

    /****************** Default Measurement Parameters ********************/

    private static int defaultService                               = 913;

    /***************************** Variables ******************************/

    private final Context ctx;

    //Module Objects
    private Tool mTool;

    //Vars
    private boolean testRunning = false;

    //Measurement Object
    private static JSONObject jsonMTWSMeasurement;
    private static JSONObject jsonMTWSMeasurementAdditional;

    /**************************** Public Functions ****************************/

    /**
     * Construtor
     * @param ctx Context of Common
     */
    public Common(Context ctx)
    {
        this.ctx = ctx;

        mTool = new Tool();

        jsonMTWSMeasurement = new JSONObject();
        jsonMTWSMeasurementAdditional = new JSONObject();
    }

    //Getter/Setter --------------------------------------------------------------------------------

    /**
     * Set DEBUG Flag
     * @param flag
     */
    public void setDebug( boolean flag )
    {
        DEBUG = flag;
    }

    public static int getService()
    {
        return defaultService;
    }

    public void setMeasurementRunning(boolean flag) {
        testRunning = flag;
    }

    public boolean getMeasurementRunning() {
        return testRunning;
    }

    public static JSONObject getJSONMTWSMeasurement()
    {
        return jsonMTWSMeasurement;
    }

    public static void addToJSONMTWSMeasurement(JSONObject object)
    {
        try
        {
            for (Iterator<String> iter = object.keys(); iter.hasNext(); )
            {
                String key = iter.next();
                jsonMTWSMeasurement.put(key,object.get(key));
            }
        }
        catch(Exception ex) {}
    }

    public static JSONObject getJSONMTWSMeasurementAdditional()
    {
        return jsonMTWSMeasurementAdditional;
    }

    public static void addToJSONMTWSMeasurementAdditional(JSONObject object)
    {
        try
        {
            for (Iterator<String> iter = object.keys(); iter.hasNext(); )
            {
                String key = iter.next();
                jsonMTWSMeasurementAdditional.put(key,object.get(key));
            }
        }
        catch(Exception ex) {}
    }

    public void deviceData()
    {
        mTool.getDeviceData(ctx);
        mTool.getTelephonyManagerData(ctx);
        mTool.getWifiManagerData(ctx);
    }
}