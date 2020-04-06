/*!
    \file tcptraceroutehandler.h
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13

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

#ifndef TCPTRACEROUTEHANDLER_H
#define TCPTRACEROUTEHANDLER_H


#include "header.h"


using namespace std;


#define ICMP_HEADER_LEN 8
#define UDP_HEADER_LEN 8
#define UDP_TARGET_PORT 33434
#define UDP_PAYLOAD_SIZE 64

class CTcpTracerouteHandler
{    
    public:
        CTcpTracerouteHandler();

        virtual ~CTcpTracerouteHandler();

        CTcpTracerouteHandler(int nSocket, string nClientIp, bool nTlsSocket);
        
        int handle_tcp_traceroute();
        
    private:
        int tcpSocket;
        string clientIp;
        bool mTlsSocket;
        string authentication_secret;
        std::unique_ptr<CConnection> mAcceptedConnection;

        //http additional header fields
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