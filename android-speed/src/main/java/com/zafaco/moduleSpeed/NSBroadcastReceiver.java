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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zafaco.moduleCommon.Tool;
import com.zafaco.moduleCommon.Common;
import com.zafaco.moduleCommon.interfaces.GenericInterface;

import org.json.JSONObject;

/**
 * Class NSBroadcastReceiver
 */
public class NSBroadcastReceiver extends BroadcastReceiver
{
    private Tool mTool = new Tool();

    private GenericInterface interfaceCallback;

    /**
     * Method NSBroadcastReceiver
     * @param intCall
     */
    public NSBroadcastReceiver(GenericInterface intCall)
    {
        interfaceCallback = intCall;
    }

    /**
     * Method onReceive
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Get extra data included in the Intent
        String message = intent.getStringExtra("message");
        /*
        mTool.printOutput( "\n--------------------------------------------------------------------- " +
                "\n -> Action: " + intent.getAction() +
                "\n -> Message: " + message +
                "\n--------------------------------------------------------------------- ");
        */

        if(intent.getAction().equals("tnsReportCallback")) {
            try {
                //Convert String from Report to JSON
                JSONObject jsonReport = new JSONObject(message);

                //mTool.printOutput("Report: [" + jsonReport.getString("cmd") + "][" + jsonReport.getString("test_case") + "][" + jsonReport.getString("msg") + "] " + message);
                mTool.printOutput("Report: [" + jsonReport.getString("cmd") + "][" + jsonReport.getString("test_case") + "] " + message);

                switch (jsonReport.getString("cmd")) {
                    case "info":
                    case "finish":
                    case "completed":
                    case "error":
                    case "report":
                        Common.addToJSONMTWSMeasurement(jsonReport);
                        Common.addToJSONMTWSMeasurement(Common.getJSONMTWSMeasurementAdditional());
                        interfaceCallback.reportCallback(Common.getJSONMTWSMeasurement());
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                mTool.printTrace(ex);
            }
        }

        if(intent.getAction().equals("tnsConsoleLogCallback")) {
            if(Common.DEBUG_CONSOLE)
            {
                mTool.printOutput("Console: " + message);

                interfaceCallback.consoleCallback(message);
            }
        }
    }
}
