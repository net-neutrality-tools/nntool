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
 *      \date Last update: 2019-04-29
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef FILE_READER_H
#define FILE_READER_H

#include "header.h"
#include "tool.h"

using namespace std;

/*!
\class CConfigManager
\brief Class for reading files and extract informations from Config-File
- Reading and Extraction
*/
class CConfigManager
{
	private:
		map<string, map<string, string> > mSections;

	public:
		//! Standard Constructor
		CConfigManager();

		//! Standard Destructor
		virtual ~CConfigManager();
		
		//! Reading String out of config
		string readString( string sSection, string sKey, string sDefault );
		
		int writeString( string sSection, string sKey, string sValue );
		
		//! Reading Long out of config
		unsigned long long readLong( string sSection, string sKey, unsigned long long nDefault );
		
		int writeLong( string sSection, string sKey, int nValue );
		
		int getXmlData( map<string, string> &settings );
};

#endif
