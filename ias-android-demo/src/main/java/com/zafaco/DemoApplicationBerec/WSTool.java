package com.zafaco.DemoApplicationBerec;

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

import java.util.Iterator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zafaco.common.Common;
import com.zafaco.common.Database;
import com.zafaco.common.Http;
import com.zafaco.common.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class WSTool
 */
public final class WSTool
{
	//Singleton
	private static volatile WSTool mInstance = null;

	/**************************** Loging Level ****************************/
	private boolean DEBUG = false;

	/****************** Measurement Parameters ********************/
	private String platform                         = "mobile";

	private boolean performRttMeasurement           = true;
	private boolean performDownloadMeasuement       = true;
	private boolean performUploadMeasurement        = true;

	private int performRouteToClientLookup          = 8080;

	private String wsTargets						= "peer-ias-de-01";
	private String wsTargetsRtt						= "peer-ias-de-01";
	private String wsTLD 							= "net-neutrality.tools";

	private int wsParallelStreamsDownload      		= 4;
	private int wsParallelStreamsUpload        		= 4;

	private boolean wsUseEncryption		        		= true;

	/************************* Default Parameters *************************/

	private static String defaultIndexUrl                         = "";

	/***************************** Variables ******************************/
	private Context ctx;

	private static JSONObject mDataStorage;
	private static JSONObject mDataStorageUI;
	private static JSONObject mDataStorageProblem;

	private boolean startSequence = false;
	private boolean cancelSequence = false;

	/****************************** Objects *******************************/
	private Common mCommon;
	private Tool mTool;
	private Http mHttp;
	private Database mDatabase;

	private SharedPreferences appPref;

	/**************************** Public Functions ****************************/

	/**
	 * Method getInstance
	 * @return
	 */
	public static WSTool getInstance()
	{
		if (mInstance == null)
		{
			if (mInstance == null)
			{
				if (mInstance == null)
				{
					mInstance = new WSTool();
				}
			}
		}
		return mInstance;
	}

	/**
	 * constructor of java
	 */
	private WSTool()
	{
		mTool = new Tool();
		mHttp = new Http();

		mDataStorage = new JSONObject();
		mDataStorageUI = new JSONObject();
		mDataStorageProblem = new JSONObject();

		setString("app_version", BuildConfig.VERSION_NAME);
		setStringUI("app_version", BuildConfig.VERSION_NAME);
	}

	//----------------------------------------------------------------------------------------------

	/**
	 * Method getCtx
	 * @return
	 */
	public Context getCtx()
	{
		return this.ctx;
	}

	/**
	 * Method setCtx
	 * @param ctx
	 */
	public void setCtx(Context ctx)
	{
		this.ctx = ctx;

        mCommon = new Common(ctx);

		//mSpeed = new Speed(ctx);

		mCommon.setDebug(DEBUG);
	}

	/**
	 * Method getCommonObject
	 * @return
	 */
	public Common getCommonObject()
	{
		return this.mCommon;
	}

	/**
	 * Method getToolObject
	 * @return
	 */
	public Tool getToolObject()
	{
		return this.mTool;
	}

	/**
	 * Method getVersion
	 * @param sTestCase
	 * @return
	 */
	public String getVersion(String sTestCase)
	{
		JSONObject jData = new JSONObject();

		try
        {
            jData.put("common", com.zafaco.common.BuildConfig.VERSION_NAME);

            switch (sTestCase)
            {
                case "app":
                    //Special Case
                    return BuildConfig.VERSION_NAME;
                case "speed":
                    jData.put("speed", com.zafaco.moduleSpeed.BuildConfig.VERSION_NAME);
                    break;
            }
        }
		catch (Exception ex) { mTool.printTrace(ex); }

		return jData.toString();
	}

	//----------------------------------------------------------------------------------------------

