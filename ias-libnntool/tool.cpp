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
 *      \date Last update: 2019-09-09
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */


#include "tool.h"

//! \brief
//!	Standard Constructor
CTool::CTool()
{
}

//! \brief
//!	Virtual Destructor
CTool::~CTool()
{
}

//! \brief
//!	Convert String to Int
//! \param s String
//! \return i Int
int CTool::toInt( string s )
{
	int i;
	stringstream sstr(s);
	sstr >> i;
	return i;
}

//! \brief
//!	Convert String to Int
//! \param s String
//! \return i Int
unsigned int CTool::toUInt( string s )
{
	unsigned int b;
	stringstream sstr(s);
	sstr >> b;
	return b;
}

//! \brief
//!	Convert String to unsigned long long
//! \param s String
//! \return b unsigned long long
unsigned long long CTool::toULL( string s )
{
	unsigned long long b;
	stringstream sstr(s);
	sstr >> b;
	return b;
}

//! \brief
//!	Convert String to long long
//! \param s String
//! \return b long long
long long CTool::toLL( string s )
{
	long long b;
	stringstream sstr(s);
	sstr >> b;
	return b;
}

//! \brief
//!	Convert String to float
//! \param s String
//! \return b float
float CTool::toFloat(string s)
{
	float b;
	stringstream sstr(s);
	sstr >> b;
	return b;
}

//! \brief
//!	Convert to lower case
//! \param sText String
void CTool::toLower(string &sText)
{
	for(unsigned int n = 0; n < sText.size(); n++)
	{
		sText.at(n) = tolower(sText.at(n));
	}
}

//! \brief
//!	Convert to upper case
//! \param sText String
void CTool::toUpper(string &sText)
{
	for(unsigned int n = 0; n < sText.size(); n++)
	{
		sText.at(n) = toupper(sText.at(n));
	}
}

string CTool::get_ip_str(const struct sockaddr *sa)
{
    string ip;
    switch(sa->sa_family)
    {
        case AF_INET:
        {
            char s1[INET_ADDRSTRLEN];
            inet_ntop(AF_INET, &(((struct sockaddr_in *)sa)->sin_addr),s1, INET_ADDRSTRLEN);
            ip = string(s1);
            break;
        }
        case AF_INET6:
        {
            char s2[INET6_ADDRSTRLEN];
            inet_ntop(AF_INET6, &(((struct sockaddr_in6 *)sa)->sin6_addr),s2, INET6_ADDRSTRLEN);
            ip = string(s2);
            break;
        }
        default:
        {
            ip = "0.0.0.0";
        }
    }

    return ip;
}

unsigned long long CTool::calculateResultsMin( map<int,unsigned long long> dmap )
{
	unsigned long long min = dmap.begin()->second;

	map<int, unsigned long long>::iterator AI;
	
	//Starting multiple Instances for every Probe
	for (AI = dmap.begin(); AI != dmap.end(); ++AI)
	{
		if( (*AI).second < min )
			min = (*AI).second;
	}
	
	return min;
}

unsigned long long CTool::calculateResultsAvg( map<int,unsigned long long> dmap )
{
	int count = 0;
	
	unsigned long long avg = 0;
	
	map<int, unsigned long long>::iterator AI;
	
	//Starting multiple Instances for every Probe
	for (AI = dmap.begin(); AI != dmap.end(); ++AI)
	{	
		avg +=  (*AI).second;
		count++;
	}
	
	if( count != 0 )
	{
		avg = avg/count;
	}
	
	return avg;
}

int CTool::calculateResults(struct measurement_data &sMeasurement)
{
	return CTool::calculateResults(sMeasurement, 1, 0, 0);
}

