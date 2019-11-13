package com.zafaco.moduleCommon.listener;

/*!
    \file ListenerTelefon.java
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import android.content.Context;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.zafaco.moduleCommon.Tool;
import com.zafaco.moduleCommon.interfaces.ModulesInterface;

import org.json.JSONObject;

/**
 * Class ListenerTelefon
 */
public class ListenerTelefon extends PhoneStateListener
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
     * Method ListenerTelefon
     * @param ctx
     * @param intCall
     */
	public ListenerTelefon(Context ctx, ModulesInterface intCall)
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
