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
 *      \date Last update: 2019-05-08
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "callback.h"

//! \brief
//!	Standard Destructor
CCallback::CCallback()
{
}

//! \brief
//!	Virtual Destructor
CCallback::~CCallback()
{
}

//! \brief
//!    Run-Function
//! \return 0
int CCallback::run()
{
	//Log Message
	TRC_INFO( ("Starting Callback Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );

	//++++++MAIN++++++
	
	while( RUNNING && !m_fStop)
	{
		//Sleep 10ms
		usleep(1000);

		if( m_fStop )
			break;
	}	
	
	//++++++END+++++++

	//Log Message
	TRC_INFO( ("Ending Callback Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );
	return 0;
}

void CCallback::callback(string cmd, string msg, int error_code, string error_description)
{
	TRC_DEBUG("Callback Received: cmd: " + cmd + ", msg: " + msg);

	if (cmd.compare("report") == 0 || cmd.compare("finish") == 0)
	{
		if (mTestCase == 2)
		{
			rttUdpCallback(cmd);

			if (cmd.compare("finish") == 0)
			{
				PERFORMED_RTT = true;
			}
		}

		if (mTestCase == 3)
		{
			downloadCallback(cmd);

			if (cmd.compare("finish") == 0)
			{
				PERFORMED_DOWNLOAD = true;
			}
		}

		if (mTestCase == 4)
		{
			uploadCallback(cmd);

			if (cmd.compare("finish") == 0)
			{
				PERFORMED_UPLOAD = true;
			}
		}
	}

	callbackToPlatform(cmd, msg, error_code, error_description);

	if (cmd.compare("finish") == 0 && ::RTT == PERFORMED_RTT && ::DOWNLOAD == PERFORMED_DOWNLOAD && ::UPLOAD == PERFORMED_UPLOAD)
	{
		callbackToPlatform("completed", msg, error_code, error_description);
	}
}

void CCallback::callbackToPlatform(string cmd, string msg, int error_code, string error_description)
{
	//construct JSON callback
	jMeasurementResults = Json::object{};
	jMeasurementResults["cmd"] = cmd;
	jMeasurementResults["msg"] = msg;
	jMeasurementResults["test_case"] = conf.sTestName;

	if (error_code != 0)
	{
		jMeasurementResults["error_code"] = error_code;
		jMeasurementResults["error_description"] = error_description;
		TRC_ERR("Error: " + error_description + ", code: " + to_string(error_code));
	}

	if (Json(jMeasurementResultsRttUdp).object_items().size() > 0)
	{
		jMeasurementResults["rtt_udp_info"] = Json(jMeasurementResultsRttUdp);
	}

	if (Json(jMeasurementResultsDownload).array_items().size() > 0)
	{
		jMeasurementResults["download_info"] = Json(jMeasurementResultsDownload);
	}

	if (Json(jMeasurementResultsUpload).array_items().size() > 0)
	{
		jMeasurementResults["upload_info"] = Json(jMeasurementResultsUpload);
	}

	if (Json(jMeasurementResultsTime).object_items().size() > 0)
	{
		jMeasurementResults["time_info"] = Json(jMeasurementResultsTime);
	}
	
	string platform = ::PLATFORM;
	string clientos = ::CLIENT_OS;

	if (platform.compare("desktop") == 0 && clientos.compare("linux") == 0)
	{
		TRC_DEBUG("Callback: " + Json(jMeasurementResults).dump());
	}
	if (platform.compare("mobile") == 0 && clientos.compare("android") == 0)
	{
		//android callback hookup
		//callback Json(jMeasurementResults).dump() via ndk
	}
}

void CCallback::rttUdpCallback(string cmd)
{
	struct measurement tempMeasurement;

	//Lock Mutex
	pthread_mutex_lock(&mutex);

		struct measurement_data mPingResult = pingThread->mPingResult;

		pingThread->measurementTimeDuration = CTool::get_timestamp() - pingThread->measurementTimeStart;

		if (cmd.compare("finish") == 0)
		{
			pingThread->measurementTimeEnd = CTool::get_timestamp();
			jMeasurementResultsTime["rtt_udp_end"] = to_string(pingThread->measurementTimeEnd * 1000);
		}

		int nMissing = 0;
		int nReply = 0;
	
		//Starting multiple Instances for every Probe
		for(map<int, unsigned long long>::iterator AI = std::next(mPingResult.results.begin(),1); AI!= mPingResult.results.end(); ++AI)
		{
			//write to Global Object
			tempMeasurement.ping.results[(*AI).first] += (*AI).second;
			
			if( (*AI).second < 0 )
				nMissing++;
			else
				nReply++;
		}
		
		//---------------------------
		
		//Calculate Min, Avg, Max
		CTool::calculateResults( tempMeasurement.ping, 1, 0);
			
		//---------------------------
	
		tempMeasurement.ping.packetsize 	= pingThread->nSize;
		tempMeasurement.ping.hops			= pingThread->nHops;
		tempMeasurement.ping.requests 		= pingThread->mPingQuery - 1;
		tempMeasurement.ping.replies 		= nReply;
		tempMeasurement.ping.missing 		= nMissing;
		tempMeasurement.ping.errors 		= pingThread->nError;
		
		tempMeasurement.ping.starttime  	= pingThread->measurementTimeStart;
		tempMeasurement.ping.endtime    	= pingThread->measurementTimeEnd;
		tempMeasurement.ping.totaltime  	= pingThread->measurementTimeDuration;

		jMeasurementResultsTime["rtt_udp_start"] = to_string(tempMeasurement.ping.starttime * 1000);
		
		tempMeasurement.ping.client			= pingThread->mClient;
		tempMeasurement.ping.server    		= pingThread->mServer;
		tempMeasurement.ping.servername    	= pingThread->mServerName;
		
		tempMeasurement.ping.ipversion 		= pingThread->ipversion;
		
		tempMeasurement.ping.system_availability 	= pingThread->system_availability;
		
		if( nMissing > 0 )
		{
			pingThread->service_availability 	= 0;
			pingThread->error 					= 1;
			pingThread->error_description 		= "Missing Pings";
		}	
		else
		{
			pingThread->service_availability 	= 1;
			pingThread->error 					= 0;
			pingThread->error_description 		= "-";
		}
		
		tempMeasurement.ping.service_availability 	= pingThread->service_availability;
		tempMeasurement.ping.error_code				= pingThread->error;
		tempMeasurement.ping.error_description		= pingThread->error_description;
			
	//Unlock Mutex
	pthread_mutex_unlock(&mutex);

	TRC_INFO( ("RTT UDP: " + CTool::toString(tempMeasurement.ping.avg )).c_str());


	Json::object jMeasurementResults;
	jMeasurementResults["duration_ns"] = to_string(tempMeasurement.ping.duration_ns);
	jMeasurementResults["average_ns"] = to_string(tempMeasurement.ping.avg * 1000 * 1000);
	jMeasurementResults["median_ns"] = to_string(tempMeasurement.ping.median_ns);
	jMeasurementResults["min_ns"] = to_string(tempMeasurement.ping.min * 1000 * 1000);
	jMeasurementResults["max_ns"] = to_string(tempMeasurement.ping.max * 1000 * 1000);
	jMeasurementResults["num_sent"] = to_string(tempMeasurement.ping.requests );
	jMeasurementResults["num_received"] = to_string(tempMeasurement.ping.replies);
	jMeasurementResults["num_error"] = to_string(tempMeasurement.ping.errors);
	jMeasurementResults["num_missing"] = to_string(tempMeasurement.ping.missing);
	jMeasurementResults["packet_size"] = to_string(tempMeasurement.ping.packetsize);
	jMeasurementResults["standard_deviation_ns"] = to_string(tempMeasurement.ping.standard_deviation_ns);
	jMeasurementResults["peer"] = tempMeasurement.ping.servername;

	jMeasurementResultsRttUdp = jMeasurementResults;
}

void CCallback::downloadCallback(string cmd)
{
	struct measurement tempMeasurement;
	tempMeasurement.download.datasize_total = 0;
	tempMeasurement.streams = 0;
	tempMeasurement.download.totime = 0;

	for(vector<Download*>::iterator itThread = vDownloadThreads.begin(); itThread != vDownloadThreads.end(); ++itThread)
	{
		//Lock Mutex
		pthread_mutex_lock(&mutex);

			struct measurement_data mDownload = (*itThread)->mDownload;

			(*itThread)->measurementTimeDuration = CTool::get_timestamp() - (*itThread)->measurementTimeStart;

			if (cmd.compare("finish") == 0)
			{
				(*itThread)->measurementTimeEnd = CTool::get_timestamp();
				jMeasurementResultsTime["download_end"] = to_string((*itThread)->measurementTimeEnd * 1000);
			}
	
			unsigned long long nDownload0 = mDownload.results.begin()->first;
			
			//Get Max T0
			if( tempMeasurement.download.totime < nDownload0 )
				tempMeasurement.download.totime = nDownload0;
			
			//Starting multiple Instances for every Probe
			for(map<int, unsigned long long>::iterator AI = mDownload.results.begin(); AI!= mDownload.results.end(); ++AI)
			{
				//write to Global Object
				tempMeasurement.download.results[(*AI).first] 	+= (*AI).second;
				tempMeasurement.download.datasize  		+= (*AI).second;
				
				//TRC_DEBUG( ("Results ["+CTool::toString( (*AI).first )+"]: "+CTool::toString( (*AI).second ) ).c_str() );
			}

			tempMeasurement.download.datasize_total += mDownload.datasize_total;
			
			//Must be a valid value and non zero
			if( (*itThread)->nHttpResponseDuration != 0 )
			{	
				tempMeasurement.download.httpresponse[(*itThread)->pid]		= (*itThread)->nHttpResponseDuration;
				//TRC_DEBUG( ("httpresponse ["+CTool::toString( (*itThread)->pid )+"]: "+CTool::toString( (*itThread)->nHttpResponseDuration ) ).c_str() );
			}
			
			tempMeasurement.download.packetsize 		= MAX_PACKET_SIZE;
			
			tempMeasurement.download.starttime  		= (*itThread)->measurementTimeStart;
			tempMeasurement.download.endtime    		= (*itThread)->measurementTimeEnd;
			tempMeasurement.download.totaltime  		= (*itThread)->measurementTimeDuration;
			
			jMeasurementResultsTime["download_start"] = to_string(tempMeasurement.download.starttime * 1000);

			//Socket closed unexpectedly
			if( (*itThread)->mResponse == -1 )
			{
				//If an error occured twice
				if( (*itThread)->error != 0 )
					(*itThread)->error_description		+= "/";
				
				(*itThread)->service_availability 		= 0;
				(*itThread)->error 						= 1;
				(*itThread)->error_description 			+= "Socket closed";
			}	
			
			//No Data from Socket
			if( (*itThread)->mResponse == 0 )
			{
				//If an error occured twice
				if( (*itThread)->error != 0 )
					(*itThread)->error_description		+= "/";
				
				(*itThread)->service_availability 		= 0;
				(*itThread)->error 						= 1;
				(*itThread)->error_description 			+= "No Data from Socket";
			}
			
			tempMeasurement.download.system_availability 	= (*itThread)->system_availability;
			
			//If one thread as finished with ok, then test is ok, 
			//or we detected a httpresponse above the limit
			if( tempMeasurement.download.service_availability == 0 || tempMeasurement.download.error_code == 2 || (*itThread)->error == 2 )
			{
				tempMeasurement.download.service_availability 	= (*itThread)->service_availability;
				tempMeasurement.download.error_code				= (*itThread)->error;
				tempMeasurement.download.error_description		= (*itThread)->error_description;
			}
			
			if( (*itThread)->mResponse > 0 )
				tempMeasurement.streams++;

		//Unlock Mutex
		pthread_mutex_unlock(&mutex);
	}

	map<int, unsigned long long> mTmpMap;

	//Starting multiple Instances for every Probe
	for (map<int, unsigned long long>::iterator AI = tempMeasurement.download.results.begin(); AI!= tempMeasurement.download.results.end(); ++AI)
	{
		mTmpMap[(*AI).first] 	+= (*AI).second;
	}

	//Reset Data Structs
	tempMeasurement.download.results.clear();
	tempMeasurement.download.datasize = 0;
		
	for( unsigned int i = tempMeasurement.download.totime; i < tempMeasurement.download.totime + (TIMER_DURATION/500000); i++ )
	{
		tempMeasurement.download.results[ i ] = 0;

		map<int, unsigned long long>::iterator AI = mTmpMap.find(i);
		
		if( AI != mTmpMap.end() )
		{
			//write to Global Object
			tempMeasurement.download.results[i]		+= (*AI).second;
			tempMeasurement.download.datasize 		+= (*AI).second;
			
			//TRC_DEBUG( ("All Results ["+CTool::toString( (*AI).first )+"]: "+CTool::toString( (*AI).second ) ).c_str() );
		}
	}

	//---------------------------

	//Calculate Min, Avg, Max
	CTool::calculateResults( tempMeasurement.download, 0.5, 0 );

	TRC_INFO( ("DOWNLOAD: " + CTool::toString(tempMeasurement.download.avg )).c_str());

	Json::object jMeasurementResults = getMeasurementResults(tempMeasurement, tempMeasurement.download, cmd);

	jMeasurementResultsDownload.push_back(jMeasurementResults);
}

void CCallback::uploadCallback(string cmd)
{
	struct measurement tempMeasurement;
	tempMeasurement.upload.datasize_total = 0;
	tempMeasurement.streams = 0;
	tempMeasurement.upload.totime = 0;

	for(vector<Upload*>::iterator itThread = vUploadThreads.begin(); itThread != vUploadThreads.end(); ++itThread)
	{
		struct measurement_data mUpload = (*itThread)->mUpload;

		(*itThread)->measurementTimeDuration = CTool::get_timestamp() - (*itThread)->measurementTimeStart;

		if (cmd.compare("finish") == 0)
		{
			(*itThread)->measurementTimeEnd = CTool::get_timestamp();
			jMeasurementResultsTime["upload_end"] = to_string((*itThread)->measurementTimeEnd * 1000);
		}
		
		//Lock Mutex
		pthread_mutex_lock(&mutex);
		
			unsigned long long nUploadt0 = mUpload.results.begin()->first;
			
			//Get Max T0
			if( tempMeasurement.upload.totime < nUploadt0 )
				tempMeasurement.upload.totime = nUploadt0;
			
			//Starting multiple Instances for every Probe
			for (map<int, unsigned long long>::iterator AI = mUpload.results.begin(); AI!= mUpload.results.end(); ++AI)
			{
				//write to Global Object
				tempMeasurement.upload.results[(*AI).first] 	+= (*AI).second;
							
				//TRC_DEBUG( ("Results ["+CTool::toString( (*itThread)->pid )+"]["+CTool::toString( (*AI).first )+"]: "+CTool::toString( (*AI).second ) ).c_str() );
			}

			tempMeasurement.upload.datasize_total += mUpload.datasize_total;
			
			//Must be a valid value and non zero
			if( (*itThread)->nHttpResponseDuration != 0 )
			{
				tempMeasurement.upload.httpresponse[(*itThread)->pid]		= (*itThread)->nHttpResponseDuration;
				//TRC_DEBUG( ("httpresponse ["+CTool::toString( (*itThread)->pid )+"]: "+CTool::toString( (*itThread)->nHttpResponseDuration ) ).c_str() );
			}
			
			tempMeasurement.upload.packetsize 			= MAX_PACKET_SIZE;
			
			tempMeasurement.upload.starttime  			= (*itThread)->measurementTimeStart;
			tempMeasurement.upload.endtime    			= (*itThread)->measurementTimeEnd;
			tempMeasurement.upload.totaltime  			= (*itThread)->measurementTimeDuration;

			jMeasurementResultsTime["upload_start"] = to_string(tempMeasurement.upload.starttime * 1000);

			//Socket closed unexpectedly
			if( (*itThread)->mResponse == -1 )
			{
				//If an error occured twice
				if( (*itThread)->error == 1 )
					(*itThread)->error_description		+= "/";
				
				(*itThread)->service_availability 		= 0;
				(*itThread)->error 						= 1;
				(*itThread)->error_description 			+= "Socket closed";
			}	
			
			//No Data from Socket
			if( (*itThread)->mResponse == 0 )
			{
				//If an error occured twice
				if( (*itThread)->error == 1 )
					(*itThread)->error_description		+= "/";
				
				(*itThread)->service_availability 		= 0;
				(*itThread)->error 						= 1;
				(*itThread)->error_description 			+= "No Data from Socket";
			}
			
			tempMeasurement.upload.system_availability 	= (*itThread)->system_availability;
			
			//If one thread as finished with ok, then test is ok, 
			//or we detected a httpresponse above the limit
			if( tempMeasurement.upload.service_availability == 0 || tempMeasurement.upload.error_code == 2 || (*itThread)->error == 2 )
			{
				tempMeasurement.upload.service_availability 	= (*itThread)->service_availability;
				tempMeasurement.upload.error_code				= (*itThread)->error;
				tempMeasurement.upload.error_description		= (*itThread)->error_description;
			}
			
			if( (*itThread)->mResponse > 0 )
				tempMeasurement.streams++;
			
		//Unlock Mutex
		pthread_mutex_unlock(&mutex);
	}

	map<int, unsigned long long> mTmpMap;

	//Starting multiple Instances for every Probe
	for (map<int, unsigned long long>::iterator AI = tempMeasurement.upload.results.begin(); AI!= tempMeasurement.upload.results.end(); ++AI)
	{
		mTmpMap[(*AI).first] 	+= (*AI).second;
	}
	
	//Reset Data Structs
	tempMeasurement.upload.results.clear();
	tempMeasurement.upload.datasize = 0;
	
	unsigned int addedTimer = TIMER_DURATION/500000;
	if (addedTimer > (MEASUREMENT_DURATION-2)*2)
	{
		addedTimer = (MEASUREMENT_DURATION-2)*2;
	}
	for( unsigned int i = tempMeasurement.upload.totime; i < tempMeasurement.upload.totime + addedTimer*5; i+=5 )
	{
		tempMeasurement.upload.results[ i ] = 0;
		
		map<int, unsigned long long>::iterator AI = mTmpMap.find(i);
		
		if( AI != mTmpMap.end() )
		{
			//write to Global Object
			tempMeasurement.upload.results[i]	+= (*AI).second;
			tempMeasurement.upload.datasize 	+= (*AI).second;
			
			//TRC_DEBUG( ("All Results ["+CTool::toString( (*AI).first )+"]: "+CTool::toString( (*AI).second ) ).c_str() );
		}
	}

	//---------------------------

	//Calculate Min, Avg, Max
	CTool::calculateResults( tempMeasurement.upload, 0.5, 0 );

	TRC_INFO( ("UPLOAD: " + CTool::toString(tempMeasurement.upload.avg )).c_str());

	Json::object jMeasurementResults = getMeasurementResults(tempMeasurement, tempMeasurement.upload, cmd);

	jMeasurementResultsUpload.push_back(jMeasurementResults);
}

Json::object CCallback::getMeasurementResults(struct measurement tempMeasurement, struct measurement_data data, string cmd)
{
	Json::object jMeasurementResults;
	jMeasurementResults["throughput_avg_bps"] = to_string(data.avg);
	jMeasurementResults["bytes"] = to_string(data.datasize / 8);
	jMeasurementResults["bytes_including_slow_start"] = to_string(data.datasize_total / 8);
	jMeasurementResults["duration_ns"] = to_string(data.duration_ns);
	jMeasurementResults["duration_ns_total"] = to_string(data.totaltime * 1000);
	jMeasurementResults["num_streams_start"] = to_string(tempMeasurement.streams);

	if (cmd.compare("finish") == 0)
	{
		jMeasurementResults["num_streams_end"] = to_string(tempMeasurement.streams);
	}
	else
	{
		jMeasurementResults["num_streams_end"] = to_string(0);
	}

	return jMeasurementResults;
}
