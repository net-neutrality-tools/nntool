#include "catch.hpp"
#include "configmanager.h"

TEST_CASE("Configmanager") {
    SECTION("Write/Read String"){
        auto cman = std::make_unique<CConfigManager>();
        std::string key = "test";
        std::string section = "section";
        std::string value = "42";
        
        cman->writeString(section, key, value );

        CHECK(cman->readString(section, key, "default")==value);

    }

    SECTION("Write/Read long "){
        auto cman = std::make_unique<CConfigManager>();
        std::string key = "test";
        std::string section = "section";
        int value = 42;
        
        cman->writeLong(section, key, value );

        CHECK(cman->readLong(section, key, 24)==value);
    }
}