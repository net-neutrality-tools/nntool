/*!
    \file tcpserver_test.cpp
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
#include "header.h"
#include "tcpserver.h"

TEST_CASE("TCP Server")
{
    int err;
    SECTION("Start and stop TCP server")
    {
        ::RUNNING = true;
        CTcpServer* tcpListener = new CTcpServer(8080, 8081, false);
        err = tcpListener->createThread();
        ::RUNNING = false;
        tcpListener->waitForEnd();
        delete (tcpListener);
        CHECK(err == 0);
    }


    SECTION("TCP Server accpets valid WebSocket request for download testcase and sends data")
    {
        ::RUNNING = true;
        ::DEBUG_NOPOLL = false;
        CTcpServer* tcpListener = new CTcpServer(8082, 8084, false);
        int err = tcpListener->createThread();
        // Should be 0 for successful thread creation
        CHECK(err == 0);
        usleep(1000000);

        std::unique_ptr<CConnection> mConnection = std::make_unique<CConnection>();
        int port = 8082;
        string server = "localhost";
        string intf = "::1";

        err = mConnection->tcp6Socket(intf, server, port);
        // Should return a valid (>0) file descriptor
        CHECK(err != -1);

        usleep(10000);

        // Create token
        long long time = CTool::get_timestamp();
        string authentication_secret = ::CONFIG["authentication"]["secret"].string_value();
        std::string token = sha1(to_string(time) + authentication_secret);

        string request = "";
        request += "GET / HTTP/1.1\r\n";
        request += "Host: my_hostname:8082\r\n";
        request += "Origin: http://localhost\r\n";
        request += "Connection: Upgrade\r\n";
        request += "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\n";
        request += "Sec-WebSocket-Version: 13\r\n";
        request += "Sec-WebSocket-Protocol: download, " + token + ", " + to_string(time) + "\r\n";
        request += "Upgrade: websocket\r\n\r\n";
        err = mConnection->send(request.c_str(), strlen(request.c_str()), 0);
        // Should return the amount of sent characters
        CHECK(err == strlen(request.c_str()));

        // usleep(10000);

        // Read HTTP response
        std::unique_ptr<char[]> rbufferOwner = std::make_unique<char[]>(MAXBUFFER);
        char* rbuffer = rbufferOwner.get();
        string response;
        bzero(rbuffer, MAXBUFFER);
        mConnection->receive(rbuffer, MAXBUFFER, 0);
        response = string(rbuffer);

        // Server acknowledges HTTP upgrade to WebSocket
        CHECK(response.find("HTTP/1.1 101 Switching Protocols") != string::npos);

        // Over local loopback we should receive at least every 1ms a data frame
        timeval tv;
        tv.tv_sec = 0;
        tv.tv_usec = 1000;
        setsockopt(mConnection->mSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval*)&tv, sizeof(timeval));
        setsockopt(mConnection->mSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval*)&tv, sizeof(timeval));

        // Wait up to 1s for the first frame
        int nBytes = 0;
        for (int i = 0; i < 10; i++) {
            nBytes = mConnection->receive(rbuffer, MAXBUFFER, 0);
            if (nBytes > 0)
                break;
            usleep(100000);
        }

        int nTimeouts = 0;
        for (int i = 0; i < 100; i++) {
            nBytes = mConnection->receive(rbuffer, MAXBUFFER, 0);
            if (nBytes <= 0)
                nTimeouts++;
            // TRC_DEBUG(to_string(nBytes));
        }
        CHECK(nTimeouts == 0);

        // Force ws server to teardown the connection beacause of an invalid frame
        request = "Force Stop";
        mConnection->send(request.c_str(), strlen(request.c_str()), 0);
        err = mConnection->close();
        // err should be zero for successfull connection teardowns
        CHECK(err == 0);

        usleep(100000);

        ::RUNNING = false;
        tcpListener->waitForEnd();
    }

    SECTION("TCP Server accpets valid HTTP request for download testcase and sends data")
    {
        ::RUNNING = true;
        ::DEBUG_NOPOLL = false;
        CTcpServer* tcpListener = new CTcpServer(8092, 8084, false);
        int err = tcpListener->createThread();
        // Should be 0 for successful thread creation
        CHECK(err == 0);
        usleep(100000);

        std::unique_ptr<CConnection> mConnection = std::make_unique<CConnection>();
        int port = 8092;
        string server = "localhost";
        string intf = "::1";

        err = mConnection->tcp6Socket(intf, server, port);
        // Should return a valid (>0) file descriptor
        CHECK(err != -1);

        usleep(10000);

        // Create token
        long long time = CTool::get_timestamp();
        string authentication_secret = ::CONFIG["authentication"]["secret"].string_value();
        std::string token = sha1(to_string(time) + authentication_secret);

        // Request download test over HTTP
        string request = "";
        request += "GET /data.img HTTP/1.1\r\n";
        request += "Host: my_hostname:8092\r\n";
        request += "Cookie: tk=" + token + "; " + "ts=" + to_string(time) + "\r\n";
        request += "Origin: http://localhost\r\n\r\n";
        err = mConnection->send(request.c_str(), strlen(request.c_str()), 0);

        // Should return the amount of sent characters
        CHECK(err == strlen(request.c_str()));

        // Read HTTP response
        std::unique_ptr<char[]> rbufferOwner = std::make_unique<char[]>(MAXBUFFER);
        char* rbuffer = rbufferOwner.get();
        string response;
        bzero(rbuffer, MAXBUFFER);
        mConnection->receive(rbuffer, MAXBUFFER, 0);
        response = string(rbuffer);
        TRC_DEBUG(response);
        // Server should response with HTTP 200 OK as we  have triggered HTTP fallback
        CHECK(response.find("HTTP/1.1 200 OK") != string::npos);

        // Over local loopback we should receive at least every 1ms a data frame
        timeval tv;
        tv.tv_sec = 0;
        tv.tv_usec = 1000;
        setsockopt(mConnection->mSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval*)&tv, sizeof(timeval));
        setsockopt(mConnection->mSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval*)&tv, sizeof(timeval));

        // Wait up to 1s for the first frame
        int nBytes = 0;
        for (int i = 0; i < 10; i++) {
            nBytes = mConnection->receive(rbuffer, MAXBUFFER, 0);
            if (nBytes > 0)
                break;
            usleep(100000);
        }

        int nTimeouts = 0;
        for (int i = 0; i < 100; i++) {
            nBytes = mConnection->receive(rbuffer, MAXBUFFER, 0);
            if (nBytes <= 0)
                nTimeouts++;
            // TRC_DEBUG(to_string(nBytes));
        }
        // Over loopback we should never see any timeouts (Server load test)
        CHECK(nTimeouts == 0);

        // Force close by invalid frame
        request = "Force Stop";
        mConnection->send(request.c_str(), strlen(request.c_str()), 0);
        err = mConnection->close();
        // err should be zero for successfull connection teardowns
        CHECK(err == 0);

        usleep(100000);

        ::RUNNING = false;
        tcpListener->waitForEnd();
        delete (tcpListener);
        TRC_DEBUG("Wait for server download process to timeout (1:10min) ...");
        usleep(70000000);
    }

    SECTION("TCP Server accpets valid WebSocket request for upload testcase")
    {
        ::RUNNING = true;
        ::DEBUG_NOPOLL = false;
        CTcpServer* tcpListener = new CTcpServer(8095, 8084, false);
        int err = tcpListener->createThread();
        // Should be 0 for successful thread creation
        CHECK(err == 0);
        usleep(100000);

        std::unique_ptr<CConnection> mConnection = std::make_unique<CConnection>();
        int port = 8095;
        string server = "localhost";
        string intf = "::1";

        err = mConnection->tcp6Socket(intf, server, port);
        // Should return a valid (>0) file descriptor
        CHECK(err != -1);

        usleep(10000);

        // Create token
        long long time = CTool::get_timestamp();
        string authentication_secret = ::CONFIG["authentication"]["secret"].string_value();
        std::string token = sha1(to_string(time) + authentication_secret);

        string request = "";
        request += "GET / HTTP/1.1\r\n";
        request += "Host: my_hostname:8095\r\n";
        request += "Origin: http://localhost\r\n";
        request += "Connection: Upgrade\r\n";
        request += "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\n";
        request += "Sec-WebSocket-Version: 13\r\n";
        request += "Sec-WebSocket-Protocol: upload, " + token + ", " + to_string(time) + "\r\n";
        request += "Upgrade: websocket\r\n\r\n";
        err = mConnection->send(request.c_str(), strlen(request.c_str()), 0);
        // Should return the amount of sent characters
        CHECK(err == strlen(request.c_str()));

        // Read HTTP response
        std::unique_ptr<char[]> rbufferOwner = std::make_unique<char[]>(MAXBUFFER);
        char* rbuffer = rbufferOwner.get();
        string response;
        bzero(rbuffer, MAXBUFFER);
        mConnection->receive(rbuffer, MAXBUFFER, 0);
        response = string(rbuffer);

        // Server acknowledges HTTP upgrade to WebSocket
        CHECK(response.find("HTTP/1.1 101 Switching Protocols") != string::npos);
        // Force close by invalid frame
        request = "Force Stop";
        mConnection->send(request.c_str(), strlen(request.c_str()), 0);
        err = mConnection->close();
        // err should be zero for successfull connection teardowns
        CHECK(err == 0);

        usleep(100000);

        ::RUNNING = false;
        tcpListener->waitForEnd();
        delete (tcpListener);
        TRC_DEBUG("Wait for server download process to timeout...");
        // usleep(10000000);
    }

    SECTION("TCP traceroute server returns IPv4 route when connecting over v4")
    {
        ::RUNNING = true;
        CTcpServer* tcpListener = new CTcpServer(8086, 8086, false);
        int err = tcpListener->createThread();
        // Should be 0 for successful thread creation
        CHECK(err == 0);
        usleep(10000);

        std::unique_ptr<CConnection> mConnection = std::make_unique<CConnection>();
        int port = 8086;
        string server = "127.0.0.1";
        string intf = "127.0.0.1";

        err = mConnection->tcpSocket(intf, server, port);
        // Should return a valid (>0) file descriptor
        CHECK(err != -1);

        usleep(100000);
        string request = "";
        request += "POST / HTTP/1.1\r\n";
        request += "Origin: localhost\r\n";
        request += "Connection: keep-alive\r\n";
        request += "Content-Length: 100\r\n";
        request += "Host: localhost\r\n\r\n";
        err = mConnection->send(request.c_str(), strlen(request.c_str()), 0);
        // Should return the amount of sent characters
        CHECK(err == strlen(request.c_str()));

        usleep(10000);

        // Read HTTP response
        std::unique_ptr<char[]> rbufferOwner = std::make_unique<char[]>(MAXBUFFER);
        char* rbuffer = rbufferOwner.get();
        string response;
        bzero(rbuffer, MAXBUFFER);
        mConnection->receive(rbuffer, MAXBUFFER, 0);
        response = string(rbuffer);

        // Traceroute should report single hop for local loopback connection
        CHECK(response.find("{\"hops\":[{\"id\":\"1\",\"ip\":\"127.0.0.1\"}]}") != string::npos);
        err = mConnection->close();
        // err should be non-negative for successfull connection teardowns
        CHECK(err != -1);

        usleep(10000);

        ::RUNNING = false;
        tcpListener->waitForEnd();
        delete (tcpListener);
    }
    SECTION("TCP traceroute server returns IPv6 route when connecting over v6")
    {
        ::RUNNING = true;
        CTcpServer* tcpListener = new CTcpServer(8087, 8087, false);
        int err = tcpListener->createThread();
        // Should be 0 for successful thread creation
        CHECK(err == 0);
        usleep(10000);

        std::unique_ptr<CConnection> mConnection = std::make_unique<CConnection>();
        int port = 8087;
        string server = "::1";
        string intf = "::1";

        err = mConnection->tcp6Socket(intf, server, port);
        // Should return a valid (>0) file descriptor
        CHECK(err != -1);

        usleep(1000);
        string request = "";
        request += "POST / HTTP/1.1\r\n";
        request += "Origin: localhost\r\n";
        request += "Connection: keep-alive\r\n";
        request += "Content-Length: 0\r\n";
        request += "Host: localhost\r\n\r\n";
        err = mConnection->send(request.c_str(), strlen(request.c_str()), 0);
        // Should return the amount of sent characters
        CHECK(err == strlen(request.c_str()));

        usleep(100000);

        // Read HTTP response
        std::unique_ptr<char[]> rbufferOwner = std::make_unique<char[]>(MAXBUFFER);
        char* rbuffer = rbufferOwner.get();
        string response;
        bzero(rbuffer, MAXBUFFER);
        mConnection->receive(rbuffer, MAXBUFFER, 0);
        response = string(rbuffer);

        // Traceroute should report single hop for local loopback connection
        CHECK(response.find("{\"hops\":[{\"id\":\"1\",\"ip\":\"::1\"}]}") != string::npos);
        err = mConnection->close();
        // err should be zero for successfull connection teardowns
        CHECK(err != -1);

        usleep(10000);

        ::RUNNING = false;
        tcpListener->waitForEnd();
        delete (tcpListener);
    }
}