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


#include "wslistener.h"
#include "wshandler.h"


using namespace std;


CWsListener::CWsListener()
{
}

CWsListener::~CWsListener()
{
    delete(mSocket);
}

CWsListener::CWsListener(int nPort, bool nTlsSocket)
{
    mPort       = nPort;
    mTlsSocket  = nTlsSocket;
    
    mSocket     = new CConnection();
}


int CWsListener::run()
{
    signal(SIGCLD, SIG_IGN);

    int on = 1;

    struct sockaddr_in6 client;
    unsigned int clientlen = sizeof (client);

    if ((mSock = mSocket->tcp6SocketServer(mPort)) == 1)
    {
        TRC_CRIT("Socket creation failed - Could not establish connection on Port: " + to_string(mPort));
        return EXIT_FAILURE;
    }
    
    string sTlsEnabled = "";
    if (mTlsSocket) sTlsEnabled = "TLS ";
    
    TRC_INFO("Start Thread: WS " + sTlsEnabled + "Listener on Port: " + to_string(mPort) + " with PID: " + std::to_string(syscall(SYS_gettid)));

    timeval tv;
    tv.tv_sec = 60;
    tv.tv_usec = 0;

    while (::RUNNING)
    {
        int nSocket = accept(mSock, (struct sockaddr *) &client, &clientlen);
        
        setsockopt(nSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *) &tv, sizeof (timeval));
        setsockopt(nSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *) &tv, sizeof (timeval));
        
        setsockopt(nSocket, IPPROTO_TCP, TCP_QUICKACK,  (void *) &on, sizeof (on));

        if (fork() == 0)
        {
            string ip = CTool::get_ip_str((struct sockaddr *)&client);  
            
            if (ip.find("::ffff:") != string::npos)
            {
                ip = ip.substr(7,string::npos);
            }
            
            TRC_DEBUG("---------------------------------------------------------");
            TRC_INFO("Socket: wsListener: Connection Received from Client IP: " + ip);
            
            //create new wshandler object
            CWsHandler *pWsHandler = new CWsHandler(&client, nSocket, ip, mTlsSocket);

            pWsHandler->handle_websocket();

            TRC_INFO("Socket: wsListener: Connection Shutdown for Client IP: " + ip);
            
            close(nSocket);
            
            exit(0);
        }

        close(nSocket);
    }
    
    close(mSock);

    TRC_DEBUG("End Thread: WS Listener with PID: " + std::to_string(syscall(SYS_gettid)));

    return 0;
}