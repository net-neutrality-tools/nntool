/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2019                                                          *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2019-05-08
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "header.h"
#include "callback.h"
#include "timer.h"
#include "measurement.h"




/*--------------Global Variables--------------*/

bool DEBUG;
bool RUNNING;
const char* PLATFORM;
const char* CLIENT_OS;

unsigned long long TCP_STARTUP;

bool RTT;
bool DOWNLOAD;
bool UPLOAD;

bool TIMER_ACTIVE;
bool TIMER_RUNNING;
bool TIMER_STOPPED;

int TIMER_INDEX;
int TIMER_DURATION;
unsigned long long MEASUREMENT_DURATION;

int NETTYPE;


struct conf_data conf;
struct measurement measurements;

vector<char> randomDataValues;

pthread_mutex_t mutex;

map<int,int> syncing_threads;

CTrace* pTrace;

CConfigManager* pConfig;
CConfigManager* pXml;
CConfigManager* pService;

CCallback *pCallback;
CMeasurement* pMeasurement;




/*--------------Forward declarations--------------*/

void        show_usage      	(char* argv0);
void		measurementStart	(string measurementParameters);
void		measurementStop		();
void 		startTestCase		(int nTestCase);
void		shutdown			();
static void signal_handler  	(int signal);




/*--------------Beginning of Program--------------*/

int main(int argc, char** argv)
{
	::DEBUG 			= false;
	::RUNNING 			= true;

	::RTT				= false;
	::DOWNLOAD 			= false;
	::UPLOAD 			= false;
	
	NETTYPE = 4;
		
	long int opt;

	while ( ( opt = getopt( argc, argv, "rdunhv" ) ) != -1 )
	{
		switch (opt)
		{
			case 'r':
				::RTT = true;
				break;
			case 'd':
				::DOWNLOAD = true;
				break;
			case 'u':
				::UPLOAD = true;
				break;
			case 'n':
				::DEBUG = true;
				break;
			case 'h':
                show_usage(argv[0]);
                return EXIT_SUCCESS;
			case 'v':
				cout << "ias client" << endl;
				cout << "Version: " << VERSION << endl;	
				return 0;
			case '?':
			default:
				printf("Error: Unknown Argument -%c\n", optopt);
				show_usage(argv[0]);
                return EXIT_FAILURE;
		}
	}

	if (!::RTT && !::DOWNLOAD && !::UPLOAD)
	{
		printf("Error: At least one test case is required");
		show_usage(argv[0]);
        return EXIT_FAILURE;
	}

    //Signal Handler
    signal(SIGINT, signal_handler);
	signal(SIGFPE, signal_handler);
	signal(SIGABRT, signal_handler);
	signal(SIGSEGV, signal_handler);
	signal(SIGCHLD, signal_handler);

	Json::object jRttParameters;
	Json::object jDownloadParameters;
	Json::object jUploadParameters;

	Json::object jMeasurementParameters;

	//set requested test cases
	jRttParameters["performMeasurement"] = ::RTT;
	jDownloadParameters["performMeasurement"] = ::DOWNLOAD;
	jUploadParameters["performMeasurement"] = ::UPLOAD;

	//set default measurement parameters
	jDownloadParameters["streams"] = "4";
	jUploadParameters["streams"] = "4";
	jMeasurementParameters["rtt"] = Json(jRttParameters);
	jMeasurementParameters["download"] = Json(jDownloadParameters);
	jMeasurementParameters["upload"] = Json(jUploadParameters);

	jMeasurementParameters["platform"] = "desktop";
	jMeasurementParameters["clientos"] = "linux";
	jMeasurementParameters["wsTLD"] = "net-neutrality.tools";
	jMeasurementParameters["wsTargetPort"] = "80";
	jMeasurementParameters["wsWss"] = "0";
	jMeasurementParameters["wsAuthToken"] = "placeholderToken";
	jMeasurementParameters["wsAuthTimestamp"] = "placeholderTimestamp";

	Json::array jTargets;
	jTargets.push_back("peer-ias-de-01");
	jMeasurementParameters["wsTargets"] = Json(jTargets);
	jMeasurementParameters["wsTargetsRtt"] = Json(jTargets);

	Json jMeasurementParametersJson = jMeasurementParameters;

	measurementStart(jMeasurementParametersJson.dump());
}

/**
 * @function measurementStart
 * @description API Function to start a measurement
 * @public
 * @param {string} measurementParameters JSON coded measurement Parameters
 */
