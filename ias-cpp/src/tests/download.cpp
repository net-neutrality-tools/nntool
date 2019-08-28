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

#include "download.h"

//! \brief
//!	Standard Constructor
Download::Download()
{
}

//! \brief
//!	Virtual Destructor
Download::~Download()
{
}

//! \brief
//!	Download init function. Copy information to local vars
//! \param &settings
//! \return 0
Download::Download( CConfigManager *pConfig, CConfigManager *pXml, CConfigManager *pService, string sProvider )
{
	mServerName = pXml->readString(sProvider,"DNS_HOSTNAME","default.com");
	mServer 	= pXml->readString(sProvider,"IP","1.1.1.1");
	#ifdef __ANDROID__
	    mClient = pXml->readString(sProvider, "CLIENT_IP", "0.0.0.0");
	#else
	    mClient = "0.0.0.0";
	#endif
	mPort   	= pXml->readLong(sProvider,"DL_PORT",80);
	mTls		= pXml->readLong(sProvider,"TLS",0);

	#ifndef NNTOOL
		mClient 	= CTool::getIP( pService->readString("TAC51","LAN-IF","eth1"), pXml->readLong(sProvider, "NET_TYPE", 4) );
		//Security Credentials
		pConfig->writeString("security","user",pXml->readString(sProvider,"USER",""));
		pConfig->writeString("security","psw",pXml->readString(sProvider,"PSW",""));
	#endif

	//Set HTTP-Response Limt
	if( pXml->readString(sProvider,"testname","dummy") == "http_down_dataload" )
		mLimit = 5000000;
	else
		mLimit = 1000000;

	mConfig = pConfig;

	mDownloadString = "GET";
}

