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
 *      \date Last update: 2019-05-20
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
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
