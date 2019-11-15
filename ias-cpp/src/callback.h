/*!
    \file callback.h
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
		Json jMeasurementParameters;

		void callbackToPlatform(string cmd, string msg, int error_code, string error_description);
		void rttUdpCallback(string cmd);
		void downloadCallback(string cmd);
		void uploadCallback(string cmd);
		Json::object getMeasurementResults(struct measurement tempMeasurement, struct measurement_data data, string cmd);

		
	
	public:
		Json::object jMeasurementResultsTime;
		Json::object jMeasurementResultsPeer;
		Json::object jMeasurementResultsRttUdp;
		Json::array jMeasurementResultsDownload;
		Json::array jMeasurementResultsDownloadStream;
		Json::array jMeasurementResultsUpload;
		Json::array jMeasurementResultsUploadStream;
		int mTestCase;

		Ping *pingThread;
		vector<Download*> vDownloadThreads;
		vector<Upload*> vUploadThreads;		

		CCallback(Json measurementParameters);
		
		virtual ~CCallback();
		
		void callback(string cmd, string msg, int error_code, string error_description);
};

#endif
