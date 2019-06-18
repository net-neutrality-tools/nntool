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
 *      \date Last update: 2019-06-18
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "connection.h"

//! \brief
//!	Standard Destructor
CConnection::CConnection()
{
	mSocket					= 0;
	mTls 					= 0;
	mTlsSubjectValidation	= "";
}

//! \brief
//!	Virtual Destructor
CConnection::~CConnection()
{
}

//! \brief
//!	Open Raw-Socket in UNIX
//! \param &interface
//! \return mSocket
int CConnection::rawSocketEth()
{
	//Open Socket
	mSocket = socket(PF_PACKET, SOCK_RAW, htons(ETH_P_ALL));
	if( mSocket == -1 )
	{
		TRC_ERR("Error [rawSocketEth]: Could not create socket" );
		return -1;
	}
	
	return mSocket;
}

//! \brief
//!    Open UDP-Socket in UNIX
//! \param &interface
//! \return mSocket
int CConnection::udpSocket(string &interface)
{
	//Open Socket
	mSocket = socket( AF_INET, SOCK_DGRAM, IPPROTO_UDP );
	if( mSocket == -1 )
	{
		TRC_ERR("Error [udpSocket]: Could not create socket" );
		return -1;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in, 0, sizeof(sockinfo_in));
	sockinfo_in.sin_family 		= AF_INET;
	sockinfo_in.sin_addr.s_addr 	= inet_addr(interface.c_str());

	//Bind Socket to Interface
	if( ::bind( mSocket, (struct sockaddr*)&sockinfo_in, sizeof(sockinfo_in)) == -1 )
	{
		TRC_ERR("Error [udpSocket]: Could not bind socket to interface" );
		return -1;
	}

	return mSocket;
}

//! \brief
//!    Open UDP-Socket in UNIX
//! \param &interface
//! \return mSocket
int CConnection::udpSocketServer(int &nPort)
{
	return udpSocketServer(nPort, "");
}

