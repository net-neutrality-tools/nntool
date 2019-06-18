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

#ifndef UDPSERVER_H
#define UDPSERVER_H


#include "header.h"


class CUdpListener : public CBasisThread
{
	private:
		int mPort;
		
		string mClient;
		
		std::unique_ptr<CConnection> mConnectionUdp4Send;
		std::unique_ptr<CConnection> mConnectionUdp6Send;
		std::unique_ptr<CConnection> mConnectionRawRecv;

	public:
		CUdpListener();
		
		virtual ~CUdpListener();

		CUdpListener(int nPort);
		
		int run();
};

#endif
