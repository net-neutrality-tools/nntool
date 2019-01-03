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


#ifndef TOOL_H
#define TOOL_H


#include "header.h"


using namespace std;

class CTool
{
    public:
        CTool();

        virtual ~CTool();

        static string get_timestamp_string();
        
        static long long toLL(string s);
                        
        static float toFloat(string s);
        
        static void toLower(string &sText);
        
        static string get_ip_str(const struct sockaddr *sa);
        
        static string getHostname();
        
        static void replaceStringInPlace(string& subject, const string& search, const string& replace);
        
        static void tokenize(string &str,vector<string> &tokens, string &delimiters);
        
        static unsigned long long get_timestamp();
        
        static int randomData();
        
        static int randomData(vector<char> &vVector, int size);
        
        static int randomData(char *sbuffer, int size);
        
        static int validateIp( const string &ipAddress );
        
        static int get_timestamp_usec();
        
        static int get_timestamp_sec();
        
        //! Get Timestamp-Offset CET / CEST
        static int get_timestamp_offset();
        
        //Template Code-------------------------------------------------------
        //! \brief
        //!	Convert Number to String
        //! 	We have to implement the code here, otherwise the compiler will only 
        //! 	instantiate the method string toString<typename T>(T i). This means, we do not have methods like
        //!	toString(unsigned long long) or toString(int)
        //! \param s String
        //! \return b float
        template <typename T>
        static string toString(T i)
        {
                stringstream s;
                s << i;
                return s.str();
        }
};

#endif


