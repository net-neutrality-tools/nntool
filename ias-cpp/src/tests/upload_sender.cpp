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
CUploadSender::CUploadSender( int nSocket )
{
	mSocket = nSocket;
	
	nPointer = 0;
}

//! \brief
//!	CUploadSender init function. Copy information to local vars
//! \return 0
int CUploadSender::run()
{
	//Syslog Message
	TRC_INFO( ("Starting Sender Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );
	
	//++++++MAIN++++++
	//Measurement Loop
	while( RUNNING )
	{
		vector<char> payload(&randomDataValues[nPointer], &randomDataValues[nPointer+MAX_PACKET_SIZE]);
		nPointer += MAX_PACKET_SIZE;
		
		if (nPointer > randomDataValues.size())
		{
			nPointer = nPointer - randomDataValues.size();

			vector<char> payload(&randomDataValues[nPointer], &randomDataValues[nPointer+MAX_PACKET_SIZE]);
			nPointer += MAX_PACKET_SIZE;
		}
		
		mResponse = send(mSocket ,payload.data(), MAX_PACKET_SIZE, 0);
		
		//Got an error
		if(mResponse == -1)
		{
			TRC_ERR("Received an Error: Upload SEND == -1");
			
			//break to the end of the loop
			break;
		}
		//Got an error
		if(mResponse == 0)
		{
			TRC_ERR("Received an Error: Upload SEND == 0");
			
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
