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
 *      \date Last update: 2019-08-07
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "http.h"

//! \brief
//!	Standard Constructor
CHttp::CHttp()
{
}

//! \brief
//!	Virtual Destructor
CHttp::~CHttp()
{
}

//! \brief
//!	Standard Constructor
CHttp::CHttp( CConfigManager *pConfig, CConnection *nConnection )
{
	mConnection = nConnection;
	
	mConfig = pConfig;
	
	//Hostname
	mHostname = mConfig->readString("general", "hostname", "") + " (" + mConfig->readString("general", "version", "1.0") + ")";
	
	//authentication
	mAuthenticationToken 		= mConfig->readString("security", "authToken", "");
	mAuthenticationTimestamp 	= mConfig->readString("security", "authTimestamp", "");
}

//! \brief
//!	Standard Constructor
CHttp::CHttp( CConfigManager *pConfig, CConnection *nConnection, string sType)
{
	mConnection = nConnection;

	mConfig = pConfig;
	mType = sType;	
		
	mHttpRequestTime = 0;
	mHttpResponseTime = 0;
	
	//Hostname
	mHostname = mConfig->readString("general", "hostname", "") + " (" + mConfig->readString("general", "version", "1.0") + ")";
	
	//authentication
	mAuthenticationToken 		= mConfig->readString("security", "authToken", "");
	mAuthenticationTimestamp 	= mConfig->readString("security", "authTimestamp", "");
}

//! \brief
//!	Syslog Function for generally logging
//! \param message
//! \return 0
int CHttp::parseResponse()
{
	bool firstRequest = true;
	int nValue = -1;
	int recv_len;
	char rbuffer[MAXBUFFER];
	string buffer;

	//Zero buffer
	bzero(rbuffer, MAXBUFFER);
	
	//get data from socket
	recv_len = mConnection->receive(rbuffer, MAXBUFFER, 0);
	
	buffer = string(rbuffer);
	
	TRC_DEBUG("Received: (" + to_string(recv_len) + ") --------------------------------" + "\r\n" + buffer);
	
	if( buffer.find("HTTP/1.1 200 OK") != string::npos )
	{
		mHttpResponseTime = CTool::get_timestamp();
		
		//Return Flag 0 for invalid query (Query at wrong time)
		nValue = 0;
	}
	
	if( buffer.find("HTTP/1.1 100 Continue") != string::npos )
	{
		mHttpResponseTime = CTool::get_timestamp();
		
		//Return Flag 0 for invalid query (Query at wrong time)
		nValue = 0;
	}

	if( buffer.find(HTTP_FORBIDDEN) != string::npos )
	{
		//Return Flag -2 and close everything
		nValue = -2;
	}

	if( buffer.find(HTTP_BANDWIDTH_LIMIT_EXCEEDED) != string::npos )
	{
		//Return Flag -3 and close everything
		nValue = -3;
	}
	
	if( firstRequest && buffer.find(HTTP_GET_DATA) != string::npos )
	{	
		//We need a identifier for our return code in the responseOk()
		mType="GET";
		
		nValue = responseOk();
	}
	
	if( firstRequest && buffer.find(HTTP_POST_DATA) != string::npos )
	{
		//We need a identifier for our return code in the responseOk()
		mType="POST";
		
		nValue = responseOk();
	}
	
	#ifndef NNTOOL_CLIENT
		if( nValue == -1 )
		{
			return responseNotFound();
		}
	#endif

	return nValue;
}

//! \brief
//!	Init and Auth Server
//! \return 0
int CHttp::generateHTML(string &content, string message)
{
	content = "<!DOCTYPE html>\n";
	content += "<html>\n";
	content += "<head>\n";
	content += "<title>"+message+"</title>\n";
	content += "</head>\n";
	content += "<body>\n";
	content += message;
	content += "</body>\n";
	content += "</html>\r\n\n";
	
	return content.size();
}

