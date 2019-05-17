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

#ifndef UDPSERVER_H
#define UDPSERVER_H


#include "header.h"


class CUdpListener : public CBasisThread
{
	private:
		int mUdp4SendSocket;
		int mUdp6SendSocket;
		int mRecvSocket;
		int mPort;
		
		string mClient;
		
		CConnection *mSocket;
		
	public:
		CUdpListener();
		
		virtual ~CUdpListener();

		CUdpListener(int nPort);
		
		int run();
};

#endif