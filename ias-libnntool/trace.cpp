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
 *      \date Last update: 2019-06-18
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "trace.h"

std::function<void(std::string, std::string)> CTrace::logFunction = nullptr;

//! \brief
//!	Standard Constructor
CTrace::CTrace()
{	
}

CTrace& CTrace::getInstance()
{
    static CTrace instance;
    return instance;
}


#ifdef NNTOOL_SERVER

void CTrace::init(string sIniFileName, string sCategory)
{
	mCategory = sCategory;
	
	log4cpp::PropertyConfigurator::configure( sIniFileName );
}

#endif


void CTrace::logCritical(const string &sMessage)
{
	#ifdef NNTOOL_CLIENT
	logToPlatform("CRITICAL", sMessage);
	#endif

	#ifdef NNTOOL_SERVER
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.crit( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str());
	#endif
}

void CTrace::logErr(const string &sMessage)
{
	#ifdef NNTOOL_CLIENT
	logToPlatform("ERROR", sMessage);
	#endif

	#ifdef NNTOOL_SERVER
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.error( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
	#endif
}

void CTrace::logWarn(const string &sMessage)
{
	#ifdef NNTOOL_CLIENT
	logToPlatform("WARN", sMessage);
	#endif

	#ifdef NNTOOL_SERVER
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.warn( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
	#endif
}

void CTrace::logInfo(const string &sMessage)
{
	#ifdef NNTOOL_CLIENT
	logToPlatform("INFO", sMessage);
	#endif

	#ifdef NNTOOL_SERVER
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.info( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
	#endif
}

void CTrace::logDebug(const string &sMessage)
{
	#if defined(NNTOOL_CLIENT) && !defined(__ANDROID__)
	if (::DEBUG)
	{
		logToPlatform("DEBUG", sMessage);
	}
	#endif

	#ifdef NNTOOL_SERVER
	//Get Instance of Category
	log4cpp::Category& cat = log4cpp::Category::getInstance( mCategory );
	
	//Save Message to Category
	cat.debug( ("#" + CTool::toString(getpid()) +  " ["+CTool::toString(pthread_self())+"] "+sMessage).c_str() );
	#endif
}

#ifdef NNTOOL_CLIENT

void CTrace::logToPlatform(const string &category, const string &sMessage)
{
//	string platform = ::PLATFORM;
//	string clientos = ::CLIENT_OS;
	if (CTrace::logFunction != nullptr) {
		CTrace::logFunction(category, sMessage);
	}
//	if (platform.compare("desktop") == 0 && clientos.compare("linux") == 0)
	{
//		cout << "[" << CTool::get_timestamp_string() << "] " << category << ": " << sMessage << endl;
	}
//	if (platform.compare("mobile") == 0 && clientos.compare("android") == 0)
	{
		//android logging hookup
		//log category and sMessage via ndk
	}
}

#endif