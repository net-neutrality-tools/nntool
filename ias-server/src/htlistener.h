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


#ifndef HTLISTENER_H
#define HTLISTENER_H


#include "header.h"


class CHtListener : public CBasisThread
{
    public:
        CHtListener();

        virtual ~CHtListener();

        CHtListener(int nPort, string nTestCase);

        int run();
        
    private:
        int mTcpSocket;
        int mRecvSocket;
        int mPort;
        
        string mTestCase;
        string mClient;

        CConnection *mSocket;
};

#endif