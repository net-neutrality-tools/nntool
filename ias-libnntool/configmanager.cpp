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
 *      \date Last update: 2019-05-02
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "configmanager.h"

//! \brief
//!	Standard Constructor
CConfigManager::CConfigManager()
{
}
 
//! \brief
//!	Standard Destructor
CConfigManager::~CConfigManager()
{
}

//! \brief
//!	Get specified "String" back from Config-File
//! \param mSections
//! \param sSection
//! \param sKey
//! \param sDefault
//! \return sValue
string CConfigManager::readString( string sSection, string sKey, string sDefault )
{
	string sValue = sDefault;
	
	CTool::toLower(sSection);
	CTool::toLower(sKey);

	map<string, map<string, string> >::iterator itSection;
		
	if( mSections.find(sSection) != mSections.end() )
	{	
		itSection = mSections.find(sSection);
		
		map<string,string> key;
		map<string,string>::iterator itKey;
		
		key = itSection->second;
		
		if( (itKey = key.find(sKey) ) != key.end() )
		{
			sValue = itKey->second;
		}
	}

	return sValue;
	
}

//! \brief
//!	Get specified "Long" back from Config-File
//! \param mSections
//! \param sSection
//! \param strKey
//! \param nDefault
//! \return sValue
int CConfigManager::writeString( string sSection, string sKey, string sValue )
{
	CTool::toLower(sSection);
	CTool::toLower(sKey);

	map<string, map<string, string> >::iterator itSection;
		
	mSections[sSection][sKey] = sValue;

	return 0;
}

//! \brief
//!	Get specified "Long" back from Config-File
//! \param mSections
//! \param sSection
//! \param strKey
//! \param nDefault
//! \return sValue
unsigned long long CConfigManager::readLong( string sSection, string sKey, unsigned long long nDefault )
{
	unsigned long long nValue = nDefault;
	
	CTool::toLower(sSection);
	CTool::toLower(sKey);

	map<string, map<string, string> >::iterator itSection;
	
	if( mSections.find(sSection) != mSections.end() )
	{
		itSection = mSections.find(sSection);
		
		map<string,string> key;
		map<string,string>::iterator itKey;
		
		key = itSection->second;
		
		if( (itKey = key.find(sKey) ) != key.end() )
		{
			nValue = CTool::toULL(itKey->second);
		}
	}
	
	return nValue;
}

//! \brief
//!	Get specified "Long" back from Config-File
//! \param mSections
//! \param sSection
//! \param strKey
//! \param nDefault
//! \return sValue
int CConfigManager::writeLong( string sSection, string sKey, int nValue )
{	
	CTool::toLower(sSection);
	CTool::toLower(sKey);

	map<string, map<string, string> >::iterator itSection;
		
	mSections[sSection][sKey] = nValue;
	
	return 0;
}
