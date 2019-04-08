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
 *      \date Last update: 2019-01-02
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef HTHANDLER_H
#define HTHANDLER_H


#include "header.h"

#define ICMP_HEADER_LEN 8
#define UDP_HEADER_LEN 8
#define UDP_TARGET_PORT 33434
#define UDP_PAYLOAD_SIZE 64

class CHtHandler
{    
    public:
        CHtHandler();

        virtual ~CHtHandler();

        CHtHandler(int nSocket, string nClientIp, string nTestCase);
        
        int handle_http();
        
    private:
        int tcpSocket;
        string clientIp;
        string testCase;
        
        //http preflight header fields
        string options          = "OPTIONS / HTTP/1.1";
        string host             = "Host:";
        string accessMethod     = "Access-Control-Request-Method:";
        string accessHeaders    = "Access-Control-Request-Headers:";
        string origin           = "Origin:";
        string connection       = "Connection:";

        //http additional header fields
        string contentLength    = "Content-Length:";
        string post             = "POST / HTTP/1.1";
        string cache            = "Cache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0";
        string keepAlive        = "Keep-Alive: timeout=5, max=100\r\n";
        string noCache          = "Pragma: no-cache\r\n";

        //udp/icmp traceroute
        int mMaxHops = 15;
        unsigned char sBuffer[IP_MAXPACKET+1];
        unsigned char* sBuffer_ptr;
        int mTimeoutIcmp = 500;
        int mTimeoutMaxCount = 0;
        int mTimeoutMax = 3;
        map <int, string> mRoute;
        
        
        
        
        void handle_preflight_request(string rAccessMethod, string rAccessheaders, string rOrigin, string rConnection);
        void handle_traceroute_request(string rOrigin, string rConnection);
        void handle_invalid_request();
        void send_response(string rOrigin, string rConnection, string responseBody);
        
        string get_value_from_string(string message, string key);
        void print_request(string rHost, string rAccessMethod, string rAccessheaders, string rOrigin, string rConnection, string rContentLength);
        int generate_html(string &content, string message);
        string get_lower_string(string input);
        int get_route();
        int send_udp_packet(sockaddr_storage &sockaddr_s, int sock, int ttl);
};

#endif