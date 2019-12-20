/*!
    \file ping.h
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
