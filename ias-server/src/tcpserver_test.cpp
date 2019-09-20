#include "catch.hpp"
#include "header.h"
#include "tcpserver.h"

TEST_CASE("TCP Server"){
    int err;
    SECTION("Start and stop TCP server"){
        ::RUNNING = true;
        CTcpServer *tcpListener = new CTcpServer(8080, 8081, false);
        err = tcpListener->createThread();
        ::RUNNING = false;
        tcpListener->waitForEnd();
        delete(tcpListener);
        CHECK(err == 0);
    }
    SECTION("TCP Server listens on configured ports"){
        ::RUNNING = true;
        CTcpServer *tcpListener = new CTcpServer(8080, 8081, false);
        int err = tcpListener->createThread();
        usleep(10000);
        
        
        //Binding the same ports should fail
        std::unique_ptr<CConnection> mConnection = std::make_unique<CConnection>();
        int port = 8080;
        err = mConnection->tcpSocketServer(port);     
        //tcpListener->waitForEnd();
        CHECK(err == -1);
        
        ::RUNNING = false;
        delete(tcpListener);
    }

    SECTION("TCP Server accpets connection"){
        ::RUNNING = true;
        CTcpServer *tcpListener = new CTcpServer(8082, 8084, false);
        int err = tcpListener->createThread();
        usleep(100000);

        std::unique_ptr<CConnection> mConnection = std::make_unique<CConnection>();
        int port = 8082;
        string server = "localhost";
        string intf = "::1";

        err = mConnection->tcp6Socket(intf, server,port);
        CHECK(err != -1);
        usleep(100000);
        string request = "";
        request += "GET ws://websocket.example.com/ HTTP/1.1\r\n";
        request += "Host: localhost\r\n";
        request += "Upgrade: websocket\r\n";
        request += "Connection: Upgrade\r\n\r\n";
        mConnection->send(request.c_str(), strlen(request.c_str()), 0);
        
        usleep(100000);
        err = mConnection->close();
        usleep(100000);
        ::RUNNING=false;
        tcpListener->waitForEnd();
        delete(tcpListener);
        CHECK(err != -1);
    }

}