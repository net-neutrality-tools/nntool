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
 *      \date Last update: 2019-07-01
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "ping.h"

//! \brief
//!	Standard Constructor
Ping::Ping()
{
}

//! \brief
//!	Virtual Destructor
Ping::~Ping()
{
}

//! \brief
//!	Ping init function. Copy information to local vars
//! \param &settings
Ping::Ping( CConfigManager *pXml, CConfigManager *pService, string sProvider )
{		
	mServerName	= pXml->readString(sProvider,"DNS_HOSTNAME_RTT","default.com");
	mServer 	= pXml->readString(sProvider,"PING_DESTINATION","1.1.1.1");	
	mClient 	= "0.0.0.0";
	mPingQuery 	= pXml->readLong(sProvider,"PING_QUERY",30);
	mPingQuery++;
	nPingTarget	= mPingQuery -1;
	mPingPort 	= pXml->readLong(sProvider,"PING_PORT",80);
	
	#ifndef NNTOOL
		mClient = CTool::getIP( pService->readString("TAC51","LAN-IF","eth1"), pXml->readLong(sProvider, "NET_TYPE", 4) );
		CTool::logging( ( "Ping: "+mServer+" from: "+mClient+" Query: "+CTool::toString(nPingTarget)+" Port: "+CTool::toString(mPingPort)).c_str() );
	#endif

	//Create Socket Object
	mSocket = std::make_unique<CConnection>();
	
	mTimeDiff = 1;
}

