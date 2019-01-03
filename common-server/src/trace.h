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
 *      \date Last update: 2019-01-02
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
		
		void init(string strIniFileName, string sCat);
		
		void logFatal(string strMessage);
		void logFatal(string strCategory, string strMessage);
		
		void logAlert(string strMessage);
		void logAlert(string strCategory, string strMessage);
		
		void logCritical(string strMessage);
		void logCritical(string strCategory, string strMessage);
		
		void logErr(string strMessage);
		void logErr(string strCategory, string strMessage);
		
		void logWarn(string strMessage);
		void logWarn(string strCategory, string strMessage);
		
		void logNotice(string strMessage);
		void logNotice(string strCategory, string strMessage);
		
		void logInfo(string strMessage);
		void logInfo(string strCategory, string strMessage);
		
		void logDebug(string strMessage);
		void logDebug(string strCategory, string strMessage);
};

#endif // TRACE_H
