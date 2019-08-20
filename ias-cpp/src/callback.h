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
 *      \date Last update: 2019-08-20
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef CALLBACK_H
#define CALLBACK_H

#include "header.h"

#include "tests/ping.h"
#include "tests/download.h"
#include "tests/upload.h"


using namespace std;

/*!
\class CTimer
\brief Thread CTimer
*/
class CCallback
{
    friend void startTestCase(int nTestCase);
    friend void measurementStart(std::string measurementParameters);
	private:
		void callbackToPlatform(string cmd, string msg, int error_code, string error_description);
		void rttUdpCallback(string cmd);
		void downloadCallback(string cmd);
		void uploadCallback(string cmd);
		Json::object getMeasurementResults(struct measurement tempMeasurement, struct measurement_data data, string cmd);

		Json::object jMeasurementResultsTime;
		Json::object jMeasurementResultsRttUdp;
		Json::array jMeasurementResultsDownload;
		Json::array jMeasurementResultsUpload;
	
	public:
		int mTestCase;

		Ping *pingThread;
		vector<Download*> vDownloadThreads;
		vector<Upload*> vUploadThreads;		

		CCallback();
		
		virtual ~CCallback();
		
		void callback(string cmd, string msg, int error_code, string error_description);
};

#endif
