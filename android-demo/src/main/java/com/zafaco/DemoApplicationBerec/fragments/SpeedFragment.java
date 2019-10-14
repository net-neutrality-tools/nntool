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

import android.content.ClipData;
import android.content.ClipboardManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import java.util.Objects;

import at.alladin.nettest.nntool.android.speed.SpeedMeasurementState;
import at.alladin.nettest.nntool.android.speed.SpeedTaskDesc;
import at.alladin.nettest.nntool.android.speed.jni.JniSpeedMeasurementClient;
import at.alladin.nettest.nntool.android.speed.jni.exception.AndroidJniCppException;

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

        RadioGroup radioIP          = mView.findViewById(R.id.radioGroupIP);
        RadioGroup radioStream      = mView.findViewById(R.id.radioGroupST);
        RadioGroup radioTLS      = mView.findViewById(R.id.radioGroupTLS);

        buttonStart.setOnClickListener(handleClickMeasurementStart);
        buttonRtt.setOnClickListener(handleClickMeasurementStart);
        buttonDownload.setOnClickListener(handleClickMeasurementStart);
        buttonUpload.setOnClickListener(handleClickMeasurementStart);

        radioIP.setOnCheckedChangeListener(handleCheckMeasurementSettings);
        radioStream.setOnCheckedChangeListener(handleCheckMeasurementSettings);
        radioTLS.setOnCheckedChangeListener(handleCheckMeasurementSettings);

        //this contains the config
        speedTaskDesc = new SpeedTaskDesc();
        speedTaskDesc.setSpeedServerAddrV4("peer-ias-de-01.net-neutrality.tools");
        speedTaskDesc.setSpeedServerPort(443);
        speedTaskDesc.setDownloadStreams(4);
        speedTaskDesc.setRttCount(11);
        speedTaskDesc.setUploadStreams(4);
        speedTaskDesc.setUseEncryption(true);

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
                try {
                    jniSpeedMeasurementClient.stopMeasurement();
                } catch (AndroidJniCppException ex) {
                    ex.printStackTrace();
                }

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

                jniSpeedMeasurementClient.addMeasurementListener(measurementStringListener);

                jniSpeedMeasurementClient.addMeasurementFinishedListener(measurementFinishedStringListener);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            jniSpeedMeasurementClient.startMeasurement();
                        } catch (AndroidJniCppException ex) {
                            ex.printStackTrace();
                        }
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
                //IP Version auto
                case R.id.radioButtonIPAuto:
                    wsTool.setIPAuto();
                    break;
                //IP Version 4 only
                case R.id.radioButtonIPV4:
                    wsTool.setIPV4();
                    break;
                //IP Version 6 only
                case R.id.radioButtonIPV6:
                    wsTool.setIPV6();
                    break;
                //Single Stream off
                case R.id.radioButtonSSOff:
                    wsTool.setSingleStreamOff();
                    speedTaskDesc.setDownloadStreams(4);
                    speedTaskDesc.setUploadStreams(4);
                    break;
                //Single Stream on
                case R.id.radioButtonSSOn:
                    wsTool.setSingleStreamOn();
                    speedTaskDesc.setDownloadStreams(1);
                    speedTaskDesc.setUploadStreams(1);
                    break; //Single Stream off
                case R.id.radioButtonTLSOff:
                    wsTool.setUseEncryption(false);
                    speedTaskDesc.setUseEncryption(false);
                    speedTaskDesc.setSpeedServerPort(80);
                    break;
                //Single Stream on
                case R.id.radioButtonTLSOn:
                    wsTool.setUseEncryption(true);
                    speedTaskDesc.setUseEncryption(true);
                    speedTaskDesc.setSpeedServerPort(443);
                    break;
            }
        }
    } ;

    private final JniSpeedMeasurementClient.MeasurementStringListener measurementStringListener = new JniSpeedMeasurementClient.MeasurementStringListener() {
        @Override
        public void onMeasurement(String result)
        {
            try
            {
                JSONObject jsonReport = new JSONObject(result);

                updateUi(jsonReport.toString(2), (TextView) mView.findViewById(R.id.results));

                switch(jsonReport.getString("cmd"))
                {
                    //------------------------------------------------------------------------------
                    case "info":
                    case "finish":
                        //Show TestCase
                        updateUi(jsonReport.getString("test_case")+": "+jsonReport.getString("msg"), (TextView) mView.findViewById(R.id.status));
                        break;
                    //------------------------------------------------------------------------------
                    case "error":
                        break;
                    //------------------------------------------------------------------------------
                    case "report":

                        switch(jsonReport.getString("test_case"))
                        {
                            case "rtt_udp":
                                JSONArray jRTTArray = new JSONArray(jsonReport.getString("rtt_udp_info"));
                                JSONObject jRTT = jRTTArray.getJSONObject(jRTTArray.length()-1);

                                updateUi(jRTT.getDouble("average_ns")/1000/1000+" ms", (TextView) mView.findViewById(R.id.rtt));
                                break;
                            case "download":
                                JSONArray jDownloadArray = new JSONArray(jsonReport.getString("download_info"));
                                JSONObject jDownload = jDownloadArray.getJSONObject(jDownloadArray.length()-1);

                                updateUi(jDownload.getDouble("throughput_avg_bps")/1000/1000+" Mbit/s", (TextView) mView.findViewById(R.id.download));
                                break;
                            case "upload":
                                JSONArray jUploadArray = new JSONArray(jsonReport.getString("upload_info"));
                                JSONObject jUpload = jUploadArray.getJSONObject(jUploadArray.length()-1);

                                updateUi(jUpload.getDouble("throughput_avg_bps")/1000/1000+" Mbit/s", (TextView) mView.findViewById(R.id.upload));
                                break;
                        }

                        updateUi(jsonReport.toString(2), (TextView) mView.findViewById(R.id.results));

                        break;
                    //------------------------------------------------------------------------------
                    case "completed":

                       //Write to Database
                       //Database mtdatabase = new Database(ctx, "mtmeasurement","measurement");
                       //mtdatabase.createDB(mCommon.getJSONMTWSMeasurement());
                       //mtdatabase.insert(mCommon.getJSONMTWSMeasurement());

                        break;
                    //------------------------------------------------------------------------------
                    default:
                        break;
                }
            }
            catch (JSONException ex)
            {
                mTool.printTrace(ex);
            }
        }
    };

    private final JniSpeedMeasurementClient.MeasurementFinishedStringListener measurementFinishedStringListener = new JniSpeedMeasurementClient.MeasurementFinishedStringListener() {
        @Override
        public void onMeasurementFinished(String result)
        {
            try
            {
                final JSONObject jsonObject = new JSONObject(result);
                updateButtonUi(R.string.name_msetting_all,buttonStart);
                updateUi(jsonObject.toString(2), (TextView) mView.findViewById(R.id.results));

                getActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            final android.content.ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("Source Text", jsonObject.toString(2));
                            clipboardManager.setPrimaryClip(clipData);
                        }
                        catch (JSONException ex)
                        {
                            mTool.printTrace(ex);
                        }

                        buttonRtt.setEnabled(true);
                        buttonDownload.setEnabled(true);
                        buttonUpload.setEnabled(true);
                    }
                });
            }
            catch (JSONException ex)
            {
                mTool.printTrace(ex);
            }
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
