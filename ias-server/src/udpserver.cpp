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
 
#include "udpserver.h"

//! \brief
//!	Standard Constructor
CUdpListener::CUdpListener()
{
}

//! \brief
//!	Virtual Destructor
CUdpListener::~CUdpListener()
{
}

//! \brief
//!	Standard Constructor
CUdpListener::CUdpListener(int nPort, int nIpType, string sBindIp)
{
	mPort 				= nPort;
	mIpType				= nIpType;
	mBindIp				= sBindIp;
	
	mClient 			= "-";

	mConnection 		= std::make_unique<CConnection>();
}

//! \brief
//!    Run-Function
//! \return 0
int CUdpListener::run()
{
	int nResponse = 0;

	char *buffer = (char *)malloc(MAX_PACKET_SIZE);
	bzero(buffer, MAX_PACKET_SIZE);

    struct sockaddr_in6 client;
    unsigned int clientlen = sizeof(client);

    switch(mIpType)
    {
    	case 0:
    	{
    		nResponse = mConnection->udp6SocketServer(mPort);
    		break;
    	}
    	case 4:
    	{
    		nResponse = mConnection->udpSocketServer(mPort, mBindIp);
    		break;
    	}
    	case 6:
    	{
    		nResponse = mConnection->udp6SocketServer(mPort, mBindIp);
    		break;
    	}
    }

    if (nResponse < 0)
    {
		TRC_CRIT("Socket creation failed - Could not establish connection on Port: " + to_string(mPort));
		return EXIT_FAILURE;
    }

    TRC_INFO("Start Thread: UDP" + to_string(mIpType) +  " Listener on Port: " + to_string(mPort) + " with PID: " + std::to_string(syscall(SYS_gettid)));
   
	
	//While Loop for listening
	while(RUNNING)
	{
		nResponse = recvfrom(mConnection->mSocket, buffer, MAX_PACKET_SIZE, 0, (struct sockaddr *)&client, &clientlen);

        string ip = CTool::get_ip_str((struct sockaddr *)&client);  
        
        if (ip.find("::ffff:") != string::npos)
        {
            ip = ip.substr(7,string::npos);
        }

        TRC_INFO("Socket: udpListener: Connection Received from Client IP: " +CTool::toString(ip) + " on target Port: " + CTool::toString(mPort));

        TRC_DEBUG("UDP handler: started");

		nResponse = sendto(mConnection->mSocket, buffer, nResponse, 0, (struct sockaddr *)&client, sizeof(client));

		if( nResponse == -1 )
		{
			break;
		}
                
        TRC_DEBUG("UDP handler: closed");
    
        TRC_INFO("Socket: udpListener: Connection Shutdown for Client IP: " +CTool::toString(ip) + " on target Port: " + CTool::toString(mPort));

	}

    free(buffer);

    close(mConnection->mSocket);

    TRC_DEBUG("End Thread: UDP Listener with PID: " + std::to_string(syscall(SYS_gettid)));
	
	return 0;
}
