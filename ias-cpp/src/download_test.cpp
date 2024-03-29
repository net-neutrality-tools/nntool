/*!
    \file download_test.cpp
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
#include "test.h"
#include "header.h"
#include "measurement.h"
#include "callback.h"
#include "sha1.hpp"



bool DEBUG;
bool OVERLOADED;
struct conf_data conf;

TEST_CASE("Download test")
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
    SECTION("Performs successfull download measurement against ias-server")
    {
        syncing_threads.clear();
        conf.sProvider="testing";
        conf.sTestName="download";
        conf.nTestCase=3;
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

        pXml->writeString("testing", "DNS_HOSTNAME", "localhost");
        pXml->writeLong("testing","DL_PORT",80);
        pXml->writeLong("testing","DL_DURATION",10);
        pConfig->writeString("security","authToken",token);
	    pConfig->writeString("security","authTimestamp",to_string(time));

        pXml->writeLong(conf.sProvider,"DL_STREAMS", 4);

        pCallback = std::make_unique<CCallback>(jMeasurementParameters);
	
        pCallback->mTestCase=3;
        std::unique_ptr<CMeasurement> pMeasurement = std::make_unique<CMeasurement>(
            pConfig.get(), pXml.get(), pService.get(), "testing", 3, pCallback.get());

        //Number of parallel streams is set
        CHECK(conf.instances == pXml->readLong(conf.sProvider,"DL_STREAMS",4));
        
        pMeasurement->startMeasurement();
        Json::array result = pCallback->jMeasurementResultsDownload;
        CHECK(result[0]["num_streams_start"]=="4");
        CHECK(result[result.size()-1]["num_streams_end"]=="4");
        CHECK(result.size()==pXml->readLong("testing","DL_DURATION",10)*2);
        CHECK(stol(result[result.size()-1]["duration_ns"].string_value())/1000000000 == pXml->readLong("testing","DL_DURATION",10));
        CHECK((stol(result[result.size()-1]["duration_ns_total"].string_value())-stol(result[result.size()-1]["duration_ns"].string_value()))/1000000000 == (::TCP_STARTUP/1000000));
        ::RUNNING = false;

    }
}