//! \brief
//!	Download test. Worker function with testcase implemented
//! \param &mutex
//! \param &download
//! \param &syncing_thread
//! \return 0
int Download::run()
{
    //Create Socket Object
    std::unique_ptr<CConnection> mConnection = std::make_unique<CConnection>();

    try {
		bool ipv6validated = false;

		//Syslog Message
		TRC_INFO( ("Starting Download Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );

		//Get Hostname and make DNS Request
		TRC_DEBUG( ("Resolving Hostname for Measurement: "+mServerName).c_str() );

		#if defined(NNTOOL) && defined(__ANDROID__)

		if( CTool::validateIp(mClient) == 6) {
			mServer = CTool::getIpFromHostname( mServerName, 6 );
		} else {
			mServer = CTool::getIpFromHostname( mServerName, 4 );
        }
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
			TRC_DEBUG( ("Resolved Hostname for Measurement: "+mServer).c_str() );

		 	if (CTool::validateIp(mServer) == 6) ipv6validated = true; 
		#endif


		#ifndef NNTOOL
			//MYSQL_LOG("Measurement-DL-Hostname",mServerName);
			if( CTool::validateIp(mClient) == 6)
				mServer = CTool::getIpFromHostname( mServerName, 6 );
			else
				mServer = CTool::getIpFromHostname( mServerName, 4 );
			//MYSQL_LOG("Measurement-DL-Server",mServer);
		#endif

		TRC_DEBUG( ("Resolved Hostname for Measurement: "+mServer).c_str() );

		pid = syscall(SYS_gettid);

		measurementTimeStart 	= 0;
		measurementTimeEnd 		= 0;
		measurementTimeDuration	= 0;

		measurementTimeStart = CTool::get_timestamp();

		//Start syncing threads
		syncing_threads[pid] = 0;

        //Create Buffer for sending data with assured deletion
        std::unique_ptr<char[]> rbufferOwner = std::make_unique<char[]>(MAX_PACKET_SIZE);
        //call the get once to prevent unnecessary function calls
        char *rbuffer = rbufferOwner.get();

		//Default Values for the test
		system_availability  = 1;
		service_availability = 1;
		error = 0;
		error_description = "/";

		nHttpResponseDuration = 0;
		nHttpResponseReportValue = 0;

		#ifndef NNTOOL
			if( CTool::validateIp(mClient) == 6 && CTool::validateIp(mServer) == 6 ) ipv6validated = true;
		#endif

        int ipversion;

		if (ipv6validated)
		{
			//Create a TCP socket
			if( ( mConnection->tcp6Socket(mClient, mServer, mPort, mTls, mServerName) ) < 0 )
			{
				//Error
				::UNREACHABLE = true;
				::hasError = true;
				TRC_ERR("no connection to measurement peer: " + mServer);
				return -1;
			}

			ipversion = 6;
			#ifndef NNTOOL
				//MYSQL_LOG("Measurement-DL-Socket","IPv6");
			#endif
		}
		else
		{
			//Create a TCP socket
			if( ( mConnection->tcpSocket(mClient, mServer, mPort, mTls, mServerName) ) < 0 )
			{
				//Error
				::UNREACHABLE = true;
				::hasError = true;
				TRC_ERR("no connection to measurement peer: " + mServer);
				return -1;
			}

			ipversion = 4;
			#ifndef NNTOOL
				//MYSQL_LOG("Measurement-DL-Socket","IPv4");
			#endif
		}

		bzero(rbuffer, MAX_PACKET_SIZE);

		timeval tv;
		tv.tv_sec = 5;
		tv.tv_usec = 0;

		setsockopt( mConnection->mSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval) );
		setsockopt( mConnection->mSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval) );

		//Send Request and Authenticate Client
		std::unique_ptr<CHttp> pHttp = std::make_unique<CHttp>( mConfig, mConnection.get(), mDownloadString );

		int response = pHttp->requestToReferenceServer();
		if (response < 0 )
		{
			if (response == -1)
			{
				::UNREACHABLE = true;
				TRC_ERR("no connection to measurement peer: " + mServer);

				#ifndef NNTOOL
					//MYSQL_LOG("Measurement-DL-Auth","No valid credentials for this server: "+mServer);
				#endif
			}
			if (response == -2)
			{
				::FORBIDDEN = true;
				TRC_ERR("authorization unsuccessful on peer: " + mServer);

				#ifndef NNTOOL
					//MYSQL_LOG("Measurement-DL-Auth","No valid credentials for this server: "+mServer);
				#endif
			}
			if (response == -3)
			{
				::OVERLOADED = true;
				TRC_ERR("measurement peer overloaded: " + mServer);
			}

			::hasError = true;

			mConnection->close();

			//Syslog Message
			TRC_DEBUG( ("Ending Download Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );

			return 0;
		}

		#ifndef NNTOOL
			//MYSQL_LOG("Measurement-DL-Auth","ok");
		#endif

		nHttpResponseDuration = pHttp->getHttpResponseDuration();
		std::string mServerHostname = pHttp->getHttpServerHostname();

		#ifndef NNTOOL
			//MYSQL_LOG("Measurement-DL-Connection",mServerHostname);
		#endif

		mDownload.datasize_total = 0;

		while( RUNNING && TIMER_ACTIVE && !TIMER_STOPPED && !m_fStop )
		{
			//Get data from socket
			mResponse = mConnection->receive(rbuffer, MAX_PACKET_SIZE, 0);

			//Send signal, we are ready
			syncing_threads[pid] = 1;

			//Got an error
			if(mResponse == -1 || mResponse == 0)
			{
				TRC_ERR("Received an Error: Download RECV == " + std::to_string(mResponse));
                ::hasError = true;
				//break to the end of the loop
				break;
			}

			//Zero buffer
			bzero(rbuffer, MAX_PACKET_SIZE);

			mResponse = mResponse * 8;
			mDownload.datasize_total += mResponse;

			//Timer is running
			if( TIMER_RUNNING && !hasError)
			{
				if(mDownload.results.find(TIMER_INDEX) == mDownload.results.end())
					mDownload.results[TIMER_INDEX] = mResponse;
				else
					mDownload.results[TIMER_INDEX] += mResponse;
			}
		}

		#ifndef NNTOOL
			measurementTimeEnd = CTool::get_timestamp();

			measurementTimeDuration = measurementTimeEnd - measurementTimeStart;

			//Lock Mutex
			pthread_mutex_lock(&mutex1);

				unsigned long long nDownload0 = mDownload.results.begin()->first;

				//Get Max T0
				if( measurements.download.totime < nDownload0 )
					measurements.download.totime = nDownload0;

				//Starting multiple Instances for every Probe
				for(map<int, unsigned long long>::iterator AI = mDownload.results.begin(); AI!= mDownload.results.end(); ++AI)
				{
					//write to Global Object
					measurements.download.results[(*AI).first] 	+= (*AI).second;
					measurements.download.datasize  		+= (*AI).second;

					//TRC_DEBUG( ("Results ["+CTool::toString( (*AI).first )+"]: "+CTool::toString( (*AI).second ) ).c_str() );
				}

				//Must be a valid value and non zero
				if( nHttpResponseDuration != 0 )
				{
					measurements.download.httpresponse[pid]		= nHttpResponseDuration;
					//TRC_DEBUG( ("httpresponse ["+CTool::toString( pid )+"]: "+CTool::toString( nHttpResponseDuration ) ).c_str() );
				}

				measurements.download.packetsize 		= MAX_PACKET_SIZE;

				measurements.download.starttime  		= measurementTimeStart;
				measurements.download.endtime    		= measurementTimeEnd;
				measurements.download.totaltime  		= measurementTimeDuration;

				measurements.download.client			= mClient;
				measurements.download.server    		= mServer;
				measurements.download.servername    	= mServerName;
				measurements.download.serverhostname   	= mServerHostname;

				measurements.download.ipversion 		= ipversion;

				if( mLimit == 5000000 )
					nHttpResponseReportValue =  CTool::calculateResultsAvg( measurements.download.httpresponse );
				else
					nHttpResponseReportValue =  CTool::calculateResultsMin( measurements.download.httpresponse );

				//Check min of http response of all values
				if( nHttpResponseReportValue > mLimit )
				{
					//If an error occured twice
					if( error != 0 )
						error_description		+= "/";

					service_availability 		= 0;
					error 						= 2;
					error_description 			+= "HTTP Response > "+CTool::toString( (mLimit/1000000) )+"s";
				}

				//Socket closed unexpectedly
				if( mResponse == -1 )
				{
					//If an error occured twice
					if( error != 0 )
						error_description		+= "/";

					service_availability 		= 0;
					error 						= 1;
					error_description 			+= "Socket closed";
				}

				//No Data from Socket
				if( mResponse == 0 )
				{
					//If an error occured twice
					if( error != 0 )
						error_description		+= "/";

					service_availability 		= 0;
					error 						= 1;
					error_description 			+= "No Data from Socket";
				}

				measurements.download.system_availability 	= system_availability;

				//If one thread as finished with ok, then test is ok,
				//or we detected a httpresponse above the limit
				if( measurements.download.service_availability == 0 || measurements.download.error_code == 2 || error == 2 )
				{
					measurements.download.service_availability 	= service_availability;
					measurements.download.error_code			= error;
					measurements.download.error_description		= error_description;
				}

				if( mResponse > 0 )
					measurements.streams++;

				TRC_DEBUG(
					(
					"/sys:"+CTool::toString(system_availability)+
					"/ser:"+CTool::toString(service_availability)+
					"("+CTool::toString(measurements.download.service_availability)+")"+
					"/err:"+CTool::toString(error)+
					"/des:"+error_description
					).c_str() );

			//Unlock Mutex
			pthread_mutex_unlock(&mutex1);
		#endif

    } catch (std::exception & ex) {
        ::hasError = true;
        ::RUNNING = false;
        ::recentException = ex;
	}

	mConnection->close();

	//Syslog Message
	TRC_DEBUG( ("Ending Download Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );

	return 0;
}
