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
 *      \date Last update: 2019-08-19
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef CONNECTION_H_
#define CONNECTION_H_

#include "header.h"
#include "typedef.h"
#include "trace.h"

using namespace std;

/*!
\class CConnection
\brief Class for reading the RTP-Header
- Declarations for the Messsystem
*/
class CConnection
{
	private:
		int mTls;
		string mTlsSubjectValidation;

		/*!
		\struct sockaddr addr;
		\brief Struct of Socket-Address
		- Initialisation of the struct named sockaddr
		*/
		struct sockaddr_in sockinfo_in;
		struct sockaddr_in6 sockinfo_in6;

		SSL*     			ssl;

		int connectTLS();

	public:
		int mSocket;

		//!Standard Constructor
		CConnection();

		//! Virtual Destructor
		virtual ~CConnection();

		//! UDP Client Sockets
		int udpSocket( string &interface );
		int udp6Socket( string &interface );
		
		//! UDP Server Sockets
		int udpSocketServer( int &nPort );
		int udpSocketServer(int &nPort, string sIp);
		int udp6SocketServer( int &nPort );
		int udp6SocketServer( int &nPort, string sIp );

		//! TCP Client Sockets
		int tcpSocket( string &interface, string &sServer, int &nPort );
		int tcpSocket( string &interface, string &sServer, int &nPort, int nTls, string sTlsSubjectValidation );
		int tcp6Socket(string &interface, string &sServer, int &nPort );
		int tcp6Socket(string &interface, string &sServer, int &nPort, int nTls, string sTlsSubjectValidation );

		//! TCP Server Sockets
		int tcpSocketServer( int &nPort );
		int tcp6SocketServer( int &nPort );

		//! Generic send function
		int send(const void *buf, int num, int flags );

		//! Generic receive function
		int receive(void *buf, int num, int flags );

		int close();
};

#endif
