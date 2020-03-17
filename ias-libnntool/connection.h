/*!
    \file connection.h
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2020-03-16

    Copyright (C) 2016 - 2020 zafaco GmbH

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

		struct sockaddr_in sockinfo_in;
		struct sockaddr_in6 sockinfo_in6;

		SSL_CTX* ctx;
		SSL* ssl;

		void toggleBlocking(bool activate);
		void tlsSetup(bool client);
		int tlsConnect();
		void tlsPrintError();

	public:
		int mSocket;

		CConnection();

		virtual ~CConnection();

		//! UDP Client Sockets
		int udpSocket( string &interface );
		int udp6Socket( string &interface );
		
		//! UDP Server Sockets
		int udpSocketServer( int &nPort );
		int udpSocketServer(int &nPort, string sIp );
		int udp6SocketServer( int &nPort );
		int udp6SocketServer( int &nPort, string sIp );

		//! TCP Client Sockets
		int tcpSocket( string &interface, string &sServer, int &nPort );
		int tcpSocket( string &interface, string &sServer, int &nPort, int nTls, string sTlsSubjectValidation );
		int tcp6Socket( string &interface, string &sServer, int &nPort );
		int tcp6Socket( string &interface, string &sServer, int &nPort, int nTls, string sTlsSubjectValidation );

		//! TCP Server Sockets
		int tcpSocketServer( int &nPort );
		int tcpSocketServer( int &nPort , string sIp);
		int tcp6SocketServer( int &nPort );
		int tcp6SocketServer( int &nPort , string sIp);

		//! Set Socket to non-blocking‚
		void setBlocking();

		//! Set Socket to non-blocking
		void setNonBlocking();

		//! TLS specific functions
		int tlsServe();

		//! Generic send function
		int send(const void *buf, int num, int flags );

		//! Generic receive function
		int receive(void *buf, int num, int flags );

		int close();
};

#endif
