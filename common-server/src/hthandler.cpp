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


#include <sys/socket.h>
#include "hthandler.h"
#include <ctype.h>
#include <stdio.h>


using namespace std;




CHtHandler::CHtHandler()
{
}


CHtHandler::~CHtHandler()
{
}


CHtHandler::CHtHandler(int nSocket, string nClientIp, string nTestCase)
{
    tcpSocket   = nSocket;
    clientIp    = nClientIp;
    testCase    = nTestCase;
}


int CHtHandler::handle_http()
{
    TRC_DEBUG("HTTP handler: started");
    
    char rbuffer[MAXBUFFER];
    string request;
    
    bzero(rbuffer, MAXBUFFER);

    recv(tcpSocket, rbuffer, MAXBUFFER, 0);

    request = string(rbuffer);
    
    TRC_DEBUG("HTTP handler: request received");

    string rHost            = get_value_from_string(request, host);
    string rAccessMethod    = get_value_from_string(request, accessMethod);
    string rAccessHeaders   = get_value_from_string(request, accessHeaders);
    string rOrigin          = get_value_from_string(request, origin);
    string rConnection      = get_value_from_string(request, connection);
    string rContentLength   = get_value_from_string(request, contentLength);
    
    if (rHost.empty())          rHost           = get_value_from_string(request, get_lower_string(host));
    if (rAccessMethod.empty())  rAccessMethod   = get_value_from_string(request, get_lower_string(accessMethod));
    if (rAccessHeaders.empty()) rAccessHeaders  = get_value_from_string(request, get_lower_string(accessHeaders));
    if (rOrigin.empty())        rOrigin         = get_value_from_string(request, get_lower_string(origin));
    if (rConnection.empty())    rConnection     = get_value_from_string(request, get_lower_string(connection));
    if (rContentLength.empty()) rContentLength  = get_value_from_string(request, get_lower_string(contentLength));
    
    print_request(rHost, rAccessMethod, rAccessHeaders, rOrigin, rConnection, rContentLength);

    if (    ((request.find(options)         != string::npos) || (request.find(get_lower_string(options))        != string::npos))
        &&  ((request.find(host)            != string::npos) || (request.find(get_lower_string(host))           != string::npos))
        &&  ((request.find(accessMethod)    != string::npos) || (request.find(get_lower_string(accessMethod))   != string::npos))
        &&  ((request.find(accessHeaders)   != string::npos) || (request.find(get_lower_string(accessHeaders))  != string::npos))
        &&  ((request.find(origin)          != string::npos) || (request.find(get_lower_string(origin))         != string::npos)) 
        &&  ((request.find(connection)      != string::npos) || (request.find(get_lower_string(connection))     != string::npos))
        )
    {
        TRC_DEBUG("HTTP handler: valid OPTIONS PreFlight request received");
        handle_preflight_request(rAccessMethod, rAccessHeaders, rOrigin, rConnection);
    }
    else
    if (    ((request.find(post)            != string::npos) || (request.find(get_lower_string(post))           != string::npos))
        &&  ((request.find(host)            != string::npos) || (request.find(get_lower_string(host))           != string::npos))
        &&  ((request.find(origin)          != string::npos) || (request.find(get_lower_string(origin))         != string::npos)) 
        &&  ((request.find(connection)      != string::npos) || (request.find(get_lower_string(connection))     != string::npos))
        &&  ((request.find(contentLength)   != string::npos) || (request.find(get_lower_string(contentLength))  != string::npos))
        )
    {
        if (testCase.compare("traceroute") == 0)
        {
            TRC_DEBUG("HTTP handler: valid Traceroute POST request received");
            handle_traceroute_request(rOrigin, rConnection);
        }
    }
    else
    {
        TRC_DEBUG("HTTP handler: invalid request");
        handle_invalid_request();
    }
     
    TRC_DEBUG("HTTP handler: stopped");

    return 0;
}

