/*!
    \file configmanager.cpp
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
		
	mSections[sSection][sKey] = CTool::toString(nValue);
	
	return 0;
}
