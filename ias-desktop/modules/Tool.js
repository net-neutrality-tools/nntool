/*!
    \file Tool.js
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

/* global __dirname, process */

var network         = require('network');
var os              = require('os');
var async           = require('async');
var exec            = require('child_process').exec;
var os_functions    = require(__dirname + '/modules/' + process.platform + '.js');
var ping            = require('ping');
var dns             = require('dns');
var fetch           = require('isomorphic-fetch');
var jsTool          = new JSTool();

var systemUsageUpdateInterval   = 500;
var systemUsageCheckInterval    = 400;
var systemUsageUpdateTimer;
var systemUsageRawData          = [];
var cpuUsageCheckTimer;
var cpuLoadSum                  = 0.0;
var cpuLoadCount                = 0;
var cpuLoadCurrent              = 0.0;
var cpuLoadAvg                  = 0.0;
var cpuLoadMax                  = 0.0;
var cpuLoadMaxCore              = 0.0;
var memUsageCheckTimer;
var memLoadSum                  = 0.0;
var memLoadCount                = 0;
var memLoadCurrent              = 0.0;
var memLoadAvg                  = 0.0;
var memLoadMax                  = 0.0;

var timestampMeasurementStart   = 0;




/*-------------------------class Tool------------------------*/

/**
 * @class Tool
 * @description node.js Tool Class
 */
var Tool = function ()
{
    return(this);
};




/*-------------------------public functions------------------------*/

/**
 * @function getInterfaceConfiguration
 * @description Function to get active Interface Configuration
 * @public
 */
Tool.prototype.getInterfaceConfiguration = function()
{
    return new Promise((success) =>
    {
        network.get_network_interfaces(function(error, interfaces)
        {        
            var interfaceConfiguration = {};
            interfaceConfiguration.error = '-';
            interfaceConfiguration.name = '-';
            interfaceConfiguration.type = '-';
            interfaceConfiguration.description = '-';
            interfaceConfiguration.speed = '-';
            interfaceConfiguration.mac = '-';
            interfaceConfiguration.ip = '-';
            interfaceConfiguration.netmask = '-';
            interfaceConfiguration.gateway = '-';
            interfaceConfiguration.mtu = 0;
            interfaceConfiguration.ipv4Primary = false;
            interfaceConfiguration.ipv6Primary = false;

            if (!error && typeof interfaces !== 'undefined')
            {      
                var interfacePrimary;

                for (var index in interfaces)
                {
                    if (interfaces[index].ipv6Primary)
                    {
                        interfacePrimary = interfaces[index];
                        break;
                    }
                    else if (interfaces[index].ipv4Primary)
                    {
                        interfacePrimary = interfaces[index];
                    }
                }
				
				if (typeof interfacePrimary !== 'undefined')
				{
					if (typeof interfacePrimary.name !== 'undefined')                    interfaceConfiguration.name         = interfacePrimary.name;
					if (typeof interfacePrimary.type !== 'undefined')                    interfaceConfiguration.type         = interfacePrimary.type;
					if (typeof interfacePrimary.description !== 'undefined')             interfaceConfiguration.description  = interfacePrimary.description;
					if (typeof interfacePrimary.speed !== 'undefined')                   interfaceConfiguration.speed        = interfacePrimary.speed;
					if (typeof interfacePrimary.mac_address !== 'undefined')             interfaceConfiguration.mac          = interfacePrimary.mac_address;
					if (typeof interfacePrimary.ipv4_address !== 'undefined')            interfaceConfiguration.ip           = interfacePrimary.ipv4_address;
					if (typeof interfacePrimary.ipv4_netmask !== 'undefined')            interfaceConfiguration.netmask      = interfacePrimary.ipv4_netmask;
					if (typeof interfacePrimary.ipv4_gateway_address !== 'undefined')    interfaceConfiguration.gateway      = interfacePrimary.ipv4_gateway_address;
					if (typeof interfacePrimary.mtu !== 'undefined')                     interfaceConfiguration.mtu          = interfacePrimary.mtu;
					if (typeof interfacePrimary.ipv4Primary !== 'undefined')             interfaceConfiguration.ipv4Primary  = interfacePrimary.ipv4Primary;
					if (typeof interfacePrimary.ipv6Primary !== 'undefined')             interfaceConfiguration.ipv6Primary  = interfacePrimary.ipv6Primary;
				}
            }
            else
            {
                interfaceConfiguration.error = error;
            }
            
            success(JSON.stringify(interfaceConfiguration));
        });
    });
};

/**
 * @function getDeviceKPIs
 * @description Function to collect Device KPIs
 * @public
 */
