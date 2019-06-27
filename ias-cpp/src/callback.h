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
	private:
		void callbackToPlatform(string cmd, string msg, int error_code, string error_description);
		void rttUdpCallback(string cmd);
		void downloadCallback(string cmd);
		void uploadCallback(string cmd);
		Json::object getMeasurementResults(struct measurement tempMeasurement, struct measurement_data data, string cmd);

		Json::object jMeasurementResults;
		Json::object jMeasurementResultsTime;
		Json::array jMeasurementResultsRttUdp;
		Json::array jMeasurementResultsDownload;
		Json::array jMeasurementResultsUpload;

		bool PERFORMED_RTT = false;
		bool PERFORMED_DOWNLOAD = false;
		bool PERFORMED_UPLOAD = false;
	
	public:
		int mTestCase;

		Ping *pingThread;
		vector<Download*> vDownloadThreads;
		vector<Upload*> vUploadThreads;		

		CCallback();
		
		virtual ~CCallback();
		
		void callback(string cmd, string msg, int error_code, string error_description);

		inline bool isPerformedRtt() const {
		    return PERFORMED_RTT;
		}

		inline bool isPerformedDownload() const {
            return PERFORMED_DOWNLOAD;
        }

        inline bool isPerformedUpload() const {
            return PERFORMED_UPLOAD;
        }
};

#endif
