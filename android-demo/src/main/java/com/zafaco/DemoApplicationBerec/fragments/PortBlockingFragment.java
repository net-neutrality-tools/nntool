package com.zafaco.DemoApplicationBerec.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zafaco.DemoApplicationBerec.R;
import com.zafaco.DemoApplicationBerec.WSTool;
import com.zafaco.DemoApplicationBerec.interfaces.FocusedFragment;
import com.zafaco.moduleCommon.Common;
import com.zafaco.moduleCommon.Tool;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import at.alladin.nettest.qos.QoSMeasurementClientControlAdapter;
import at.alladin.nettest.qos.QoSMeasurementClientProgressAdapter;
import at.alladin.nettest.qos.android.QoSMeasurementClientAndroid;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.rmbt.client.RMBTClient;
import at.alladin.rmbt.client.v2.task.result.QoSResultCollector;

public class PortBlockingFragment extends Fragment implements FocusedFragment
{
    private WSTool wsTool = WSTool.getInstance();
    private Common mCommon = wsTool.getCommonObject();
    private Tool mTool = wsTool.getToolObject();

    private QoSMeasurementClientAndroid qosClient;

    DecimalFormat f = new DecimalFormat("#0.00");

    private final static String TAG = "PORT_BLOCKING_FRAGMENT";

    private final static Pattern resultFormatPattern = Pattern.compile("[^:]\\{");

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

        TextView textView = view.findViewById(R.id.download_name);
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
        textView = view.findViewById(R.id.download);
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
        textView = view.findViewById(R.id.upload_name);
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
        textView = view.findViewById(R.id.upload);
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
        textView = view.findViewById(R.id.rtt_name);
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }
        textView = view.findViewById(R.id.rtt);
        if (textView != null) {
            textView.setVisibility(View.GONE);
        }



        RMBTClient client = RMBTClient.getInstance(getResources().getString(R.string.default_qos_control_host),
                Integer.toString(getResources().getInteger(R.integer.default_qos_control_port)),
                getResources().getIntArray(R.array.qos_tcp_test_port_list),
                getResources().getIntArray(R.array.qos_udp_test_port_list));
        qosClient = new QoSMeasurementClientAndroid(client, this.getContext().getApplicationContext());

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
            qosClient.addProgressListener(new QoSMeasurementClientProgressAdapter() {
                @Override
                public void onProgress(float progress) {
                    Log.i(TAG, String.format("Progress %f", progress));
                }
            });
            qosClient.addControlListener(new QoSMeasurementClientControlAdapter() {
                @Override
                public void onMeasurementStarted(List<QosMeasurementType> testsToBeExecuted) {
                    Log.i(TAG, testsToBeExecuted.toString());
                }

                @Override
                public void onMeasurementStopped() {
                    Log.i(TAG, "Measurement stopped");
                }

                @Override
                public void onMeasurementError(Exception e) {
                    Log.e(TAG, e.getMessage());
                }

                @Override
                public void onMeasurementFinished(String qosTestUuid, QoSResultCollector qoSResultCollector) {
                    Log.i(TAG, qoSResultCollector.toJson().toString());
                    updateUi(resultFormatPattern.matcher(qoSResultCollector.toJson().toString().replace(",", ",\n")).replaceAll("\n\n{"), (TextView) view.findViewById(R.id.results));
                }
            });
            qosClient.start();

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
