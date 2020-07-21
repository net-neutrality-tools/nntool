/*!
    \file timer_test.cpp
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2020-07-01

    Copyright (C) 2016 - 2020 zafaco GmbH

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
#include "timer.h"
#include "callback.h"

TEST_CASE("CTimer") {
    SECTION("test timer") {
        REQUIRE(TIMER_ACTIVE == false);

        Json::object jMeasurementParameters = Json::object{};
        auto callback = std::make_unique<CCallback>(jMeasurementParameters);
        auto timer = std::make_unique<CTimer>(1, callback.get(), 1000000);

        REQUIRE(TIMER_ACTIVE == true);

        //int ret = timer->run();
        //REQUIRE(ret == 0);
    }
}
