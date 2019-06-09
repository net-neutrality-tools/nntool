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
 *      \date Last update: 2019-05-03
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
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
		string mCategory;

		static std::function<void(std::string)> logFunction;
		
		static CTrace* global_pHandlerTrace;
		static pthread_mutex_t global_mutexCreateTrace;
		
		CTrace();
	public:
	
		virtual ~CTrace();
		
		static CTrace* getInstance();

		static void setLogFunction(std::function<void(std::string)> func) {
			CTrace::logFunction = func;
			func("Assigned log function to trace");
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
