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
 *      \date Last update: 2019-05-03
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#define VERSION 1.0


#ifndef HEADER_H
#define HEADER_H

#include <sys/socket.h>
#include <sys/time.h>
#include <sys/ioctl.h>
#include <sys/resource.h>
#include <sys/syscall.h> 
#include <sys/wait.h>
#include <sys/stat.h>


#include <syslog.h>
#include <signal.h>
#include <sched.h>
#include <unistd.h>


#include <netinet/ether.h>
#include <netinet/ip.h>
#include <netinet/ip6.h>
#include <netinet/ip_icmp.h>
#include <netinet/tcp.h>
#include <netinet/udp.h>
#include <netinet/in.h>
#include <netpacket/packet.h>
#include <net/if.h>
#include <arpa/inet.h>
#include <arpa/nameser.h>
#include <netdb.h>
#include <resolv.h>
#include <ifaddrs.h>


#include <cmath>
#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <iostream>
#include <algorithm>
#include <exception>


#include <pthread.h>
#include <fcntl.h>
#include <map>
#include <list>
#include <vector>
#include <getopt.h>
#include <getopt.h>
#include <errno.h>
#include <execinfo.h>
#include <cxxabi.h>


//libnntool
#include "../../ias-libnntool/json11.hpp"
#include "../../ias-libnntool/typedef.h"
#include "../../ias-libnntool/tool.h"
#include "../../ias-libnntool/connection.h"
#include "../../ias-libnntool/configmanager.h"
#include "../../ias-libnntool/basisthread.h"
#include "../../ias-libnntool/http.h"


//project
#include "type.h"


using namespace std;
using namespace json11;


//colors
#define BLK "\x1b[0;30m"
#define RED "\x1b[0;31m"
#define GRN "\x1b[0;32m"
#define BRN "\x1b[0;33m"
#define BLU "\x1b[0;34m"
#define MGN "\x1b[0;35m"
#define CYA "\x1b[0;36m"
#define NOR "\x1b[0;37m"
#define GRA "\x1b[1;30m"
#define LRD "\x1b[1;31m"
#define LGN "\x1b[1;32m"
#define YEL "\x1b[1;33m"
#define LBL "\x1b[1;34m"
#define PIN "\x1b[1;35m"
#define LCY "\x1b[1;36m"
#define BRI "\x1b[1;37m"
#define RST "\x1b[0m"


#define MAX_PACKET_SIZE 1500
#define MAXBUFFER 1580
#define MAX_NUM_THREADS 256
#define ECHOMAX 64
#define ECHO 64


#define TRC_DEBUG(s)  CTrace::getInstance()->logDebug(s)
#define TRC_INFO(s)   CTrace::getInstance()->logInfo(s)
#define TRC_WARN(s)   CTrace::getInstance()->logWarn(s)
#define TRC_ERR(s)    CTrace::getInstance()->logErr(s)
#define TRC_CRIT(s)   CTrace::getInstance()->logCritical(s)


extern bool DEBUG;
extern bool RUNNING;
extern const char* PLATFORM;
extern const char* CLIENT_OS;

extern unsigned long long TCP_STARTUP;

extern bool RTT;
extern bool DOWNLOAD;
extern bool UPLOAD;

extern bool TIMER_ACTIVE;
extern bool TIMER_RUNNING;
extern bool TIMER_STOPPED;

extern int TIMER_INDEX;
extern int TIMER_DURATION;
extern unsigned long long MEASUREMENT_DURATION;

extern int INSTANCE;

extern vector<char> randomDataValues;

extern struct conf_data conf;
extern struct measurement measurements;

extern pthread_mutex_t mutex;
extern map<int,int> syncing_threads;

#endif