void CHtHandler::handle_preflight_request(string rAccessMethod, string rAccessHeaders, string rOrigin, string rConnection)
{
    string response     = "";
    
    response += "HTTP/1.1 200 OK\r\n";
    
    response += cache;
    response += "Connection: " + rConnection + "\r\n";
    response += "Content-Length: 0\r\n";
    response += "Content-Type: text/html\r\n";
    response += "Date: " + CTool::get_timestamp_string() + "\r\n";
    response += keepAlive;
    response += noCache;
    response += "access-control-allow-headers: " + rAccessHeaders + "\r\n";
    response += "access-control-allow-methods: " + rAccessMethod + "\r\n";
    response += "access-control-allow-origin: " + rOrigin + "\r\n\r\n";
    
    send(tcpSocket, response.c_str(), response.size(), 0);
    
    TRC_DEBUG("HTTP handler: sent 200 OK");
    
    return;
}

void CHtHandler::handle_traceroute_request(string rOrigin, string rConnection)
{
    string responseBody = "{\"hops\":[";
  
    if (get_route() == 0)
    {
        bool first = true;
        
        for(map<int, string>::iterator AI = mRoute.begin(); AI!= mRoute.end(); ++AI)
        {
            if (!first)
            {
                responseBody += ",";
            }
            first = false;
            TRC_DEBUG("HTTP handler: Traceroute: Hop #" + CTool::toString((*AI).first) + ": " + CTool::toString((*AI).second));
            responseBody += "{\"id\":\"" + CTool::toString((*AI).first) + "\",\"ip\":\"" + CTool::toString((*AI).second) + "\"}";
        }
    }
    else
    {
        TRC_ERR("HTTP handler: Traceroute: failed");
    }
    
    responseBody += "]}";

    send_response(rOrigin, rConnection, responseBody);
    
    TRC_DEBUG("HTTP handler: Traceroute: sent 200 OK with body: " + responseBody);
    
    return;
}

void CHtHandler::handle_invalid_request()
{
    string response     = "";
    string responseBody = "";
    
    response += "HTTP/1.1 404 Not Found\r\n";
    response += "Connection: close\r\n";
    response += "Content-Length: " + to_string(generate_html(responseBody, "404 Not Found")) + "\r\n";
    response += "Content-Type: text/html\r\n";
    response += "Date: " + CTool::get_timestamp_string() + "\r\n";
    response += "Content-Language: en\r\n\r\n";
    response += responseBody;
    
    send(tcpSocket, response.c_str(), response.size(), 0);
    
    TRC_DEBUG("HTTP handler: sent 404 Not Found");
    
    return;
}

void CHtHandler::send_response(string rOrigin, string rConnection, string responseBody)
{
    string response  = "";
    
    response += "HTTP/1.1 200 OK\r\n";  
    response += cache;
    response += "Connection: " + rConnection + "\r\n";
    response += "Content-Length: " + to_string(responseBody.size()) + "\r\n";
    response += "Content-Type: application/json\r\n";
    response += "Date: " + CTool::get_timestamp_string() + "\r\n";
    response += keepAlive;
    response += noCache;
    response += "access-control-allow-origin: " + rOrigin + "\r\n\r\n";
    response += responseBody;
    
    send(tcpSocket, response.c_str(), response.size(), 0);
}

string CHtHandler::get_value_from_string(string message, string key)
{
	string value = "";
	
	const char *needle = key.c_str();
	
	unsigned int nPosFirst;
	unsigned int nPos1;
	unsigned int nPos2;
        
	if ((message.find(needle)) != string::npos)
	{
            nPosFirst = message.find(needle);
            if ((nPos1 = message.find(" ",nPosFirst)) != string::npos)
            {
                nPos2 = message.find("\r",nPos1+1);

                for (string::iterator it = message.begin()+nPos1+1; it != message.begin()+nPos2; ++it)
                {
                    value += *it;
                }	
            }

            return value;	
	}

        return value;
}

