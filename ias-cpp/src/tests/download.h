/*!
    \file download.h
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2020-05-26

    Copyright (C) 2016 - 2020 zafaco GmbH

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

#ifndef DOWNLOAD_H
#define DOWNLOAD_H

#include "../header.h"

/*!
\class Download
\brief Class with Download Functions
- Declarations for the Measurementsystem
*/
class Download : public CBasisThread
{
	private:
		int mPort;
		int mTls;
		unsigned long long mLimit;
		string mDownloadString;

		CConfigManager *mConfig;
	public:
		int mResponse;

		struct measurement_data mDownload;

		int pid;

		unsigned long long measurementTimeStart;
		unsigned long long measurementTimeEnd;
		unsigned long long measurementTimeDuration;

		string mServer;
		string mServerName;
		string mClient;

		int system_availability;
		int service_availability;
		int error;
		string error_description;

		unsigned long long nHttpResponseDuration;
		unsigned long long nHttpResponseReportValue;
		
		//! Standard Constructor
		Download();

		//! Standard Destructor
		virtual ~Download();
		
		//! Init
		Download( CConfigManager *pConfig, CConfigManager *pXml, CConfigManager *pService, string sProvider );
		
		//! Test
		int run();

};

#endif 