void measurementStart(string measurementParameters)
{
	//android api hookup
	//call with json measurementParameters via ndk

	::TCP_STARTUP	= 3000000;
	conf.sProvider 	= "nntool";

	Json jMeasurementParameters = Json::object{};
	string error = "";
    jMeasurementParameters = Json::parse(measurementParameters, error);

    if (error.compare("") != 0)
    {
    	TRC_ERR("JSON parameter parse failed");
    	shutdown();
    }

    //parameter 		- example 1, example 2
    //-------------------------------------------------------------
    //platform 			- "cli", "mobile"
    //clientos 			- "linux", "android"
    //wsTargets 		- ["peer-ias-de-01"]
    //wsTargetsRtt 		- ["peer-ias-de-01"]
    //wsTLD 			- "net-neutrality.tools"
    //wsTargetPort		- "80"
    //wsWss 			- "0"
    //wsAuthToken 		- placeholderToken
    //wsAuthTimestamp	- placeholderTimestamp
    //rtt 				- {"performMeasurement":true}
    //download 			- {"performMeasurement":true, "streams":"4"}
    //upload 			- {"performMeasurement":true, "streams":"4"}

    ::PLATFORM = jMeasurementParameters["platform"].string_value().c_str();
    ::CLIENT_OS = jMeasurementParameters["clientos"].string_value().c_str();

	pTrace = CTrace::getInstance(); 
	
	TRC_INFO("Status: ias-client started");

	//map measurement parameters to internal variables
	pConfig = new CConfigManager();
	pXml 	= new CConfigManager();
	pService = new CConfigManager();

	Json::array jTargets = jMeasurementParameters["wsTargets"].array_items();
	string wsTLD = jMeasurementParameters["wsTLD"].string_value();
	pXml->writeString(conf.sProvider, "DNS_HOSTNAME", jTargets[0].string_value() + "." + wsTLD);
	jTargets = jMeasurementParameters["wsTargetsRtt"].array_items();
	pXml->writeString(conf.sProvider, "DNS_HOSTNAME_RTT", jTargets[0].string_value() + "." + wsTLD);
	
	pXml->writeString(conf.sProvider,"DL_PORT",jMeasurementParameters["wsTargetPort"].string_value());
	pXml->writeString(conf.sProvider,"UL_PORT",jMeasurementParameters["wsTargetPort"].string_value());

	pXml->writeString(conf.sProvider,"TLS",jMeasurementParameters["wsWss"].string_value());

	pConfig->writeString("security","authToken",jMeasurementParameters["wsAuthToken"].string_value());
	pConfig->writeString("security","authTimestamp",jMeasurementParameters["wsAuthTimestamp"].string_value());

	Json::object jRtt = jMeasurementParameters["rtt"].object_items();
	::RTT = jRtt["performMeasurement"].bool_value();

	Json::object jDownload = jMeasurementParameters["download"].object_items();
	::DOWNLOAD = jDownload["performMeasurement"].bool_value();
	pXml->writeString(conf.sProvider,"DL_STREAMS", jDownload["streams"].string_value());

	Json::object jUpload = jMeasurementParameters["upload"].object_items();
	::UPLOAD = jUpload["performMeasurement"].bool_value();
	pXml->writeString(conf.sProvider,"UL_STREAMS", jUpload["streams"].string_value());

	pXml->writeString(conf.sProvider,"PING_QUERY","11");


	pCallback = new CCallback();
	if( pCallback->createThread() != 0 )
	{
		TRC_ERR( "Error: Failure while creating the Thread - Callback!" );
		shutdown();
	}

    if (!::RTT && !::DOWNLOAD && !::UPLOAD)
    {
    	pCallback->callback("error", "no test case enabled", 1, "no test case enabled");

    	shutdown();
    }

	//perform requested test cases
	if (::RTT)
	{
		usleep(1000000);
		conf.nTestCase = 2;
		conf.sTestName = "rtt_udp";
		TRC_INFO( ("Taking Testcase RTT UDP ("+CTool::toString(conf.nTestCase)+")").c_str() );
		startTestCase(conf.nTestCase);
	}

	if (::DOWNLOAD)
	{
		usleep(1000000);
		conf.nTestCase = 3;
		conf.sTestName = "download";
		TRC_INFO( ("Taking Testcase DOWNLOAD ("+CTool::toString(conf.nTestCase)+")").c_str() );
		startTestCase(conf.nTestCase);
	}

	if (::UPLOAD)
	{
		CTool::randomData( randomDataValues, 1123457*10 );
		usleep(1000000);
		conf.nTestCase = 4;
		conf.sTestName = "upload";
		TRC_INFO( ("Taking Testcase UPLOAD ("+CTool::toString(conf.nTestCase)+")").c_str() );
		startTestCase(conf.nTestCase);
	}

	shutdown();
}

/**
 * @function measurementStop
 * @public
 * @description API Function to stop a measurement
 */
void measurementStop()
{
	//android api hookup
	//call via ndk

	shutdown();
}

void startTestCase(int nTestCase)
{
	syncing_threads.clear();
	pCallback->mTestCase = nTestCase;
	pMeasurement = new CMeasurement( pConfig, pXml, pService, conf.sProvider, nTestCase, pCallback);
	pMeasurement->startMeasurement();
	delete(pMeasurement);
}

void shutdown()
{
	usleep(1000000);

	::RUNNING = false;

	delete(pService);
	delete(pXml);
	delete(pConfig);

	pCallback->stopThread();
	pCallback->waitForEnd();
	delete(pCallback);

	TRC_INFO("Status: ias-client stopped");

	delete(pTrace);

	exit(EXIT_SUCCESS);
}

void show_usage(char* argv0)
{
	cout<< "                                                 " <<endl;
	cout<< "Usage: " << argv0 << " [ options ... ]           " <<endl;
	cout<< "                                                 " <<endl;
	cout<< "  -r             - Perform RTT measurement       " <<endl;
	cout<< "  -d             - Perform Download measurement  " <<endl;
	cout<< "  -u             - Perform Upload measurement    " <<endl;
	cout<< "  -n             - Show debugging output	 	 " <<endl;
	cout<< "  -h             - Show Help                     " <<endl;
	cout<< "  -v             - Show Version                  " <<endl;
	cout<< "                                                 " <<endl;

    exit(EXIT_FAILURE);
}

static void signal_handler(int signal)
{
	CTool::print_stacktrace();
	
    ::RUNNING = false;
    sleep(1);
    exit(signal);
}