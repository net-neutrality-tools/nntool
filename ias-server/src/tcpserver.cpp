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
 *      \date Last update: 2019-08-19
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */


#include "tcpserver.h"


using namespace std;


//! \brief
//!	Standard Destructor
CTcpServer::CTcpServer()
{
}

//! \brief
//!	Virtual Destructor
CTcpServer::~CTcpServer()
{
}

//! \brief
//!	Standard Destructor
CTcpServer::CTcpServer(int nTargetPort, int nTargetPortTraceroute, bool nTlsSocket)
{
    mTargetPort				= nTargetPort;
    mTargetPortTraceroute 	= nTargetPortTraceroute;
    mTlsSocket  			= nTlsSocket;
	
    mConnection             = std::make_unique<CConnection>();
}

//! \brief
//!    Run-Function
//! \return 0
int CTcpServer::run()
{
	signal(SIGCLD, SIG_IGN);

    int on = 1;

    struct sockaddr_in6 client;
    unsigned int clientlen = sizeof(client);

    if ((mSock = mConnection->tcp6SocketServer(mTargetPort)) == 1)
    {
        TRC_CRIT("Socket creation failed - Could not establish connection on target Port " + to_string(mTargetPort));
        return EXIT_FAILURE;
    }

    string sTlsEnabled = "";
    if (mTlsSocket) sTlsEnabled = "TLS ";
    
    TRC_INFO("Start Thread: TCP " + sTlsEnabled + "socket on target Port " + to_string(mTargetPort) + " with PID " + std::to_string(syscall(SYS_gettid)));

    timeval tv;
    timeval tv_l;
    tv.tv_sec = 60;
    tv.tv_usec = 0;
    tv_l.tv_sec = 1;
    tv_l.tv_usec = 0;
    setsockopt(mConnection->mSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv_l, sizeof(timeval));
    setsockopt(mConnection->mSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv_l, sizeof(timeval));

	while (::RUNNING)
    {
        int nSocket = accept(mSock, (struct sockaddr *)&client, &clientlen);
        
        setsockopt(nSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
        setsockopt(nSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));
        
        setsockopt(nSocket, IPPROTO_TCP, TCP_QUICKACK,  (void *)&on, sizeof(on));
        if ((nSocket > 0) && fork() == 0)
        {
            string ip = CTool::get_ip_str((struct sockaddr *)&client);  
            
            if (ip.find("::ffff:") != string::npos)
            {
                ip = ip.substr(7,string::npos);
            }
            
            TRC_DEBUG("---------------------------------------------------------");
            TRC_INFO("Socket: TCP " + sTlsEnabled + "connection received from Client IP " + ip + " on target Port " + to_string(mTargetPort));
            
            if (mTargetPort != mTargetPortTraceroute)
            {
            	//start tcp handler
                std::unique_ptr<CTcpHandler> pTcpHandler = std::make_unique<CTcpHandler>( nSocket, ip, mTlsSocket, &client );
            	pTcpHandler->handle_tcp();
            }
            else
            {
            	//start tcp traceroute handler
            	std::unique_ptr<CTcpTracerouteHandler> pTcpTracerouteHandler = std::make_unique<CTcpTracerouteHandler>(nSocket, ip, mTlsSocket );
            	pTcpTracerouteHandler->handle_tcp_traceroute();
            }

            TRC_INFO("Socket: TCP " + sTlsEnabled + "connection shutdown for Client IP " + ip + " on target Port " + to_string(mTargetPort));
            
            close(nSocket);
            
            exit(0);
        }

        close(nSocket);
    }

	TRC_DEBUG("End Thread: TCP " + sTlsEnabled + "socket on target Port " + to_string(mTargetPort) + " with PID " + std::to_string(syscall(SYS_gettid)));
	
	return 0;
}