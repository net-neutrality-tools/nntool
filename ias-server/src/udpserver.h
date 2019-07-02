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
 *      \date Last update: 2019-06-24
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef UDPSERVER_H
#define UDPSERVER_H


#include "header.h"


class CUdpListener : public CBasisThread
{
	private:
		int mPort;
		int mIpType;
		string mBindIp;
		
		string mClient;
		
		std::unique_ptr<CConnection> mConnection;

	public:
		CUdpListener();
		
		virtual ~CUdpListener();

		CUdpListener(int nPort, int nIpType, string sBindIp);
		
		int run();
};

#endif
