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

#include "trace.h"

CTrace* CTrace::global_pHandlerTrace = NULL;
pthread_mutex_t CTrace::global_mutexCreateTrace = PTHREAD_MUTEX_INITIALIZER;

//! \brief
//!	Standard Constructor
CTrace::CTrace()
{	
}

//! \brief
//!	Standard Destructor
CTrace::~CTrace()
{
}

CTrace* CTrace::getInstance()
{
	pthread_mutex_lock(&global_mutexCreateTrace);
	{
		if ( global_pHandlerTrace == NULL)
		{
			global_pHandlerTrace = new CTrace();
		}
	}
	pthread_mutex_unlock(&global_mutexCreateTrace);
	
	return global_pHandlerTrace;
}

void CTrace::init(string sIniFileName, string sCategory)
{
	mCategory = sCategory;
	
	log4cpp::PropertyConfigurator::configure( sIniFileName );
}

void CTrace::logFatal(string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.fatal( ("#" + CTool::toString(getpid()) +  "["+CTool::toString(getpid())+"] "+sMessage).c_str() );
}

void CTrace::logFatal(string sCategory, string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( sCategory );
	
	//Save Message to Category
	cat.fatal( ("#" + CTool::toString(getpid()) +  "["+CTool::toString(getpid())+"] "+sMessage).c_str() );
}

void CTrace::logAlert(string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.alert( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}

void CTrace::logAlert(string sCategory, string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( sCategory );
	
	//Save Message to Category
	cat.alert( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}

void CTrace::logCritical(string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.crit( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}

void CTrace::logCritical(string sCategory, string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( sCategory );
	
	//Save Message to Category
	cat.crit( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}

void CTrace::logErr(string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.error( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}

void CTrace::logErr(string sCategory, string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( sCategory );
	
	//Save Message to Category
	cat.error( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}

void CTrace::logWarn(string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.warn( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}


void CTrace::logWarn(string sCategory, string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( sCategory );
	
	//Save Message to Category
	cat.warn( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}

void CTrace::logNotice(string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.notice( ("["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}


void CTrace::logNotice(string sCategory, string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( sCategory );
	
	//Save Message to Category
	cat.notice( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}

void CTrace::logInfo(string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.info( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}


void CTrace::logInfo(string sCategory, string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( sCategory );
	
	//Save Message to Category
	cat.info( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}

void CTrace::logDebug(string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.debug( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}

void CTrace::logDebug(string sCategory, string sMessage)
{
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( sCategory );
	
	//Save Message to Category
	cat.debug( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
}

