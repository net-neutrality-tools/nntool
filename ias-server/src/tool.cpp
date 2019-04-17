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
 *      \date Last update: 2019-01-02
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */


#include "tool.h"


CTool::CTool()
{
}

CTool::~CTool()
{
}

string CTool::get_timestamp_string()
{
    string date;

    time_t now = time(0);

    char* dt = ctime(&now);

    date = string( dt );

    date.erase( remove(date.begin(), date.end(), '\r'), date.end() );
    date.erase( remove(date.begin(), date.end(), '\n'), date.end() );

    return date;
}

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

void CTool::toLower(string &sText)
{
    for(unsigned int n = 0; n < sText.size(); n++)
    {
        sText.at(n) = tolower(sText.at(n));
    }
}

string CTool::get_ip_str(const struct sockaddr *sa)
{
    string ip;
    switch(sa->sa_family)
    {
        case AF_INET:
            char s1[INET_ADDRSTRLEN];
            inet_ntop(AF_INET, &(((struct sockaddr_in *)sa)->sin_addr),s1, INET_ADDRSTRLEN);
            ip = string(s1);
            break;

        case AF_INET6:
            char s2[INET6_ADDRSTRLEN];
            inet_ntop(AF_INET6, &(((struct sockaddr_in6 *)sa)->sin6_addr),s2, INET6_ADDRSTRLEN);
            ip = string(s2);
            break;

        default:
            return "0.0.0.0";
    }

    return ip;
}

string CTool::getHostname()
{
    char hostname[1024];
    gethostname(hostname, 1024);
    return string(hostname);
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

void CTool::tokenize(string &str,vector<string> &tokens, string &delimiters)
{
    string::size_type lastPos = str.find_first_not_of(delimiters, 0);
    string::size_type pos     = str.find_first_of(delimiters, lastPos);

    while( string::npos != pos || string::npos != lastPos )
    {
        tokens.push_back(str.substr(lastPos, pos - lastPos));
        lastPos = str.find_first_not_of(delimiters, pos);
        pos = str.find_first_of(delimiters, lastPos);
    }
}

int CTool::validateIp(const string &ipAddress)
{
	struct sockaddr_in sa4;
	struct sockaddr_in6 sa6;
	
	return (inet_pton(AF_INET6, ipAddress.c_str(), &(sa6.sin6_addr)) != 0) ? 6 : (inet_pton(AF_INET, ipAddress.c_str(), &(sa4.sin_addr)) != 0) ? 4 : 0;
}

unsigned long long CTool::get_timestamp()
{
    struct timeval tp;

    gettimeofday(&tp,NULL);

    return ( ( (unsigned long long)(tp.tv_sec) * 1000000 ) + (unsigned long long)(tp.tv_usec));
}

int CTool::randomData()
{
    return (255 * (rand() / (RAND_MAX + 1.0)) );
}

int CTool::randomData(vector<char> &vVector, int size)
{	
    srand(CTool::get_timestamp());

    for( int i = 0; i < size; i++ )
        vVector.push_back( (char)randomData() );

    return 0;
}

int CTool::randomData(char *sbuffer, int size)
{		
    srand(CTool::get_timestamp());

    for (int counter = 0; counter < size; counter++)
    {
        sbuffer[counter] = (char)randomData();
    }

    return 0;
}

int CTool::get_timestamp_usec()
{
    struct timeval tp;

    gettimeofday(&tp,NULL);

    return tp.tv_usec;
}

int CTool::get_timestamp_sec()
{
    struct timeval tp;

    gettimeofday(&tp,NULL);

    return tp.tv_sec;
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