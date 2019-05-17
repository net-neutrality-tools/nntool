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
 *      \date Last update: 2019-05-17
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
		int sock;

		/*!
		\struct sockaddr addr;
		\brief Struct of Socket-Address
		- Initialisation of the struct named sockaddr
		*/
		struct sockaddr_ll sockinfo_ll;
		struct sockaddr_in sockinfo_in;
		struct sockaddr_in6 sockinfo_in6;

		/*!
		\struct ifreq ifinfo;
		\brief Struct of Interface-Register
		- Initialisation of the struct named ifinfo
		*/
		struct ifreq ifinfo;

	public:
		//!Standard Constructor
		CConnection();

		//! Virtual Destructor
		virtual ~CConnection();

		int rawSocketEth();
 
		//! Open Datagram-Socket in UNIX
		int udpSocket( string &interface );
		
		//! Open Datagram-Socket in UNIX
		int udpSocketServer( int &nPort );

		//! Open Datagram-Socket in UNIX
		int udpSocketServer(int &nPort, string sIp);
		
		//! Open Datagram-Socket in UNIX
		int udp6Socket( string &interface );
		
		//! Open Datagram-Socket in UNIX
		int udp6SocketServer( int &nPort );
		
		//! Open Datagram-Socket in UNIX
		int udp6SocketServer( int &nPort, string sIp );

		//! Open TCP-Socket in UNIX
		int tcpSocket( string &interface, string &sServer, int &nPort );
		
		//! Open TCP-Socket in UNIX
		int tcpSocketServer( int &nPort );
		
		//! Open TCP-Socket in UNIX
		int tcp6Socket(string &interface, string &sServer, int &nPort );
		
		//! Open TCP-Socket in UNIX
		int tcp6SocketServer( int &nPort );
};

#endif
