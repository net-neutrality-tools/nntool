/*!
    \file tcphandler_test.cpp
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
#include "tcphandler.cpp" //Globel declarations here...
#include "tcphandler.h"

TEST_CASE("CTcpHandler")
{
    CTrace& pTrace = CTrace::getInstance();
    pTrace.init("/etc/ias-server/trace.ini", "ias-server");

    ifstream in("/etc/ias-server/config.json");
    stringstream buffer;
    buffer << in.rdbuf();
    string error;
    ::CONFIG = Json::parse(buffer.str(), error);
    SECTION("Authentication token validation")
    {
        long long time = CTool::get_timestamp();
        ::authentication_secret = ::CONFIG["authentication"]["secret"].string_value();
        TRC_DEBUG(to_string(time) + authentication_secret);
        std::string token = sha1(to_string(time) + authentication_secret);
        TRC_DEBUG(token);
        REQUIRE(CTcpHandler::checkAuth(token, to_string(time), "dummy_handler") == true);
        REQUIRE(CTcpHandler::checkAuth(token, to_string(time + 1000000), "dummy_handler") == false);
        REQUIRE(CTcpHandler::checkAuth("not_our_token", to_string(time), "dummy_handler") == false);
    }
}
