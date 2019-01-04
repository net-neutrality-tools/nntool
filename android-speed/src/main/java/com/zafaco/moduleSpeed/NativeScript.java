package com.zafaco.moduleSpeed;

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

import android.app.Application;
import android.content.Context;

import com.zafaco.moduleCommon.Http;
import com.zafaco.moduleCommon.Tool;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * Class NativeScript
 */
public class NativeScript
{
    /**************************** Variables ****************************/

    private Context ctx;

    private String defaultIndexUrl;

    //Module Objects
    private Tool mTool;
    private Http mHttp;

    private com.tns.Runtime nsRuntime;

    /*******************************************************************/

    /**
     * Method NativeScript
     */
    public NativeScript()
    {
        mTool = new Tool();
        mHttp = new Http();
    }

    /**
     * Method setDefaultIndexUrl
     * @param sDefaultIndexUrl
     */
    public void setDefaultIndexUrl(String sDefaultIndexUrl)
    {
        defaultIndexUrl = sDefaultIndexUrl;

        JSONObject jDataAppVersion = new JSONObject();

        HashMap<String, String> hHeader = new HashMap<>();
        hHeader.put("Content-Type", "application/x-www-form-urlencoded");

        JSONObject jData = mHttp.genericDownloadRequest(defaultIndexUrl, "POST",hHeader, jDataAppVersion.toString(), ctx.getFilesDir().getPath()+"/index.mobile.js"  );
    }

    /**
     * Method startMeasurement
     * @param app
     */
    @com.tns.JavaScriptImplementation(javaScriptFile = "index.js")
    public void startMeasurement(Application app)
    {
        //------------------------------------------------------------------------------------------
        //NativeScript Runtime
        mTool.printOutput("initRuntime");
        while (nsRuntime == null)
        {
            mTool.printOutput("Trying");
            nsRuntime = com.tns.RuntimeHelper.initRuntime(app);
        }

        //nsRuntime.run();
        //nsRuntime.runModule(new File(app.getApplicationContext().getFilesDir(), "app/index.js"));
        nsRuntime.runScript(new File(app.getApplicationContext().getFilesDir(), "index.js"));
        //------------------------------------------------------------------------------------------
    }

    /**
     * Method stopMeasurement
     */
    public void stopMeasurement()
    {
    }
}
