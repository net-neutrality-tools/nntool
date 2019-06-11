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
 *      \date Last update: 2019-05-20
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "upload.h"

//! \brief
//!	Standard Constructor
Upload::Upload()
{
}

//! \brief
//!	Virtual Destructor
Upload::~Upload()
{
	delete(mConnection);
}

//! \brief
//!	Upload init function. Copy information to local vars
//! \param *pConfig
//! \param &settings
//! \return 0
Upload::Upload( CConfigManager *pConfig, CConfigManager *pXml, CConfigManager *pService, string sProvider )
{
	mClient 	= CTool::getIP( pService->readString("TAC51","LAN-IF","eth1"), pXml->readLong(sProvider, "NET_TYPE", 4)  );
	
	mServerName = pXml->readString(sProvider,"DNS_HOSTNAME","default.com");
	mServer 	= pXml->readString(sProvider,"IP","1.1.1.1");
	mPort   	= pXml->readLong(sProvider,"DL_PORT",80);
	mTls		= pXml->readLong(sProvider,"TLS",0);

	#ifndef NNTOOL
	//Security Credentials
	pConfig->writeString("security","user",pXml->readString(sProvider,"USER",""));
	pConfig->writeString("security","psw",pXml->readString(sProvider,"PSW",""));
	#endif

	//Set HTTP-Response Limt
	if( pXml->readString(sProvider,"testname","dummy") == "http_up_dataload" ) 
		mLimit = 5000000;
	else
		mLimit = 1000000;
	
	//Create Socket Object
	mConnection = new CConnection();
		
	mConfig = pConfig;
	
	mUploadString = "POST";
}

