/*!
    \file connection_test.cpp
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

#include "catch.hpp"
#include "connection.h"

TEST_CASE("CConnection") {
    SECTION("UDP") {
        SECTION("IPv4 loopback socket communication") {
            // Loopback
            int sPort = 5060;
            std::string lIP = "127.0.0.1";
            
            // Local server socket
            auto sCon = std::make_unique<CConnection>();
            int sSocket = sCon->udpSocketServer(sPort);
            CHECK(sSocket != -1);
            
            // Local client socket
            auto cCon = std::make_unique<CConnection>();
            int cSocket = cCon->udpSocket( lIP);
            CHECK(cSocket != -1);

            // set the receive timeout to 1 seconds
            timeval tv;
		    tv.tv_sec = 1;
		    tv.tv_usec = 0;
		    setsockopt(cSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
		    setsockopt(cSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));
            setsockopt(sSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
		    setsockopt(sSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));

            // Addr 
            struct sockaddr_in server;
            server.sin_family       = AF_INET;
            server.sin_port         = htons(sPort);
            server.sin_addr.s_addr  = inet_addr(lIP.c_str());

            char cbuf[8] = "NNT";
            char sbuf[8];

            //Send message to loopback socket
            int response = sendto(cSocket, cbuf, 8, 0, (struct sockaddr *) &server, sizeof(server));
            CHECK(response != -1);

            


            //Check receiving the message
            response = recvfrom(sSocket, sbuf, 8, 0, NULL, NULL);
            CHECK(response != -1);
            CHECK(strcmp(cbuf, sbuf)==0);

            //Close both sockets
            sCon->close();
            cCon->close();

        };

         SECTION("IPv6 loopback socket communication") {
            // Loopback socket
            int sPort = 5060;
            std::string lIP = "::1";
            // Local server socket
            auto sCon = std::make_unique<CConnection>();
            int sSocket = sCon->udp6SocketServer(sPort);
            CHECK(sSocket != -1);
            
            // Local server socket
            auto cCon = std::make_unique<CConnection>();
            int cSocket = cCon->udp6Socket( lIP);
            CHECK(cSocket != -1);

            // set the receive timeout to 1 seconds
            timeval tv;
		    tv.tv_sec = 1;
		    tv.tv_usec = 0;
		    setsockopt(cSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
		    setsockopt(cSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));
            setsockopt(sSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
		    setsockopt(sSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));


            // Addr 
            struct sockaddr_in6 server;
            server.sin6_family       = AF_INET6;
            server.sin6_port         = htons(sPort);
            inet_pton(AF_INET6, lIP.c_str(), server.sin6_addr.s6_addr);

            char cbuf[8] = "NNT";
            char sbuf[8];

            int response = sendto(cSocket, cbuf, 8, 0, (struct sockaddr *) &server, sizeof(server));
            CHECK(response != -1);

            response = recvfrom(sSocket, sbuf, 8, 0, NULL, NULL);
            CHECK(response != -1);
            CHECK(strcmp(cbuf, sbuf)==0);



        };
    };

    SECTION("TCP"){
        SECTION("IPv4 looback socket communication"){
            // Loopback
            int port = 8080;
            std::string sIP = "127.0.0.1";

            // Server listening socket
            auto sCon = std::make_unique<CConnection>();
            int sSock = sCon->tcpSocketServer(port);
            CHECK(sSock !=-1);

            // Client socket
            auto cCon = std::make_unique<CConnection>();
            int cSock = cCon->tcpSocket(sIP,sIP, port);
            CHECK(cSock != -1);

            // set the receive timeout to 1 seconds
            timeval tv;
		    tv.tv_sec = 1;
		    tv.tv_usec = 0;
		    setsockopt(sSock, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
		    setsockopt(sSock, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));
            setsockopt(cSock, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
		    setsockopt(cSock, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));

            char cBuf[8]="NNT";
            char sBuf[8];

            //Send message
            int response;
            response = cCon->send(cBuf, 8, 0);
            CHECK(response != -1);
            
            //receive message
            response = sCon->receive(sBuf, 8, 0);

            CHECK(strcmp(sBuf, cBuf));
            
            //Close both
            sCon->close();
            cCon->close();
        };

        SECTION("IPv6 loopback communication"){
            //Loopback socket
            std::string ip = "::1";
            int port = 8080;

            auto sCon = std::make_unique<CConnection>();
            int sSock = sCon->tcp6SocketServer(port);
            CHECK(sSock !=-1);

            // Client socket
            auto cCon = std::make_unique<CConnection>();
            int cSock = cCon->tcp6Socket(ip,ip, port);
            CHECK(cSock != -1);

            // set the receive timeout to 1 seconds
            timeval tv;
		    tv.tv_sec = 1;
		    tv.tv_usec = 0;
		    setsockopt(sSock, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
		    setsockopt(sSock, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));
            setsockopt(cSock, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
		    setsockopt(cSock, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));

            char cBuf[8]="NNT";
            char sBuf[8];

            //Send message
            int response;
            response = cCon->send(cBuf, 8, 0);
            CHECK(response != -1);
            
            //receive message
            response = sCon->receive(sBuf, 8, 0);

            CHECK(strcmp(sBuf, cBuf));
            
            //Close both
            sCon->close();
            cCon->close();
        }
    }
}