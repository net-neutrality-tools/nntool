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