Tool.prototype.getDeviceKPIs = function ()
{
    return new Promise((success) =>
    {
        var deviceKPIs = {};
        deviceKPIs.dsk_os_version       = '-';
        deviceKPIs.dsk_os_architecture  = '-';
        deviceKPIs.dsk_cpu_type         = '-';
        deviceKPIs.dsk_cpu_cores        = '-';
        deviceKPIs.dsk_cpu_speed        = '-';
        deviceKPIs.dsk_mem_total        = '-';

        if (typeof deviceKPIs.dsk_os_version !== 'undefined') deviceKPIs.dsk_os_version = os.release();

        deviceKPIs.dsk_os_architecture  = os.arch();

        var cpus = os.cpus();
        deviceKPIs.dsk_cpu_type         = cpus[0].model;
        deviceKPIs.dsk_cpu_cores        = cpus.length;
        deviceKPIs.dsk_cpu_speed        = cpus[0].speed;

        deviceKPIs.dsk_mem_total        = Math.round(os.totalmem() / 1024 / 1024 / 1024);

        console.log('dsk device kpis:');
        console.log('dsk os version:        ' + deviceKPIs.dsk_os_version);
        console.log('dsk os architecture:   ' + deviceKPIs.dsk_os_architecture);
        console.log('dsk cpu type:          ' + deviceKPIs.dsk_cpu_type);
        console.log('dsk cpu cores:         ' + deviceKPIs.dsk_cpu_cores);
        console.log('dsk cpu speed:         ' + deviceKPIs.dsk_cpu_speed);
        console.log('dsk mem total:         ' + deviceKPIs.dsk_mem_total);

        success(JSON.stringify(deviceKPIs));
    });
};

Tool.prototype.startUpdatingSystemUsage = function (measurementStartTimestamp)
{
    timestampMeasurementStart = measurementStartTimestamp;

    var cpuFirstMeasure = currentCPUUsage();

    cpuUsageCheckTimer = setInterval(function()
    { 
        var cpuSecondMeasure = currentCPUUsage(); 

        //cur
        var idleDifference  = cpuSecondMeasure.idle     - cpuFirstMeasure.idle;
        var totalDifference = cpuSecondMeasure.total    - cpuFirstMeasure.total;
        cpuLoadCurrent = ( 1 - (idleDifference / totalDifference) ).toFixed(10);

        //avg
        cpuLoadSum += 1 - (idleDifference / totalDifference);
        cpuLoadCount ++;
        cpuLoadAvg = (cpuLoadSum / cpuLoadCount).toFixed(10);
        
        //max per core
        var idleDifferenceMax  = cpuSecondMeasure.maxIdle   - cpuFirstMeasure.maxIdle;
        var totalDifferenceMax = cpuSecondMeasure.maxTick   - cpuFirstMeasure.maxTick;
        var max = (1 - (idleDifferenceMax / totalDifferenceMax)).toFixed(10);
        if (max > cpuLoadMaxCore)
        {
            cpuLoadMaxCore = max;
        }

        cpuFirstMeasure = cpuSecondMeasure;
        
        if (cpuLoadAvg > cpuLoadMax)
        {
            cpuLoadMax = cpuLoadAvg;
        }

        /*
        console.log('CPU Usage Current:      ' + cpuLoadCurrent);
        console.log('CPU Usage Avg:          ' + cpuLoadAvg);
        console.log('CPU Usage Avg Max:      ' + cpuLoadMax);
        console.log('CPU Usage Avg Max Core: ' + cpuLoadMaxCore);
        */
        
    }, systemUsageCheckInterval);
    
    memUsageCheckTimer = setInterval(function()
    { 
        memLoadCurrent = ( 1 - (os.freemem() / os.totalmem()) ).toFixed(10);
        memLoadSum += 1 - (os.freemem() / os.totalmem());
        memLoadCount ++;
        memLoadAvg = (memLoadSum / memLoadCount).toFixed(10);

        if (memLoadAvg > memLoadMax)
        {
            memLoadMax = memLoadAvg;
        }
    }, systemUsageCheckInterval);
    
    systemUsageUpdateTimer = setInterval(function()
    {
        var data = {};

        var raw_data = {};
        raw_data.dsk_cpu_load_current = parseFloat(cpuLoadCurrent).toFixed(4);
        raw_data.dsk_mem_load_current = parseFloat(memLoadCurrent).toFixed(4);
        raw_data.relative_time_ns_measurement_start = (jsTool.getTimestamp() * 1000 * 1000 ) - timestampMeasurementStart;
        systemUsageRawData.push(raw_data);
        
        data.system_usage_raw_data        = systemUsageRawData;
        data.dsk_cpu_load_avg             = Number(cpuLoadAvg).toFixed(4);
        data.dsk_cpu_load_avg_max         = Number(cpuLoadMax).toFixed(4);
        data.dsk_cpu_load_avg_max_core    = Number(cpuLoadMaxCore).toFixed(4);
        data.dsk_mem_load_avg             = Number(memLoadAvg).toFixed(4);
        data.dsk_mem_load_avg_max         = Number(memLoadAvg).toFixed(4);
        
        systemUsageCallback(JSON.stringify(data));
        
    }, systemUsageUpdateInterval);
};

Tool.prototype.stopUpdatingSystemUsage = function ()
{
    clearInterval(cpuUsageCheckTimer);
    clearInterval(memUsageCheckTimer);
    
    clearInterval(systemUsageUpdateTimer);
};

