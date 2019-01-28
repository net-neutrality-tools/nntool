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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    private DecimalFormat f = new DecimalFormat("#0.00");

    /**************************** Variables ****************************/

    private Context ctx;
    private View mView;

    private Button buttonStart;
    private Button buttonRtt;
    private Button buttonDownload;
    private Button buttonUpload;

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
        mView = inflater.inflate(R.layout.fragment_speed, container, false);

        //Start button
        buttonStart      = mView.findViewById(R.id.buttonStart);
        buttonRtt        = mView.findViewById(R.id.buttonRTT);
        buttonDownload   = mView.findViewById(R.id.buttonDownload);
        buttonUpload     = mView.findViewById(R.id.buttonUpload);

        RadioGroup radioDownload    = mView.findViewById(R.id.radioGroupDL);
        RadioGroup radioUpload      = mView.findViewById(R.id.radioGroupUL);
        RadioGroup radioIP          = mView.findViewById(R.id.radioGroupIP);
        RadioGroup radioStream      = mView.findViewById(R.id.radioGroupST);

        buttonStart.setOnClickListener(handleClickMeasurementStart);
        buttonRtt.setOnClickListener(handleClickMeasurementStart);
        buttonDownload.setOnClickListener(handleClickMeasurementStart);
        buttonUpload.setOnClickListener(handleClickMeasurementStart);

        radioDownload.setOnCheckedChangeListener(handleCheckMeasurementSettings);
        radioUpload.setOnCheckedChangeListener(handleCheckMeasurementSettings);
        radioIP.setOnCheckedChangeListener(handleCheckMeasurementSettings);
        radioStream.setOnCheckedChangeListener(handleCheckMeasurementSettings);

        return mView;
    }

    /**
     * Method onResume
     */
    @Override
    public void onResume()
    {
        ctx = Objects.requireNonNull(getActivity()).getApplicationContext();

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
            switch (v.getId())
            {
                case R.id.buttonStart:      wsTool.setTestcaseAll();        break;
                case R.id.buttonRTT:        wsTool.setTestcaseRTT();        break;
                case R.id.buttonDownload:   wsTool.setTestcaseDownload();   break;
                case R.id.buttonUpload:     wsTool.setTestcaseUpload();     break;
            }

            wsTool.initSpeedParameter();

            //if running
            if(mSpeed.getMeasurementRunning())
            {
                //Update Button Text
                updateButtonUi(R.string.name_msetting_all, buttonStart);

                //Start Measurement
                mSpeed.stopMeasurement();

                mSpeed.setMeasurementRunning(false);

                buttonRtt.setEnabled(true);
                buttonDownload.setEnabled(true);
                buttonUpload.setEnabled(true);
            }

            //if not running
            else
            {
                //Update Button Text
                updateButtonUi(R.string.name_msetting_cancel, buttonStart);

                //Start Measurement
                mSpeed.startMeasurement(Objects.requireNonNull(getActivity()).getApplication(), interfaceCallback);

                mSpeed.setMeasurementRunning(true);

                buttonRtt.setEnabled(false);
                buttonDownload.setEnabled(false);
                buttonUpload.setEnabled(false);
            }
        }
    };

    /**
     * Hanlder handleCheckMeasurementSettings
     */
    private final RadioGroup.OnCheckedChangeListener handleCheckMeasurementSettings = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId)
        {
            // This will get the radiobutton that has changed in its check state
            // RadioButton checkedRadioButton = radioGroup.findViewById(checkedId);

            switch (checkedId)
            {
                //Download "low" Profile
                case R.id.radioButtonDLLow:         wsTool.setDownloadProfileLow();         break;
                //Download "middle" Profile
                case R.id.radioButtonDLMiddle:      wsTool.setDownloadProfileMiddle();      break;
                //Download "high" Profile
                case R.id.radioButtonDLHigh:        wsTool.setDownloadProfileHigh();        break;
                //Download "very high" Profile
                case R.id.radioButtonDLVeryHigh:    wsTool.setDownloadProfileVeryHigh();    break;
                //Upload "low" Profile
                case R.id.radioButtonULLow:         wsTool.setUploadProfileLow();           break;
                //Upload "middle" Profile
                case R.id.radioButtonULMiddle:      wsTool.setUploadProfileMiddle();        break;
                //Upload "high" Profile
                case R.id.radioButtonULHigh:        wsTool.setUploadProfileHigh();          break;
                //Upload "very high" Profile
                case R.id.radioButtonULVeryHigh:    wsTool.setUploadProfileVeryHigh();      break;
                //IP Version auto
                case R.id.radioButtonIPAuto:        wsTool.setIPAuto();                     break;
                //IP Version 4 only
                case R.id.radioButtonIPV4:          wsTool.setIPV4();                       break;
                //IP Version 6 only
                case R.id.radioButtonIPV6:          wsTool.setIPV6();                       break;
                //Single Stream off
                case R.id.radioButtonSSOff:         wsTool.setSingleStreamOff();            break;
                //Single Stream on
                case R.id.radioButtonSSOn:          wsTool.setSingleStreamOn();             break;
            }
        }
    } ;

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

                        updateUi(jsonReport.getString("test_case")+": "+jsonReport.getString("msg"), (TextView) mView.findViewById(R.id.status));

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
                    case "error":
                        break;
                    //------------------------------------------------------------------------------
                    case "finish":
                        //Show Cancel Button
                        updateButtonUi(R.string.name_msetting_cancel,(Button) mView.findViewById(R.id.buttonStart));
                        //Show TestCase
                        updateUi(jsonReport.getString("test_case")+": "+jsonReport.getString("msg"), (TextView) mView.findViewById(R.id.status));

                        if(jsonReport.getString("test_case").equals("download"))
                        {
                            mSpeed.setDownloadStopped();
                        }
                        if(jsonReport.getString("test_case").equals("upload"))
                        {
                            mSpeed.setUploadStopped();
                        }

                        switch(jsonReport.getString("test_case"))
                        {
                            case "rtt":
                                updateUi(f.format(jsonReport.getJSONObject("rtt_info").getDouble("average_ns")/1000)+" ms", (TextView) mView.findViewById(R.id.rtt));
                                break;
                            case "download":
                                updateUi(f.format(jsonReport.getJSONObject("download_info").getDouble("throughput_avg_bps")/1000/1000)+" Mbit/s", (TextView) mView.findViewById(R.id.download));
                                break;
                            case "upload":

                                updateUi(f.format(jsonReport.getJSONObject("upload_info").getDouble("throughput_avg_bps")/1000/1000)+" Mbit/s", (TextView) mView.findViewById(R.id.upload));
                                break;
                        }

                        break;
                    //------------------------------------------------------------------------------
                    case "report":

                        switch(jsonReport.getString("test_case"))
                        {
                            case "rtt":
                                updateUi(f.format(jsonReport.getJSONObject("rtt_info").getDouble("average_ns")/1000)+" ms", (TextView) mView.findViewById(R.id.rtt));
                                break;
                            case "download":
                                updateUi(f.format(jsonReport.getJSONObject("download_info").getDouble("throughput_avg_bps")/1000/1000)+" Mbit/s", (TextView) mView.findViewById(R.id.download));
                                break;
                            case "upload":

                                updateUi(f.format(jsonReport.getJSONObject("upload_info").getDouble("throughput_avg_bps")/1000/1000)+" Mbit/s", (TextView) mView.findViewById(R.id.upload));
                                break;
                        }

                        updateUi(jsonReport.toString(2), (TextView) mView.findViewById(R.id.results));

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
                        updateButtonUi(R.string.name_msetting_all,buttonStart);
                        updateUi(jsonReport.getString("test_case")+": "+jsonReport.getString("msg"), (TextView) mView.findViewById(R.id.status));
                        updateUi(Common.getJSONMTWSMeasurement().toString(2), (TextView) mView.findViewById(R.id.results));

                        //Stop Listener
                        mSpeed.stopMeasurement();

                        mSpeed.setMeasurementRunning(false);

                        buttonRtt.setEnabled(true);
                        buttonDownload.setEnabled(true);
                        buttonUpload.setEnabled(true);

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
