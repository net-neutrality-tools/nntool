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
 *      \date Last update: 2019-05-29
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
	delete(mConnectionUdp4Send);
	delete(mConnectionUdp6Send);
	delete(mConnectionRawRecv);
}

//! \brief
//!	Standard Constructor
CUdpListener::CUdpListener(int nPort)
{
	mPort = nPort;
	
	mClient = "-";
	
	//Create Socket Objects
	mConnectionUdp4Send = new CConnection();
	mConnectionUdp6Send = new CConnection();
	mConnectionRawRecv 	= new CConnection();
	
}

//! \brief
//!    Run-Function
//! \return 0
int CUdpListener::run()
{
	int nResponse = 0;
	string sResponse;
	
	struct sockaddr client;
	struct sockaddr_in clientv4;
	struct sockaddr_in6 clientv6;
		
	unsigned int clientlen = sizeof(client);
	
	char buffer[MAX_PACKET_SIZE];
	
	int eth_proto = 0;
	int eth_h_len = 0;
	int ip_h_len = 0;
	int ip_ttl = 0;
	int ip_proto = 0;
	string ip_saddr = "";
	string ip_daddr = "";
	int nUdpDstPort = 0;
	int nUdpSrcPort = 0;
	
	struct ethhdr *eth;
	struct ip *iph;
	struct ip6_hdr *iphv6;
	struct udphdr *udp;
	
	//if no ip bindings are configured, bind on ::
	if (::CONFIG["ip_bindings"]["v4"].string_value().compare("") == 0 && ::CONFIG["ip_bindings"]["v6"].string_value().compare("") == 0)
	{
		if( mConnectionUdp6Send->udp6SocketServer(mPort) < 0 )
		{
			TRC_CRIT("Socket creation failed - Could not establish connection on Port: " + to_string(mPort));
		}
	} 
	else
	{
		if( ::CONFIG["ip_bindings"]["v4"].string_value().compare("") != 0 && ( mConnectionUdp4Send->udpSocketServer(mPort, ::CONFIG["ip_bindings"]["v4"].string_value()) ) < 0 )
		{
			TRC_CRIT("Socket creation failed - Could not establish connection on Port: " + to_string(mPort));
		}

		if( ::CONFIG["ip_bindings"]["v6"].string_value().compare("") != 0 && ( mConnectionUdp6Send->udp6SocketServer(mPort, ::CONFIG["ip_bindings"]["v6"].string_value()) ) < 0 )
		{
			TRC_CRIT("Socket creation failed - Could not establish connection on Port: " + to_string(mPort));
		}
	}
	
	
	//Create a RAW socket
	if( ( mConnectionRawRecv->rawSocketEth() ) < 0 )
	{
		//Error
		TRC_CRIT("Socket creation failed - Could not establish RAW Socket connection");
		return EXIT_FAILURE;
	}
        
    TRC_INFO("Start Thread: UDP Listener on Port: " + to_string(mPort) + " with PID: " + std::to_string(syscall(SYS_gettid)));
   
	
	//While Loop for listening
	while(RUNNING)
	{
		nResponse = recvfrom(mConnectionRawRecv->mSocket, buffer, MAX_PACKET_SIZE, 0, (struct sockaddr *)&client, &clientlen);
		
		//--------------------------------------------------------------------------------
		
		//get ethernet header
		eth = (struct ethhdr *)(buffer);
		eth_h_len = sizeof(struct ethhdr);
		eth_proto = ntohs( eth->h_proto );
		
		//--------------------------------------------------------------------------------
		
		//IPv4 Packet - Collect Infos if IP Header
		if( eth_proto == 0x0800 )
		{			
			//IPv4
			ip_h_len = sizeof(struct ip);
			iph = (struct ip *)(buffer + eth_h_len);
			
			ip_ttl = iph->ip_ttl;
			ip_proto = iph->ip_p;
			ip_saddr = inet_ntoa(iph->ip_src);
			ip_daddr = inet_ntoa(iph->ip_dst);
		}
		//IPv6 Packet - Collect Infos if IP Header
		if( eth_proto == 0x86dd )
		{
			//IPv6
			ip_h_len = sizeof(struct ip6_hdr);
			iphv6 = (struct ip6_hdr *)(buffer + eth_h_len);
			
			char abuf[128];
			ip_ttl = iphv6->ip6_hops;
			ip_proto = iphv6->ip6_nxt;
			ip_saddr = inet_ntop(AF_INET6, (char *)&iphv6->ip6_src, (char *)&abuf, sizeof(abuf));
			ip_daddr = inet_ntop(AF_INET6, (char *)&iphv6->ip6_dst, (char *)&abuf, sizeof(abuf));
		}
		
		//--------------------------------------------------------------------------------
		
		//if it is NOT UDP and NOT our measurement port - exit here
		if(ip_proto != 0x11)
			continue;
		
		//--------------------------------------------------------------------------------
		
		//UDP - Collect Infos if UDP Header
		udp = (struct udphdr *)(buffer + eth_h_len + ip_h_len);
		
		nUdpDstPort = htons(udp->dest);
		nUdpSrcPort = htons(udp->source);
		
		//--------------------------------------------------------------------------------
		
		if(nUdpDstPort != mPort)
			continue;
		
		//--------------------------------------------------------------------------------
		
		sResponse.clear();
                
        TRC_INFO("Socket: udpListener: Connection Received from Client IP: " +CTool::toString(ip_saddr) + " on Port: " + CTool::toString(nUdpSrcPort));

        TRC_DEBUG("UDP handler: started");

		// store the amount of bytes received in the actual and the previous timeframe
		sResponse = ""+CTool::toString( ip_ttl )+","+CTool::toString( (int)(htons(udp->len)-8) )+";";
		
		if( eth_proto == 0x0800 )
		{
			//Build IPv4 Sender information
			inet_pton(AF_INET, ip_saddr.c_str(), &clientv4.sin_addr);
			clientv4.sin_family 	= AF_INET;
			clientv4.sin_port 	= htons(nUdpSrcPort);
			
			// send the sResponse to the client
			nResponse = sendto(mConnectionUdp4Send->mSocket, sResponse.c_str(), sResponse.size(), 0, (struct sockaddr *)&clientv4, sizeof(clientv4));
		}
		if( eth_proto == 0x86dd )
		{
			//Build IPv6 Sender information
			inet_pton(AF_INET6, ip_saddr.c_str(), &clientv6.sin6_addr);
			clientv6.sin6_family 	= AF_INET6;
			clientv6.sin6_port 	= htons(nUdpSrcPort);
			
			// send the sResponse to the client
			nResponse = sendto(mConnectionUdp6Send->mSocket, sResponse.c_str(), sResponse.size(), 0, (struct sockaddr *)&clientv6, sizeof(clientv6));
		}
		
		//on Error
		if( nResponse == -1 )
			break;
                
        TRC_DEBUG("UDP handler: closed");
    
        TRC_INFO("Socket: udpListener: Connection Shutdown for Client IP: " +CTool::toString(ip_saddr) + " on Port: " + CTool::toString(nUdpSrcPort));
    }

    close(mConnectionUdp4Send->mSocket);
    close(mConnectionUdp6Send->mSocket);
    close(mConnectionRawRecv->mSocket);

    TRC_DEBUG("End Thread: UDP Listener with PID: " + std::to_string(syscall(SYS_gettid)));
	
	return 0;
}
