/*!
    \file trace.cpp
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
	if (CTrace::logFunction != nullptr) {
		CTrace::logFunction(category, sMessage);
	}
}

#endif