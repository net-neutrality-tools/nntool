/*!
    \file load_monitoring.cpp
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

#include "load_monitoring.h"




CLoadMonitoring::CLoadMonitoring()
{
    overloadedChanged   = false;
    balancer            = false;
}

CLoadMonitoring::~CLoadMonitoring()
{
}

int CLoadMonitoring::run()
{
    TRC_INFO("Start Thread: Load Monitoring with PID " + std::to_string(syscall(SYS_gettid)));

    unsigned long long nBandwidthMax = 1000000000;
    unsigned long long nBandwidthFree = 200000000;

    if (::CONFIG["load"]["monitoring"]["bandwidth"]["bits_max"].int_value() != 0)
    {
        nBandwidthMax = ::CONFIG["load"]["monitoring"]["bandwidth"]["bits_max"].int_value();
    }
    if (::CONFIG["load"]["monitoring"]["bandwidth"]["bits_free"].int_value() != 0)
    {
        nBandwidthFree = ::CONFIG["load"]["monitoring"]["bandwidth"]["bits_free"].int_value();
    }
    
    txBytes_traffic     = 0;
    txPackets_traffic   = 0;
    rxBytes_traffic     = 0;
    rxPackets_traffic   = 0;
    
    string interface = ::CONFIG["load"]["monitoring"]["interface"].string_value();
    
    timestamp_diff = 0;
    timestamp_diff_report = 0;
    timestamp_mem = CTool::get_timestamp();
    
    if (getInterfaceMetrics(interface, true) != 0)
    {
        TRC_CRIT("Load Monitoring: configured interface " + interface + " not found");
        return -1;
    }

    CLoadBalancing *loadBalancing;

    if (::CONFIG["load"]["balancer"]["enabled"].bool_value())
    {
        balancer = true;

        loadBalancing = new CLoadBalancing(&jLoad);

        if (loadBalancing->createThread() != 0)
        {
            TRC_ERR("Error: Failure while creating Load Balancing Thread");
        }
    }

    while(RUNNING)
    {
        timestamp_current = CTool::get_timestamp();
        
        if (getInterfaceMetrics(interface, false) != 0)
        {
            TRC_CRIT("Load Monitoring: configured interface " + interface + "not found");
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
        
        timestamp_diff  = timestamp_current - timestamp_mem;
        timestamp_diff_report += timestamp_diff;

        txBytes_traffic     += txBytes_diff;
        txPackets_traffic   += txPackets_diff;
        rxBytes_traffic     += rxBytes_diff;
        rxPackets_traffic   += rxPackets_diff;

        if( ((nBandwidthMax - ((txBytes_traffic*8)/(timestamp_diff_report * 1e-6))) < nBandwidthFree) || ((nBandwidthMax - ((rxBytes_traffic*8)/(timestamp_diff_report * 1e-6))) < nBandwidthFree) )
        {
            if (!OVERLOADED)
            {
                overloadedChanged = true;
                TRC_WARN("Load Monitoring: OVERLOADED status changed to true");
            }
            OVERLOADED = true;
        }
        else
        {
            if (OVERLOADED)
            {
                overloadedChanged = true;
                TRC_WARN("Load Monitoring: OVERLOADED status changed to false");
            }
            OVERLOADED = false;
        }

        overloadedChanged = false;
        
        txBytes_rate    = txBytes_traffic / (timestamp_diff_report * 1e-6);
        txPackets_rate  = txPackets_traffic / (timestamp_diff_report * 1e-6);
        rxBytes_rate    = rxBytes_traffic / (timestamp_diff_report * 1e-6);
        rxPackets_rate  = rxPackets_traffic / (timestamp_diff_report * 1e-6);

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

        if (balancer)
        {
            setBalancerMetrics();
        }
        
        timestamp_diff_report   = 0;
        txBytes_traffic         = 0;
        txPackets_traffic       = 0;
        rxBytes_traffic         = 0;
        rxPackets_traffic       = 0;

        timestamp_mem   = timestamp_current;
        
        txBytes_mem     = txBytes;
        txPackets_mem   = txPackets;
        rxBytes_mem     = rxBytes;
        rxPackets_mem   = rxPackets;
        
        usleep(1000000);
    }
    //Stop webservice
    if (::CONFIG["load"]["balancer"]["enabled"].bool_value())
    {
        loadBalancing->waitForEnd();
    }
    delete(loadBalancing);

    TRC_DEBUG("End Thread: Load Monitoring with PID " + std::to_string(syscall(SYS_gettid)));

    return 0;
}

int CLoadMonitoring::getInterfaceMetrics(string interface, bool mem)
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

void CLoadMonitoring::setBalancerMetrics()
{
    pthread_mutex_lock(&mutexLoad);
    
        jLoad["timestamp"] = to_string(CTool::get_timestamp_sec());
        jLoad["timezone"] = to_string(CTool::get_timestamp_offset());
        jLoad["overloaded"] = OVERLOADED;

        Json::object jTx;
        jTx["pps"] = to_string(txPackets_rate);
        jTx["bps"] = to_string(txBytes_rate * 8);

        Json::object jRx;
        jRx["pps"] = to_string(rxPackets_rate);
        jRx["bps"] = to_string(rxBytes_rate * 8);

        Json::object jMem;
        jMem["total"] = to_string(mem_total * 1024);
        jMem["free"] = to_string(mem_free * 1024);
        jMem["buffers"] = to_string(mem_buffers * 1024);
        jMem["cached"] = to_string(mem_cached * 1024);

        Json::object jCpu;
        jCpu["1"] = to_string(cpu_usage1);
        jCpu["5"] = to_string(cpu_usage5);
        jCpu["15"] = to_string(cpu_usage15);

        jLoad["tx_rates"] = jTx;
        jLoad["rx_rates"] = jRx;
        jLoad["mem_bytes"] = jMem;
        jLoad["cpu_avg"] = jCpu;

    pthread_mutex_unlock(&mutexLoad);
}
