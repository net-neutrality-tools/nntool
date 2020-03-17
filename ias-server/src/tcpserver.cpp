/*!
    \file tcpserver.cpp
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2020-03-16

    Copyright (C) 2016 - 2020 zafaco GmbH

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
CTcpServer::CTcpServer(int nTargetPort, int nTargetPortTraceroute, int nIpType, string sIp, bool nTlsSocket)
{
    mTargetPort				= nTargetPort;
    mTargetPortTraceroute 	= nTargetPortTraceroute;
    mTlsSocket  			= nTlsSocket;
	mIpType			    	= nIpType;
    mIp                     = sIp;
    mConnection             = std::make_unique<CConnection>();
}

//! \brief
//!    Run-Function
//! \return 0
int CTcpServer::run()
{

    int nResponse = 0;
	signal(SIGCLD, SIG_IGN);

    int on = 1;

    struct sockaddr_in6 client;
    unsigned int clientlen = sizeof(client);


    switch(mIpType)
    {
    	case 0:
    	{
    		nResponse = mConnection->tcp6SocketServer(mTargetPort);
    		break;
    	}
    	case 4:
    	{
    		nResponse = mConnection->tcpSocketServer(mTargetPort, mIp);
    		break;
    	}
    	case 6:
    	{
    		nResponse = mConnection->tcp6SocketServer(mTargetPort, mIp);
    		break;
    	}
    }

    if (nResponse < 0)
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
    mSock = nResponse;
	while (::RUNNING)
    {
        int nSocket = accept(mSock, (struct sockaddr *)&client, &clientlen);
        
        setsockopt(nSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
        setsockopt(nSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));
        
        setsockopt(nSocket, IPPROTO_TCP, TCP_QUICKACK,  (void *)&on, sizeof(on));
        if ((nSocket > 0) && fork() == 0)
        {
            mConnection->close();
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