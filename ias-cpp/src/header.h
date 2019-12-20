/*!
    \file header.h
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-26

    Copyright (C) 2016 - 2019 zafaco GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
#include <atomic>


#include <pthread.h>
#include <fcntl.h>
#include <map>
#include <list>
#include <vector>
#include <getopt.h>
#include <getopt.h>
#include <errno.h>
#include <cxxabi.h>

#ifndef __ANDROID__
    #include <execinfo.h>
    #include <ifaddrs.h>
#endif


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

#define MAX_NUM_THREADS 256
#define ECHO 64
#define UPLOAD_ADDITIONAL_MEASUREMENT_DURATION 2

extern bool DEBUG;
extern bool RUNNING;
extern bool UNREACHABLE;
extern bool FORBIDDEN;
extern bool OVERLOADED;
extern const char* PLATFORM;
extern const char* CLIENT_OS;

extern unsigned long long TCP_STARTUP;

extern bool RTT;
extern bool DOWNLOAD;
extern bool UPLOAD;

extern bool TIMER_ACTIVE;
extern bool TIMER_RUNNING;
extern bool TIMER_STOPPED;

extern std::atomic_bool hasError;
extern std::exception recentException;

extern int TIMER_INDEX;
extern int TIMER_DURATION;
extern unsigned long long MEASUREMENT_DURATION;
extern long long TIMESTAMP_MEASUREMENT_START;

extern bool PERFORMED_RTT;
extern bool PERFORMED_DOWNLOAD;
extern bool PERFORMED_UPLOAD;

extern int INSTANCE;

extern vector<char> randomDataValues;

extern struct conf_data conf;
extern struct measurement measurements;

extern pthread_mutex_t mutex1;
extern map<int,int> syncing_threads;

#endif
