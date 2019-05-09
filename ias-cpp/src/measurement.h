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
 *      \date Last update: 2019-05-06
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef MEASUREMENT_H
#define MEASUREMENT_H

#include "header.h"

#include "tests/ping.h"
#include "tests/download.h"
#include "tests/upload.h"

#include "callback.h"
#include "timer.h"


using namespace std;

/*!
\class CMeasurement
\brief Thread CMeasurement
*/
class CMeasurement
{
	private:
		string mProvider;
		int mTestCase;
		
		Ping *ping;
		Download *download;
		Upload *upload;
		
		CConfigManager *mConfig;
		CConfigManager *mXml;
		CConfigManager *mService;
		CCallback *mCallback;
		CTimer *mTimer;
		
		int nResponse;
	
	public:
		CMeasurement();
		
		virtual ~CMeasurement();
		
		CMeasurement( CConfigManager *pConfig, CConfigManager *pXml, CConfigManager *pService, string sProvider, int sChoice, CCallback *pCallback);
			
		int startMeasurement();
};

#endif 
