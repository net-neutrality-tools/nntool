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


#ifndef CONNECTION_H_
#define CONNECTION_H_


#include "header.h"


using namespace std;

class CConnection
{
    private:
        int sock;

        struct sockaddr_ll sockinfo_ll;
        struct sockaddr_in sockinfo_in;
        struct sockaddr_in6 sockinfo_in6;

    public:
        CConnection();

        virtual ~CConnection();

        int tcp6SocketServer( int &nPort );
};

#endif
