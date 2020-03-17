/*!
    \file iasserver.cpp
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2020-03-16

    Copyright (C) 2016 - 2020 zafaco GmbH

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

#define __NOPOLL_PTHREAD_SUPPORT__


#include "header.h"
#include "tcpserver.h"




/*--------------Global Variables--------------*/

bool DEBUG;
bool DEBUG_NOPOLL;
bool DEBUG_NOPOLL_ERROR;
bool DEBUG_NOPOLL_CRITICAL;
bool DAEMONMODE;
bool RUNNING;
bool OVERLOADED;
Json CONFIG;

int mPort               = 80;
int mPortTls            = 443;
int mPortTraceroute     = 8080;
int mPortTracerouteTls  = 8443;
int mPortUdp            = 80;

pthread_mutex_t mutexLoad;




/*--------------Forward declarations--------------*/

void        show_usage      (char* argv0);
int         start_daemon    (int nochdir, int noclose);
static void signal_handler  (int signal);
int         write_pidfile   (string sFilename);




/*--------------Beginning of Program--------------*/

int main(int argc, char** argv)
{
    ::DEBUG_NOPOLL          = false;
    ::DEBUG_NOPOLL_ERROR    = false;
    ::DEBUG_NOPOLL_CRITICAL = true;
    
    ::RUNNING               = true;

    ::OVERLOADED            = false;

    long int opt;

    //bind to all interfaces by default
    string sIPv4            = "0.0.0.0";
    string sIPv6            = "::";

    bool genericSockets     = true;

    while ((opt = getopt(argc, argv, "dnhv")) != -1)
    {
        switch (opt)
        {
            case 'd':
                ::DAEMONMODE = true;
                break;
            case 'n':
                ::DEBUG_NOPOLL          = true;
                ::DEBUG_NOPOLL_ERROR    = true;
                ::DEBUG_NOPOLL_CRITICAL = true;
                break;
            case 'h':
                show_usage(argv[0]);
                return EXIT_SUCCESS;
            case 'v':
                cout << "ias server" << endl;
                cout << "Version: " << VERSION << endl;
                return EXIT_SUCCESS;
            case '?':
            default:
                cout << "Error: Unknown Argument -" << opt << endl;
                show_usage(argv[0]);
                return EXIT_FAILURE;
        }
    }

    if (::DAEMONMODE)
    {
        //Start Daemon
        if (start_daemon(0, 0) < 0)
        {
            cout << "Error: Failed to create a Daemon" << endl;
            return EXIT_FAILURE;
        }
    }

    //Signal Handler
    signal(SIGFPE, signal_handler);
    signal(SIGABRT, signal_handler);
    signal(SIGSEGV, signal_handler);
    signal(SIGCHLD, signal_handler);

    write_pidfile("/var/run/ias-server.pid");

    CTrace& pTrace = CTrace::getInstance();
    pTrace.init("/etc/ias-server/trace.ini","ias-server");

    TRC_INFO("Status: ias-server started");

    ifstream in("/etc/ias-server/config.json");
    stringstream buffer;
    buffer << in.rdbuf();
    string error;
    ::CONFIG = Json::parse(buffer.str(), error);

    if (error.compare("") != 0)
    {
        TRC_ERR("Configuration File not found, using defaults");
    }
    else
    {
        if (::CONFIG["port_bindings"]["tcp"].int_value() != 0)
        {
            mPort = ::CONFIG["port_bindings"]["tcp"].int_value();
        }
        if (::CONFIG["port_bindings"]["tcp_tls"].int_value() != 0)
        {
            mPortTls = ::CONFIG["port_bindings"]["tcp_tls"].int_value();
        }
        if (::CONFIG["port_bindings"]["tcp_traceroute"].int_value() != 0)
        {
            mPortTraceroute = ::CONFIG["port_bindings"]["tcp_traceroute"].int_value();
        }
        if (::CONFIG["port_bindings"]["tcp_traceroute_tls"].int_value() != 0)
        {
            mPortTracerouteTls = ::CONFIG["port_bindings"]["tcp_traceroute_tls"].int_value();
        }
        if (::CONFIG["port_bindings"]["udp"].int_value() != 0)
        {
            mPortUdp= ::CONFIG["port_bindings"]["udp"].int_value();
        }
    }

    if (::CONFIG["ip_bindings"]["v6"].string_value().compare("") != 0)
    {
        sIPv6 = ::CONFIG["ip_bindings"]["v6"].string_value();
        genericSockets = false;
    }

    if (::CONFIG["ip_bindings"]["v4"].string_value().compare("") != 0)
    {
        sIPv4 = ::CONFIG["ip_bindings"]["v4"].string_value();
        genericSockets = false;
    }

    CTcpServer *tcpListener4 = new CTcpServer(mPort, mPortTraceroute, 4, sIPv4, false);
    CTcpServer *tcpListener6 = new CTcpServer(mPort, mPortTraceroute, 6, sIPv6, false);
    CTcpServer *tcpTlsListener4 = new CTcpServer(mPortTls, mPortTraceroute, 4, sIPv4, true);
    CTcpServer *tcpTlsListener6 = new CTcpServer(mPortTls, mPortTraceroute, 6, sIPv6, true);
    CTcpServer *tcpTracerouteListener4 = new CTcpServer(mPortTraceroute, mPortTraceroute, 4, sIPv4, false);
    CTcpServer *tcpTracerouteListener6 = new CTcpServer(mPortTraceroute, mPortTraceroute, 6, sIPv6, false);
    CTcpServer *tcpTracerouteTlsListener4 = new CTcpServer(mPortTracerouteTls, mPortTracerouteTls, 4, sIPv4, true);
    CTcpServer *tcpTracerouteTlsListener6 = new CTcpServer(mPortTracerouteTls, mPortTracerouteTls, 6, sIPv6, true);


    //use a dual stack socket
    if (genericSockets)
    {
        CTcpServer *tcpListener6 = new CTcpServer(mPort, mPortTraceroute, false);
        if (mPort != 0)
        {
            if (tcpListener4->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP Listener Thread on target Port " + to_string(mPort));
                return EXIT_FAILURE;
            }
        }

        CTcpServer *tcpTlsListener6 = new CTcpServer(mPortTls, mPortTraceroute, true);
        if (mPort != 0)
        {
            if (tcpTlsListener6->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP TLS Listener Thread on target Port " + to_string(mPort));
                return EXIT_FAILURE;
            }
        }

        CTcpServer *tcpTracerouteListener6 = new CTcpServer(mPortTraceroute, mPortTraceroute, false);
        if (mPort != 0)
        {
            if (tcpTracerouteListener6->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP Listener Thread on target Port " + to_string(mPort));
                return EXIT_FAILURE;
            }
        }

        CTcpServer *tcpTracerouteTlsListener6 = new CTcpServer(mPortTracerouteTls, mPortTracerouteTls, true);
        if (mPort != 0)
        {
            if (tcpTracerouteTlsListener6->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP TLS Listener Thread on target Port " + to_string(mPort));
                return EXIT_FAILURE;
            }
        }
    }
    else
    {
        //use seperated sockets for each ip version family    
        if (mPort != 0)
        {
            if (tcpListener4->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP Listener IPv4 Thread on target Port " + to_string(mPort));
                return EXIT_FAILURE;
            }
            if (tcpListener6->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP Listener IPv6 Thread on target Port " + to_string(mPort));
                return EXIT_FAILURE;
            }
        }
        
        if (mPortTls != 0)
        {
            if (tcpTlsListener4->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP TLS IPv4 Listener Thread on target Port " + to_string(mPortTls));
                return EXIT_FAILURE;
            }
            if (tcpTlsListener6->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP TLS IPv6 Listener Thread on target Port " + to_string(mPortTls));
                return EXIT_FAILURE;
            }
        }

        if (mPortTraceroute != 0)
        {
            if (tcpTracerouteListener4->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP Traceroute Listener IPv4 Thread on target Port " + to_string(mPortTraceroute));
                return EXIT_FAILURE;
            }
            if (tcpTracerouteListener6->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP Traceroute Listener IPv6 Thread on target Port " + to_string(mPortTraceroute));
                return EXIT_FAILURE;
            }
        }
    
        if (mPortTracerouteTls != 0)
        {
            if (tcpTracerouteTlsListener4->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP Traceroute TLS Listener IPv4 Thread on target Port " + to_string(mPortTracerouteTls));
                return EXIT_FAILURE;
            }
            if (tcpTracerouteTlsListener6->createThread() != 0)
            {
                TRC_ERR("Error: Failure while creating TCP Traceroute TLS Listener IPv6 Thread on target Port " + to_string(mPortTracerouteTls));
                return EXIT_FAILURE;
            }
        }
    }   

    CUdpListener *pUdpListenerGeneric = new CUdpListener(mPortUdp, 0, "");
    CUdpListener *pUdpListenerIPv4 = new CUdpListener(mPortUdp, 0, "");
    if (mPortUdp != 0) 
    { 
        //check for ip bindings
        if (genericSockets)
        {
            //no bindings, use generic IP socket
            if (pUdpListenerGeneric->createThread() != 0) 
            { 
                TRC_ERR("Error: Failure while creating generic IP UDP Listener Thread");
                return EXIT_FAILURE;
            }
        } 
        else
        {
            //IPv4 binding
            if( ::CONFIG["ip_bindings"]["v4"].string_value().compare("") != 0 )
            {
                pUdpListenerIPv4 = new CUdpListener(mPortUdp, 4, ::CONFIG["ip_bindings"]["v4"].string_value()); 
                if (pUdpListenerIPv4->createThread() != 0) 
                { 
                    TRC_ERR("Error: Failure while creating IPv4 UDP Listener Thread");
                    return EXIT_FAILURE;
                }
            }

            //IPv6 binding
            if( ::CONFIG["ip_bindings"]["v6"].string_value().compare("") != 0 )
            {
                pUdpListenerGeneric = new CUdpListener(mPortUdp, 6, ::CONFIG["ip_bindings"]["v6"].string_value()); 
                if (pUdpListenerGeneric->createThread() != 0) 
                { 
                    TRC_ERR("Error: Failure while creating IPv6 UDP Listener Thread");
                    return EXIT_FAILURE;
                }
            }
        }
    }

    CLoadMonitoring *pLoadMonitoring = new CLoadMonitoring();
    if (::CONFIG["load"]["monitoring"]["enabled"].bool_value())
    {
        if(pLoadMonitoring->createThread() != 0 )
        {
            TRC_ERR("Error: Failure while creating Load Monitoring Thread");
            return EXIT_FAILURE;
        }
    }       
    
    tcpListener4->waitForEnd();
    tcpListener6->waitForEnd();
    tcpTlsListener4->waitForEnd();
    tcpTlsListener6->waitForEnd();
    tcpTracerouteListener4->waitForEnd();
    tcpTracerouteListener6->waitForEnd();
    tcpTracerouteTlsListener4->waitForEnd();
    tcpTracerouteTlsListener6->waitForEnd();
    pLoadMonitoring->waitForEnd();
    pUdpListenerGeneric->waitForEnd();
    pUdpListenerIPv4->waitForEnd();

    delete(tcpListener4);
    delete(tcpListener6);
    delete(tcpTlsListener4);
    delete(tcpTlsListener6);
    delete(tcpTracerouteListener4);
    delete(tcpTracerouteListener6);
    delete(tcpTracerouteTlsListener4);
    delete(tcpTracerouteTlsListener6);
    delete(pLoadMonitoring);
    delete(pUdpListenerGeneric);
    delete(pUdpListenerIPv4);
    
    TRC_INFO("Status: ias-server stopped");

    return EXIT_SUCCESS;
}


