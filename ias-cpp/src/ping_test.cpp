/*!
    \file ping_test.cpp
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-12-13

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
#include "test.h"
#include "header.h"
#include "measurement.h"
#include "callback.h"
#include "sha1.hpp"



TEST_CASE("Ping test")
{
    ::OVERLOADED = false;
    ::DEBUG =false;
    ::RUNNING= true;
    ::PLATFORM = "desktop";
    ::CLIENT_OS = "linux";
    ::TCP_STARTUP	= 3000000;
    //CTrace& pTrace = CTrace::getInstance();
    CTrace::setLogFunction([] (std::string const & cat, std::string const  &s) { std::cout << "[" + CTool::get_timestamp_string() + "] " + cat + ": " + s + "\n"; });
    ifstream in("/etc/ias-server/config.json");
    stringstream buffer;
    buffer << in.rdbuf();
    string error;
    ::CONFIG = Json::parse(buffer.str(), error);

    
    std::unique_ptr<CCallback> pCallback;
    std::unique_ptr<CTimer> mTimer;
    std::unique_ptr<CConfigManager> pConfig;
    std::unique_ptr<CConfigManager> pXml;
    std::unique_ptr<CConfigManager> pService;
    SECTION("Performs successfull RTT measurement against ias-server")
    {
        syncing_threads.clear();
        conf.sProvider="testing";
        conf.sTestName = "rtt_udp";
        conf.nTestCase=2;
        Json::object jMeasurementParameters;
        jMeasurementParameters["wsTargets"]=Json::array {"localhost"};
        jMeasurementParameters["wsTLD"]="";
        pConfig = std::make_unique<CConfigManager>();
        pXml = std::make_unique<CConfigManager>();
        pService = std::make_unique<CConfigManager>();

        // Create token
        long long time = CTool::get_timestamp();
        string authentication_secret = ::CONFIG["authentication"]["secret"].string_value();
        std::string token = sha1(to_string(time) + authentication_secret);

        pXml->writeString("testing", "PING_DESTINATION", "127.0.0.1");
        pXml->writeString("testing", "DNS_HOSTNAME", "localhost");
        pXml->writeString("testing", "PING_QUERY", "1");
        pConfig->writeString("security","authToken",token);
	    pConfig->writeString("security","authTimestamp",to_string(time));
        pCallback = std::make_unique<CCallback>(jMeasurementParameters);
	
        pCallback->mTestCase=2;
        std::unique_ptr<CMeasurement> pMeasurement = std::make_unique<CMeasurement>(
            pConfig.get(), pXml.get(), pService.get(), "testing", 2, pCallback.get());

        //Number of parallel streams is set
        //CHECK(conf.instances == pXml->readLong(conf.sProvider,"DL_STREAMS",4));
        
        pMeasurement->startMeasurement();
        Json::object result = pCallback->jMeasurementResultsRttUdp;
        TRC_DEBUG(Json(result).dump());
        string median = result["median_ns"].string_value();
        Json::array rtts = result["rtts"].array_items();

        CHECK(pXml->readLong("testing", "PING_QUERY", 0)==rtts.size());
        
        CHECK(stoi(result["num_received"].string_value()) == pXml->readLong("testing", "PING_QUERY", -1));
        
        CHECK(stoi(result["num_received"].string_value()) == stoi(result["num_sent"].string_value()));

        auto &n = result["rtts"].array_items()[result["rtts"].array_items().size() - 1];
        Json::object m = n.object_items();

        CHECK( (median.compare(to_string(m["rtt_ns"].int_value())) == 0 ? true : false) );

        ::RUNNING = false;

    }
}