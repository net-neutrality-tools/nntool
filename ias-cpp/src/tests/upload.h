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
 *      \date Last update: 2019-05-17
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef UPLOAD_H
#define UPLOAD_H

#include "../header.h"
#include "upload_sender.h"

/*!
\class Upload
\brief Class with Upload Functions
- Declarations for the Measurementsystem
*/
class Upload : public CBasisThread
{
	private:
		int mPort;
		int mTls;
		unsigned long long mLimit;
		string mUploadString;
		
		string mServer;
		string mServerName;
		string mServerHostname;
		string mClient;
		
		int ipversion;
				
		CConnection *mConnection;
		
		CConfigManager *mConfig;
		
	public:
		int mResponse;

		struct measurement_data mUpload;

		int pid;

		unsigned long long measurementTimeStart;
		unsigned long long measurementTimeEnd;
		unsigned long long measurementTimeDuration;

		int system_availability;
		int service_availability;
		int error;
		string error_description;

		unsigned long long nDataRecv;
		unsigned long long nTimeRecv;
	
		unsigned long long nTimeRecvExa;
		unsigned long long nTimeRecvExaFirst;
		unsigned long long nTimeRecvFirst;

		unsigned long long nHttpResponseDuration;
		unsigned long long nHttpResponseReportValue;

		//! Standard Constructor
		Upload();

		//! Standard Destructor
		virtual ~Upload();
		
		//! Init
		Upload( CConfigManager *pConfig, CConfigManager *pXml, CConfigManager *pService, string sProvider );
		
		//! Test
		int run();
};

#endif 
