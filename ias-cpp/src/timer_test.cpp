#include "catch.hpp"
#include "timer.h"
#include "callback.h"

TEST_CASE("CTimer") {
    SECTION("test timer") {
        REQUIRE(TIMER_ACTIVE == false);

        auto callback = std::make_unique<CCallback>();
        auto timer = std::make_unique<CTimer>(1, callback.get());

        REQUIRE(TIMER_ACTIVE == true);

        //int ret = timer->run();
        //REQUIRE(ret == 0);
    }
}
