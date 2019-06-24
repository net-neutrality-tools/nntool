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
 *      \date Last update: 2019-04-28
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "header.h"

#ifndef TYPE_H_
#define TYPE_H_

using namespace std;

/*!
 \ c*lass measurement
 \brief Class for storing all measurement objects
 - Initialisation of the class named measurement
 */
class measurement
{
	public:
		unsigned long long time;
		unsigned long long timezone;
		string date;
		
		unsigned long long measurementDuration;
		
		int streams;
		string client;
		string server;
		string servername;
		
		int ipversion;
		
		string os;
		string osversion;
		string hostname;
		string softwareversion;
		string softwareversiontool;
		
		unsigned long long starttime;
		unsigned long long endtime;
		unsigned long long totaltime;
		
		struct measurement_data ping;
		struct measurement_data download;
		struct measurement_data upload;
		
		int service_availability;
		int system_availability;
		int error_code;
		string error_description;
		
		map <string, string> parameter;
		map <string, map<string, string> > settings;
		
		map<string, string> metric;
		map<string, string> metatag;
		map<string, string> units;
		map<string, string> message;
};

enum class MeasurementPhase {
    INIT,
    PING,
    DOWNLOAD,
    UPLOAD,
    END
};

#endif
