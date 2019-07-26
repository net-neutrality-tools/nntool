#include "catch.hpp"
#include "json11.hpp"

TEST_CASE("JSON11")
{
    SECTION("string functions")
    {
        json11::Json json = json11::Json::object{
            {"key1", "value1"},
            {"key2", "value2"},
        };
        std::string json_str = json.dump();
        CHECK(json_str == "{\"key1\": \"value1\", \"key2\": \"value2\"}");

    }
}