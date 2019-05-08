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

#include "basisthread.h"

//! \brief
//!	Standard Constructor
CBasisThread::CBasisThread()
{
	m_fStop = false;
	m_hThread = 0;
}

//! \brief
//!	Standard Destructor
CBasisThread::~CBasisThread()
{
}
 
//! \brief
//!	Init an Instance of this class
//! \return 0
int CBasisThread::initInstance()
{
	return 0;
}

//! \brief
//!	Exit an Instance of this class
//! \return 0
int CBasisThread::exitInstance()
{
	return 0;
}

//! \brief
//!	Method run() - Will be overritten
//! \return 0
int CBasisThread::run()
{
	return 0;
}

//! \brief
//!	Method waitForEnd() - Will join the thread 
//! \return 0
int CBasisThread::waitForEnd()
{	
	pthread_join( m_hThread, NULL );
	
	return 0;
}

//! \brief
//!	Method detachThread() - Will detach the thread
//! \return 0
int CBasisThread::detachThread()
{	
	pthread_detach( m_hThread );
	
	return 0;
}

//! \brief
//!	Getter for testing if thread is running
//! \return m_fStop
bool CBasisThread::isRunning()
{
	return !m_fStop;
}

//! \brief
//!	Request a thread to stop
//! \return 0
int CBasisThread::stopThread()
{
// 	char chTrace[1024];
// 	snprintf(chTrace, sizeof(chTrace), "Thread (%02x): asked to stop", (int) m_hThread);
// 	TRC_DEBUG(chTrace);
	
	TRC_DEBUG("Thread "+CTool::toString( (int) m_hThread )+": asked to stop");
	
	m_fStop = true;
	
	return 0;
}

//! \brief
//!	Getter for the exit code of the Thread
//! \return m_dwExitCode
unsigned long CBasisThread::getExitCode()
{
	return m_dwExitCode;
}

int CBasisThread::createThread()
{
	int nError = 0;
	
	if(m_hThread == 0)
	{
		nError = pthread_create(&m_hThread, NULL, threadStart, this);
		
		if ( 0 != nError )
			TRC_ERR("Creating Thread failed with Code: "+CTool::toString( nError ) );

		TRC_INFO("Thread "+CTool::toString( (int) m_hThread )+": created");
	}
	else
		TRC_WARN("Thread "+CTool::toString( (int) m_hThread )+": already created");

	return nError;
}

void* threadStart(void* pParam)
{
	CBasisThread* pThread = (CBasisThread*) pParam;
	
	int nExit = 0;

	//Init Thread
	nExit = pThread->initInstance();
	//On Error
	if(0 != nExit)
		pthread_exit(NULL);

	//Run Thread 
	nExit = pThread->run();
	//On Error
	if(0 != nExit)
		pthread_exit(NULL);

	//Exit Thread
	nExit = pThread->exitInstance();
	
	pthread_exit(NULL);
}



