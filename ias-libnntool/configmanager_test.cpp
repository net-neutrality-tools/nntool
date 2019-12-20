/*!
    \file configmanager_test.cpp
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