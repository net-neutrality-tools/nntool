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
 *      \date Last update: 2019-04-29
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef TIMER_H
#define TIMER_H

#include "header.h"
#include "callback.h"

using namespace std;

/*!
\class CTimer
\brief Thread CTimer
*/
class CTimer : public CBasisThread
{
	private:
		string mHostname;
		int mPort;
		int mInstances;

		CCallback *mCallback;
	
	public:
		CTimer();
		
		virtual ~CTimer();
		
		CTimer( int nInstances, CCallback *pCallback );
		
		int run();
};

#endif
