package com.zafaco.moduleCommon.listener;

/*!
    \file ListenerNetwork.java
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
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.zafaco.moduleCommon.Tool;
import com.zafaco.moduleCommon.interfaces.ModulesInterface;

import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * Class ListenerNetwork
 */
public class ListenerNetwork extends PhoneStateListener
{
	/**************************** Variables ****************************/

	private Context ctx;

	//Module Objects
	private Tool mTool;
	private ModulesInterface interfaceCallback;
	private ServiceState serviceState;
	private TelephonyManager tm;
	private Thread pThread;
	private boolean withIntervall = false;

	/*******************************************************************/

	/**
	 * Method ListenerNetwork
	 * @param ctx
	 * @param intCall
	 */
	public ListenerNetwork(Context ctx, ModulesInterface intCall)
	{
		this.ctx = ctx;
		this.interfaceCallback = intCall;

		mTool = new Tool();

		tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
	}

	/**
	 * Method getState
	 */
	public void getState()
	{
		getData();

		//ACCESS_FINE_LOCATION
		tm.listen(
				this,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS |
						PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
						PhoneStateListener.LISTEN_DATA_ACTIVITY |
						PhoneStateListener.LISTEN_CELL_LOCATION |
						PhoneStateListener.LISTEN_SERVICE_STATE
		);
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
		tm.listen(this, PhoneStateListener.LISTEN_NONE);

		if( withIntervall )
			pThread.interrupt();

		withIntervall = false;
	}

	/**
	 * Method onSignalStrengthsChanged
	 * @require PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
	 * @param signalStrength
	 */
	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength)
	{
		super.onSignalStrengthsChanged(signalStrength);

		getData();
	}

	/**
	 * Method onDataConnectionStateChanged
	 * @require PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
	 * @param networkState
	 * @param networkType
	 */
	@Override
	public void onDataConnectionStateChanged(int networkState, int networkType)
	{
		super.onDataConnectionStateChanged(networkState, networkType);

		getData();
	}

	/**
	 * Method onDataActivity
	 * @require PhoneStateListener.LISTEN_DATA_ACTIVITY
	 * @param directon
	 */
	@Override
	public void onDataActivity(int directon)
	{
		super.onDataActivity(directon);

		getData();
	}

	/**
	 * Method onCellLocationChanged
	 * @require PhoneStateListener.LISTEN_CELL_LOCATION
	 * @param location
	 */
	@Override
	public void onCellLocationChanged(CellLocation location)
	{
		super.onCellLocationChanged(location);

		getData();
	}

	/**
	 * Method onServiceStateChanged
	 * @require PhoneStateListener.LISTEN_SERVICE_STATE
	 * @param serviceState
	 */
	@Override
	public void onServiceStateChanged(ServiceState serviceState)
	{
		super.onServiceStateChanged(serviceState);

		this.serviceState = serviceState;

		getData();
	}

	/**
	 * Method getData
	 */
	private void getData()
	{
		JSONObject jData = new JSONObject();

		String app_operator_netcode = tm.getNetworkOperator();
		String app_operator_simcode = tm.getSimOperator();

		String app_operator_sim = "";
		String app_operator_net = tm.getNetworkOperatorName();

		if(tm.getSimState() == TelephonyManager.SIM_STATE_READY)
		{
			app_operator_sim = tm.getSimOperatorName();
		}

		try
		{
			jData.put("app_mode", mTool.isWifi(ctx) ? "WIFI" : "WWAN");
			jData.put("app_access", mTool.getNetType(tm.getNetworkType()));
			jData.put("app_access_id", tm.getNetworkType());

			//--------------------------------------------------------------------------------------

			if( serviceState != null )
			{
				jData.put("app_voice", mTool.getVoicetype(serviceState.getState()));
				jData.put("app_voice_id", getVoiceNetworkType(serviceState));
			}
			else
			{
				jData.put("app_voice", mTool.getVoicetype(-1));
				jData.put("app_voice_id", -1);
			}

			//--------------------------------------------------------------------------------------

			if( !app_operator_netcode.equals("") )
			{
				jData.put("app_operator_net_mcc", app_operator_netcode.substring(0, 3));
				jData.put("app_operator_net_mnc", app_operator_netcode.substring(3));
			}

			if( !app_operator_simcode.equals("") )
			{
				jData.put("app_operator_sim_mcc", app_operator_simcode.substring(0, 3));
				jData.put("app_operator_sim_mnc", app_operator_simcode.substring(3));
			}

			if( !app_operator_net.equals("") && !app_operator_net.equals("null"))
				jData.put("app_operator_net", app_operator_net);

			if( !app_operator_sim.equals("") && !app_operator_sim.equals("null"))
				jData.put("app_operator_sim", app_operator_sim);

			//--------------------------------------------------------------------------------------

			//Callback
			interfaceCallback.receiveData(jData);
		}
		catch (Exception ex)
		{
			mTool.printTrace(ex);
		}
	}

	/**
	 * Method getVoiceNetworkType
	 * @param serviceState
	 * @return
	 */
    private int getVoiceNetworkType( ServiceState serviceState )
    {
        try
		{
            // Java reflection to gain access to TelephonyManager's
            // ITelephony getter
            Class c = Class.forName(serviceState.getClass().getName());
            Method mI = c.getDeclaredMethod("getRilVoiceRadioTechnology");

            mI.setAccessible(true);

			return (int) (Integer)mI.invoke(serviceState);
        }
        catch (Exception ex)
		{
			mTool.printTrace(ex);
		}

        return -1;
    }

	/**
	 * Class WorkerThread
	 */
	class WorkerThread extends Thread
	{
		/**
		 * Method run
		 */
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