int CTool::calculateResults(struct measurement_data &sMeasurement, double increment, int ai_offset, unsigned long long duration)
{
	double count = 0;
	
	unsigned long long min 		= std::next(sMeasurement.results.begin(), ai_offset)->second;
	unsigned long long avg 		= 0;
	unsigned long long max 		= 0;
	unsigned long long sum 		= 0;
	unsigned long long sumSq	= 0;
	double avgDouble			= 0;

	#ifdef NNTOOL
		vector<double> medianVector;
	#endif
	
	map<int, unsigned long long>::iterator AI;
	
	//Starting multiple Instances for every Probe
	for (AI = std::next(sMeasurement.results.begin(), ai_offset); AI != sMeasurement.results.end(); ++AI)
	{
		if( (*AI).second < min )
			min = (*AI).second;
		
		if( (*AI).second > max )
			max = (*AI).second;
		
		sum +=  (*AI).second;

		#ifdef NNTOOL
			medianVector.push_back((*AI).second);
			sumSq += (*AI).second * (*AI).second;
		#endif

		count += increment;
	}

	if( count != 0 )
	{	
		if (duration != 0)
		{
			avg = avgDouble = (double)sum / ( (double)duration / 1e6);
		}
		else
		{
			avg = avgDouble = (double)sum / (double)count;
		}
		

		sMeasurement.min = min;
		sMeasurement.avg = avg;
		sMeasurement.max = max;

		#ifdef NNTOOL
			if (duration != 0)
			{
				sMeasurement.duration_ns = duration * 1000;
			}
			else
			{
				sMeasurement.duration_ns = count * 1000 * 1000 * 1000;
			}

			sMeasurement.interim_values = medianVector;

			//calculate median
	        sort(medianVector.begin(), medianVector.end());
	        size_t samples = medianVector.size();
	        
	        if (samples%2 == 0)
	        {
	            sMeasurement.median_ns  = ((medianVector[samples/2-1] + medianVector[samples/2])/2) * 1000;
	        }
	        else
	        {
	            sMeasurement.median_ns  = medianVector[samples/2] * 1000;
	        }

	        //calculate population standard deviation
	        double variancePopulation = (double)sumSq / (double)count - avgDouble * avgDouble;
	        sMeasurement.standard_deviation_ns = sqrt(variancePopulation) * 1000;
		#endif
	}
	
	return 0;
}

//! \brief
//!	Get Systeminfo OS
//! \return time Time in Milliseconds
string CTool::getSystemInfoOS()
{
	struct utsname unameData;
	
	if( uname(&unameData) != 0 )
		return "-";
	
	return unameData.sysname;
}

//! \brief
//!	Get Systeminfo OS Version
//! \return time Time in Milliseconds
string CTool::getSystemInfoOSVersion()
{
	struct utsname unameData;
	
	if( uname(&unameData) != 0 )
		return "-";
	
	return unameData.release;
}

//! \brief
//!	Get active IP
//! \param sInterface Name of Device
//! \param nType Switch between IPv4 and IPv6
//! \return sIp IP Adress
int CTool::validateIp(const string &ipAddress)
{
	struct sockaddr_in sa4;
	struct sockaddr_in6 sa6;
	
	return (inet_pton(AF_INET6, ipAddress.c_str(), &(sa6.sin6_addr)) != 0) ? 6 : (inet_pton(AF_INET, ipAddress.c_str(), &(sa4.sin_addr)) != 0) ? 4 : 0;
}

//! \brief
//!	Get Timestamps in ms
//! \return time Time in Milliseconds
int CTool::get_timestamp_usec()
{
	struct timeval tp;

	gettimeofday(&tp,NULL);

	return tp.tv_usec;
}

//! \brief
//!	Get Timestamps in s
//! \return time Time in Seconds
int CTool::get_timestamp_sec()
{
	struct timeval tp;

	gettimeofday(&tp,NULL);
	
	return tp.tv_sec;
}

//! \brief
//!	Get Timestamps complete
//! \return time Time in uSeconds
unsigned long long CTool::get_timestamp()
{
	struct timeval tp;
	
	gettimeofday(&tp,NULL);
	
	return ( ( (unsigned long long)(tp.tv_sec) * 1000000 ) + (unsigned long long)(tp.tv_usec));
}

//! \brief
//!	Get Timestamp-Offset CET / CEST
//! \return offset
int CTool::get_timestamp_offset()
{
	struct tm * lt;
	time_t t = time(NULL);
	lt = localtime(&t);

	long int offset = lt->tm_gmtoff;

	return offset;
}

//! \brief
//!	Get Timestamp-Offset CET / CEST
//! \return offset
string CTool::get_timestamp_string()
{
	string date;
	// current date/time based on current system
	time_t now = time(0);
	
	// convert now to string form
	char* dt = ctime(&now);
	
	date = string( dt );
	
	date.erase( remove(date.begin(), date.end(), '\r'), date.end() );
	date.erase( remove(date.begin(), date.end(), '\n'), date.end() );
	
	return date;

}

