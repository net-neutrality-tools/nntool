/*!
    \file configmanager.h
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