//! \brief
//!	Ping test. Worker function with testcase implemented
//! \param &ping
//! \return 0
int Ping::run()
{	
    try {
		//Syslog Message
		TRC_INFO( ("Starting Ping Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );

		measurementTimeStart 	= 0;
		measurementTimeEnd 		= 0;
		measurementTimeDuration = 0;
		
		measurementTimeStart = CTool::get_timestamp();
		
		//Create Buffer for sending data
		char sbuffer[ECHO];
	    char rbuffer[ECHO];
		
		nSize = ECHO;
		nError = 0;

		#ifndef NNTOOL
			int nReply = 0;
			int nMissing = 0;
		#endif
		
		int timeout = 1000000;
		system_availability  = 1;
		service_availability = 0;
		error = 0;
		error_description = "";
		int i = 1;
		
		bool ipv6 = false;
		bool ipv4 = false;

		bool ipv6validated = false;
		
		//Get Hostname and make DNS Request
		TRC_DEBUG( ("Resolving Hostname for Measurement: "+mServerName).c_str() );

		#if defined(NNTOOL) && defined(__ANDROID__)
			if( CTool::validateIp(mClient) == 6)
				mServer = CTool::getIpFromHostname( mServerName, 6 );
			else
				mServer = CTool::getIpFromHostname( mServerName, 4 );
		#endif

		#if defined(NNTOOL) && !defined(__ANDROID__)
			struct addrinfo *ips;
			memset(&ips, 0, sizeof ips);

			ips = CTool::getIpsFromHostname( mServerName, true );

			char host[NI_MAXHOST];
			
			getnameinfo(ips->ai_addr, ips->ai_addrlen, host, sizeof host, NULL, 0, NI_NUMERICHOST);
			mServer = string(host);
		#endif

		#ifdef NNTOOL
		 	::MEASUREMENT_DURATION = (int)mPingQuery * 1.5 * 1.1;

			TRC_DEBUG( ("Resolved Hostname for Measurement: "+mServer).c_str() );

		 	if (CTool::validateIp(mServer) == 6) ipv6validated = true; 
		#endif

		#ifndef NNTOOL
			if( CTool::validateIp(mClient) == 6 && CTool::validateIp(mServer) == 6 ) ipv6validated = true;
		#endif

		if (ipv6validated)	
		{
			//Create a datagram/UDP socket
			if( ( mSock = mSocket->udp6Socket(mClient) ) < 0 )
			{
				//Error
				TRC_ERR("Creating socket failed - socket()");

				return -1;
			}
			
			ipv4 = false;
			ipv6 = true;
			ipversion = 6;
		}
		else
		{
			//Create a datagram/UDP socket
			if( ( mSock = mSocket->udpSocket(mClient) ) < 0 )
			{
				//Error
				TRC_ERR("Creating socket failed - socket()");

				return -1;
			}
			
			ipv4 = true;
			ipv6 = false;
			ipversion = 4;
		}
		
		timeval tv;
		tv.tv_sec = 1;
		tv.tv_usec = 0;
		
		// set the receive timeout to 1 seconds
		setsockopt(mSock, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
		setsockopt(mSock, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));

		while( RUNNING && i <= mPingQuery )
		{
			#ifdef NNTOOL
				//Send signal, we are ready
				syncing_threads[syscall(SYS_gettid)] = 1;
			#endif

			memset(sbuffer, 0, sizeof(sbuffer));
			memset(rbuffer, 0, sizeof(rbuffer));
			
			//Get Random Data with defined payload size
			CTool::randomData(sbuffer,ECHO);
			
			//Set Timestamp T1
			time1 = CTool::get_timestamp();

			if(ipv4)
			{
				mClientDataSizev4 = sizeof(mClientDatav4);
				
				/* Construct the server address structure */
				memset(&mClientDatav4, 0, sizeof(mClientDatav4));		/* Zero out structure */
				mClientDatav4.sin_family 		= AF_INET;				/* Internet addr family */
				mClientDatav4.sin_port			= htons(mPingPort);
				mClientDatav4.sin_addr.s_addr 	= inet_addr(mServer.c_str());
				
				//Send the string to the server
				mResponse = sendto(mSock, sbuffer, ECHO, 0, (struct sockaddr *) &mClientDatav4, sizeof(mClientDatav4));
				
				mResponse = recvfrom(mSock, rbuffer, ECHO, 0, (struct sockaddr *) &mClientDatav4, &mClientDataSizev4);
			}
			if(ipv6)
			{
				mClientDataSizev6 = sizeof(mClientDatav6);
				
				/* Construct the server address structure */
				memset(&mClientDatav6, 0, sizeof(mClientDatav6));		/* Zero out structure */
				mClientDatav6.sin6_family 	= AF_INET6;					/* Internet addr family */
				mClientDatav6.sin6_port		= htons(mPingPort);
				(void) inet_pton (AF_INET6, mServer.c_str(), mClientDatav6.sin6_addr.s6_addr);
				
				//Send the string to the server
				mResponse = sendto(mSock, sbuffer, ECHO, 0, (struct sockaddr *) &mClientDatav6, sizeof(mClientDatav6));
				
				mResponse = recvfrom(mSock, rbuffer, ECHO, 0, (struct sockaddr *) &mClientDatav6, &mClientDataSizev6);
			}
			
			//Check size of response, because when we get a timeout, mResponse will be -1
			if( mResponse != -1 )
			{
				//Set Timestamp T2
				time2 = CTool::get_timestamp();
			
				//Calculate timediff
				mTimeDiff = time2 - time1;

				//check for mirrored payload
				for(int i=0;i<mResponse;i++)
				{
					if (static_cast<short int>(rbuffer[i]) != static_cast<short int>(sbuffer[i]))
					{
						TRC_ERR("Ping payload mismatch");
						mTimeDiff = 0;
						break;
					}
				}
			}
			else
			{
				mTimeDiff = 0;
			}
			
			if(mPingResult.results.find(i) == mPingResult.results.end())
				mPingResult.results[i] = mTimeDiff;
			else
				mPingResult.results[i] += mTimeDiff;

			i++;
			
			//Sleep 1000ms
			usleep(timeout);	
		}
		
		#ifndef NNTOOL
			measurementTimeEnd = CTool::get_timestamp();
				
			measurementTimeDuration = measurementTimeEnd - measurementTimeStart;

			//Lock Mutex
			pthread_mutex_lock(&mutex1);
			
				//Starting multiple Instances for every Probe
				for(map<int, unsigned long long>::iterator AI = mPingResult.results.begin(); AI!= mPingResult.results.end(); ++AI)
				{
					//write to Global Object
					measurements.ping.results[(*AI).first] += (*AI).second;
					
					if( (*AI).second == 0 )
						nMissing++;
					else
						nReply++;
				}
				
				//---------------------------
				
				//Calculate Min, Avg, Max
				CTool::calculateResults( measurements.ping, 1, 0 );
					
				//---------------------------
			
				measurements.ping.packetsize 	= nSize;
				measurements.ping.requests 		= nReply + nMissing + nError;
				measurements.ping.replies 		= nReply;
				measurements.ping.missing 		= nMissing;
				measurements.ping.errors 		= nError;
				
				measurements.ping.starttime  	= measurementTimeStart;
				measurements.ping.endtime    	= measurementTimeEnd;
				measurements.ping.totaltime  	= measurementTimeDuration;
				
				measurements.ping.client		= mClient;
				measurements.ping.server    	= mServer;
				measurements.ping.servername    = "-";
				
				measurements.ping.ipversion 	= ipversion;
				
				measurements.ping.system_availability 	= system_availability;
				
				if( nMissing > 0 )
				{
					service_availability 	= 0;
					error 					= 1;
					error_description 		= "Missing Pings";
				}	
				else
				{
					service_availability 	= 1;
					error 					= 0;
					error_description 		= "-";
				}
				
				measurements.ping.service_availability 	= service_availability;
				measurements.ping.error_code			= error;
				measurements.ping.error_description		= error_description;
					
			//Unlock Mutex
			pthread_mutex_unlock(&mutex1);
		#endif

		#ifdef NNTOOL
			::TIMER_STOPPED = true;
        #endif
    } catch (std::exception & ex) {
        ::hasError = true;
        ::RUNNING = false;
        ::recentException = ex;
	}

	close(mSock);
	
	//Syslog Message
	TRC_DEBUG( ("Ending Ping Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );
	
	return 0;
}
