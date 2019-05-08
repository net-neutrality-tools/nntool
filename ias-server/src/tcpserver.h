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
 *      \date Last update: 2019-03-21
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */


#ifndef TCPSERVER_H
#define TCPSERVER_H


#include "header.h"


using namespace std;


/*!
\class CTcpServer
\brief Thread CTcpServer
*/
class CTcpServer : public CBasisThread
{
	private:
        int mTargetPort;
        int mTargetPortTraceroute;
        bool mTlsSocket;
        
        int mSock;
        
        CConnection *mSocket;
	
	public:
		CTcpServer();
		
		virtual ~CTcpServer();

        CTcpServer(int nTargetPort, int nTargetPortTraceroute, bool nTlsSocket);

        int run() override;
};

#endif