//! \brief
//!	Init and Auth Server
//! \return 0
int CHttp::getHttpResponseDuration()
{
	if( mHttpResponseTime == 0 )
		return 0;
		
	return mHttpResponseTime - mHttpRequestTime;
}

//! \brief
//!	Init and Auth Server
//! \return 0
string CHttp::getHttpServerHostname()
{
	return mHttpServerHostname;
}

//! \brief
//!	Init and Auth Server
//! \return 0
int CHttp::responseNotFound()
{
	//Generate Reply 
	string send_init = "";
	string content = "";
	
	send_init += "HTTP/1.1 404 Not Found\r\n";
	send_init += "Server: "+mHostname+"\r\n";
	send_init += "Date: "+CTool::get_timestamp_string()+"\r\n";
	send_init += "Content-Length: "+CTool::toString( generateHTML(content, "404 Not Found") )+"\r\n";
	send_init += "Content-Language: en\r\n";
	send_init += "Content-Type: text/html; charset=utf-8\r\n";
	send_init += "Connection: close\r\n\r\n";
	send_init += content;
	//send_init +="\0";
	
	//String to Server
	int send_len = mConnection->send(send_init.c_str(), send_init.size(), 0);
	
	TRC_DEBUG("SEND-Buffer: (" + to_string(send_len) + ") --------------------------------" + "\r\n" + send_init);
		
	return 404;
}

//! \brief
//!	Init and Auth Server
//! \return 0
int CHttp::responseOk()
{
	int ret = -1;
		
	//get time for ETag and Nonce
	int time = CTool::get_timestamp_sec();
	
	//Generate Reply 
	string send_init = "";
	string content = "";
	
	send_init += "HTTP/1.1 200 OK\r\n";
	send_init += "Server: "+mHostname+"\r\n";
	send_init += "Date: "+CTool::get_timestamp_string()+"\r\n";
	send_init += "ETag: "+CTool::toString( time )+"\r\n";
	
	if( mType == "POST" )
	{
		send_init ="";
		send_init += "HTTP/1.1 100 Continue\r\n";
		send_init += "Server: "+mHostname+"\r\n";
		send_init += "Date: "+CTool::get_timestamp_string()+"\r\n";
		send_init += "ETag: "+CTool::toString( time )+"\r\n";
		
		send_init += "Content-Length: 1024000000\r\n";
		send_init += "Content-Type: application/octet-stream\r\n";
		send_init += "Connection: keep-alive\r\n\r\n";
		
		ret = 202;
	}
	else if( mType == "GET" )
	{	
		send_init += "Accept-Ranges: bytes\r\n";
		send_init += "Content-Language: en\r\n";
		send_init += "Content-Type: application/octet-stream\r\n";
		send_init += "Cache-Control: max-age=0, no-cache, no-store\r\n";
		send_init += "Pragma: no-cache\r\n";
		send_init += "X-Rack-Cache: miss\r\n";
		send_init += "Connection: keep-alive\r\n\r\n";
		
		ret = 201;
	}
	else
	{
		ret = -1;
	}
				
	//String to Server
	int send_len = mConnection->send(send_init.c_str(), send_init.size(), 0);
	
	TRC_DEBUG("SEND-Buffer: (" + to_string(send_len) + ") --------------------------------" + "\r\n" + send_init);
		
	return ret;
}

//! \brief
//!	Init and Auth Server
//! \return 0
int CHttp::requestToReferenceServer()
{
	string send_init = "";
	send_init += "" + mType + " /data.img HTTP/1.1\r\n";
	send_init += "Host: " + mHostname + "\r\n";
	send_init += "Cookie: overload=true; tk=" + mAuthenticationToken + "; ts=" + mAuthenticationTimestamp + "\r\n";
	send_init += "Connection: keep-alive\r\n\r\n";
		
	//String to Server
	int send_len = mConnection->send(send_init.c_str(), send_init.size(), 0);
	
	TRC_DEBUG("SEND-Buffer: (" + to_string(send_len) + ") --------------------------------" + "\r\n" + send_init);
		
	return parseResponse();
}
