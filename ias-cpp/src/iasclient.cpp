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
 *      \date Last update: 2019-08-16
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#include "header.h"
#include "callback.h"
#include "timer.h"
#include "measurement.h"




/*--------------Global Variables--------------*/

bool DEBUG;
bool RUNNING;
bool UNREACHABLE;
bool FORBIDDEN;
bool OVERLOADED;
const char* PLATFORM;
const char* CLIENT_OS;

unsigned long long TCP_STARTUP;

bool RTT;
bool DOWNLOAD;
bool UPLOAD;

std::atomic_bool hasError;
std::exception recentException;

bool TIMER_ACTIVE;
bool TIMER_RUNNING;
bool TIMER_STOPPED;

int TIMER_INDEX;
int TIMER_DURATION;
unsigned long long MEASUREMENT_DURATION;

bool PERFORMED_RTT;
bool PERFORMED_DOWNLOAD;
bool PERFORMED_UPLOAD;

struct conf_data conf;
struct measurement measurements;

vector<char> randomDataValues;

pthread_mutex_t mutex1;

map<int,int> syncing_threads;

std::unique_ptr<CConfigManager> pConfig;
std::unique_ptr<CConfigManager> pXml;
std::unique_ptr<CConfigManager> pService;

std::unique_ptr<CCallback> pCallback;

MeasurementPhase currentTestPhase = MeasurementPhase::INIT;

std::function<void(int)> signalFunction = nullptr;

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
	::UNREACHABLE 		= false;
	::FORBIDDEN 		= false;
	::OVERLOADED 		= false;

	::RTT				= false;
	::DOWNLOAD 			= false;
	::UPLOAD 			= false;

	long int opt;
	int tls = 0;
	string tcp_target_port = "80";

	while ( ( opt = getopt( argc, argv, "rdut:nhv" ) ) != -1 )
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
			case 't':
				tls = 1;
				tcp_target_port = optarg;
				if (optopt == 't')
				{
	                show_usage(argv[0]);
                	return EXIT_SUCCESS;
				}
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
	jMeasurementParameters["wsTargetPort"] = tcp_target_port;
	jMeasurementParameters["wsWss"] = to_string(tls);
	jMeasurementParameters["wsAuthToken"] = "placeholderToken";
	jMeasurementParameters["wsAuthTimestamp"] = "placeholderTimestamp";

	Json::array jTargets;
	jTargets.push_back("peer-ias-de-01");
	jMeasurementParameters["wsTargets"] = Json(jTargets);
	jMeasurementParameters["wsTargetsRtt"] = Json(jTargets);

	Json jMeasurementParametersJson = jMeasurementParameters;

    #ifdef NNTOOL_CLIENT
    //register callback
    CTrace::setLogFunction([] (const std::string &s) { std::cout << s; });
    #endif

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
    //Signal Handler

    signal(SIGINT, signal_handler);
    signal(SIGFPE, signal_handler);
    signal(SIGABRT, signal_handler);
    signal(SIGSEGV, signal_handler);
    signal(SIGCHLD, signal_handler);

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

	TRC_INFO("Status: ias-client started");

	//map measurement parameters to internal variables
	pConfig = std::make_unique<CConfigManager>();
	pXml 	= std::make_unique<CConfigManager>();
	pService = std::make_unique<CConfigManager>();

	Json::array jTargets = jMeasurementParameters["wsTargets"].array_items();
	string wsTLD = jMeasurementParameters["wsTLD"].string_value();

	#ifdef __ANDROID__
	pXml->writeString(conf.sProvider, "DNS_HOSTNAME", jTargets[0].string_value() /* + "." + wsTLD*/);
	#else
	pXml->writeString(conf.sProvider, "DNS_HOSTNAME", jTargets[0].string_value() + "." + wsTLD);
	#endif

	jTargets = jMeasurementParameters["wsTargetsRtt"].array_items();
	
	#ifdef __ANDROID__
    pXml->writeString(conf.sProvider, "DNS_HOSTNAME_RTT", jTargets[0].string_value() /*+ "." + wsTLD*/);
    #else
    pXml->writeString(conf.sProvider, "DNS_HOSTNAME_RTT", jTargets[0].string_value() + "." + wsTLD);
    #endif

	pXml->writeString(conf.sProvider,"DL_PORT",jMeasurementParameters["wsTargetPort"].string_value());
	pXml->writeString(conf.sProvider,"UL_PORT",jMeasurementParameters["wsTargetPort"].string_value());

	pXml->writeString(conf.sProvider,"TLS",jMeasurementParameters["wsWss"].string_value());
	#ifdef __ANDROID__
	    pXml->writeString(conf.sProvider, "CLIENT_IP", jMeasurementParameters["clientIp"].string_value());
	#endif

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

    #ifdef __ANDROID__
        pXml->writeString(conf.sProvider,"PING_QUERY",jRtt["ping_query"].string_value());
    #else
	    pXml->writeString(conf.sProvider,"PING_QUERY","10");
	#endif


	pCallback = std::make_unique<CCallback>();

	try
	{
	    if (!::RTT && !::DOWNLOAD && !::UPLOAD)
	    {
	    	pCallback->callback("error", "no test case enabled", 1, "no test case enabled");

	    	shutdown();
	    }

		//perform requested test cases
		if (::RTT)
		{
			conf.nTestCase = 2;
			conf.sTestName = "rtt_udp";
			TRC_INFO( ("Taking Testcase RTT UDP ("+CTool::toString(conf.nTestCase)+")").c_str() );
			currentTestPhase = MeasurementPhase::PING;
			startTestCase(conf.nTestCase);
		}

		if (::hasError) {
	        throw ::recentException;
		}

		if (::DOWNLOAD)
		{
			conf.nTestCase = 3;
			conf.sTestName = "download";
			TRC_INFO( ("Taking Testcase DOWNLOAD ("+CTool::toString(conf.nTestCase)+")").c_str() );
			currentTestPhase = MeasurementPhase::DOWNLOAD;
			startTestCase(conf.nTestCase);
		}

		if (::hasError) {
	        throw ::recentException;
	    }

		if (::UPLOAD)
		{
			CTool::randomData( randomDataValues, 1123457*10 );
			conf.nTestCase = 4;
			conf.sTestName = "upload";
			TRC_INFO( ("Taking Testcase UPLOAD ("+CTool::toString(conf.nTestCase)+")").c_str() );
			currentTestPhase = MeasurementPhase::UPLOAD;
			startTestCase(conf.nTestCase);
		}

	    if (::hasError) {
	        throw ::recentException;
	    }
    } 
    catch (std::exception & ex)
    {
    	
    }

	currentTestPhase = MeasurementPhase::END;

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
	#ifdef __ANDROID__
	    //set off measurement start callback
	    pCallback->callbackToPlatform("started", "", 0, "");
	#endif
	std::unique_ptr<CMeasurement> pMeasurement = std::make_unique<CMeasurement>( pConfig.get(), pXml.get(), pService.get(), conf.sProvider, nTestCase, pCallback.get());
	pMeasurement->startMeasurement();
}

