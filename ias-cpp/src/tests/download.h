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
 *      \date Last update: 2019-05-20
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef DOWNLOAD_H
#define DOWNLOAD_H

#include "../header.h"

/*!
\class Download
\brief Class with Download Functions
- Declarations for the Measurementsystem
*/
class Download : public CBasisThread
{
	private:
		int mPort;
		int mTls;
		unsigned long long mLimit;
		string mDownloadString;
		
		string mServer;
		string mServerName;
		string mServerHostname;
		string mClient;
		
		int ipversion;
		
		CConnection *mConnection;
		
		CConfigManager *mConfig;	
	public:
		int mResponse;

		struct measurement_data mDownload;

		int pid;

		unsigned long long measurementTimeStart;
		unsigned long long measurementTimeEnd;
		unsigned long long measurementTimeDuration;

		int system_availability;
		int service_availability;
		int error;
		string error_description;

		unsigned long long nHttpResponseDuration;
		unsigned long long nHttpResponseReportValue;
		
		//! Standard Constructor
		Download();

		//! Standard Destructor
		virtual ~Download();
		
		//! Init
		Download( CConfigManager *pConfig, CConfigManager *pXml, CConfigManager *pService, string sProvider );
		
		//! Test
		int run();

};

#endif 

