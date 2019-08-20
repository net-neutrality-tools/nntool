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
 *      \date Last update: 2019-08-20
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
#include "load_monitoring.h"
#include "load_balancing.h"


using namespace std;
using namespace json11;


#define MAX_PACKET_SIZE 1500
#define MAXBUFFER 1580
#define AUTHENTICATION_MAX_AGE 120


#define TRC_DEBUG(s)  CTrace::getInstance().logDebug(s)
#define TRC_INFO(s)   CTrace::getInstance().logInfo(s)
#define TRC_WARN(s)   CTrace::getInstance().logWarn(s)
#define TRC_ERR(s)    CTrace::getInstance().logErr(s)
#define TRC_CRIT(s)   CTrace::getInstance().logCritical(s)


extern bool DEBUG_NOPOLL;
extern bool DEBUG_NOPOLL_ERROR;
extern bool DEBUG_NOPOLL_CRITICAL;
extern bool OVERLOADED;
extern bool SERVERMONITORING;
extern Json CONFIG;

#define IF_SCAN_PATTERN " %[^:]:%llu %llu %*d %*d %*d %*d %*d %*d %llu %llu"
#define MEM_SCAN_PATTERN "%[^:]: %u"

extern pthread_mutex_t mutexLoad;

#endif