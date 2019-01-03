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

#ifndef SERVERMONITORING_H
#define SERVERMONITORING_H


#include "header.h"


using namespace std;

class CServerMonitoring : public CBasisThread
{
    public:
        virtual ~CServerMonitoring();

        CServerMonitoring();

        int run() override;
        
    private:
        int getInterfaceMetrics(string interface, bool mem);
        void mysqlServerMonitoring();
        void mysqlSystemMonitoring();

        long long timestamp_current, timestamp_mem;
        long double timestamp_diff, timestamp_diff_report;
        
        FILE *file;
        char line[1024];
        
        long long unsigned int txBytes_mem, txBytes, txBytes_diff;
        int txBytes_traffic;
        long txBytes_rate;
        
        long long unsigned int txPackets_mem, txPackets, txPackets_diff;
        int txPackets_traffic;
        long txPackets_rate;
        
        long long unsigned int rxBytes_mem, rxBytes, rxBytes_diff;
        int rxBytes_traffic;
        long rxBytes_rate;
        
        long long unsigned int rxPackets_mem, rxPackets, rxPackets_diff;
        int rxPackets_traffic;
        long rxPackets_rate;
        
        bool overloadedChanged;
        
        string mysqlHost;
        string mysqlPort;
        string mysqlDb;
        string mysqlUser;
        string mysqlPw;
        
        string mysqlPortSystemMonitoring;
        string mysqlDbSystemMonitoring;
        string mysqlUserSystemMonitoring;
        string mysqlPwSystemMonitoring;
        
        char mem_typename[20];
        unsigned int mem_typvalue;
        
        unsigned int mem_total;
        unsigned int mem_free;
        unsigned int mem_buffers;
        unsigned int mem_cached;
        unsigned int mem_used;
        
        char cpu_usage1str[20], cpu_usage5str[20], cpu_usage15str[20];
        
        float cpu_usage1;
        float cpu_usage5;
        float cpu_usage15;
};

#endif
