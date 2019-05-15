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
 *      \date Last update: 2019-05-10
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "connection.h"

//! \brief
//!	Standard Destructor
CConnection::CConnection()
{
	sock = 0;
}

//! \brief
//!	Virtual Destructor
CConnection::~CConnection()
{
}

//! \brief
//!	Open Raw-Socket in UNIX
//! \param &interface
//! \return sock
int CConnection::rawSocketEth()
{
	//Open Socket
	sock = socket(PF_PACKET, SOCK_RAW, htons(ETH_P_ALL));
	if( sock == -1 )
	{
		TRC_ERR("Error [rawSocketEth]: Could not create socket" );
		return EXIT_FAILURE;
	}
	
	return sock;
}

//! \brief
//!    Open UDP-Socket in UNIX
//! \param &interface
//! \return sock
int CConnection::udpSocket(string &interface)
{
	//Open Socket
	sock = socket( AF_INET, SOCK_DGRAM, IPPROTO_UDP );
	if( sock == -1 )
	{
		TRC_ERR("Error [udpSocket]: Could not create socket" );
		return EXIT_FAILURE;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in, 0, sizeof(sockinfo_in));
	sockinfo_in.sin_family 		= AF_INET;
	sockinfo_in.sin_addr.s_addr 	= inet_addr(interface.c_str());

	//Bind Socket to Interface
	if( bind( sock, (struct sockaddr*)&sockinfo_in, sizeof(sockinfo_in)) == -1 )
	{
		TRC_ERR("Error [udpSocket]: Could not bind socket to interface" );
		return EXIT_FAILURE;
	}

	return sock;
}

//! \brief
//!    Open UDP-Socket v6 in UNIX
//! \param &interface
//! \return sock
int CConnection::udpSocketServer()
{
	int mPort = 0;
	return udpSocketServer(mPort, "");
}

//! \brief
//!    Open UDP-Socket in UNIX
//! \return sock
int CConnection::udpSocketServer(int &nPort, string sIp)
{
	//Open Socket
	sock = socket( AF_INET, SOCK_DGRAM, IPPROTO_UDP );
	if( sock == -1 )
	{
		TRC_ERR("Error [udpSocket]: Could not create socket" );
		return EXIT_FAILURE;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in, 0, sizeof(sockinfo_in));
	sockinfo_in.sin_family 		= AF_INET;
	sockinfo_in.sin_addr.s_addr = htonl(INADDR_ANY);

	if (nPort != 0)
	{
		sockinfo_in.sin_port = htons(nPort);
	}
	if (sIp.compare("") != 0)
	{
		sockinfo_in.sin_addr.s_addr = inet_addr(sIp.c_str());
	}
	
	//Bind Socket to Interface
	if( bind( sock, (struct sockaddr*)&sockinfo_in, sizeof(sockinfo_in)) == -1 )
	{
		TRC_ERR("Error [udpSocket]: Could not bind socket to interface" );
		return EXIT_FAILURE;
	}
	
	return sock;
}

//! \brief
//!    Open UDP-Socket v6 in UNIX
//! \param &interface
//! \return sock
int CConnection::udp6Socket(string &interface)
{
	int no = 0;
	
	//Open Socket
	sock = socket( AF_INET6, SOCK_DGRAM, IPPROTO_UDP );
	if( sock == -1 )
	{
		TRC_ERR("Error [udp6Socket]: Could not create socket" );
		return EXIT_FAILURE;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in6, 0, sizeof(sockinfo_in6));
	sockinfo_in6.sin6_flowinfo 	= 0;
	sockinfo_in6.sin6_family 	= AF_INET6;
	

	setsockopt(sock, IPPROTO_IPV6, IPV6_V6ONLY, (char*)&no, sizeof(no));
	
	(void) inet_pton (AF_INET6, interface.c_str(), sockinfo_in6.sin6_addr.s6_addr);
	
	//Bind Socket to Interface
	if( bind( sock, (struct sockaddr*)&sockinfo_in6, sizeof(sockinfo_in6)) == -1 )
	{
		TRC_ERR("Error [udp6Socket]: Could not bind socket to interface" );
		return EXIT_FAILURE;
	}

	return sock;
}

//! \brief
//!    Open UDP-Socket v6 in UNIX
//! \param &interface
//! \return sock
int CConnection::udp6SocketServer(int &nPort)
{
	return udp6SocketServer(nPort, "");
}

