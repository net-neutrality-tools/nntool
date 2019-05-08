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
 *      \date Last update: 2019-04-28
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef BASISTHREAD_H
#define BASISTHREAD_H

#include "header.h"
#include "tool.h"
#include "typedef.h"

using namespace std;

void* threadStart(void* pParam);

class CBasisThread
{
	public:
		CBasisThread();
		virtual ~CBasisThread();

		virtual int		initInstance();
		virtual int		exitInstance();
		virtual int		run();
		
		int			waitForEnd();
		int			detachThread();
		unsigned long		getExitCode();
		int			stopThread();
		int 			createThread( );
		bool			isRunning();

		pthread_t		m_hThread;
		void *params;
		
	protected:

		friend void*		threadStart(void* pParam);

		bool			m_fStop;
		unsigned long		m_dwExitCode;

};




#endif // CBASISTHREAD_H
