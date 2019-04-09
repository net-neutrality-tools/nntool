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

#include "htlistener.h"


using namespace std;


CHtListener::CHtListener()
{
}

CHtListener::~CHtListener()
{
    delete(mSocket);
}

CHtListener::CHtListener(int nPort, string nTestCase)
{
    mPort = nPort;
    
    mTestCase = nTestCase;
    mClient = "-";

    mSocket = new CConnection();   
}

int CHtListener::run()
{
    signal(SIGCLD, SIG_IGN);

    int on = 1;

    if ((mTcpSocket = mSocket->tcp6SocketServer(mPort)) == 1)
    {
        TRC_CRIT("Socket creation failed - Could not establish connection on Port: " + to_string(mPort));
        return EXIT_FAILURE;
    }

    TRC_INFO("Start Thread: HTTP " + mTestCase + " Listener on Port: " + to_string(mPort) + " with PID: " + std::to_string(syscall(SYS_gettid)));
    
    while(::RUNNING)
    {
        timeval tv;
        tv.tv_sec = 60;
        tv.tv_usec = 0;
         
        struct sockaddr_storage sockaddr_s_tcp;
        memset(&sockaddr_s_tcp, 0, sizeof(sockaddr_s_tcp));
        socklen_t len_tcp = sizeof(sockaddr_s_tcp);
        
        int nSocket = accept(mTcpSocket, (struct sockaddr *) &sockaddr_s_tcp, &len_tcp);
        
        char ip_saddr_char[128];
        
        switch (sockaddr_s_tcp.ss_family)
        {
            case AF_INET:
            {
                struct sockaddr_in* sockinfo_in = (struct sockaddr_in*)(&sockaddr_s_tcp);
                inet_ntop(AF_INET, &sockinfo_in->sin_addr, ip_saddr_char, sizeof(ip_saddr_char));
                break;
            }
            case AF_INET6:
            {
                struct sockaddr_in6* sockinfo_in6 = (struct sockaddr_in6*)(&sockaddr_s_tcp);
                inet_ntop(AF_INET6, &sockinfo_in6->sin6_addr.s6_addr, ip_saddr_char, sizeof(ip_saddr_char));
                break; 
            }
        }
        
        setsockopt(nSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *) &tv, sizeof (timeval));
        setsockopt(nSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *) &tv, sizeof (timeval));
        
        setsockopt(nSocket, IPPROTO_TCP, TCP_NODELAY,   (void *) &on, sizeof (on));
        setsockopt(nSocket, IPPROTO_TCP, TCP_QUICKACK,  (void *) &on, sizeof (on));

        if (fork() == 0)
        {
            string ip_saddr(ip_saddr_char);
                    
            if (ip_saddr.find("::ffff:") != string::npos)
            {
                ip_saddr = ip_saddr.substr(7,string::npos);
            }
            
            TRC_DEBUG("---------------------------------------------------------");
            TRC_INFO("Socket: htListener: Connection Received from Client IP: " + ip_saddr);
            
            //create new hthandler object
            CHtHandler *pHtHandler = new CHtHandler(nSocket, ip_saddr, mTestCase);

            pHtHandler->handle_http();

            TRC_INFO("Socket: htListener: Connection Shutdown for Client IP: " + ip_saddr);
            
            delete(pHtHandler);
            close(nSocket);
            
            exit(0);
        }

        close(nSocket); 
    }
    
    close(mTcpSocket);
    close(mRecvSocket);

    TRC_DEBUG("End Thread: HTTP Listener with PID: " + std::to_string(syscall(SYS_gettid)));

    return 0;
}