//! \brief
//!    Open UDP-Socket v6 in UNIX
//! \param &interface
//! \return sock
int CConnection::udp6SocketServer(int &nPort, string sIp)
{
	int no = 0;
	
	//Open Socket
	sock = socket( AF_INET6, SOCK_DGRAM, IPPROTO_UDP );
	if( sock == -1 )
	{
		TRC_ERR("Error [udp6Socket]: Could not create socket" );
		return EXIT_FAILURE;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in6, 0, sizeof(sockinfo_in6));
	sockinfo_in6.sin6_flowinfo 	= 0;
	sockinfo_in6.sin6_family 	= AF_INET6;
	sockinfo_in6.sin6_port		= htons(nPort);

	if (sIp.compare("") != 0)
	{
		(void) inet_pton (AF_INET6, sIp.c_str(), sockinfo_in6.sin6_addr.s6_addr);
	}

	setsockopt(sock, IPPROTO_IPV6, IPV6_V6ONLY, (char*)&no, sizeof(no));
		
	//Bind Socket to Interface
	if( bind( sock, (struct sockaddr*)&sockinfo_in6, sizeof(sockinfo_in6)) == -1 )
	{
		TRC_ERR("Error [udp6Socket]: Could not bind socket to interface" );
		return EXIT_FAILURE;
	}
	
	return sock;
}

//! \brief
//!    Open TCP-Socket in UNIX
//! \param &interface
//! \return sock
int CConnection::tcpSocket(string &interface, string &sServer, int &nPort )
{	
	int flags = 0;
	
	timeval tv;
	tv.tv_sec = 5;
	tv.tv_usec = 0;
	
	//Open Socket
	sock = socket( AF_INET, SOCK_STREAM, 0 );
	if( sock == -1 )
	{
		TRC_ERR( "Error [tcpSocket]: Could not create socket" );
		return EXIT_FAILURE;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in, 0, sizeof(sockinfo_in));
	sockinfo_in.sin_family = AF_INET;
	sockinfo_in.sin_addr.s_addr = inet_addr(interface.c_str());
	
	//Bind Socket to Interface
	if( bind( sock, (struct sockaddr*)&sockinfo_in, sizeof(sockinfo_in) ) == -1 )
	{
		TRC_ERR( "Error [tcpSocket]: Could not bind socket to interface" );
		return EXIT_FAILURE;
	}
	
	/* Construct the server address structure */
	memset(&sockinfo_in, 0, sizeof(sockinfo_in));			/* Zero out structure */
	sockinfo_in.sin_family 		= AF_INET;			/* Internet addr family */
	sockinfo_in.sin_addr.s_addr 	= inet_addr(sServer.c_str());	/* Server IP address */
	sockinfo_in.sin_port   		= htons(nPort);			/* Server port */
	
	//Set Socket to Non-Blocking
	fcntl(sock, F_SETFL, flags | O_NONBLOCK);
	
	//Connect to Server
	connect(sock, (sockaddr *)&sockinfo_in, sizeof(sockinfo_in) );
	
	TRC_DEBUG( ("Connection from: "+interface+" to: "+sServer+":"+CTool::toString(nPort)).c_str() );
	
	//Set Socket to Blocking
	fcntl(sock, F_SETFL, flags & (~O_NONBLOCK) );
	
	fd_set set;
	FD_ZERO(&set);
	FD_SET(sock, &set);

	//Check if Socket is ready and connect was ok.
	if( select(sock+1, NULL, &set, NULL, &tv) == -1 )
	{
		TRC_ERR( "Error [tcpSocket]: Creating socket failed - Could not connect to Server" );
		return EXIT_FAILURE;
	}
	
	return sock;
}

//! \brief
//!    Open TCP-Socket in UNIX
//! \param &interface
//! \return sock
int CConnection::tcpSocketServer( int &nPort )
{	
	int on = 1;
	
	//Open Socket
	sock = socket( AF_INET, SOCK_STREAM, 0 );
	if( sock == -1 )
	{
		TRC_ERR( "Error [tcpSocketServer]: Could not create socket" );
		return EXIT_FAILURE;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in, 0, sizeof(sockinfo_in));
	sockinfo_in.sin_family 		= AF_INET;
	sockinfo_in.sin_addr.s_addr 	= htonl(INADDR_ANY);
	sockinfo_in.sin_port		= htons(nPort);
	
	setsockopt(sock, SOL_SOCKET,SO_REUSEADDR,(const char *) &on, sizeof(on));
	
	//Bind Socket to Interface
	if( bind( sock, (struct sockaddr*)&sockinfo_in, sizeof(sockinfo_in) ) == -1 )
	{
		TRC_ERR( "Error [tcpSocketServer]: Could not bind socket to interface" );
		return EXIT_FAILURE;
	}
	
	listen(sock, 1);
	
	return sock;
}

