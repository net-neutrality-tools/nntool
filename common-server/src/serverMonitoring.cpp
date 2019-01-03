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

#include "serverMonitoring.h"




CServerMonitoring::~CServerMonitoring()
{
}

CServerMonitoring::CServerMonitoring()
{
    overloadedChanged   = false;
    
    mysqlHost   = "localhost";
    mysqlPort   = "33061";
    mysqlDb     = "server-monitoring";
    mysqlUser   = "";
    mysqlPw     = "";
    
    mysqlPortSystemMonitoring   = "3306";
    mysqlDbSystemMonitoring     = "system-monitoring";
    mysqlUserSystemMonitoring   = "";
    mysqlPwSystemMonitoring     = "";
}

int CServerMonitoring::run()
{
    TRC_INFO("Start Thread: Server Monitoring with PID: " + std::to_string(syscall(SYS_gettid)));

    unsigned long long nBandwidthMax = 125000000;
    unsigned long long nBandwidthFree = 25000000;
    
    txBytes_traffic     = 0;
    txPackets_traffic   = 0;
    rxBytes_traffic     = 0;
    rxPackets_traffic   = 0;
    
    string interface = "eth1";
    
    timestamp_diff = 0;
    timestamp_diff_report = 0;
    timestamp_mem = CTool::get_timestamp();
    
    if (getInterfaceMetrics(interface, true) != 0)
    {
        TRC_CRIT("Server Monitoring: configured interface " + interface + "not found");
        return -1;
    }
    sleep(5);

    while(RUNNING)
    {
        timestamp_current = CTool::get_timestamp();
        
        if (getInterfaceMetrics(interface, false) != 0)
        {
            TRC_CRIT("Server Monitoring: configured interface " + interface + "not found");
            return -1;
        }
        
        txBytes_rate    = 0;
        txPackets_rate  = 0;
        rxBytes_rate    = 0;
        rxPackets_rate  = 0;
        
        txBytes_diff    = txBytes - txBytes_mem;
        txPackets_diff  = txPackets - txPackets_mem;
        rxBytes_diff    = rxBytes - rxBytes_mem;
        rxPackets_diff  = rxPackets - rxPackets_mem;
        
        timestamp_diff = timestamp_current - timestamp_mem;
        timestamp_diff_report += timestamp_diff;

        txBytes_traffic     += txBytes_diff;
        txPackets_traffic   += txPackets_diff;
        rxBytes_traffic     += rxBytes_diff;
        rxPackets_traffic   += rxPackets_diff;
        
        if((nBandwidthMax - (txBytes_traffic/(timestamp_diff_report * 1e-6))) < nBandwidthFree)
        {
            if (!OVERLOADED)
            {
                overloadedChanged = true;
                TRC_WARN("Server Monitoring: OVERLOADED status changed to true");
            }
            OVERLOADED = true;
        }
        else
        {
            if (OVERLOADED)
            {
                overloadedChanged = true;
                TRC_WARN("Server Monitoring: OVERLOADED status changed to false");
            }
            OVERLOADED = false;
        }

        if (timestamp_diff_report * 1e-6 > 5 || overloadedChanged)
        {
            overloadedChanged = false;
            
            txBytes_rate    = txBytes_traffic / (timestamp_diff_report * 1e-6);
            txPackets_rate  = txPackets_traffic / (timestamp_diff_report * 1e-6);
            rxBytes_rate    = rxBytes_traffic / (timestamp_diff_report * 1e-6);
            rxPackets_rate  = rxPackets_traffic / (timestamp_diff_report * 1e-6);

            mysqlServerMonitoring();
            
            //---------- Memory Infos ------------------------//
            if ((file = fopen("/proc/meminfo", "r")) != NULL)
            {
                while (fgets(line, sizeof(line), file))
                {
                    if (sscanf(line, MEM_SCAN_PATTERN, mem_typename, &mem_typvalue))
                    {
                        if (strcmp(mem_typename, "MemTotal")==0)
                        {
                            mem_total   = mem_typvalue;
                        }
                        else if (strcmp(mem_typename, "MemFree")==0)
                        {
                            mem_free    = mem_typvalue;
                        }
                        else if (strcmp(mem_typename, "Buffers")==0)
                        {
                            mem_buffers = mem_typvalue;
                        }
                        else if (strcmp(mem_typename, "Cached")==0)
                        {
                            mem_cached  = mem_typvalue;
                        }
                    }
                }
                fclose(file);
            }
            
            //----------- CPU Info AVG --------------------------------//
            if ((file = fopen("/proc/loadavg", "r")) != NULL)
            {
                fscanf(file, "%s %s %s", cpu_usage1str, cpu_usage5str, cpu_usage15str );
                fclose(file);
            }
            cpu_usage1   = CTool::toFloat(cpu_usage1str);
            cpu_usage5   = CTool::toFloat(cpu_usage5str);
            cpu_usage15  = CTool::toFloat(cpu_usage15str);

            mysqlSystemMonitoring();
            
            timestamp_diff_report = 0;
            txBytes_traffic     = 0;
            txPackets_traffic   = 0;
            rxBytes_traffic     = 0;
            rxPackets_traffic   = 0;
        }

        timestamp_mem   = timestamp_current;
        
        txBytes_mem     = txBytes;
        txPackets_mem   = txPackets;
        rxBytes_mem     = rxBytes;
        rxPackets_mem   = rxPackets;
        
        sleep(1);
    }

    TRC_DEBUG("End Thread: Server Monitoring with PID: " + std::to_string(syscall(SYS_gettid)));

    return 0;
}

