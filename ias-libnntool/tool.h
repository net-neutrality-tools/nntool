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
 *      \date Last update: 2019-09-09
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef TOOL_H
#define TOOL_H

#include "header.h"
#include "typedef.h"
#include "configmanager.h"
#include "trace.h"

using namespace std;

/*!
\class CTool
\brief Class with CToolfunctions
- Declarations for the Measurementsystem
*/
class CTool
{
	public:
		//! Standard Constructor
		CTool();

		//! Standard Destructor
		virtual ~CTool();

		//! Convert String to Int
		static int toInt(string s);

		//! Convert String to Int
		static unsigned int toUInt(string s);

		//! Convert String to unsigned long long
		static unsigned long long toULL(string s);

		//! Convert String to long long
		static long long toLL(string s);
		
		static float toFloat(string s);
		
		static void toLower(string &sText);
		
		static void toUpper(string &sText);

		static string get_ip_str(const struct sockaddr *sa);
		
		static unsigned long long calculateResultsMin( map<int,unsigned long long> dmap );
		
		static unsigned long long calculateResultsAvg( map<int,unsigned long long> dmap );

		static int calculateResults(struct measurement_data &sMeasurement);

		static int calculateResults(struct measurement_data &sMeasurement, double increment, int ai_offset, unsigned long long duration);
		
		static string getSystemInfoOS();
		
		static string getSystemInfoOSVersion();
		
		static int validateIp( const string &ipAddress );
		
		//! Get Timestamps in ms
		static int get_timestamp_usec();

		//! Get Timestamps in s
		static int get_timestamp_sec();

		//! Get Timestamps complete
		static unsigned long long get_timestamp();

		//! Get Timestamp-Offset CET / CEST
		static int get_timestamp_offset();
		
		static string get_timestamp_string();

		static void replaceStringInPlace(string& subject, const string& search, const string& replace);
		
		//! Tokenize a String into vector elements of type string
		static void tokenize(string &str,vector<string> &tokens, string &delimiters);
		
		static pid_t spawn_process( char *args1[] );
		
		static string getDownloadSpeed( string sString );
		
		static string getIpFromHostname( string sString );
		
		static string getIpFromHostname( string sString, int nType );

		static struct addrinfo* getIpsFromHostname( string sString, bool bReachable );
		
		static string getHostname();
		
		//! Generate random data of size int
		static int randomData();
		
		//! Generate random data of size int
		static int randomData(char *sbuffer, int size);
		
		static int randomData(vector<char> &vVector, int size);

		static void print_stacktrace();

		static string to_string_precision(double value, const int precision);
		
		//Template Code-------------------------------------------------------
		//! \brief
		//!	Convert Number to String
		//! 	We have to implement the code here, otherwise the compiler will only 
		//! 	instantiate the method string toString<typename T>(T i). This means, we do not have methods like
		//!	toString(unsigned long long) or toString(int)
		//! \param s String
		//! \return b float
		template <typename T>
		static string toString(T i)
		{
			stringstream s;
			s << i;
			return s.str();
		}
		
		//! \brief
		//!	Convert String to Number
		//! 	We have to implement the code here, otherwise the compiler will only 
		//! 	instantiate the method string toString<typename T>(T i). This means, we do not have methods like
		//!	toString(unsigned long long) or toString(int)
		//! \param s String
		//! \return b float
		template <typename T>
		static T toNumber(string s)
		{
			T value;
			stringstream sstr(s);
			sstr >> value;
			return value;
		}
		//Template Code-------------------------------------------------------
};

#endif


