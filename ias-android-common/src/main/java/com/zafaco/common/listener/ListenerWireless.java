package com.zafaco.common.listener;

/*!
    \file ListenerWireless.java
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.zafaco.common.Tool;
import com.zafaco.common.interfaces.ModulesInterface;

import org.json.JSONObject;

public class ListenerWireless extends BroadcastReceiver
{
	/**************************** Variables ****************************/

	Context ctx;

	//Module Objects
	private Tool mTool;
	private ModulesInterface interfaceCallback;
	private WifiManager wm;
	private Thread pThread;
	private boolean withIntervall = false;

	/*******************************************************************/

	private String sState = "COMPLETED";

	/*******************************************************************/

	/**
	 * Constructor
	 */
	public ListenerWireless(Context ctx, ModulesInterface intCall)
	{
		this.ctx = ctx;
		this.interfaceCallback = intCall;

		mTool = new Tool();

		wm = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

		ctx.registerReceiver(this, iFilter);

	}

	public void getState()
	{
		onReceive(ctx, new Intent());
	}

	public void withIntervall()
	{
		pThread = new Thread(new WorkerThread());
		pThread.start();

		withIntervall = true;
	}

	public void stopUpdates()
	{
		ctx.unregisterReceiver(this);

		if( withIntervall )
			pThread.interrupt();

		withIntervall = false;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		getData();
	}

	private void getData()
	{
		JSONObject jData = new JSONObject();

		try
		{
			wm = (WifiManager)ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

			//---------------------------------------------

			jData.put("app_mode", mTool.isWifi(ctx) ? "WIFI" : "WWAN");

			//----------------------------------------------

			//Callback
			interfaceCallback.receiveData(jData);
		}
		catch (Exception ex)
		{
			mTool.printTrace(ex);
		}
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