//! \brief
//!    Open TCP-Socket v6 in UNIX
//! \param &interface
//! \return sock
int CConnection::tcp6Socket(string &interface, string &sServer, int &nPort )
{
	int on = 1;
	int no = 0;
	int flags = 0;
	
	timeval tv;
	tv.tv_sec = 10;
	tv.tv_usec = 0;
	
	//Open Socket
	sock = socket( AF_INET6, SOCK_STREAM, 0 );
	if( sock == -1 )
	{
		TRC_ERR("Error [tcp6Socket]: Could not create socket" );
		return EXIT_FAILURE;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in6, 0, sizeof(sockinfo_in6));
	sockinfo_in6.sin6_flowinfo 	= 0;
	sockinfo_in6.sin6_family 	= AF_INET6;
	(void) inet_pton (AF_INET6, interface.c_str(), sockinfo_in6.sin6_addr.s6_addr);
	
	setsockopt(sock, IPPROTO_IPV6, IPV6_V6ONLY, (char*)&no, sizeof(no));
	
	setsockopt(sock, SOL_SOCKET,SO_REUSEADDR,(const char *) &on, sizeof(on));
	
	//Bind Socket to Interface
	if( bind( sock, (struct sockaddr*)&sockinfo_in6, sizeof(sockinfo_in6)) == -1 )
	{
		TRC_ERR("Error [tcp6Socket]: Could not bind socket to interface" );
		return EXIT_FAILURE;
	}
		
	/* Construct the server address structure */
	memset(&sockinfo_in6, 0, sizeof(sockinfo_in6));			/* Zero out structure */
	sockinfo_in6.sin6_family 		= AF_INET6;			/* Internet addr family */
	sockinfo_in6.sin6_port   		= htons(nPort);			/* Server port */
	(void) inet_pton (AF_INET6, sServer.c_str(), sockinfo_in6.sin6_addr.s6_addr);
	
	//Set Socket to Non-Blocking
	fcntl(sock, F_SETFL, flags | O_NONBLOCK);
	
	//Connect to Server
	connect(sock, (sockaddr *)&sockinfo_in6, sizeof(sockinfo_in6) );
	
	TRC_DEBUG( ("Connection from: "+interface+" to: "+sServer+":"+CTool::toString(nPort)).c_str() );
	
	//Set Socket to Blocking
	fcntl(sock, F_SETFL, flags & (~O_NONBLOCK) );
	
	fd_set set;
	FD_ZERO(&set);
	FD_SET(sock, &set);

	//Check if Socket is ready and connect was ok.
	if( select(sock+1, NULL, &set, NULL, &tv) == -1 )
	{
		TRC_ERR( "Error [tcp6Socket]: Creating socket failed - Could not connect to Server" );
		return EXIT_FAILURE;
	}

	return sock;
}

//! \brief
//!    Open TCP-Socket v6 in UNIX
//! \param &interface
//! \return sock
int CConnection::tcp6SocketServer( int &nPort )
{
	int on = 1;
	int no = 0;
	
	//Open Socket
	sock = socket( AF_INET6, SOCK_STREAM, 0 );
	if( sock == -1 )
	{
		TRC_ERR("Error [tcp6SocketServer]: Could not create socket" );
		return EXIT_FAILURE;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in6, 0, sizeof(sockinfo_in6));
	sockinfo_in6.sin6_flowinfo 	= 0;
	sockinfo_in6.sin6_family 	= AF_INET6;
	sockinfo_in6.sin6_port		= htons(nPort);
		
	setsockopt(sock, IPPROTO_IPV6, IPV6_V6ONLY, (char*)&no, sizeof(no));
	
	setsockopt(sock, SOL_SOCKET,SO_REUSEADDR,(const char *) &on, sizeof(on));
	
	//Bind Socket to Interface
	if( bind( sock, (struct sockaddr*)&sockinfo_in6, sizeof(sockinfo_in6)) == -1 )
	{
		TRC_ERR("Error [tcp6SocketServer]: Could not bind socket to interface" );
		return EXIT_FAILURE;
	}
	
	listen(sock, 1);
	
	return sock;
}

