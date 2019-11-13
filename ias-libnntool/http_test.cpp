/*!
    \file http_test.cpp
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
#include "http.h"

TEST_CASE("CHttp") {
    SECTION("HTML functions") {
        SECTION("test generateHTML") {
            string content = "";
            string message = "_MESSAGE_";

            auto http = std::make_unique<CHttp>();

            int ret = http->generateHTML(content, message);

            // search for <!DOCTYPE html>
            REQUIRE(content.find("<!DOCTYPE html>") != string::npos);

            // search for <body>
            REQUIRE(content.find("<body>") != string::npos);

            // search for message
            REQUIRE(content.find(message) != string::npos);
        }
    }
}
