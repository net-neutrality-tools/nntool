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
 *      \date Last update: 2019-01-02
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */


#include "connection.h"


CConnection::CConnection()
{
    sock = 0;
}

CConnection::~CConnection()
{
}

int CConnection::tcp6SocketServer( int &nPort )
{
    int on = 1;
    int no = 0;

    sock = socket( AF_INET6, SOCK_STREAM, 0 );
    if( sock == -1 )
    {
            TRC_ERR("Error [tcp6SocketServer]: Could not create socket" );
            return EXIT_FAILURE;
    }
    
    memset(&sockinfo_in6, 0, sizeof(sockinfo_in6));
    sockinfo_in6.sin6_flowinfo 	= 0;
    sockinfo_in6.sin6_family 	= AF_INET6;
    sockinfo_in6.sin6_port  = htons(nPort);

    setsockopt(sock, IPPROTO_IPV6, IPV6_V6ONLY, (char*)&no, sizeof(no));

    setsockopt(sock, SOL_SOCKET,SO_REUSEADDR,(const char *) &on, sizeof(on));

    if( bind( sock, (struct sockaddr*)&sockinfo_in6, sizeof(sockinfo_in6)) == -1 )
    {
            TRC_ERR("Error [tcp6SocketServer]: Could not bind socket to interface" );
            return EXIT_FAILURE;
    }

    listen(sock, 1);

    return sock;
}