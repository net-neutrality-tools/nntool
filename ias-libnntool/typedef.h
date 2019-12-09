/*!
    \file typedef.h
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-26

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
		vector<double>interim_values;

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
		map <int, long long> results_timestamp;
		map <int, int> values_int;
		map <int, double> values_double;
		map <int, string> values_string;
		
		int service_availability;
		int system_availability;
		int error_code;
		string error_description;

		float measurement_phase_progress;
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
