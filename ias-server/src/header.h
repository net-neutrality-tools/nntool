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
 *      \date Last update: 2019-05-06
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#define VERSION 1.0


#ifndef HEADER_H
#define HEADER_H

#include <sys/socket.h>
#include <sys/types.h>
#include <sys/syscall.h>
#include <sys/ioctl.h>


#include <cstdlib>
#include <iostream>
#include <unistd.h>
#include <fstream>
#include <sstream>
#include <signal.h>
#include <thread>
#include <vector>
#include <cmath>
#include <dirent.h>
#include <strings.h>
#include <iomanip> 
#include <algorithm>
#include <stdio.h>
#include <string.h>


#include <net/if.h>


#include <netinet/ether.h>
#include <netinet/ip.h>
#include <netinet/ip6.h>
#include <netinet/tcp.h>
#include <netinet/udp.h>
#include <netinet/ip_icmp.h>
#include <netinet/icmp6.h>


#include <netpacket/packet.h>


//log4cpp
#include <log4cpp/Category.hh>
#include <log4cpp/PropertyConfigurator.hh>


//libnntool
#include "../../ias-libnntool/json11.hpp"
#include "../../ias-libnntool/sha1.hpp"
#include "../../ias-libnntool/tool.h"
#include "../../ias-libnntool/connection.h"
#include "../../ias-libnntool/basisthread.h"


//libnopoll
#include "nopoll.h"
#include "nopoll_ctx.h"
#include "nopoll_conn.h"
#include "nopoll_msg.h"
#include "nopoll_listener.h"
#include "nopoll_private.h"


//project
#include "tcphandler.h"
#include "tcptraceroutehandler.h"
#include "udpserver.h"


using namespace std;


#define MAX_PACKET_SIZE 1500
#define MAXBUFFER 1580


#define TRC_DEBUG(s)  CTrace::getInstance()->logDebug(s)
#define TRC_INFO(s)   CTrace::getInstance()->logInfo(s)
#define TRC_WARN(s)   CTrace::getInstance()->logWarn(s)
#define TRC_ERR(s)    CTrace::getInstance()->logErr(s)
#define TRC_CRIT(s)   CTrace::getInstance()->logCritical(s)


//http header fields
#define HTTP_GET			"GET / HTTP/1.1"
#define HTTP_POST 			"POST / HTTP/1.1"
#define HTTP_OTPIONS 		"OPTIONS / HTTP/1.1"
#define HTTP_CONTENT_LENGTH	"Content-Length:"
#define HTTP_HOST 			"Host:"
#define HTTP_ACCESS_METHOD	"Access-Control-Request-Method:"
#define HTTP_ACCESS_HEADERS	"Access-Control-Request-Headers:"
#define HTTP_ORIGIN			"Origin:"
#define HTTP_CONNECTION		"Connection:"
#define HTTP_COOKIE			"Cookie"
#define HTTP_DATA			"/data.img"
#define HTTP_USER_AGENT		"User-Agent"
#define HTTP_BAD_REQUEST	"HTTP/1.1 400 Bad Request\r\n\r\n";


extern bool DEBUG;
extern bool DEBUG_NOPOLL;
extern bool DEBUG_NOPOLL_ERROR;
extern bool DEBUG_NOPOLL_CRITICAL;
extern bool RUNNING;
extern bool OVERLOADED;
extern bool SERVERMONITORING;

#define IF_SCAN_PATTERN " %[^:]:%llu %llu %*d %*d %*d %*d %*d %*d %llu %llu"
	
#define MEM_SCAN_PATTERN "%[^:]: %u"
#define UPTIME_SCAN_PATTERN "%d.%ds %d.%ds"

#endif