void CTool::replaceStringInPlace(string& subject, const string& search, const string& replace) 
{
    size_t pos = 0;
    while((pos = subject.find(search, pos)) != string::npos) 
    {
        subject.replace(pos, search.length(), replace);
        pos += replace.length();
    }
}

//! \brief
//!	Tokenize a String into vector elements of type string
//! \param &str
//! \param &tokens
//! \param &delimiters
void CTool::tokenize(string &str,vector<string> &tokens, string &delimiters)
{
	// Skip delimiters at beginning.
	string::size_type lastPos = str.find_first_not_of(delimiters, 0);
	// Find first "non-delimiter".
	string::size_type pos     = str.find_first_of(delimiters, lastPos);

	while( string::npos != pos || string::npos != lastPos )
	{
		// Found a token, add it to the vector.
		tokens.push_back(str.substr(lastPos, pos - lastPos));
		// Skip delimiters.  Note the "not_of"
		lastPos = str.find_first_not_of(delimiters, pos);
		// Find next "non-delimiter"
		pos = str.find_first_of(delimiters, lastPos);
	}
}

//! \brief
//!    Fork a child process, execute Programm, and return it's pid.
//! \return -1 if fork failed.
pid_t CTool::spawn_process( char *args1[] )
{	
	pid_t pid = fork();

	if (pid == -1)
	{
		TRC_ERR( "Error: Could not fork Process" );
		return -1;
	}

	// If fork() returns 0, this is is the child process.
	// If fork() returns non-zero, we are the parent and the return value is the PID of the child process.
	if (pid == 0)
	{
		// when you call exec, it replaces the currently running process (the child process) with whatever we pass to exec.
		execv( args1[0], args1 );
		
		abort();

	}
	else
	{
		// parent, return the child's PID back to main.
		return pid;
	}
}

//! \brief
//!    Get the WAN interface speed, configured in ntservice.ini
//! \param sString
//! \return s
string CTool::getDownloadSpeed( string sString )
{
	string s;
	
	size_t first;
	size_t last;
	
	const char seperator = '_';
	
	last 	= sString.rfind(seperator);
	first 	= sString.rfind(seperator, last - 1);
	
	s = sString.substr( first + 1, last - first - 1 );
	return s;
}

//! \brief
//!    Fork a child process, execute Programm, and return it's pid.
//! \param sString
//! \return s
string CTool::getIpFromHostname( string sString )
{
	return getIpFromHostname(sString, 6);
}

//! \brief
//!    Fork a child process, execute Programm, and return it's pid.
//! \param sString
//! \return s
string CTool::getIpFromHostname( string sString, int nType )
{
	int error = 0;
	
	char host[NI_MAXHOST];
	struct addrinfo query, *result;
	
	// Request only one socket type from getaddrinfo(). Else we would get both SOCK_DGRAM and SOCK_STREAM, and print two copies of each numeric address.
	memset(&query, 0, sizeof query);
	
	if( nType == 4 )
		query.ai_family 	= AF_INET;		//IPv4
	if( nType == 6)
		query.ai_family 	= AF_INET6;		//IPv6
		
	query.ai_socktype 	= SOCK_DGRAM;

	// Use getaddrinfo() to resolve "servername" and allocate a linked list of addresses.
	// If we get an error, break loop and jump to end for saving results
	if( ( error = getaddrinfo(sString.c_str(), NULL, &query, &result) ) != 0 )
	{
		query.ai_family 	= AF_INET;		//IPv4
		
		// Use getaddrinfo() to resolve "servername" and allocate a linked list of addresses.
		// If we get an error, break loop and jump to end for saving results
		if( ( error = getaddrinfo(sString.c_str(), NULL, &query, &result) ) != 0 )
		{
			TRC_ERR( "Could not Request DNS - DNS ERROR" );
			
			return "1.1.1.1";
		}
	}
	
	//Convert Server Name in Human Readable
	getnameinfo(result->ai_addr, result->ai_addrlen,host, sizeof host, NULL, 0, NI_NUMERICHOST);
	
	freeaddrinfo(result);
	
	return string(host);
}

struct addrinfo* CTool::getIpsFromHostname( string sString, bool bReachable )
{
	int error = 0;
	
	struct addrinfo query, *ips;

	memset(&query, 0, sizeof query);
	query.ai_family = AF_UNSPEC;

	if (bReachable)
	{
		query.ai_flags = (AI_V4MAPPED | AI_ADDRCONFIG);
	}
	else
	{
		query.ai_flags = AI_V4MAPPED;
	}
	
