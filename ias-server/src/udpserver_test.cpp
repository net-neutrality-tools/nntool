#include "catch.hpp"
#include "udpserver.h"

TEST_CASE("CUdpListener") {
    SECTION("test") {
        REQUIRE(RUNNING == false);

        auto udpListener = std::make_unique<CUdpListener>();
    }
}
