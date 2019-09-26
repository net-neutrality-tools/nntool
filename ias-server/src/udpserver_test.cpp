#include "catch.hpp"
#include "header.h"
#include "udpserver.h"

TEST_CASE("CUdpListener")
{
    SECTION("Server echos TSDU")
    {
        ::RUNNING = true;
        string intf = "127.0.0.1";
        auto udpListener = std::make_unique<CUdpListener>(8095, 4, "127.0.0.1");
        int err = udpListener->createThread();
        // Should be 0 for successful thread creation
        CHECK(err == 0);
        usleep(100000);

        std::unique_ptr<CConnection> mConnection = std::make_unique<CConnection>();

        mConnection->udpSocket(intf);
        // Should return a valid (>0) file descriptor
        CHECK(err != -1);

        string msg = "Hello Server!";
        struct sockaddr_in servaddr;
        memset((char*)&servaddr, 0, sizeof(servaddr));
        servaddr.sin_family = AF_INET;
        inet_pton(AF_INET, "127.0.0.1", &(servaddr.sin_addr));
        servaddr.sin_port = htons(8095);

        ::RUNNING = false;
        sendto(mConnection->mSocket, msg.c_str(), strlen(msg.c_str()), 0, (struct sockaddr*)&servaddr, sizeof(servaddr));
        // Should return the amount of sent characters
        CHECK(err == 0);

        std::unique_ptr<char[]> rbufferOwner = std::make_unique<char[]>(MAXBUFFER);
        char* rbuffer = rbufferOwner.get();
        string response;
        bzero(rbuffer, MAXBUFFER);
        mConnection->receive(rbuffer, MAXBUFFER, 0);
        response = string(rbuffer);
        // TRC_DEBUG(response);

        CHECK(response == msg);

        udpListener->waitForEnd();
    }
}