	/**
	 * Method initSpeedParameter
	 */
	public void initSpeedParameter()
	{
		try
		{
			JSONObject mMeasurementParameter = new JSONObject();

			mMeasurementParameter.put("cmd", "start");
			mMeasurementParameter.put("platform", platform);
			mMeasurementParameter.put("wsTargets", new JSONArray().put(wsTargets));
			mMeasurementParameter.put("wsTargetsRtt", new JSONArray().put(wsTargetsRtt));
			mMeasurementParameter.put("wsTLD", wsTLD);
			mMeasurementParameter.put("wsTargetPort", 80);
			mMeasurementParameter.put("wsWss", 0);
			mMeasurementParameter.put("wsAuthToken", "placeholderToken");
			mMeasurementParameter.put("wsAuthTimestamp", "placeholderTimestamp");

            mMeasurementParameter.put("performRttMeasurement", performRttMeasurement);
            mMeasurementParameter.put("performDownloadMeasurement", performDownloadMeasuement);
            mMeasurementParameter.put("performUploadMeasurement", performUploadMeasurement);

			mMeasurementParameter.put("wsParallelStreamsDownload", wsParallelStreamsDownload);
			mMeasurementParameter.put("wsParallelStreamsUpload", wsParallelStreamsUpload);

			mMeasurementParameter.put("cookieId", false);

			//Add to Library
			Common.addToJSONMTWSMeasurement(mMeasurementParameter);

			//Add to UI
			addToJSONUI(mMeasurementParameter);
		}
		catch(JSONException ex) { mTool.printTrace(ex); }
	}

	//----------------------------------------------------------------------------------------------

	/**
	 * Method getObject
	 * @param sKey
	 * @param nDefault
	 * @return
	 */
	public Object getObject(String sKey, Object nDefault)
	{
		try
		{
			return mDataStorage.get(sKey);
		}
		catch(Exception ex)
		{
			mTool.printTrace(ex);

			return nDefault;
		}
	}

	/**
	 * Method getBoolean
	 * @param sKey
	 * @param nDefault
	 * @return
	 */
	public boolean getBoolean(String sKey, boolean nDefault)
	{
		try
		{
			return mDataStorage.getBoolean(sKey);
		}
		catch(Exception ex)
		{
			mTool.printTrace(ex);

			return nDefault;
		}
	}

	/**
	 * Method getInt
	 * @param sKey
	 * @param nDefault
	 * @return
	 */
	public int getInt(String sKey, int nDefault)
	{
		try
		{
			return mDataStorage.getInt(sKey);
		}
		catch(Exception ex)
		{
			mTool.printTrace(ex);

			return nDefault;
		}
	}

	/**
	 * Method getDouble
	 * @param sKey
	 * @param nDefault
	 * @return
	 */
	public double getDouble(String sKey, double nDefault)
	{
		try
		{
			return mDataStorage.getDouble(sKey);
		}
		catch(Exception ex)
		{
			mTool.printTrace(ex);

			return nDefault;
		}
	}

	/**
	 * Method getString
	 * @param sKey
	 * @param nDefault
	 * @return
	 */
	public String getString(String sKey, String nDefault)
	{
		try
		{
			return mDataStorage.getString(sKey);
		}
		catch(Exception ex)
		{
			mTool.printTrace(ex);

			return nDefault;
		}
	}

	//----------------------------------------------------------------------------------------------

	/**
	 * Method setObject
	 * @param sKey
	 * @param nDefault
	 */
	public void setObject(String sKey, Object nDefault)
	{
		try
		{
			mDataStorage.put(sKey,nDefault);
		}
		catch(Exception ex) { mTool.printTrace(ex); }
	}

	/**
	 * Method setBoolean
	 * @param sKey
	 * @param sValue
	 */
	public void setBoolean(String sKey, boolean sValue)
	{
		try
		{
			mDataStorage.put(sKey, sValue);
		}
		catch(Exception ex) { mTool.printTrace(ex); }
	}

	/**
	 * Method setInt
	 * @param sKey
	 * @param sValue
	 */
	public void setInt(String sKey, int sValue)
	{
		try
		{
			mDataStorage.put(sKey, sValue);
		}
		catch(Exception ex) { mTool.printTrace(ex); }
	}

	/**
	 * Method setDouble
	 * @param sKey
	 * @param sValue
	 */
	public void setDouble(String sKey, double sValue)
	{
		try
		{
			mDataStorage.put(sKey, sValue);
		}
		catch(Exception ex) { mTool.printTrace(ex); }
	}

	/**
	 * Method setString
	 * @param sKey
	 * @param sValue
	 */
	public void setString(String sKey, String sValue)
	{
		try
		{
			mDataStorage.put(sKey,sValue);
		}
		catch(Exception ex) { mTool.printTrace(ex); }
	}

	//----------------------------------------------------------------------------------------------

	/**
	 * Method getObjectUI
	 * @param sKey
	 * @param nDefault
	 * @return
	 */
	public Object getObjectUI(String sKey, Object nDefault)
	{
		try
		{
			return mDataStorageUI.get(sKey);
		}
		catch(Exception ex)
		{
			mTool.printTrace(ex);

			return nDefault;
		}
	}

