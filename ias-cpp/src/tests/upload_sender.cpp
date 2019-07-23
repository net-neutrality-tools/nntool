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
 *      \date Last update: 2019-05-20
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "upload_sender.h"

//! \brief
//!	Standard Constructor
CUploadSender::CUploadSender()
{
}

//! \brief
//!	Virtual Destructor
CUploadSender::~CUploadSender()
{
}

//! \brief
//!	Standard Constructor
//! \param nSocket
CUploadSender::CUploadSender( CConnection *nConnection )
{
	mConnection = nConnection;
}

//! \brief
//!	CUploadSender init function. Copy information to local vars
//! \return 0
int CUploadSender::run()
{
	//Syslog Message
	TRC_INFO( ("Starting Sender Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );

    const char *firstChar = randomDataValues.data();

    unsigned long long nPointer = 1;
    int mResponse;
	//++++++MAIN++++++
	//Measurement Loop
	while( RUNNING && !::hasError)
	{

		if (nPointer + MAX_PACKET_SIZE > randomDataValues.size()) {
		    nPointer += MAX_PACKET_SIZE;
		    nPointer -= randomDataValues.size();
		}
		
		mResponse = mConnection->send(firstChar + nPointer, MAX_PACKET_SIZE, 0);

        nPointer += MAX_PACKET_SIZE;

		//Got an error
		if(mResponse == -1 || mResponse == 0)
		{
			TRC_ERR("Received an Error: Upload SEND == " + std::to_string(mResponse));
			::hasError = true;
			//break to the end of the loop
			break;
		}

		//If Thread should stop
		if( m_fStop )
			break;
	}

	//++++++END+++++++
	
	//Syslog Message
	TRC_INFO( ("Ending Sender Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );
	
	return 0;
}
