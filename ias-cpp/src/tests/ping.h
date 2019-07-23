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
 *      \date Last update: 2019-06-24
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
		int mPingPort;
		int mPingQuery;
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