	/**
	 * Method getBooleanUI
	 * @param sKey
	 * @param nDefault
	 * @return
	 */
	public boolean getBooleanUI(String sKey, boolean nDefault)
	{
		try
		{
			return mDataStorageUI.getBoolean(sKey);
		}
		catch(Exception ex)
		{
			mTool.printTrace(ex);

			return nDefault;
		}
	}

	/**
	 * Method getIntUI
	 * @param sKey
	 * @param nDefault
	 * @return
	 */
	public int getIntUI(String sKey, int nDefault)
	{
		try
		{
			return mDataStorageUI.getInt(sKey);
		}
		catch(Exception ex)
		{
			mTool.printTrace(ex);

			return nDefault;
		}
	}

	/**
	 * Method getDoubleUI
	 * @param sKey
	 * @param nDefault
	 * @return
	 */
	public double getDoubleUI(String sKey, double nDefault)
	{
		try
		{
			return mDataStorageUI.getDouble(sKey);
		}
		catch(Exception ex)
		{
			mTool.printTrace(ex);

			return nDefault;
		}
	}

	/**
	 * Method getStringUI
	 * @param sKey
	 * @param nDefault
	 * @return
	 */
	public String getStringUI(String sKey, String nDefault)
	{
		try
		{
			return mDataStorageUI.getString(sKey);
		}
		catch(Exception ex)
		{
			mTool.printTrace(ex);

			return nDefault;
		}
	}

	//----------------------------------------------------------------------------------------------

	/**
	 * Method setObjectUI
	 * @param sKey
	 * @param nDefault
	 */
	public void setObjectUI(String sKey, Object nDefault)
	{
		try
		{
			mDataStorageUI.put(sKey,nDefault);
		}
		catch(Exception ex) { mTool.printTrace(ex); }
	}

	/**
	 * Method setBooleanUI
	 * @param sKey
	 * @param sValue
	 */
	public void setBooleanUI(String sKey, boolean sValue)
	{
		try
		{
			mDataStorageUI.put(sKey, sValue);
		}
		catch(Exception ex) { mTool.printTrace(ex); }
	}

	/**
	 * Method setIntUI
	 * @param sKey
	 * @param sValue
	 */
	public void setIntUI(String sKey, int sValue)
	{
		try
		{
			mDataStorageUI.put(sKey, sValue);
		}
		catch(Exception ex) { mTool.printTrace(ex); }
	}

	/**
	 * Method setDoubleUI
	 * @param sKey
	 * @param sValue
	 */
	public void setDoubleUI(String sKey, double sValue)
	{
		try
		{
			mDataStorageUI.put(sKey, sValue);
		}
		catch(Exception ex) { mTool.printTrace(ex); }
	}

	/**
	 * Method setStringUI
	 * @param sKey
	 * @param sValue
	 */
	public void setStringUI(String sKey, String sValue)
	{
		try
		{
			mDataStorageUI.put(sKey,sValue);
		}
		catch(Exception ex) { mTool.printTrace(ex); }
	}

	//----------------------------------------------------------------------------------------------

	/**
	 * Method addToJSONUI
	 * @param object
	 */
	public void addToJSONUI(JSONObject object)
	{
		try
		{
			for (Iterator<String> iter = object.keys(); iter.hasNext(); )
			{
				String key = iter.next();
				mDataStorageUI.put(key,object.getString(key));
			}
		}
		catch(Exception ex) {}
	}

	/**
	 * Method getJSONUI
	 * @return
	 */
	public JSONObject getJSONUI()
	{
		return mDataStorageUI;
	}

	//----------------------------------------------------------------------------------------------

	/**
	 * Method addToJSONMeasurement
	 * @param object
	 */
	public static void addToJSONMeasurement(JSONObject object)
	{
		try
		{
			for (Iterator<String> iter = object.keys(); iter.hasNext(); )
			{
				String key = iter.next();
				mDataStorage.put(key,object.getString(key));
			}
		}
		catch(Exception ex) {}
	}

	/**
	 * Method getJSONMeasurement
	 * @return
	 */
	public static JSONObject getJSONMeasurement()
	{
		return mDataStorage;
	}

	/**
	 * Method setIPAuto
	 */
	public void setIPAuto()
	{
		if(wsTargets.contains("ipv"))
		{
			wsTargets = wsTargets.substring(0, wsTargets.lastIndexOf("-"));
		}
		if(wsTargetsRtt.contains("ipv"))
		{
			wsTargetsRtt = wsTargetsRtt.substring(0, wsTargetsRtt.lastIndexOf("-"));
		}
	}

