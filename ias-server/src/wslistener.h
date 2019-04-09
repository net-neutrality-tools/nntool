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


#ifndef WSLISTENER_H
#define WSLISTENER_H


#include "header.h"


class CWsListener : public CBasisThread
{
    public:
        CWsListener();

        virtual ~CWsListener();
        
        CWsListener(int nPort, bool nTlsSocket);

        int run() override;

    private:
        int mPort;
        bool mTlsSocket;
        
        int mSock;
        
        CConnection *mSocket;
};


#endif

