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
 *      \date Last update: 2019-08-07
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
		int mInstances;

		CCallback *mCallback;

		bool unreachableSignaled;
		bool forbiddenSignaled;
		bool overloadSignaled;
	
	public:
		CTimer();
		
		virtual ~CTimer();
		
		CTimer( int nInstances, CCallback *pCallback );
		
		int run();
};

#endif
