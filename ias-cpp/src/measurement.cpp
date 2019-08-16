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
 *      \date Last update: 2019-08-05
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "measurement.h"


//! \brief
//!	Standard Destructor
CMeasurement::CMeasurement()
{
}

//! \brief
//!	Virtual Destructor
CMeasurement::~CMeasurement()
{
}

//! \brief
//!	Standard Destructor
CMeasurement::CMeasurement( CConfigManager *pConfig, CConfigManager *pXml,  CConfigManager *pService, string sProvider, int nTestCase, CCallback *pCallback)
{
	mConfig	 	= pConfig;
	mXml	 	= pXml;
	mService 	= pService;
	mProvider	= sProvider;
	mTestCase	= nTestCase;
	mCallback 	= pCallback;
	
	switch( conf.nTestCase )
	{
		case 2:
			conf.instances = 1;
			break;
		case 3:
			conf.instances = pXml->readLong(conf.sProvider,"DL_STREAMS",4);
			break;
		case 4:
			conf.instances = pXml->readLong(conf.sProvider,"UL_STREAMS",4);
			break;
		default:
			conf.instances = 1;
			break;
	}
	
	mTimer = std::make_unique<CTimer>( conf.instances, mCallback );
	
	if( mTimer->createThread() != 0 )
	{
		TRC_ERR( "Error: Failure while creating the Thread - Timer!" );
	}
}

//! \brief
//!    Run-Function
//! \return 0
int CMeasurement::startMeasurement()
{
	string sString;
	string sSeparator;
	
	vector<string> vString;
	vector<string>::iterator iString;

	measurements.streams = 0;
	
	map<int, unsigned long long> mTmpMap;
	
	switch(mTestCase)
	{
		// PING
		case 2:
		{
			std::unique_ptr<Ping> ping = std::make_unique<Ping>(mXml, mService, mProvider);
			ping->createThread();

			mCallback->pingThread = ping.get();
			
			ping->waitForEnd();
			while (!::PERFORMED_RTT && !::hasError && ::RUNNING)
			{
				//Sleep 100ms
                usleep(100000);
			}

			break;
        }

		// DOWNLOAD
		case 3:
		{
		    std::vector<Download *> vDownloadThreads;
			//Set Measurement Duration for Timer - Download
			if( mXml->readString(mProvider,"testname","dummy") == "http_down_dataload" ) 
				MEASUREMENT_DURATION = mXml->readLong(mProvider,"DL_DURATION_DL",10);
			else
				MEASUREMENT_DURATION = mXml->readLong(mProvider,"DL_DURATION",10);
				
			for(int i = 0; i < conf.instances; i++)
			{
				Download * download = new Download( mConfig, mXml, mService, mProvider );
				if( download->createThread() != 0 )
				{
					TRC_ERR( "Error: Failure while creating the Thread - DownloadThread!" );
					return -1;
				}
				
				vDownloadThreads.push_back(download);
			}

			mCallback->vDownloadThreads = vDownloadThreads;

			for(vector<Download*>::iterator itThread = vDownloadThreads.begin(); itThread != vDownloadThreads.end(); ++itThread)
			{
				(*itThread)->waitForEnd();
			}

			while (!::PERFORMED_DOWNLOAD && !::hasError && ::RUNNING)
			{
                //Sleep 100ms
                usleep(100000);
            }

            for(vector<Download*>::iterator itThread = vDownloadThreads.begin(); itThread != vDownloadThreads.end(); ++itThread)
            {
                delete( *itThread );
            }

			break;
		}
		
		// Upload
		case 4:
		{
		    std::vector<Upload *> vUploadThreads;
			/*Set Measurement Duration for Timer - Upload. Add an additional UPLOAD_ADDITIONAL_MEASUREMENT_DURATION to 
			allow the client to receive all responses which were sent by the server during TCP_STARTUP + UL_DURATION*/
			if( mXml->readString(mProvider,"testname","dummy") == "http_up_dataload" ) 
				MEASUREMENT_DURATION = mXml->readLong(mProvider,"UL_DURATION_DL",10)+UPLOAD_ADDITIONAL_MEASUREMENT_DURATION;
			else
				MEASUREMENT_DURATION = mXml->readLong(mProvider,"UL_DURATION",10)+UPLOAD_ADDITIONAL_MEASUREMENT_DURATION;
			
			measurements.upload.datasize = 0;
			
			for(int i = 0; i < conf.instances; i++)
			{
				Upload * upload = new Upload( mConfig, mXml, mService, mProvider );
				if( upload->createThread() != 0 )
				{
					TRC_ERR( "Error: Failure while creating the Thread - UploadThread!" );
					return -1;
				}
				
				vUploadThreads.push_back(upload);
			}

			mCallback->vUploadThreads = vUploadThreads;

			for(vector<Upload*>::iterator itThread = vUploadThreads.begin(); itThread != vUploadThreads.end(); ++itThread)
			{
				(*itThread)->waitForEnd();
			}

			while (!::PERFORMED_UPLOAD && !::hasError && ::RUNNING)
			{
				//Sleep 100ms
                usleep(100000);
            }

			for(vector<Upload*>::iterator itThread = vUploadThreads.begin(); itThread != vUploadThreads.end(); ++itThread)
            {
                delete( *itThread );
            }

			break;
		}
	}
	
	return 0;
}