void show_usage(char* argv0)
{
    cout << "                                                               " << endl;
    cout << "Usage: " << argv0 << " [ options ... ]                         " << endl;
    cout << "                                                               " << endl;
    cout << "  -d             - Daemon-Mode: Switch into a daemon           " << endl;
    cout << "  -n             - Show nopoll debugging output                " << endl;
    cout << "  -h             - Show Help                                   " << endl;
    cout << "  -v             - Show Version                                " << endl;
    cout << "                                                               " << endl;

    exit(EXIT_FAILURE);
}


int start_daemon(int nochdir, int noclose)
{
    int i = 0;
    int r = 0;

    switch (fork())
    {
        case 0: break;
        case -1: return -1;

        default: exit(EXIT_SUCCESS);
    }

    if (setsid() < 0)
    {
        return -1; 
    }

    switch (fork())
    {
        case 0: break;
        case -1: return -1;

        default: exit(EXIT_FAILURE);
    }

    if (!nochdir)
    {
        r = chdir("/");       
    }

    if (!noclose)
    {
        for (; i < 255; ++i)
        {
            close(i);
        }
            
        open("/dev/null", O_RDWR);
        r = dup(0);
        r = dup(0);
    }

    r = 0;

    return r;
}


static void signal_handler(int signal)
{
    TRC_ERR("Error signal received " + std::to_string(signal));

    CTool::print_stacktrace();
    
    ::RUNNING = false;
    sleep(1);
    exit(signal);
}


int write_pidfile(string sFilename)
{
    ofstream fout((sFilename).c_str());

    fout << getpid() << endl;

    fout.close();

    return 0;
}