void shutdown()
{
	usleep(1000000);

	::RUNNING = false;

	TRC_INFO("Status: ias-client stopped");

    #ifndef __ANDROID__
        exit(EXIT_SUCCESS);
	#endif
}

void show_usage(char* argv0)
{
	cout<< "                                                 				" <<endl;
	cout<< "Usage: " << argv0 << " [ options ... ]           				" <<endl;
	cout<< "                                                 				" <<endl;
	cout<< "  -r             - Perform RTT measurement       				" <<endl;
	cout<< "  -d             - Perform Download measurement  				" <<endl;
	cout<< "  -u             - Perform Upload measurement    				" <<endl;
	cout<< "  -t port        - Enable TLS for TCP Connections on stated port" <<endl;
	cout<< "  -n             - Show debugging output	 	 				" <<endl;
	cout<< "  -h             - Show Help                     				" <<endl;
	cout<< "  -v             - Show Version                  				" <<endl;
	cout<< "                                                 				" <<endl;

    exit(EXIT_FAILURE);
}

static void signal_handler(int signal)
{
    hasError = true;

	TRC_ERR("Error signal received " + std::to_string(signal));

    #ifndef __ANDROID__
	    CTool::print_stacktrace();
	#endif
	
    ::RUNNING = false;

    if (signalFunction != nullptr) {
        signalFunction(signal);
    }

    #ifndef __ANDROID__
        sleep(1);
        exit(signal);
    #endif

}