	if( ( error = getaddrinfo(sString.c_str(), NULL, &query, &ips) ) != 0 )
	{
		TRC_ERR( "Could not Request DNS - DNS ERROR" );
	}

    return ips;
}

//! \brief
//!    Fork a child process, execute Programm, and return it's pid.
//! \param sString
//! \return s
string CTool::getHostname()
{
	char hostname[1024];
	gethostname(hostname, 1024);
	return string(hostname);
}

//! \brief
//!	Generate random data of size int
//! \param *sbuffer
//! \param size
//! \return 0
int CTool::randomData()
{
	return (255 * (rand() / (RAND_MAX + 1.0)) );
}

//! \brief
//!	Generate random data of size int
//! \param *sbuffer
//! \param size
//! \return 0
int CTool::randomData(char *sbuffer, int size)
{		
	// Provide seed for the random number generator.
	srand(CTool::get_timestamp());
	
	for (int counter = 0; counter < size; counter++)
	{
		sbuffer[counter] = (char)randomData();
	}
		
	return 0;
}

//! \brief
//!	Generate random data of size int
//! \param *sbuffer
//! \param size
//! \return 0
int CTool::randomData(vector<char> &vVector, int size)
{	
	// Provide seed for the random number generator.
	srand(CTool::get_timestamp());
	
	for( int i = 0; i < size; i++ )
		vVector.push_back( (char)randomData() );
	
	return 0;
}

/** Print a demangled stack backtrace of the caller function to FILE* out. */
void CTool::print_stacktrace()
{
    //backtrace under android doesn't work as is (no execinfo.h)
    #ifndef __ANDROID__

	char 	chTrace[1024];
	TRC_DEBUG( "stack trace:" );

	// storage array for stack trace address data
	void* addrlist[64];

	// retrieve current stack addresses
	int addrlen = backtrace(addrlist, sizeof(addrlist) / sizeof(void*));

	if (addrlen == 0)
	{
		TRC_DEBUG( "  <empty, possibly corrupt>" );
		return;
	}

	// resolve addresses into strings containing "filename(function+address)",
	// this array must be free()-ed
	char** symbollist = backtrace_symbols(addrlist, addrlen);

	// allocate string which will be filled with the demangled function name
	size_t funcnamesize = 256;
	char* funcname = (char*)malloc(funcnamesize);

	// iterate over the returned symbol lines. skip the first, it is the
	// address of this function.
	for (int i = 1; i < addrlen; i++)
	{
		char *begin_name = 0, *begin_offset = 0, *end_offset = 0;

		// find parentheses and +address offset surrounding the mangled name:
		// ./module(function+0x15c) [0x8048a6d]
		for (char *p = symbollist[i]; *p; ++p)
		{
			if (*p == '(')
				begin_name = p;
			else if (*p == '+')
				begin_offset = p;
			else if (*p == ')' && begin_offset)
			{
				end_offset = p;
				break;
			}
		}

		if (begin_name && begin_offset && end_offset
		&& begin_name < begin_offset)
		{
			*begin_name++ = '\0';
			*begin_offset++ = '\0';
			*end_offset = '\0';

			// mangled name is now in [begin_name, begin_offset) and caller
			// offset in [begin_offset, end_offset). now apply
			// __cxa_demangle():

			int status;
			char* ret = abi::__cxa_demangle(begin_name,funcname, &funcnamesize, &status);
			if (status == 0)
			{
				funcname = ret; // use possibly realloc()-ed string
				snprintf(chTrace, sizeof(chTrace), "  %s : %s+%s",symbollist[i], funcname, begin_offset);
				TRC_DEBUG(chTrace);
			}
			else
			{
				// demangling failed. Output function name as a C function with
				// no arguments.
				snprintf(chTrace, sizeof(chTrace), "  %s : %s()+%s",symbollist[i], begin_name, begin_offset);
				TRC_DEBUG(chTrace);
			}
		}
		else
		{
			// couldn't parse the line? print the whole line.
			snprintf(chTrace, sizeof(chTrace), "  %s", symbollist[i]);
			TRC_DEBUG(chTrace);
		}
	}

	free(funcname);
	free(symbollist);

	#endif
}

string CTool::to_string_precision(double value, const int precision)
{
    std::ostringstream out;
    out << std::fixed << std::setprecision(precision) << value;
    return out.str();
}
