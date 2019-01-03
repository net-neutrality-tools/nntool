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


#include "basisthread.h"


CBasisThread::CBasisThread()
{
	m_fStop = false;
	m_hThread = 0;
}

CBasisThread::~CBasisThread()
{
}
 
int CBasisThread::initInstance()
{
    return 0;
}

int CBasisThread::exitInstance()
{
    return 0;
}

int CBasisThread::run()
{
    return 0;
}

int CBasisThread::waitForEnd()
{	
    pthread_join( m_hThread, NULL );

    return 0;
}

int CBasisThread::detachThread()
{	
    pthread_detach( m_hThread );

    return 0;
}

bool CBasisThread::isRunning()
{
    return !m_fStop;
}

int CBasisThread::stopThread()
{
    TRC_DEBUG("Thread "+to_string( (int) m_hThread )+": asked to stop");

    m_fStop = true;

    return 0;
}

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
                    TRC_ERR("Creating Thread failed with Code: "+to_string( nError ) );
    }
    else
            TRC_WARN("Thread "+to_string( (int) m_hThread )+": already created");

    return nError;
}

void* threadStart(void* pParam)
{
    CBasisThread* pThread = (CBasisThread*) pParam;

    int nExit = 0;

    nExit = pThread->initInstance();

    if(0 != nExit)
            pthread_exit(NULL);


    nExit = pThread->run();

    if(0 != nExit)
            pthread_exit(NULL);

    nExit = pThread->exitInstance();

    pthread_exit(NULL);
}