void CHtHandler::print_request(string rHost, string rAccessMethod, string rAccessHeaders, string rOrigin, string rConnection, string rContentLength)
{
    TRC_DEBUG("Host: "                              + rHost);
    TRC_DEBUG("Access-Control-Request-Method: "     + rAccessMethod);
    TRC_DEBUG("Access-Control-Request-Headers: "    + rAccessHeaders);
    TRC_DEBUG("Origin: "                            + rOrigin);
    TRC_DEBUG("Connection: "                        + rConnection);
    TRC_DEBUG("Content-Length: "                    + rContentLength);
    
    return;
}

int CHtHandler::generate_html(string &content, string message)
{
	content = "<!DOCTYPE html>\n";
	content += "<html>\n";
	content += "<head>\n";
	content += "<title>" + message + "</title>\n";
	content += "</head>\n";
	content += "<body>\n";
	content += message;
	content += "</body>\n";
	content += "</html>\r\n\n";
	
	return content.size();
}

string CHtHandler::get_lower_string(string input)
{
    transform(input.begin(), input.end(), input.begin(), ::tolower);
    return input;
}




/*--------------traceroute--------------*/

int CHtHandler::get_route()
{
    int sndSocket;
    int srcPortSnd;
    int rcvSocket;
    int srcPortRcv;
    int ipType;
    int mResponse;
    bool exit = false;
    
    struct sockaddr_storage sockaddr_sto;
    memset(&sockaddr_sto, 0, sizeof(sockaddr_sto));

    ipType = CTool::validateIp(clientIp.c_str());
    TRC_DEBUG("HTTP Handler: Traceroute: IP Type: IPv" + to_string(ipType));

    switch (ipType)
    {
        case 4:
        {
            sndSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
            reinterpret_cast< struct sockaddr_in * >(&sockaddr_sto)->sin_family = AF_INET;
            reinterpret_cast< struct sockaddr_in * >(&sockaddr_sto)->sin_port = htons(UDP_TARGET_PORT);
            inet_pton(AF_INET, clientIp.c_str(), &reinterpret_cast< struct sockaddr_in * >(&sockaddr_sto)->sin_addr);
            
            rcvSocket = socket(AF_INET, SOCK_RAW, IPPROTO_ICMP);

            break;
        }
        case 6:
        {
            sndSocket = socket(AF_INET6, SOCK_DGRAM, IPPROTO_UDP);
            reinterpret_cast< struct sockaddr_in6 * >(&sockaddr_sto)->sin6_family = AF_INET6;
            reinterpret_cast< struct sockaddr_in6 * >(&sockaddr_sto)->sin6_port = htons(UDP_TARGET_PORT);
            inet_pton(AF_INET6, clientIp.c_str(), &reinterpret_cast< struct sockaddr_in6 * >(&sockaddr_sto)->sin6_addr.s6_addr);
            
            rcvSocket = socket(AF_INET6, SOCK_RAW, IPPROTO_ICMPV6);
            
            break;
        }
        default:
        {
            sndSocket = -1;
            rcvSocket = -1;
            break;
        }
    }

    if (sndSocket < 0 || rcvSocket < 0)
    {
        return -1;
    }
    
    int ttl = 1;
    while(ttl <= mMaxHops)
    {
        reinterpret_cast< struct sockaddr_in6 * >(&sockaddr_sto)->sin6_port = htons(UDP_TARGET_PORT + ttl-1);

        timeval tv;
        tv.tv_sec = 0;
        tv.tv_usec = mTimeoutIcmp * 1000;
        setsockopt(sndSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));

        if (send_udp_packet(sockaddr_sto, sndSocket, ttl) == -1)
        {
            TRC_ERR("HTTP Handler: Traceroute: send_udp_packet failed");
            return -1;
        }
        
        struct sockaddr_in6 sin6;
        socklen_t len6 = sizeof(sin6);
        if (getsockname(sndSocket, (struct sockaddr *)&sin6, &len6) == -1)
        {
            TRC_ERR("HTTP Handler: Traceroute: getsockname failed");
            ttl++;
            continue;
        }
        else
        {
            srcPortSnd = ntohs(sin6.sin6_port);
        }

        TRC_DEBUG("HTTP Handler: Traceroute: request with TTL: " + to_string(ttl) + " from Port: " + to_string(srcPortSnd) + " to Port: " + to_string(UDP_TARGET_PORT + ttl-1) + " send");
        
        timeval tim;
        gettimeofday(&tim,NULL);

        struct sockaddr_storage sockaddr_s;
        memset(&sockaddr_s, 0, sizeof(sockaddr_s));
        socklen_t len = sizeof(sockaddr_s);

        setsockopt(rcvSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
 
        while(1)
        {
            mResponse = recvfrom(rcvSocket, sBuffer, IP_MAXPACKET+1, 0, (struct sockaddr*)&sockaddr_s, &len);

            if (mResponse != -1)
            {
                if(mResponse == 0) break;
                sBuffer_ptr = sBuffer;
                char ip_address[128];

                icmp* icmp_packet;
                udphdr* udp_header;

                switch (sockaddr_s.ss_family)
                {
                    case AF_INET:
                    {
                        ip* ip_packet = (ip*)sBuffer_ptr;
                        sBuffer_ptr += ip_packet->ip_hl * 4;
                        icmp_packet = (icmp*)sBuffer_ptr;
                        sBuffer_ptr += ip_packet->ip_hl * 4 + ICMP_HEADER_LEN;
                        
                        break;
                    }
                    case AF_INET6:
                    {
                        icmp_packet = (icmp*)sBuffer_ptr;
                        sBuffer_ptr += sizeof(struct ip6_hdr) + ICMP_HEADER_LEN;
                        
                        break; 
                    }
                }

                //hexdump(sBuffer_ptr, sizeof(udp_header));
                
                udp_header = (udphdr*)sBuffer_ptr;
                srcPortRcv = ntohs(udp_header->uh_sport);
                
                //TRC_DEBUG("--------------------- snd: " + to_string(srcPortSnd) + ", rcv: " + to_string(srcPortRcv));

                if (srcPortSnd != srcPortRcv)
                {
                    TRC_INFO("HTTP Handler: Traceroute: Port mismatch: snd: " + to_string(srcPortSnd) + ", rcv: " + to_string(srcPortRcv));
                    continue;
                }


                TRC_DEBUG("HTTP Handler: Traceroute: icmp reply received: type: " + to_string(icmp_packet->icmp_type) + ", code: " + to_string(icmp_packet->icmp_code));
                
                switch (sockaddr_s.ss_family)
                {
                    case AF_INET:
                    {
                        struct sockaddr_in* sockinfo_in = (struct sockaddr_in*)(&sockaddr_s);
                        inet_ntop(AF_INET, &sockinfo_in->sin_addr, ip_address, sizeof(ip_address));
                        break;
                    }
                    case AF_INET6:
                    {
                        struct sockaddr_in6* sockinfo_in6 = (struct sockaddr_in6*)(&sockaddr_s);
                        inet_ntop(AF_INET6, &sockinfo_in6->sin6_addr.s6_addr, ip_address, sizeof(ip_address));
                        break; 
                    }
                }

                //icmpv4
                if (icmp_packet->icmp_type == ICMP_TIME_EXCEEDED && icmp_packet->icmp_code == ICMP_EXC_TTL)
                {
                    mRoute[ttl] = ip_address;
                    TRC_DEBUG("HTTP Handler: Traceroute: ICMP_TIME_EXCEEDED received from IP: " + CTool::toString(ip_address));
                    ttl++;
                    break;
                }
                else if (icmp_packet->icmp_type == ICMP_ECHOREPLY)
                {
                    mRoute[ttl] = ip_address;
                    TRC_DEBUG("HTTP Handler: Traceroute: ICMP_ECHOREPLY received from IP: " + CTool::toString(ip_address));
                    exit = true;	
                }
                else if (icmp_packet->icmp_type == ICMP_DEST_UNREACH && icmp_packet->icmp_code == ICMP_PKT_FILTERED)
                {
                    mRoute[ttl] = ip_address;
                    TRC_DEBUG("HTTP Handler: Traceroute: ICMP_PKT_FILTERED received from IP: " + CTool::toString(ip_address));
                    exit = true;	
                }
                else if (icmp_packet->icmp_type == ICMP_DEST_UNREACH && icmp_packet->icmp_code == ICMP_PORT_UNREACH)
                {
                    mRoute[ttl] = ip_address;
                    TRC_DEBUG("HTTP Handler: Traceroute: ICMP_PORT_UNREACH received from IP: " + CTool::toString(ip_address));
                    exit = true;	
                }

                //icmpv6
                else if (icmp_packet->icmp_type == ICMP6_TIME_EXCEEDED && icmp_packet->icmp_code == ICMP6_TIME_EXCEED_TRANSIT)
                {
                    mRoute[ttl] = ip_address;
                    TRC_DEBUG("HTTP Handler: Traceroute: ICMP6_TIME_EXCEEDED received from IP: " + CTool::toString(ip_address));
                    ttl++;
                    break;
                }
                else if (icmp_packet->icmp_type == ICMP6_ECHO_REPLY)
                {
                    mRoute[ttl] = ip_address;
                    TRC_DEBUG("HTTP Handler: Traceroute: ICMP6_ECHO_REPLY received from IP: " + CTool::toString(ip_address));
                    exit = true;	
                }
                else if (icmp_packet->icmp_type == ICMP6_DST_UNREACH && icmp_packet->icmp_code == ICMP6_DST_UNREACH_ADMIN)
                {
                    mRoute[ttl] = ip_address;
                    TRC_DEBUG("HTTP Handler: Traceroute: ICMP6_DST_UNREACH_ADMIN received from IP: " + CTool::toString(ip_address));
                    exit = true;
                }
                else if (icmp_packet->icmp_type == ICMP6_DST_UNREACH && icmp_packet->icmp_code == ICMP6_DST_UNREACH_NOPORT)
                {
                    mRoute[ttl] = ip_address;
                    TRC_DEBUG("HTTP Handler: Traceroute: ICMP6_DST_UNREACH_NOPORT received from IP: " + CTool::toString(ip_address));
                    exit = true;
                }   
            }
            else
            {
                mRoute[ttl] = "-";
                
                TRC_ERR("HTTP Handler: Traceroute: recvfrom failed: " + CTool::toString(strerror(errno)));

                ttl++;
                mTimeoutMaxCount++;
                break;
            }

            if (mTimeoutMaxCount >= mTimeoutMax)
            {
               TRC_INFO("HTTP Handler: Traceroute: max UDP timeouts reached");
               exit = true;
            }
            
            if (exit)
            {
                return 0;
            }
        } 
    }
    
    return 0;
}

