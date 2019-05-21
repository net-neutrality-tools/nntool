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

#ifndef HEADER_H_
#define HEADER_H_

#ifdef NNTOOL_SERVER
//log4cpp
#include <log4cpp/Category.hh>
#include <log4cpp/PropertyConfigurator.hh>
#endif

#include <sys/socket.h>
#include <sys/time.h>
#include <sys/ioctl.h>
#include <sys/resource.h>
#include <sys/syscall.h> 
#include <sys/wait.h>
#include <sys/stat.h>
#include <sys/utsname.h>


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


//openssl
#include <openssl/crypto.h>
#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/ssl.h>
#include <openssl/err.h>


#define MAX_PACKET_SIZE 1500
#define MAXBUFFER 1580


#define TRC_DEBUG(s)  CTrace::getInstance()->logDebug(s)
#define TRC_INFO(s)   CTrace::getInstance()->logInfo(s)
#define TRC_WARN(s)   CTrace::getInstance()->logWarn(s)
#define TRC_ERR(s)    CTrace::getInstance()->logErr(s)
#define TRC_CRIT(s)   CTrace::getInstance()->logCritical(s)


extern bool DEBUG;
extern bool RUNNING;
extern const char* PLATFORM;
extern const char* CLIENT_OS;

extern bool TIMER_ACTIVE;
extern bool TIMER_RUNNING;
extern bool TIMER_STOPPED;

extern int TIMER_INDEX;
extern int TIMER_DURATION;
extern unsigned long long MEASUREMENT_DURATION;

extern int INSTANCE;

extern volatile bool g_fExit;

#endif

#if __WORDSIZE == 64
# define IS64BIT
#else
# define IS32BIT
#endif 
