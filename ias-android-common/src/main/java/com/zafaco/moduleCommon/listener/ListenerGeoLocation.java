package com.zafaco.moduleCommon.listener;

/*!
    \file ListenerGeoLocation.java
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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.zafaco.moduleCommon.Tool;
import com.zafaco.moduleCommon.interfaces.ModulesInterface;

import org.json.JSONObject;

/**
 * Class ListenerGeoLocation
 */
public class ListenerGeoLocation
{
	/**************************** Variables ****************************/

	private Context ctx;

	//Module Objects
	private Tool mTool;
	private ModulesInterface interfaceCallback;
	private JSONObject jData;

	private FusedLocationProviderClient mFusedLocationClient = null;

	private boolean mtGeoService = false;

	private int min_time;
	private int min_distance;
	private int min_prio;

	private Location lastLocation;

	/*******************************************************************/

	/**
	 * Method ListenerGeoLocation
	 * @param ctx
	 * @param intCall
	 */
	public ListenerGeoLocation(Context ctx, ModulesInterface intCall)
	{
		this.ctx = ctx;

		this.interfaceCallback = intCall;

		mTool = new Tool();

		jData = new JSONObject();
	}

	/**
	 * Method setGeoService
	 */
	public void setGeoService()
	{
		mtGeoService = true;
	}

	/**
	 * Method getPosition
	 * @param min_time
	 * @param min_distance
	 * @param min_prio
	 */
	public void getPosition(int min_time, int min_distance, int min_prio)
	{
		this.min_time = min_time;
		this.min_distance = min_distance;
		this.min_prio = min_prio;

		if (	ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
				ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			return;
		}

		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);

		// Create the location request to start receiving updates
		LocationRequest mLocationRequest = new LocationRequest();
		mLocationRequest.setPriority(this.min_prio);
		mLocationRequest.setInterval(this.min_time);
		mLocationRequest.setFastestInterval(this.min_time);
		mLocationRequest.setSmallestDisplacement(this.min_distance);

		getLastLocation(mFusedLocationClient);

		mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
	}

	/**
	 * Method stopUpdates
	 */
	public void stopUpdates()
	{
		mFusedLocationClient.removeLocationUpdates(mLocationCallback);
	}

	/**
	 * Handler mLocationCallback
	 */
	private LocationCallback mLocationCallback = new LocationCallback()
	{
		/**
		 * Method onLocationResult
		 * @param locationResult
		 */
		@Override
		public void onLocationResult(LocationResult locationResult)
		{
			// do work here
			getLocation(locationResult.getLastLocation());
		}
	};

	/**
	 * Method getLastLocation
	 * @param mFusedLocationClient
	 */
	private void getLastLocation(FusedLocationProviderClient mFusedLocationClient)
	{
		if (	ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
				ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			return;
		}

		mFusedLocationClient.getLastLocation()
			.addOnSuccessListener(new OnSuccessListener<Location>()
			{
				/**
				 * Method onSuccess
				 * @param lastKnownLocation
				 */
				@Override
				public void onSuccess(Location lastKnownLocation)
				{
					// GPS location can be null if GPS is switched off
					if (lastKnownLocation != null)
					{
						//onLocationChanged(location);
						try
						{
							jData = new JSONObject();

							jData.put("app_latitude", lastKnownLocation.getLatitude());
							jData.put("app_longitude", lastKnownLocation.getLongitude());
							jData.put("app_altitude", lastKnownLocation.getAltitude());
							jData.put("app_velocity", lastKnownLocation.getSpeed());
							jData.put("app_accuracy", 100.0);

							//Callback
							interfaceCallback.receiveData(jData);
						}
						catch (Exception ex) { mTool.printTrace(ex); }
					}
				}
			})
			.addOnFailureListener(new OnFailureListener()
			{
				/**
				 * Method onFailure
				 * @param e
				 */
				@Override
				public void onFailure(@NonNull Exception e)
				{
				}
			});
	}

	/**
	 * Method getLocation
	 * @param location
	 */
	private void getLocation(Location location)
	{
		double newAccuracy = location.getAccuracy();

		if(
				(
					//If we are NOT in TestCase "Coverage", set Location only if Accuracy is smaller (better) then value before
					newAccuracy != 0.0 &&
					newAccuracy <= ( lastLocation == null ? 0.0 : lastLocation.getAccuracy() )
				)  ||
				(
					//If TestCase "Coverage" request normal Location updates
					mtGeoService
				)
		)
		{
			try
			{
				jData = new JSONObject();

				jData.put("app_latitude", location.getLatitude());
				jData.put("app_longitude", location.getLongitude());
				jData.put("app_altitude", location.getAltitude());
				jData.put("app_velocity", location.getSpeed());
				jData.put("app_accuracy", newAccuracy);

				//Callback
				interfaceCallback.receiveData(jData);
			}
			catch (Exception ex) { mTool.printTrace(ex); }

			lastLocation = location;
		}
	}
}