	/**
	 * Method setIPV4
	 */
	public void setIPV4()
	{
		setIPAuto();

		wsTargets = wsTargets+"-ipv4";
		wsTargetsRtt = wsTargetsRtt+"-ipv4";
	}

	/**
	 * Method setIPV6
	 */
	public void setIPV6()
	{
		setIPAuto();

		wsTargets = wsTargets+"-ipv6";
		wsTargetsRtt = wsTargetsRtt+"-ipv6";
	}

	/**
	 * Method setUploadProfileVeryHigh
	 */
	public void setSingleStreamOff()
	{
		wsParallelStreamsDownload = 4;

		wsParallelStreamsUpload = 4;
	}

	/**
	 * Method setUploadProfileVeryHigh
	 */
	public void setSingleStreamOn()
	{
		wsParallelStreamsDownload = 1;
		wsParallelStreamsUpload = 1;

	}

	/**
	 * Method setUploadProfileVeryHigh
	 */
	public void setUseEncryption(boolean bUseEncryption )
	{
		wsUseEncryption = bUseEncryption;

	}
	//TestCases ------------------------------------------------------------------------------------

	/**
	 * Method setTestcaseAll
	 */
	public void setTestcaseAll()
	{
		performRttMeasurement             = true;
		performDownloadMeasuement         = true;
		performUploadMeasurement          = true;
	}

	/**
	 * Method setTestcaseAll
	 */
	public void setTestcaseRTT()
	{
		performRttMeasurement             = true;
		performDownloadMeasuement         = false;
		performUploadMeasurement          = false;
	}

	/**
	 * Method setTestcaseAll
	 */
	public void setTestcaseDownload()
	{
		performRttMeasurement             = false;
		performDownloadMeasuement         = true;
		performUploadMeasurement          = false;
	}

	/**
	 * Method setTestcaseAll
	 */
	public void setTestcaseUpload()
	{
		performRttMeasurement             = false;
		performDownloadMeasuement         = false;
		performUploadMeasurement          = true;
	}

	//----------------------------------------------------------------------------------------------

//	/**
//	 * Method saveData
//	 */
//	public void saveData()
//    {
//		appPref = PreferenceManager.getDefaultSharedPreferences(ctx);
//
//		try
//        {
//			JSONObject data = new JSONObject();
//			data.put("measurement_name",getString("measurement_name","-"));
//			data.put("timestamp", System.currentTimeMillis());
//			data.put("timezone", TimeZone.getDefault().getOffset(data.getInt("timestamp")) / 1000);
//
//			//Add - Only Device
//			Common.addToJSONMTWSMeasurement(data);
//			//Add - Additional Infos
//			Common.addToJSONMTWSMeasurement(mDataStorage);
//			//Add - Additional Infos from UI
//			Common.addToJSONMTWSMeasurement(mDataStorageUI);
//
//			mTool.printJSONObject(Common.getJSONMTWSMeasurement());
//
//			//Database
//			mDatabase = new Database(ctx, "measurements","speed");
//			mDatabase.createDB(Common.getJSONMTWSMeasurement());
//			mDatabase.insert(Common.getJSONMTWSMeasurement());
//		}
//        catch (Exception ex) { mTool.printTrace(ex); }
//	}

	/**
	 * Method getAppVersion
	 */
	public void getAppVersion()
	{
		appPref = PreferenceManager.getDefaultSharedPreferences(ctx);

		//--------------------------------------

		Long user = appPref.getLong("user", 0);
		if(user == 0)
		{
			SharedPreferences.Editor editor = appPref.edit();
			Long uts = System.currentTimeMillis();
			editor.putLong("user", uts);
			editor.apply();
		}

		setInt("measurement_id",appPref.getInt("meas", 1));

        //--------------------------------------

		String id = appPref.getString("client_installation_id","-");
		if( id.equals("-"))
		{
			SharedPreferences.Editor editor = appPref.edit();

			String cii = mTool.generateInstallationId();
			editor.putString("client_installation_id", cii);
			editor.apply();
		}

		try {
			JSONObject jData = new JSONObject();
			jData.put("client_installation_id", appPref.getString("client_installation_id","-"));

			Common.addToJSONMTWSMeasurementAdditional(jData);
		}
		catch (Exception ex) { mTool.printTrace(ex); }

	}
}
