#include "catch.hpp"
#include "tool.h"

TEST_CASE("CTool") {
    SECTION("string functions") {
        SECTION("test toLower") {
            string upper = "ABC";

            CTool::toLower(upper);

            REQUIRE(upper == "abc");
        }
        SECTION("test toUpper") {
            string lower = "abc";

            CTool::toUpper(lower);

            REQUIRE(lower == "ABC");
        }
    }
}
