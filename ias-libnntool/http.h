/*!
    \file http.h
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

#ifndef HTTP_H
#define HTTP_H

#include "header.h"
#include "tool.h"
#include "typedef.h"
#include "configmanager.h"
#include "connection.h"

using namespace std;

/*!
\class CHttp
\brief Thread CHttp
*/
class CHttp
{
	private:
		CConnection *mConnection;

		string mType;
		string mHostname;

		string mAuthenticationToken;
		string mAuthenticationTimestamp;

		//Vars for Getter
		unsigned long long mHttpRequestTime;
		unsigned long long mHttpResponseTime;
		string mHttpServerHostname;

		CConfigManager *mConfig;

	public:
		CHttp();

		virtual ~CHttp();

		CHttp( CConfigManager *pConfig, CConnection *nConnection );

		CHttp( CConfigManager *pConfig, CConnection *nConnection, string sType );

		int parseResponse();

		string requestSha256( const string sString );

		int generateHTML(string &content, string message);

		int getHttpResponseDuration();

		string getHttpServerHostname();

		int responseForbidden();

		int responseNotFound();

		int responseOk();

		int requestToReferenceServer();
};

#endif