//! \brief
//!	Upload test. Worker function with testcase implemented
//! \param &mutex
//! \param &upload
//! \param &syncing_thread
//! \return 0
int Upload::run()
{	
	bool ipv6validated = false;

	//Syslog Message
	TRC_INFO( ("Starting Upload Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );

	//Get Hostname and make DNS Request
	TRC_DEBUG( ("Resolving Hostname for Measurement: "+mServerName).c_str() );

	#ifdef NNTOOL
// TODO: readd for android
//	struct addrinfo *ips;
//	memset(&ips, 0, sizeof ips);
//
//	ips = CTool::getIpsFromHostname( mServerName, true );
//
//	char host[NI_MAXHOST];
//
//	getnameinfo(ips->ai_addr, ips->ai_addrlen, host, sizeof host, NULL, 0, NI_NUMERICHOST);
//	mServer = string(host);
	if( CTool::validateIp(mClient) == 6)
		mServer = CTool::getIpFromHostname( mServerName, 6 );
	else
		mServer = CTool::getIpFromHostname( mServerName, 4 );

	if (CTool::validateIp(mServer) == 6) ipv6validated = true;
	#endif

	#ifndef NNTOOL
	if( CTool::validateIp(mClient) == 6)
		mServer = CTool::getIpFromHostname( mServerName, 6 );
	else
		mServer = CTool::getIpFromHostname( mServerName, 4 );
	#endif

	TRC_DEBUG( ("Resolved Hostname for Measurement: "+mServer).c_str() );
		
	pid = syscall(SYS_gettid);

	measurementTimeStart 	= 0;
	measurementTimeEnd 		= 0;
	measurementTimeDuration = 0;
	
	measurementTimeStart = CTool::get_timestamp();
	
	//Start syncing threads
	syncing_threads[pid] = 0;
	
	vector<string> vResponse;
	string delimiter = ",";
		
	//Default Values for the test
	system_availability  = 1;
	service_availability = 1;
	error = 0;
	error_description = "/";
	
	char *rbuffer = (char *)malloc(MAX_PACKET_SIZE);
	nDataRecv = 0;
	nTimeRecv = 0;
	
	nTimeRecvExa = 0;
	nTimeRecvExaFirst = 0;
	nTimeRecvFirst = 0;
	
	nHttpResponseDuration = 0;
	nHttpResponseReportValue = 0;

	#ifndef NNTOOL
	if( CTool::validateIp(mClient) == 6 && CTool::validateIp(mServer) == 6 ) ipv6validated = true;
	#endif

	if (ipv6validated)
	{
		//Create a TCP socket
		if( mConnection->tcp6Socket(mClient, mServer, mPort, mTls, mServerName) < 0 )
		{
			//Error
			TRC_ERR("Creating socket failed - Could not establish connection");
			return -1;
		}
		
		ipversion = 6;
	}
	else
	{
		//Create a TCP socket
		if( mConnection->tcpSocket(mClient, mServer, mPort, mTls, mServerName) < 0 )
		{
			//Error
			TRC_ERR("Creating socket failed - Could not establish connection");
			return -1;
		}
		
		ipversion = 4;
	}

	timeval tv;
	tv.tv_sec = 5;
	tv.tv_usec = 0;
	
	//Set Socket Timeout
	setsockopt( mConnection->mSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval) );
	setsockopt( mConnection->mSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval) );
	
	//Send Request and Authenticate Client
	CHttp *pHttp = new CHttp( mConfig, mConnection, mUploadString );
	if( pHttp->requestToReferenceServer() < 0 )
	{
		TRC_INFO("No valid credentials for this server: " + mServer);
		
		mConnection->close();

		return 0;
	}
	
	nHttpResponseDuration = pHttp->getHttpResponseDuration();
	mServerHostname = pHttp->getHttpServerHostname();

	//Start Upload Receiver Thread
	CUploadSender* pUploadSender = new CUploadSender(mConnection);
	pUploadSender->createThread();

	mUpload.datasize_total = 0;
	unsigned long long index = 0;
	unsigned long long match = 0;
	unsigned long long comparator = 0;

	#ifdef NNTOOL
	comparator = (MEASUREMENT_DURATION*2) + (TCP_STARTUP*2);
	#endif

	#ifndef NNTOOL
	comparator = MEASUREMENT_DURATION;
	#endif
	
	while( RUNNING && TIMER_ACTIVE && !TIMER_STOPPED )
	{
		//Emtpy Buffer
		bzero(rbuffer, MAX_PACKET_SIZE);
		
		//Emtpy Vector
		vResponse.clear();
		
		//Receive Data from Server
		mResponse = mConnection->receive(rbuffer, MAX_PACKET_SIZE, 0);
		
		//Send signal, we are ready
		syncing_threads[pid] = 1;
		
		//Got an error
		if(mResponse == -1)
		{
			TRC_ERR("Received an Error: Upload RECV == -1");
			
			//break to the end of the loop
			break;
		}
		
		//Got an error
		if(mResponse == 0)
		{
			TRC_ERR("Received an Error: Upload RECV == 0");
			
			//break to the end of the loop
			break;
		}
			
		//Cut String out of Response from Server
		string sResponse(rbuffer,find( rbuffer, rbuffer + mResponse,  ';'));

		//Split String in different String and save in Vector
		CTool::tokenize(sResponse, vResponse, delimiter);
		
		//check if Vector as at min 2 entries
		if( vResponse.size() > 2 )
		{
			//Save Values from Vector to Variable
			nDataRecv = CTool::toULL(vResponse[0]);
			nTimeRecv = CTool::toULL(vResponse[2]);
			nTimeRecvExa = CTool::toULL(vResponse[3].substr( vResponse[3].length() - 6 ));
        	nDataRecv = nDataRecv * 8;
        	mUpload.datasize_total += nDataRecv;
        }

		//Timer is running
		if( TIMER_RUNNING )
		{
			#ifdef NNTOOL
			index = nTimeRecvExa;
			match = nTimeRecvExaFirst;
			#endif

			#ifndef NNTOOL
			index = nTimeRecv;
			match = nTimeRecvFirst;
			if( index == 0 && ( nTimeRecvExa % 10 ) == 5 )
				continue;
			#endif
			
			if( match == 0 )
				match = index;
			
			if( (index - match) >= comparator)
				break;

			if(mUpload.results.find(index) == mUpload.results.end())
				mUpload.results[index] = nDataRecv;
			else
				mUpload.results[index] += nDataRecv;
		}
	}

	pUploadSender->stopThread();
	
	pUploadSender->waitForEnd();
	
	#ifndef NNTOOL
	measurementTimeEnd = CTool::get_timestamp();
	
	measurementTimeDuration = measurementTimeEnd - measurementTimeStart;
	
	//Lock Mutex
	pthread_mutex_lock(&mutex1);
	
		unsigned long long nUploadt0 = mUpload.results.begin()->first;
		
		//Get Max T0
		if( measurements.upload.totime < nUploadt0 )
			measurements.upload.totime = nUploadt0;
		
		//Starting multiple Instances for every Probe
		for (map<int, unsigned long long>::iterator AI = mUpload.results.begin(); AI!= mUpload.results.end(); ++AI)
		{
			//write to Global Object
			measurements.upload.results[(*AI).first] 	+= (*AI).second;
						
			//TRC_DEBUG( ("Results ["+CTool::toString( pid )+"]["+CTool::toString( (*AI).first )+"]: "+CTool::toString( (*AI).second ) ).c_str() );
		}
		
		//Must be a valid value and non zero
		if( nHttpResponseDuration != 0 )
		{
			measurements.upload.httpresponse[pid]		= nHttpResponseDuration;
			//TRC_DEBUG( ("httpresponse ["+CTool::toString( pid )+"]: "+CTool::toString( nHttpResponseDuration ) ).c_str() );
		}
		
		measurements.upload.packetsize 			= MAX_PACKET_SIZE;
		
		measurements.upload.starttime  			= measurementTimeStart;
		measurements.upload.endtime    			= measurementTimeEnd;
		measurements.upload.totaltime  			= measurementTimeDuration;
		
		measurements.upload.client				= mClient;
		measurements.upload.server    			= mServer;
		measurements.upload.servername    		= mServerName;
		measurements.upload.serverhostname   	= mServerHostname;
		
		measurements.upload.ipversion			= ipversion;
	
		if( mLimit == 5000000 )
			nHttpResponseReportValue =  CTool::calculateResultsAvg( measurements.upload.httpresponse );
		else
			nHttpResponseReportValue =  CTool::calculateResultsMin( measurements.upload.httpresponse );
		
		//Check min of http response of all values
		if( nHttpResponseReportValue > mLimit )
		{
			//If an error occured twice
			if( error == 1 )
				error_description		+= "/";
			
			service_availability 		= 0;
			error 						= 2;
			error_description 			+= "HTTP Response > "+CTool::toString( (mLimit/1000000) )+"s";
		}
		
		//Socket closed unexpectedly
		if( mResponse == -1 )
		{
			//If an error occured twice
			if( error == 1 )
				error_description		+= "/";
			
			service_availability 		= 0;
			error 						= 1;
			error_description 			+= "Socket closed";
		}	
		
		//No Data from Socket
		if( mResponse == 0 )
		{
			//If an error occured twice
			if( error == 1 )
				error_description		+= "/";
			
			service_availability 		= 0;
			error 						= 1;
			error_description 			+= "No Data from Socket";
		}
		
		measurements.upload.system_availability 	= system_availability;
		
		//If one thread as finished with ok, then test is ok, 
		//or we detected a httpresponse above the limit
		if( measurements.upload.service_availability == 0 || measurements.upload.error_code == 2 || error == 2 )
		{
			measurements.upload.service_availability 	= service_availability;
			measurements.upload.error_code				= error;
			measurements.upload.error_description		= error_description;
		}
		
		if( mResponse > 0 )
			measurements.streams++;
		
		TRC_DEBUG(
			(
			"/sys:"+CTool::toString(system_availability)+
			"/ser:"+CTool::toString(service_availability)+
			"("+CTool::toString(measurements.upload.service_availability)+")"+
			"/err:"+CTool::toString(error)+
			"/des:"+error_description
			).c_str() );
		
	//Unlock Mutex
	pthread_mutex_unlock(&mutex1);
	#endif
	
	#ifdef NNTOOL
	usleep(100000);
	#endif

	delete( pHttp );
	delete( pUploadSender );
	
	mConnection->close();
	free(rbuffer);
	
	//Syslog Message
	TRC_DEBUG( ("Ending Upload Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );
	
	return 0;
}
