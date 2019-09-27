#include "catch.hpp"
#include "header.h"
#define private public

// Json CONFIG;
bool RUNNING;
TEST_CASE("Load Monitoring and Load Balancer web service")
{
    CTrace& pTrace = CTrace::getInstance();
    pTrace.init("/etc/ias-server/trace.ini", "ias-server");

    ifstream in("/etc/ias-server/config.json");
    stringstream buffer;
    buffer << in.rdbuf();
    string error;
    ::CONFIG = Json::parse(buffer.str(), error);

    SECTION("Starting and stop load monitoring")
    {
        int err;
        ::RUNNING = true;
        std::unique_ptr<CLoadMonitoring> pLoadMonitoring = std::make_unique<CLoadMonitoring>();
        err = pLoadMonitoring->createThread();
        ::RUNNING = false;
        pLoadMonitoring->waitForEnd();
        CHECK(err == 0);
    };
    SECTION("LoadMonitoring starts LoadBalancer web service")
    {
        // Start Load Monitoring and Balancer
        int err;
        ::RUNNING = true;
        std::unique_ptr<CLoadMonitoring> pLoadMonitoring = std::make_unique<CLoadMonitoring>();
        err = pLoadMonitoring->createThread();
        usleep(1000000); // Wait for servers to start up
        // Prepare request
        string request = "";
        request += "GET / HTTP/1.1\r\n";
        request += "Host: localhost\r\n";
        request += "Connection: keep-alive\r\n\r\n";
        request += "{\"secret\": \"none\",\"cmd\":\"load\"}";
        // Connect to server
        string server = "localhost";
        string intf = "::1";
        int prot = 44301;
        int tls = 1;
        std::unique_ptr<CConnection> mConnection = std::make_unique<CConnection>();
        timeval tv;
        tv.tv_sec = 5;
        tv.tv_usec = 0;

        err = mConnection->tcp6Socket(intf, server, prot, tls, server);
        REQUIRE(err != -1);
        setsockopt(mConnection->mSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval*)&tv, sizeof(timeval));
        setsockopt(mConnection->mSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval*)&tv, sizeof(timeval));
        // Send request
        mConnection->send(request.c_str(), strlen(request.c_str()), 0);

        // Read from TLS socket
        std::unique_ptr<char[]> rbufferOwner = std::make_unique<char[]>(MAXBUFFER);
        char* rbuffer = rbufferOwner.get();
        string response;
        bzero(rbuffer, MAXBUFFER);
        mConnection->receive(rbuffer, MAXBUFFER, 0);
        response = string(rbuffer);
        // Close socket
        mConnection->close();

        // Stop server
        ::RUNNING = false;
        pLoadMonitoring->waitForEnd();
        // Response contains load info
        CHECK(response.find("cpu_avg") != string::npos);
        CHECK(response.find("overload") != string::npos);
        CHECK(response.find("timestamp") != string::npos);
        CHECK(response.find("mem_bytes") != string::npos);
    };
}