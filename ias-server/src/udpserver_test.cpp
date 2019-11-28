/*!
    \file udpserver_test.cpp
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
