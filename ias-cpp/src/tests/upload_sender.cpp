/*!
    \file upload_sender.cpp
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
