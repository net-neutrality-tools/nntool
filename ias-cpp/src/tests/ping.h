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
 *      \date Last update: 2019-06-14
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef PING_H
#define PING_H

#include "../header.h"

/*!
\class Ping
\brief Class with Ping Functions
- Declarations for the Measurementsystem
*/
class Ping : public CBasisThread
{
	private:
		int mSock;
		
		int mPingPort;
		
		int mPingQuery;
		int mResponse;
				
		struct sockaddr_in6 mServerDatav6;
		struct sockaddr_in mServerDatav4;
		struct sockaddr_in6 mClientDatav6;
		struct sockaddr_in mClientDatav4;
		socklen_t mClientDataSizev6;
		socklen_t mClientDataSizev4;
		
		std::unique_ptr<CConnection> mSocket;
		
		unsigned long long time1;
		unsigned long long time2;
		unsigned long long mTimeDiff;
		
	public:
		struct measurement_data mPingResult;

		unsigned long long measurementTimeStart;
		unsigned long long measurementTimeEnd;
		unsigned long long measurementTimeDuration;

		string mServer;
		string mClient;

		#ifdef NNTOOL
		string mServerName;
		#endif

		int ipversion;

		int nPingTarget;

		int nHops;
		int nSize;
		int nError;
		
		int system_availability;
		int service_availability;
		int error;
		string error_description;

		//! Standard Constructor
		Ping();

		//! Standard Destructor
		virtual ~Ping();
		
		//! Init
		Ping( CConfigManager *pXml, CConfigManager *pService, string sProvider );
		
		//! Test
		int run();
};

#endif 
