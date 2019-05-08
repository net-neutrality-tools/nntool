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

int mPort               = 80;
int mPortTls            = 443;
int mPortTraceroute     = 8080;
int mPortUdp            = 80;




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
    signal(SIGINT, signal_handler);
    signal(SIGFPE, signal_handler);
    signal(SIGABRT, signal_handler);
    signal(SIGSEGV, signal_handler);
    signal(SIGCHLD, signal_handler);

    write_pidfile("/var/run/ias-server.pid");

    CTrace *pTrace = CTrace::getInstance(); 
    pTrace->init("/etc/ias-server/trace.ini","ias-server");

    TRC_INFO("Status: ias-server started");
    
    CTcpServer *tcpListener = new CTcpServer(mPort, mPortTraceroute, false);
    if (mPort != 0)
    {
        if (tcpListener->createThread() != 0)
        {
            TRC_ERR("Error: Failure while creating TCP Listener Thread on target Port " + to_string(mPort));
            return EXIT_FAILURE;
        }
    }
    
    CTcpServer *tcpTlsListener = new CTcpServer(mPortTls, mPortTraceroute, true);
    if (mPort != 0)
    {
        if (tcpTlsListener->createThread() != 0)
        {
            TRC_ERR("Error: Failure while creating TCP Listener Thread on target Port " + to_string(mPortTls));
            return EXIT_FAILURE;
        }
    }

    CTcpServer *tcpTracerouteListener = new CTcpServer(mPortTraceroute, mPortTraceroute, false);
    if (mPort != 0)
    {
        if (tcpTracerouteListener->createThread() != 0)
        {
            TRC_ERR("Error: Failure while creating TCP Listener Thread on target Port " + to_string(mPortTraceroute));
            return EXIT_FAILURE;
        }
    }

    CUdpListener *pUdpListener = new CUdpListener(mPortUdp); 
    if (mPortUdp != 0) 
    { 
        if (pUdpListener->createThread() != 0) 
        { 
            TRC_ERR("Error: Failure while creating UDP Listener Thread");
            return EXIT_FAILURE;
        }
    }
    
    tcpListener->waitForEnd();
    tcpTlsListener->waitForEnd();
    tcpTracerouteListener->waitForEnd();
    
    delete(tcpListener);
    delete(tcpTlsListener);
    delete(tcpTracerouteListener);
    
    TRC_INFO("Status: ias-server stopped");
    
    delete(pTrace);

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