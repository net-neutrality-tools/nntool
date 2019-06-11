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
import com.zafaco.moduleCommon.Tool;
import com.zafaco.DemoApplicationBerec.R;
import com.zafaco.DemoApplicationBerec.WSTool;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import java.util.Objects;

import at.alladin.nettest.nntool.android.speed.SpeedMeasurementState;
import at.alladin.nettest.nntool.android.speed.SpeedTaskDesc;
import at.alladin.nettest.nntool.android.speed.jni.JniSpeedMeasurementClient;

/**
 * Class SpeedFragment
 */
public class SpeedFragment extends Fragment implements FocusedFragment
{
    private WSTool wsTool = WSTool.getInstance();
    private Common mCommon = wsTool.getCommonObject();
    private Tool mTool = wsTool.getToolObject();
    private JniSpeedMeasurementClient jniSpeedMeasurementClient;

    private SpeedTaskDesc speedTaskDesc;

    private SpeedMeasurementState currentMeasurementState;

    private boolean running = false;

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

        //this contains the config
        speedTaskDesc = new SpeedTaskDesc();
        speedTaskDesc.setSpeedServerAddrV4("peer-ias-de-01.net-neutrality.tools");
        speedTaskDesc.setSpeedServerPort(80);
        speedTaskDesc.setDownloadStreams(4);
        speedTaskDesc.setRttCount(11);
        speedTaskDesc.setUploadStreams(4);

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
                case R.id.buttonStart:
                    speedTaskDesc.setPerformRtt(true);
                    speedTaskDesc.setPerformDownload(true);
                    speedTaskDesc.setPerformUpload(true);
                    break;
                case R.id.buttonRTT:
                    speedTaskDesc.setPerformRtt(true);
                    speedTaskDesc.setPerformDownload(false);
                    speedTaskDesc.setPerformUpload(false);
                    break;
                case R.id.buttonDownload:
                    speedTaskDesc.setPerformRtt(false);
                    speedTaskDesc.setPerformDownload(true);
                    speedTaskDesc.setPerformUpload(false);
                    break;
                case R.id.buttonUpload:
                    speedTaskDesc.setPerformRtt(false);
                    speedTaskDesc.setPerformDownload(false);
                    speedTaskDesc.setPerformUpload(true);
                    break;
            }

            wsTool.initSpeedParameter();

            //if running
            if(running)
            {
                //Update Button Text
                updateButtonUi(R.string.name_msetting_all, buttonStart);

                //Start Measurement
                jniSpeedMeasurementClient.stopMeasurement();

                running = false;

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
                jniSpeedMeasurementClient = new JniSpeedMeasurementClient(speedTaskDesc);

                //currentMeasurementState contains the intermediate results (if display is desired)
                currentMeasurementState = jniSpeedMeasurementClient.getSpeedMeasurementState();

                jniSpeedMeasurementClient.addMeasurementFinishedListener(measurementFinishedStringListener);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        jniSpeedMeasurementClient.startMeasurement();
                    }
                });

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

    private final JniSpeedMeasurementClient.MeasurementFinishedStringListener measurementFinishedStringListener = new JniSpeedMeasurementClient.MeasurementFinishedStringListener() {
        @Override
        public void onMeasurementFinished(String result) {

            updateButtonUi(R.string.name_msetting_all,buttonStart);
            updateUi(result, (TextView) mView.findViewById(R.id.results));

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonRtt.setEnabled(true);
                    buttonDownload.setEnabled(true);
                    buttonUpload.setEnabled(true);
                }
            });

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
