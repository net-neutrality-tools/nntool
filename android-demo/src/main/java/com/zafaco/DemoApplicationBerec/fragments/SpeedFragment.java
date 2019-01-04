package com.zafaco.DemoApplicationBerec.fragments;

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

import com.zafaco.DemoApplicationBerec.interfaces.FocusedFragment;
import com.zafaco.moduleCommon.Common;
import com.zafaco.moduleCommon.Database;
import com.zafaco.moduleCommon.Tool;
import com.zafaco.moduleSpeed.Speed;
import com.zafaco.DemoApplicationBerec.R;
import com.zafaco.DemoApplicationBerec.WSTool;
import com.zafaco.moduleCommon.interfaces.GenericInterface;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;

import java.util.Objects;

/**
 * Class SpeedFragment
 */
public class SpeedFragment extends Fragment implements FocusedFragment
{
    private WSTool wsTool = WSTool.getInstance();
    private Common mCommon = wsTool.getCommonObject();
    private Tool mTool = wsTool.getToolObject();
    private Speed mSpeed = wsTool.getSpeedObject();

    DecimalFormat f = new DecimalFormat("#0.00");

    /**************************** Variables ****************************/

    Context ctx;
    View view;

    /*******************************************************************/

    /**
     * Method onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_measurement, container, false);

        //Start button
        Button buttonOne = view.findViewById(R.id.button);
        buttonOne.setOnClickListener(handleClickMeasurementStart);

        return view;
    }

    /**
     * Method onResume
     */
    @Override
    public void onResume()
    {
        ctx = Objects.requireNonNull(getActivity()).getApplicationContext();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        super.onResume();
    }

    /**
     * Method onDisplayView
     */
    @Override
    public void onDisplayView()
    {
        ctx = Objects.requireNonNull(getActivity()).getApplication().getApplicationContext();

        mCommon.setDebug(true);
    }

    /**
     * Handler handleClickMeasurementStart
     */
    private final View.OnClickListener handleClickMeasurementStart = new View.OnClickListener()
    {
        /**
         * Method onClick
         * @param v View
         */
        @Override
        public void onClick(View v)
        {
            //if running
            if(mSpeed.getMeasurementRunning())
            {
                //Update Button Text
                updateButtonUi(R.string.start_test, (Button) view.findViewById(R.id.button));

                //Start Measurement
                mSpeed.stopMeasurement();

                mSpeed.setMeasurementRunning(false);
            }

            //if not running
            else
            {
                //Update Button Text
                updateButtonUi(R.string.cancel_test, (Button) view.findViewById(R.id.button));

                //Start Measurement
                mSpeed.startMeasurement(Objects.requireNonNull(getActivity()).getApplication(), interfaceCallback);

                mSpeed.setMeasurementRunning(true);
            }
        }
    };

    /**
     * Interface interfaceCallback
     */
    private final GenericInterface interfaceCallback = new GenericInterface()
    {
        /**
         * Method reportCallback
         * @param jsonReport
         */
        @Override
        public void reportCallback(JSONObject jsonReport)
        {
            try
            {
                switch(jsonReport.getString("cmd"))
                {
                    //------------------------------------------------------------------------------
                    case "info":

                        updateUi(jsonReport.getString("test_case")+": "+jsonReport.getString("msg"), (TextView) view.findViewById(R.id.status));

                        if(jsonReport.getString("test_case").equals("download") && jsonReport.getString("msg").equals("starting measurement"))
                        {
                            mSpeed.setDownloadStarted();
                            mSpeed.performRouteToClientLookup(jsonReport);
                        }

                        if(jsonReport.getString("test_case").equals("upload") && jsonReport.getString("msg").equals("starting measurement"))
                        {
                            mSpeed.setUploadStarted();
                        }

                        break;
                    //------------------------------------------------------------------------------
                    case "finish":
                        //Show Cancel Button
                        updateButtonUi(R.string.cancel_test,(Button) view.findViewById(R.id.button));
                        //Show TestCase
                        updateUi(jsonReport.getString("test_case")+": "+jsonReport.getString("msg"), (TextView) view.findViewById(R.id.status));

                        if(jsonReport.getString("test_case").equals("download"))
                        {
                            mSpeed.setDownloadStopped();
                        }
                        if(jsonReport.getString("test_case").equals("upload"))
                        {
                            mSpeed.setUploadStopped();
                        }

                        break;
                    //------------------------------------------------------------------------------
                    case "error":
                        break;
                    //------------------------------------------------------------------------------
                    case "report":

                        switch(jsonReport.getString("test_case"))
                        {
                            case "rtt":
                                updateUi(f.format(jsonReport.getJSONObject("rtt_info").getDouble("average_ns")/1000)+" ms", (TextView) view.findViewById(R.id.rtt));
                                break;
                            case "download":
                                updateUi(f.format(jsonReport.getJSONObject("download_info").getDouble("throughput_avg_bps")/1000/1000)+" Mbit/s", (TextView) view.findViewById(R.id.download));
                                break;
                            case "upload":

                                updateUi(f.format(jsonReport.getJSONObject("upload_info").getDouble("throughput_avg_bps")/1000/1000)+" Mbit/s", (TextView) view.findViewById(R.id.upload));
                                break;
                        }

                        updateUi(jsonReport.toString(2), (TextView) view.findViewById(R.id.results));

                        break;
                    //------------------------------------------------------------------------------
                    case "completed":
                        //Add Addtional KPIs
                        JSONObject additionalKPIsFromUI = new JSONObject();
                        additionalKPIsFromUI.put("app_version", wsTool.getVersion("app"));
                        additionalKPIsFromUI.put("app_library_version", wsTool.getVersion("speed"));

                        //Database
                        Database mtdatabase = new Database(ctx, "measurements","speed");
                        mtdatabase.createDB(Common.getJSONMTWSMeasurement());
                        mtdatabase.insert(Common.getJSONMTWSMeasurement());

                        //Update UI
                        updateButtonUi(R.string.start_test,(Button) view.findViewById(R.id.button));
                        updateUi(jsonReport.getString("test_case")+": "+jsonReport.getString("msg"), (TextView) view.findViewById(R.id.status));
                        updateUi(Common.getJSONMTWSMeasurement().toString(2), (TextView) view.findViewById(R.id.results));

                        //Stop Listener
                        mSpeed.stopMeasurement();

                        mSpeed.setMeasurementRunning(false);

                        break;
                    //------------------------------------------------------------------------------
                    default:
                        break;
                }
            }
            catch (Exception ex)
            {
                mTool.printTrace(ex);
            }
        }

        /**
         * Method consoleCallback
         * @param message
         */
        @Override
        public void consoleCallback(String message)
        {
        }
    };

    /**
     * Method updateButtonUi - Show Text on UI
     * @param message Message to Show
     * @param buttonid button id
     */
    private void updateButtonUi(final int message, final Button buttonid)
    {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonid.setText(message);
            }
        });
    }

    /**
     * Method updateUi - Show Text on UI
     * @param message Message to Show
     * @param textid testView id
     */
    private void updateUi(final String message, final TextView textid)
    {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textid.setText(message);
            }
        });
    }
}
