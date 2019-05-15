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

#ifndef TRACE_H
#define TRACE_H

#include "header.h"
#include "tool.h"

using namespace std;

class CTrace
{	
	private:
		string mCategory;
		
		static CTrace* global_pHandlerTrace;
		static pthread_mutex_t global_mutexCreateTrace;
		
		CTrace();
	public:
	
		virtual ~CTrace();
		
		static CTrace* getInstance();
		
		#ifdef NNTOOL_SERVER
		void init(string strIniFileName, string sCat);
		#endif

		void logCritical(string strMessage);
		
		void logErr(string strMessage);
		
		void logWarn(string strMessage);
		
		void logInfo(string strMessage);
		
		void logDebug(string strMessage);

		#ifdef NNTOOL_CLIENT
		void logToPlatform(string category, string sMessage);
		#endif
};

#endif
