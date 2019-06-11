#include "catch.hpp"
#include "tcphandler.h"

TEST_CASE("CTcpHandler") {
    SECTION("test") {
        REQUIRE(CTcpHandler::checkAuth("a", "b", "c") == true);
    }
}
