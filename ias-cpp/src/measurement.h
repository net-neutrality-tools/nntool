/*!
    \file measurement.h
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2020-07-01

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

#ifndef MEASUREMENT_H
#define MEASUREMENT_H

#include "header.h"

#include "tests/ping.h"
#include "tests/download.h"
#include "tests/upload.h"

#include "callback.h"
#include "timer.h"


using namespace std;

/*!
\class CMeasurement
\brief Thread CMeasurement
*/
class CMeasurement
{
	private:
		string mProvider;
		int mTestCase;

		CConfigManager *mConfig;
		CConfigManager *mXml;
		CConfigManager *mService;
		CCallback *mCallback;
		std::unique_ptr<CTimer> mTimer;

        unsigned long long mInitialCallbackDelay;
		
	public:
		CMeasurement();
		
		virtual ~CMeasurement();
		
		CMeasurement( CConfigManager *pConfig, CConfigManager *pXml, CConfigManager *pService, string sProvider, int sChoice, CCallback *pCallback);
			
		int startMeasurement();
};

#endif 
