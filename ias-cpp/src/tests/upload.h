/*!
    \file upload.h
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

#ifndef UPLOAD_H
#define UPLOAD_H

#include "../header.h"
#include "upload_sender.h"

/*!
\class Upload
\brief Class with Upload Functions
- Declarations for the Measurementsystem
*/
class Upload : public CBasisThread
{
	private:
		int mPort;
		int mTls;
		unsigned long long mLimit;
		string mUploadString;
		
		string mServer;
		string mServerName;
		string mClient;

		CConfigManager *mConfig;
		
	public:
		int mResponse;

		struct measurement_data mUpload;

		int pid;

		unsigned long long measurementTimeStart;
		unsigned long long measurementTimeEnd;
		unsigned long long measurementTimeDuration;

		int system_availability;
		int service_availability;
		int error;
		string error_description;

		unsigned long long nDataRecv;
		unsigned long long nTimeRecv;
	
		unsigned long long nTimeRecvExa;
		unsigned long long nTimeRecvExaFirst;
		unsigned long long nTimeRecvFirst;

		unsigned long long nHttpResponseDuration;
		unsigned long long nHttpResponseReportValue;

		//! Standard Constructor
		Upload();

		//! Standard Destructor
		virtual ~Upload();
		
		//! Init
		Upload( CConfigManager *pConfig, CConfigManager *pXml, CConfigManager *pService, string sProvider );
		
		//! Test
		int run();
};

#endif 