//! \brief
//!    Open UDP-Socket in UNIX
//! \return mSocket
int CConnection::udpSocketServer(int &nPort, string sIp)
{
	//Open Socket
	mSocket = socket( AF_INET, SOCK_DGRAM, IPPROTO_UDP );
	if( mSocket == -1 )
	{
		TRC_ERR("Error [udpSocket]: Could not create socket" );
		return -1;
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
	if( ::bind( mSocket, (struct sockaddr*)&sockinfo_in, sizeof(sockinfo_in)) == -1 )
	{
		TRC_ERR("Error [udpSocket]: Could not bind socket to interface" );
		return -1;
	}
	
	return mSocket;
}

//! \brief
//!    Open UDP-Socket v6 in UNIX
//! \param &interface
//! \return mSocket
int CConnection::udp6Socket(string &interface)
{
	int no = 0;
	
	//Open Socket
	mSocket = socket( AF_INET6, SOCK_DGRAM, IPPROTO_UDP );
	if( mSocket == -1 )
	{
		TRC_ERR("Error [udp6Socket]: Could not create socket" );
		return -1;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in6, 0, sizeof(sockinfo_in6));
	sockinfo_in6.sin6_flowinfo 	= 0;
	sockinfo_in6.sin6_family 	= AF_INET6;
	

	setsockopt(mSocket, IPPROTO_IPV6, IPV6_V6ONLY, (char*)&no, sizeof(no));
	
	(void) inet_pton (AF_INET6, interface.c_str(), sockinfo_in6.sin6_addr.s6_addr);
	
	//Bind Socket to Interface
	if( ::bind( mSocket, (struct sockaddr*)&sockinfo_in6, sizeof(sockinfo_in6)) == -1 )
	{
		TRC_ERR("Error [udp6Socket]: Could not bind socket to interface" );
		return -1;
	}

	return mSocket;
}

//! \brief
//!    Open UDP-Socket v6 in UNIX
//! \param &interface
//! \return mSocket
int CConnection::udp6SocketServer(int &nPort)
{
	return udp6SocketServer(nPort, "");
}

//! \brief
//!    Open UDP-Socket v6 in UNIX
//! \param &interface
//! \return mSocket
int CConnection::udp6SocketServer(int &nPort, string sIp)
{
	int no = 0;
	
	//Open Socket
	mSocket = socket( AF_INET6, SOCK_DGRAM, IPPROTO_UDP );
	if( mSocket == -1 )
	{
		TRC_ERR("Error [udp6Socket]: Could not create socket" );
		return -1;
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

	setsockopt(mSocket, IPPROTO_IPV6, IPV6_V6ONLY, (char*)&no, sizeof(no));
		
	//Bind Socket to Interface
	if( ::bind( mSocket, (struct sockaddr*)&sockinfo_in6, sizeof(sockinfo_in6)) == -1 )
	{
		TRC_ERR("Error [udp6Socket]: Could not bind socket to interface" );
		return -1;
	}
	
	return mSocket;
}

//! \brief
//!    Open TCP-Socket in UNIX
//! \param &interface
//! \return mSocket
int CConnection::tcpSocket(string &interface, string &sServer, int &nPort )
{
	return tcpSocket(interface, sServer, nPort, 0, "");
}

//! \brief
//!    Open TCP-Socket in UNIX
//! \param &interface
//! \return mSocket
int CConnection::tcpSocket(string &interface, string &sServer, int &nPort, int nTls, string sTlsSubjectValidation)
{	
	int flags = 0;
	
	timeval tv;
	tv.tv_sec = 5;
	tv.tv_usec = 0;
	
	//Open Socket
	mSocket = socket( AF_INET, SOCK_STREAM, 0 );
	if( mSocket == -1 )
	{
		TRC_ERR( "Error [tcpSocket]: Could not create socket" );
		return -1;
	}

	mTls = nTls;
	mTlsSubjectValidation = sTlsSubjectValidation;
	
	//Parameterset for Socket
	memset(&sockinfo_in, 0, sizeof(sockinfo_in));
	sockinfo_in.sin_family = AF_INET;
	sockinfo_in.sin_addr.s_addr = inet_addr(interface.c_str());
	
	//Bind Socket to Interface
	if( ::bind( mSocket, (struct sockaddr*)&sockinfo_in, sizeof(sockinfo_in) ) == -1 )
	{
		TRC_ERR( "Error [tcpSocket]: Could not bind socket to interface" );
		return -1;
	}
	
	/* Construct the server address structure */
	memset(&sockinfo_in, 0, sizeof(sockinfo_in));				/* Zero out structure */
	sockinfo_in.sin_family 		= AF_INET;						/* Internet addr family */
	sockinfo_in.sin_addr.s_addr = inet_addr(sServer.c_str());	/* Server IP address */
	sockinfo_in.sin_port   		= htons(nPort);					/* Server port */
	
	//Set Socket to Non-Blocking
	fcntl(mSocket, F_SETFL, flags | O_NONBLOCK);
	
	//Connect to Server
	connect(mSocket, (sockaddr *)&sockinfo_in, sizeof(sockinfo_in) );
	
	TRC_DEBUG( ("Connection from: "+interface+" to: "+sServer+":"+CTool::toString(nPort)).c_str() );
	
	//Set Socket to Blocking
	fcntl(mSocket, F_SETFL, flags & (~O_NONBLOCK) );
	
	fd_set set;
	FD_ZERO(&set);
	FD_SET(mSocket, &set);

	//Check if Socket is ready and connect was ok.
	if( select(mSocket+1, NULL, &set, NULL, &tv) == -1 )
	{
		TRC_ERR( "Error [tcpSocket]: Creating socket failed - Could not connect to Server" );
		return -1;
	}

	if (mTls && connectTLS() != 0)
	{
		return -1;
	}
	
	return mSocket;
}

//! \brief
//!    Open TCP-Socket in UNIX
//! \param &interface
//! \return mSocket
int CConnection::tcpSocketServer( int &nPort )
{	
	int on = 1;
	
	//Open Socket
	mSocket = socket( AF_INET, SOCK_STREAM, 0 );
	if( mSocket == -1 )
	{
		TRC_ERR( "Error [tcpSocketServer]: Could not create socket" );
		return -1;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in, 0, sizeof(sockinfo_in));
	sockinfo_in.sin_family 		= AF_INET;
	sockinfo_in.sin_addr.s_addr = htonl(INADDR_ANY);
	sockinfo_in.sin_port		= htons(nPort);
	
	setsockopt(mSocket, SOL_SOCKET,SO_REUSEADDR,(const char *) &on, sizeof(on));
	
	//Bind Socket to Interface
	if( ::bind( mSocket, (struct sockaddr*)&sockinfo_in, sizeof(sockinfo_in) ) == -1 )
	{
		TRC_ERR( "Error [tcpSocketServer]: Could not bind socket to interface" );
		return -1;
	}
	
	listen(mSocket, 1);
	
	return mSocket;
}

//! \brief
//!    Open TCP-Socket in UNIX
//! \param &interface
//! \return mSocket
int CConnection::tcp6Socket(string &interface, string &sServer, int &nPort )
{
	return tcp6Socket(interface, sServer, nPort, 0, "");
}

//! \brief
//!    Open TCP-Socket v6 in UNIX
//! \param &interface
//! \return mSocket
int CConnection::tcp6Socket(string &interface, string &sServer, int &nPort, int nTls, string sTlsSubjectValidation )
{
	int on = 1;
	int no = 0;
	int flags = 0;
	
	timeval tv;
	tv.tv_sec = 10;
	tv.tv_usec = 0;
	
	//Open Socket
	mSocket = socket( AF_INET6, SOCK_STREAM, 0 );
	if( mSocket == -1 )
	{
		TRC_ERR("Error [tcp6Socket]: Could not create socket" );
		return -1;
	}

	mTls = nTls;
	mTlsSubjectValidation = sTlsSubjectValidation;

	//Parameterset for Socket
	memset(&sockinfo_in6, 0, sizeof(sockinfo_in6));
	sockinfo_in6.sin6_flowinfo 	= 0;
	sockinfo_in6.sin6_family 	= AF_INET6;
	(void) inet_pton (AF_INET6, interface.c_str(), sockinfo_in6.sin6_addr.s6_addr);
	
	setsockopt(mSocket, IPPROTO_IPV6, IPV6_V6ONLY, (char*)&no, sizeof(no));
	
	setsockopt(mSocket, SOL_SOCKET,SO_REUSEADDR,(const char *) &on, sizeof(on));
	
	//Bind Socket to Interface
	if( ::bind( mSocket, (struct sockaddr*)&sockinfo_in6, sizeof(sockinfo_in6)) == -1 )
	{
		TRC_ERR("Error [tcp6Socket]: Could not bind socket to interface" );
		return -1;
	}
		
	/* Construct the server address structure */
	memset(&sockinfo_in6, 0, sizeof(sockinfo_in6));			/* Zero out structure */
	sockinfo_in6.sin6_family 		= AF_INET6;			/* Internet addr family */
	sockinfo_in6.sin6_port   		= htons(nPort);			/* Server port */
	(void) inet_pton (AF_INET6, sServer.c_str(), sockinfo_in6.sin6_addr.s6_addr);
	
	//Set Socket to Non-Blocking
	fcntl(mSocket, F_SETFL, flags | O_NONBLOCK);
	
	//Connect to Server
	connect(mSocket, (sockaddr *)&sockinfo_in6, sizeof(sockinfo_in6) );
	
	TRC_DEBUG( ("Connection from: "+interface+" to: "+sServer+":"+CTool::toString(nPort)).c_str() );
	
	//Set Socket to Blocking
	fcntl(mSocket, F_SETFL, flags & (~O_NONBLOCK) );
	
	fd_set set;
	FD_ZERO(&set);
	FD_SET(mSocket, &set);

	//Check if Socket is ready and connect was ok.
	if( select(mSocket+1, NULL, &set, NULL, &tv) == -1 )
	{
		TRC_ERR( "Error [tcp6Socket]: Creating socket failed - Could not connect to Server" );
		return -1;
	}

	if (mTls && connectTLS() != 0)
	{
		return -1;
	}

	return mSocket;
}

//! \brief
//!    Open TCP-Socket v6 in UNIX
//! \param &interface
//! \return mSocket
int CConnection::tcp6SocketServer( int &nPort )
{
	int on = 1;
	int no = 0;
	
	//Open Socket
	mSocket = socket( AF_INET6, SOCK_STREAM, 0 );
	if( mSocket == -1 )
	{
		TRC_ERR("Error [tcp6SocketServer]: Could not create socket" );
		return -1;
	}
	
	//Parameterset for Socket
	memset(&sockinfo_in6, 0, sizeof(sockinfo_in6));
	sockinfo_in6.sin6_flowinfo 	= 0;
	sockinfo_in6.sin6_family 	= AF_INET6;
	sockinfo_in6.sin6_port		= htons(nPort);
		
	setsockopt(mSocket, IPPROTO_IPV6, IPV6_V6ONLY, (char*)&no, sizeof(no));
	
	setsockopt(mSocket, SOL_SOCKET,SO_REUSEADDR,(const char *) &on, sizeof(on));
	
	//Bind Socket to Interface
	if( ::bind( mSocket, (struct sockaddr*)&sockinfo_in6, sizeof(sockinfo_in6)) == -1 )
	{
		TRC_ERR("Error [tcp6SocketServer]: Could not bind socket to interface" );
		return -1;
	}
	
	listen(mSocket, 1);
	
	return mSocket;
}

int CConnection::send(const void *buf, int num, int flags)
{
	if ( mTls == 0 )
	{
		return ::send(mSocket, buf, num, flags);
	}
	else if ( mTls == 1 )
	{
		return SSL_write(ssl, buf, num);
	}

	return -1;
}

int CConnection::receive(void *buf, int num, int flags)
{
	if ( mTls == 0 )
	{
		return ::recv(mSocket, buf, num, flags);
	}
	else if ( mTls == 1 )
	{
		return SSL_read(ssl, buf, num);
	}

	return -1;
}

int CConnection::close()
{
	return ::close(mSocket);
}


static int tlsVerifyCertificateCallback(int ok, X509_STORE_CTX *store_ctx)
{

    int cert_error = X509_STORE_CTX_get_error(store_ctx);

    switch (cert_error)
	{
		case X509_V_ERR_CERT_NOT_YET_VALID:
		case X509_V_ERR_ERROR_IN_CERT_NOT_BEFORE_FIELD:
		case X509_V_ERR_CERT_HAS_EXPIRED:
		case X509_V_ERR_ERROR_IN_CERT_NOT_AFTER_FIELD:
		{
			TRC_ERR("TLS Certificate Validation failed with error: " + to_string(cert_error));
			return -1;
			break;
		}
	}


    return(ok);
}



int CConnection::connectTLS()
{
  	int ssl_error = 0;

	OpenSSL_add_ssl_algorithms();
	method = TLS_client_method();
	SSL_load_error_strings();
	ctx = SSL_CTX_new (method);

	ssl = SSL_new (ctx);
	SSL_set_fd(ssl, mSocket);
	ssl_error = SSL_connect(ssl);

	if (ssl_error <= 0)
	{
		ssl_error = SSL_get_error(ssl, -1);
		TRC_ERR("SSL Error " + to_string(ssl_error) + " while negotiating TLS connection");
		return -1;
	}
	else
	{
		server_cert = SSL_get_peer_certificate (ssl);

		//check certificate
	    X509_STORE *store;
	    X509_STORE_CTX *store_ctx;

	    store = X509_STORE_new();
	    X509_STORE_set_verify_cb(store, tlsVerifyCertificateCallback);
	    X509_STORE_add_cert(store, server_cert);

	    store_ctx = X509_STORE_CTX_new();
	    X509_STORE_CTX_init(store_ctx, store, server_cert, NULL);

	    if (X509_verify_cert(store_ctx) != 0)
	    {
			return -1;
	    }

		//check subject name
		string subject_name = "";
		subject_name = X509_NAME_oneline (X509_get_subject_name (server_cert), 0, 0);
		TRC_DEBUG("TLS Certificate Subject name: " + subject_name);
		TRC_DEBUG("TLS Subejct validation: " + mTlsSubjectValidation);
		if (mTlsSubjectValidation.compare("") != 1 && subject_name.find(mTlsSubjectValidation) == string::npos )
		{
			TRC_ERR("TLS Certificate Subject name validation failed");
			return -1;
		}

		TRC_INFO("TLS Connection established");
	}


	return 0;
}

