/*!
    \file trace.h
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

//#ifndef NNTOOL_CLIENT
//#define NNTOOL_CLIENT
//#endif

#ifndef TRACE_H
#define TRACE_H

#include "header.h"
#include "tool.h"
#include <functional>

using namespace std;

class CTrace
{	
	private:
	    #ifdef NNTOOL_SERVER
		    string mCategory;
		#endif

	static std::function<void(std::string, std::string)> logFunction;
		
		CTrace();
	public:
	
		static CTrace& getInstance();

		static void setLogFunction(std::function<void(std::string, std::string)> func) {
			CTrace::logFunction = func;
			func("INFO", "Assigned log function to trace");
		}
		
		#ifdef NNTOOL_SERVER
		void init(string strIniFileName, string sCat);
		#endif

		void logCritical(const string &strMessage);
		
		void logErr(const string &strMessage);
		
		void logWarn(const string &strMessage);
		
		void logInfo(const string &strMessage);
		
		void logDebug(const string &strMessage);

		#ifdef NNTOOL_CLIENT
		void logToPlatform(const string &category, const string &sMessage);
		#endif
};

#endif