int CHtHandler::send_udp_packet(sockaddr_storage &sockaddr_s, int sock, int ttl)
{
    int ipProtocol;
    int ipTtlOption;
    char cPayloadSend[UDP_PAYLOAD_SIZE];
    CTool::randomData(cPayloadSend, UDP_PAYLOAD_SIZE);
    
    switch (sockaddr_s.ss_family)
    {
        case AF_INET:
        {
            ipProtocol  = IPPROTO_IP;
            ipTtlOption = IP_TTL;

            break;
        }
        case AF_INET6:
        {
            ipProtocol  = IPPROTO_IPV6;
            ipTtlOption = IPV6_UNICAST_HOPS;
            
            break; 
        }
    }
    
    if (setsockopt(sock, ipProtocol, ipTtlOption, &ttl, sizeof(ttl)) < 0)
    {
        TRC_ERR("HTTP Handler: Traceroute: setsockopt failed");
        return -1;
    }

    if ((sendto(sock, cPayloadSend, UDP_PAYLOAD_SIZE, 0, (struct sockaddr*)&sockaddr_s, sizeof(sockaddr_s))) != UDP_PAYLOAD_SIZE)
    {
        TRC_ERR("HTTP Handler: Traceroute: sendto failed: " + CTool::toString(strerror(errno)));
        return -1;
    }
    
    //TRC_DEBUG("HTTP Handler: Traceroute: Payload send: " + cPayloadSend);

    return 0;
}