Tool.prototype.getLocationKPIs = function ()
{
    var options = {};
    
    options.enableHighAccuracy  = true;
    options.timeout             = 5000;
    options.maximumAge          = 0;
    
    navigator.geolocation.getCurrentPosition(getLocationKPIsCallbackInititator, getLocationKPIsCallbackInititator, options);
};

Tool.prototype.getRouteToTarget = function(target, maxHops, ipType)
{
    var cmd             = '-';
    var ipTypeOption    = '-';
    
    switch (process.platform)
    {
        case 'win32':
        {
            ipTypeOption = '-4';
            if (ipType === '6')
            {
                ipTypeOption = '-6';
            }
            
            cmd = 'tracert ' + ipTypeOption + ' -w 200 -d -h ' + maxHops + ' ' + target;
            
            break;
        }
        
        case 'darwin':
        {
            cmd = 'traceroute';
            if (ipType === '6')
            {
                cmd = 'traceroute6 -l';
            }
            
            cmd += ' -q 1 -p 33434 -m ' + maxHops + ' ' + target;
            
            break;
        }
        
        case 'linux':
        {
            cmd = 'tracepath';
            if (ipType === '6')
            {
                cmd = 'tracepath6';
            }
            
            cmd += ' -p 33434 -b -m ' + maxHops + ' ' + target;
            
            break;
        }
    }

    var route = {};
    asyncExec(cmd, target, 'route');
    
    var data = {};
    data.target     = target;
    data.hops       = '-';
    
    getRouteToTargetCallback(JSON.stringify(data));
};

Tool.prototype.getRttToTarget = function(target, requests)
{
    var targets = [target];
    
    var extra = [];
    
    if (process.platform === 'win32')
    {
        extra = ["-4 -l 64"];
    }
    
    targets.forEach(function(host)
    {
        ping.promise.probe(host,
        {
            timeout: 5,
            min_reply: requests,
            extra: extra
        }).then(function(result)
        {
            var data = {};
            data.avg = 0.0;
            if (typeof result.avg !== 'undefined' && !isBlank(result.avg) && !isEmpty(result.avg))
            {
                if (parseFloat(result.avg) === 0.0)
                {
                    data.avg = 1.0;
                }
                else
                {
                    data.avg = result.avg;
                }
            }
            getRttToTargetCallback(JSON.stringify(data));
        });
    });
};

Tool.prototype.reverseDNSLookup = function(ip)
{
    dns.reverse(ip, function(error, hosts)
    {
        if(error || hosts.size === 0)	
        {
            console.log('Reverse DNS Lookup to IP ' + ip + ' failed');
            reverseDNSLookupCallback(null, error);
        }
        else
        {
            console.log('Host to IP ' + ip + ': ' + hosts[0]);
            reverseDNSLookupCallback(hosts[0], null);
        }
    });
    
};




/*-------------------------private functions------------------------*/

function currentCPUUsage()
{
    var totalIdle = 0, totalTick = 0, maxIdle = 0, maxTick = 0;
    var cpus = os.cpus();

    for (var i = 0, len = cpus.length; i < len; i++)
    {
        var cpu = cpus[i];
        var tick = 0;

        for (var type in cpu.times)
        {
            tick += cpu.times[type];
        }     

        totalTick += tick;
        totalIdle += cpu.times.idle;
        
        if (cpu.times.idle < maxIdle || maxIdle === 0)
        {
            maxTick = tick;
            maxIdle = cpu.times.idle;
        }
    }

    return {idle: totalIdle / cpus.length,  total: totalTick / cpus.length, maxIdle: maxIdle, maxTick: maxTick};
}

function getLocationKPIsCallbackInititator(position)
{
    var location = {};

    location.dsk_accuracy   = 0;
    location.dsk_latitude   = 0;
    location.dsk_longitude  = 0;
    
    if (typeof position.coords !== 'undefined')
    {
        console.log('location kpis:');
        console.log('accuracy:          ' + position.coords.accuracy);
        console.log('latitude:          ' + position.coords.latitude.toFixed(8));
        console.log('longitude:         ' + position.coords.longitude.toFixed(8));
        
        location.dsk_accuracy   = position.coords.accuracy;
        location.dsk_latitude   = position.coords.latitude.toFixed(8);
        location.dsk_longitude  = position.coords.longitude.toFixed(8);
    }
    else
    {
        console.log('error on getLocation: ' + position.code + ': ' + position.message);
    }

    getLocationKPIsCallback(JSON.stringify(location));
}

function asyncExec(cmd, target, test_case)
{
    async.parallel
    ([
        function(cb)
        {
            trimExec(cmd, cb);
        }
    ], 
    function(error, data)
    {
        if (test_case === 'route')
        {
            os_functions.getRouteToTargetCallbackInititator(target, error, data);
        }
    }); 
}
    
function trimExec(cmd, cb)
{
    exec(cmd, function(error, out)
    {
        if (out && out.toString() !== '')
        {
            cb(null, out.toString().trim());
        }
        else
        {
            cb(error);
        }  
    });
}

function isEmpty(string)
{
    return (!string || 0 === string.length);
}

function isBlank(string)
{
    return (!string || /^\s*$/.test(string));
}
