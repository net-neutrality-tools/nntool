package com.zafaco.common.listener;

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
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.zafaco.common.Tool;
import com.zafaco.common.interfaces.ModulesInterface;

import org.json.JSONObject;

/**
 * Class ListenerPhone
 */
public class ListenerPhone extends PhoneStateListener
{
	/**************************** Variables ****************************/

    private Context ctx;

	//Module Objects
	private Tool mTool;
	private ModulesInterface interfaceCallback;
	private JSONObject jData;
	private TelephonyManager tm;
    private Thread pThread;

    private boolean withIntervall = false;
    private int state;

	/*******************************************************************/

    /**
     * Method ListenerPhone
     * @param ctx
     * @param intCall
     */
	public ListenerPhone(Context ctx, ModulesInterface intCall)
	{
		this.ctx = ctx;
		this.interfaceCallback = intCall;

		mTool = new Tool();

		jData = new JSONObject();

		tm = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);

		if (tm != null)
		{
			tm.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
		}
	}

    /**
     * Method getState
     */
	public void getState()
	{
		try
		{
			//----------------------------------------------

			jData.put("app_call_state", mTool.isCalling(ctx) ? "1" : "0");

			//----------------------------------------------

			//Callback
			interfaceCallback.receiveData(jData);
		}
		catch (Exception ex) { mTool.printTrace(ex); }
	}

    /**
     * Method withIntervall
     */
	public void withIntervall()
    {
        pThread = new Thread(new WorkerThread());
        pThread.start();

        withIntervall = true;
    }

    /**
     * Method stopUpdates
     */
	public void stopUpdates()
	{
        if( withIntervall )
            pThread.interrupt();

        withIntervall = false;
	}

	//PhoneStateListener.LISTEN_CALL_STATE
	@Override
	public void onCallStateChanged(int state, String incomingNumber)
	{
		super.onCallStateChanged(state, incomingNumber);

        this.state = state;

        getData();
	}

    private void getData()
    {
        try
        {
            //----------------------------------------------

            jData.put("app_call_state", state);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (tm.getPhoneCount() == 2)
                {
                    mTool.printOutput("DEBUG [getPhoneCount]:"+tm.getPhoneCount());
                }
            }
            //----------------------------------------------

            //Callback
            interfaceCallback.receiveData(jData);
        }
        catch (Exception ex) { mTool.printTrace(ex); }
    }

    class WorkerThread extends Thread
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    getData();

                    //Every 10 Seconds
                    Thread.sleep(10000);
                }
                catch (InterruptedException ex) { mTool.printTrace(ex); }
            }
        }
    }
}
