package com.zafaco.moduleCommon.listener;

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
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.zafaco.moduleCommon.Tool;
import com.zafaco.moduleCommon.interfaces.ModulesInterface;

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