int CServerMonitoring::getInterfaceMetrics(string interface, bool mem)
{    
    char ifname[16];
    int interfaceFound = -1;
    
    if((file = fopen("/proc/net/dev", "r")) != NULL)
    {
        while(fgets(line, sizeof(line), file))
        {
            if(strchr(line, '|'))
            {
                continue;
            }

            if (!mem)
            {
                if(sscanf(line, IF_SCAN_PATTERN, ifname, &rxBytes, &rxPackets, &txBytes, &txPackets))
                {
                    if(strcmp(ifname, interface.c_str()) == 0)
                    {
                        interfaceFound = 0;
                        break;
                    }
                }
            }
            else
            {
                if(sscanf(line, IF_SCAN_PATTERN, ifname, &rxBytes_mem, &rxPackets_mem, &txBytes_mem, &txPackets_mem))
                {
                    if(strcmp(ifname, interface.c_str()) == 0)
                    {
                        interfaceFound = 0;
                        break;
                    }
                }
            }
            
        }

        fclose(file);
    }
    
    return interfaceFound;
}


void CServerMonitoring::mysqlServerMonitoring()
{
    try
    {
        sql::Driver *driver;
        sql::Connection *con;
        sql::PreparedStatement *pstmt;
    
        unsigned long long timestamp = CTool::get_timestamp_sec();
         
        driver = get_driver_instance();
        con = driver->connect("tcp://" + mysqlHost + ":" + mysqlPort, mysqlUser, mysqlPw);
        con->setSchema(mysqlDb);

        pstmt = con->prepareStatement("INSERT INTO server_monitoring(timestamp, location, hostname, tx_bytes_rate, overloaded) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE timestamp=?, location=?, hostname=?, tx_bytes_rate=?, overloaded=?");
        pstmt->setBigInt(1, to_string(timestamp));
        pstmt->setString(2, "dev");
        pstmt->setString(3, "dev");
        pstmt->setString(4, to_string(txBytes_rate));
        pstmt->setString(5, to_string(OVERLOADED));
        pstmt->setBigInt(6, to_string(timestamp));
        pstmt->setString(7, "dev");
        pstmt->setString(8, "dev");
        pstmt->setString(9, to_string(txBytes_rate));
        pstmt->setString(10, to_string(OVERLOADED));
        pstmt->executeUpdate();
        
        delete pstmt;
        delete con;
        driver->threadEnd();
    }
    catch (sql::SQLException &e)
    {
        TRC_ERR("Server Monitoring: mysql error: " + to_string(e.getErrorCode()));
    }
}


void CServerMonitoring::mysqlSystemMonitoring()
{
    try
    {
        sql::Driver *driver;
        sql::Connection *con;
        sql::PreparedStatement *pstmt;
    
        driver = get_driver_instance();
        con = driver->connect("tcp://" + mysqlHost + ":" + mysqlPortSystemMonitoring, mysqlUserSystemMonitoring, mysqlPwSystemMonitoring);
        con->setSchema(mysqlDbSystemMonitoring);

        pstmt = con->prepareStatement("INSERT INTO system_monitoring_data(location, time, timezone, txPackets_rate, txBytes_rate, rxPackets_rate, rxBytes_rate, diffPitch, mem_total, mem_free, mem_buffers, mem_cached, cpu_avg1, cpu_avg5, cpu_avg15, system_availability, error_code, error_description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        pstmt->setString(1, "dev");
        pstmt->setBigInt(2, to_string(CTool::get_timestamp_sec()));
        pstmt->setBigInt(3, to_string(CTool::get_timestamp_offset()));
        pstmt->setString(4, to_string(txPackets_rate));
        pstmt->setString(5, to_string(txBytes_rate));
        pstmt->setString(6, to_string(rxPackets_rate));
        pstmt->setString(7, to_string(rxBytes_rate));
        pstmt->setDouble(8, timestamp_diff_report * 1e-6);
        pstmt->setString(9, to_string(mem_total));
        pstmt->setString(10, to_string(mem_free));
        pstmt->setString(11, to_string(mem_buffers));
        pstmt->setString(12, to_string(mem_cached));
        pstmt->setString(13, to_string(cpu_usage1));
        pstmt->setString(14, to_string(cpu_usage5));
        pstmt->setString(15, to_string(cpu_usage15));
        
        if (OVERLOADED)
        {
            pstmt->setString(16, "0");
            pstmt->setString(17, "1");
            pstmt->setString(18, "System Overloaded");
        }
        else
        {
            pstmt->setString(16, "1");
            pstmt->setString(17, "0");
            pstmt->setString(18, "-");
        }

        pstmt->executeUpdate();
        
        delete pstmt;
        delete con;
        driver->threadEnd();
    }
    catch (sql::SQLException &e)
    {
        TRC_ERR("System Monitoring: mysql error: " + to_string(e.getErrorCode()));
    }
}