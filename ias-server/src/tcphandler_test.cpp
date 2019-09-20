#include "catch.hpp"
#include "tcphandler.h"
#include "tcphandler.cpp" //Globel declarations here...
#include "header.h"

TEST_CASE("CTcpHandler") {
    CTrace& pTrace = CTrace::getInstance();
    pTrace.init("/etc/ias-server/trace.ini", "ias-server");

    ifstream in("/etc/ias-server/config.json");
    stringstream buffer;
    buffer << in.rdbuf();
    string error;
    ::CONFIG = Json::parse(buffer.str(), error);
    SECTION("Authentication token validation") {
        long long time = CTool::get_timestamp();
        ::authentication_secret = ::CONFIG["authentication"]["secret"].string_value();
        TRC_DEBUG(to_string(time) + authentication_secret);
        std::string token = sha1(to_string(time) + authentication_secret);
        TRC_DEBUG(token);
        REQUIRE(CTcpHandler::checkAuth(token, to_string(time), "dummy_handler") == true);
        REQUIRE(CTcpHandler::checkAuth(token, to_string(time+1000000), "dummy_handler") == false);
        REQUIRE(CTcpHandler::checkAuth("not_our_token", to_string(time), "dummy_handler") == false);
    }
}
