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

#include "header.h"

#ifndef TYPEDEF_H_
#define TYPEDEF_H_

using namespace std;

/*!
 \ c*lass measurement_data
 \brief Class for storing all measurement results
 - Initialisation of the class named measurement_data
 */
class measurement_data
{
	public:
		unsigned long long min;
		unsigned long long avg;
		unsigned long long max;
		unsigned long long duration_ns;
		unsigned long long median_ns;
		unsigned long long standard_deviation_ns;

		//General
		unsigned long long packetsize;
		unsigned long long datasize;
		unsigned long long datasize_total;
		string client;
		string server;
		string servername;
		string serverhostname;
		
		int ipversion;

		unsigned long long requests;
		unsigned long long replies;
		unsigned long long missing;
		unsigned long long errors;
		unsigned long long hops;
		
		unsigned long long totime;
		unsigned long long starttime;
		unsigned long long endtime;
		unsigned long long totaltime;
		
		map <int, unsigned long long> httpresponse;
		map <int, unsigned long long> results;
		map <int, int> values_int;
		map <int, double> values_double;
		map <int, string> values_string;
		
		int service_availability;
		int system_availability;
		int error_code;
		string error_description;
};

/*!
 \ c*lass conf_data
 \brief Class of Config-Data for file reading
 - Initialisation of the class named conf_data
 */
class conf_data
{
	public:	
		string sTestName;
		string sProvider;

		int nTestCase;
		int instances;
		string config_filename;
		string ntservice_filename;
		string xml_filename;
		string tmp_filename;
		string log_filename;
		
		string sHostName;
		string sConfigPath;
		string sLogPath;
		string sTmpPath;
};

#endif
