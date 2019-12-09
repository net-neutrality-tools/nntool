/*!
    \file ias-desktop.js
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

require('electron-cookies');




/*-------------------------class IasDesktop------------------------*/

/**
 * @class IasDesktop
 * @description IAS Desktop class
 */
var IasDesktop = function()
{
    this.version = '1.0.0';

    libraryKPIs = {};
    libraryKPIs.dsk_library_version = this.version;
    
    console.log('IasDesktop: Version ' + libraryKPIs.dsk_library_version);

    return(this);    
};




/*-------------------------Global Variables------------------------*/

var measurementParameters           = {};

var deviceKPIs                      = {};
var locationKPIs                    = {};
var systemUsageKPIs                 = {};
var libraryKPIs                     = {};
var networkKPIs                     = {};
var routeKPIs                       = {};

var ias;

var tool                            = new Tool();
var ipc                             = require('electron').ipcRenderer;

var routeToTargetCalled             = false;
var routeToTargetMaxHops            = 15;
var routeToClientCalled             = false;

var performRouteToClientLookup;
var routeToClientTargetPort;

var rttGatewayRequests              = 10;

var routeToTargetCallbackCalled     = false;
var routeToClientCallbackCalled     = false;
var rttToTargetCallbackCalled       = false;
var callbackWaitTime                = 0;
var callbackTimeout                 = 10000;

var systemUsageStarted              = false;
var systemUsageStopped              = false;

var measurementStartTimestamp       = 0;




/*-------------------------public functions------------------------*/

/**
 * @function measurementStart
 * @description Start Measurement
 * @public
 * @param {string} iasParameters JSON coded measurement Parameters
 */
IasDesktop.prototype.measurementStart = function(iasParameters)
{
    console.log('Measurement started');

    measurementParameters = JSON.parse(iasParameters);

    tool.getInterfaceConfiguration().then((success) =>
    {
        if (typeof networkKPIs !== 'undefined')
        {
            var interface = JSON.parse(success);

            if (typeof interface.type !== 'undefined')
            {
                if (interface.type === 'wired')
                {
                    networkKPIs.dsk_lan_detected = 1;
                }
                else
                {
                    networkKPIs.dsk_lan_detected = 0;
                }
            }

            if (typeof interface.name !== 'undefined')                              networkKPIs.dsk_interface_name          = interface.name;
            if (typeof interface.description !== 'undefined')                       networkKPIs.dsk_interface_description   = interface.description;
            if (typeof interface.speed !== 'undefined' && !isNaN(interface.speed))  networkKPIs.dsk_interface_speed         = interface.speed;
            if (typeof interface.mac !== 'undefined')                               networkKPIs.dsk_interface_mac           = interface.mac;
            if (typeof interface.ip !== 'undefined')                                networkKPIs.dsk_interface_ipv4          = interface.ip;
            if (typeof interface.netmask !== 'undefined')                           networkKPIs.dsk_interface_ipv4_netmask  = interface.netmask;
            if (typeof interface.gateway !== 'undefined')                           networkKPIs.dsk_interface_ipv4_gateway  = interface.gateway;
            if (typeof interface.mtu !== 'undefined' && !isNaN(interface.mtu))      networkKPIs.dsk_interface_mtu           = interface.mtu;
        }
    });
    tool.getDeviceKPIs().then((success) =>
    {
        deviceKPIs = JSON.parse(success);
    });

    if (typeof measurementParameters.getLocation !== 'undefined')
    {
        initializeLocationKPIs();
        tool.getLocationKPIs();
    }

    if (typeof measurementParameters.routeToClientTargetPort !== 'undefined')
    {
        routeToClientTargetPort = measurementParameters.routeToClientTargetPort;
    }
    if (typeof measurementParameters.performRouteToClientLookup !== 'undefined')
    {
        performRouteToClientLookup = measurementParameters.performRouteToClientLookup;
    }

    measurementParameters.wsWorkerPath = "modules/Worker.js";

    delete(measurementParameters.routeToClientTargetPort);
    delete(measurementParameters.performRouteToClientLookup);

    delete ias;
    ias = null;
    ias = new Ias();

    ias.measurementStart(JSON.stringify(measurementParameters));
};

/**
 * @function measurementStop
 * @description Stop Measurement
 * @public
 */
IasDesktop.prototype.measurementStop = function()
{
    if (!systemUsageStopped)
    {
        systemUsageStopped = true;
        tool.stopUpdatingSystemUsage();
    }
    ias.measurementStop();
}
   


/*-------------------------private functions------------------------*/

/**
 * @function iasCallback
 * @description Callback function of ias
 * @private
 * @param {string} data JSON coded measurement results
 */
function iasCallback(data)
{
    data = JSON.parse(data);
    var error_code = data.error_code;
    
    wsCoreKPIs = data;
    
    data.location_info = locationKPIs;
    data.system_usage_info = systemUsageKPIs;
    data = jsTool.extend(data, libraryKPIs);
    data.network_info = networkKPIs;
    data = jsTool.extend(data, deviceKPIs);
    data.route_info = routeKPIs;

    if (!systemUsageStarted && data.cmd === 'info')
    {
        systemUsageStarted = true;

        if (typeof data.time_info !== 'undefined' && typeof data.time_info.measurement_start !== 'undefined')
        {
            measurementStartTimestamp = data.time_info.measurement_start;
        }
        
        tool.startUpdatingSystemUsage(measurementStartTimestamp);
    }
    
    if (data.cmd === 'completed' || data.cmd === 'error')
    {
        if (!systemUsageStopped)
        {
            systemUsageStopped = true;
            tool.stopUpdatingSystemUsage();
        }
        
        if (data.server !== '-' && !routeToTargetCalled && !routeToClientCalled)
        {
            console.log('Measurement completed, starting route client -> server, route server -> client and rttToTarget');
            
            if (measurementParameters.wsTargets[0].indexOf('ipv6') !== -1)
            {
                measurementParameters.ndServerFamily = 6;
            }
            else if (measurementParameters.wsTargets[0].indexOf('ipv4') !== -1)
            {
                measurementParameters.ndServerFamily = 4;
            }

            routeToTargetCalled = true;
            tool.getRouteToTarget(measurementParameters.wsTargets[Math.floor(Math.random() * measurementParameters.wsTargets.length)] + '.' + measurementParameters.wsTLD, routeToTargetMaxHops, measurementParameters.ndServerFamily);
            
            routeToClientCalled = true;
            if (typeof performRouteToClientLookup !== 'undefined' && performRouteToClientLookup)
            {
                jsTool.performRouteToClientLookup(measurementParameters.wsTargets[Math.floor(Math.random() * measurementParameters.wsTargets.length)] + '.' + measurementParameters.wsTLD, routeToClientTargetPort);
            }
            else
            {
                routeToClientCallbackCalled = true;
            }

            if (typeof networkKPIs.dsk_interface_ipv4_gateway !== 'undefined' && networkKPIs.dsk_interface_ipv4_gateway !== '-')
            {
                tool.getRttToTarget(networkKPIs.dsk_interface_ipv4_gateway, rttGatewayRequests);
            }
            else
            {
                rttToTargetCallbackCalled   = true;
            }
            
            callbackWaitTime += 2000;
            setTimeout(iasCallback, 2000, JSON.stringify(data));
            return;
        }

        //wait for completion
        if ((!rttToTargetCallbackCalled || !routeToTargetCallbackCalled || !routeToClientCallbackCalled) && callbackWaitTime < callbackTimeout)
        {
            console.log('Measurement completed, waiting for completion of route client -> server, route server -> client and rttToTarget');
            console.log('rtt: ' + rttToTargetCallbackCalled + ' routeToTarget: ' + routeToTargetCallbackCalled + ' routeToClient: ' + routeToClientCallbackCalled);
            callbackWaitTime += 2000;
            setTimeout(iasCallback, 2000, JSON.stringify(data));
            return;
        }
        else 
        if (callbackWaitTime >= callbackTimeout)
        {
            console.log('Callback Timeout Reached');
        }
        
        if (data.cmd === 'error')
        {
            console.log('Measurement failed with Error: ' + error_code);
        }
        else
        {
            console.log('Measurement successful');
        }
        
        iasDidCompleteWithResponse(JSON.stringify(data), error_code);
    }
    else
    {
        iasCallbackWithResponse(JSON.stringify(data));
    }
};

function systemUsageCallback(data)
{
    var systemUsage = JSON.parse(data);
    
    if (typeof systemUsage.system_usage_raw_data !== 'undefined')                                                                                                       systemUsageKPIs.system_usage_raw_data       = systemUsage.system_usage_raw_data;
    if (typeof systemUsage.dsk_cpu_load_avg !== 'undefined' && !isNaN(systemUsage.dsk_cpu_load_avg) && systemUsage.dsk_cpu_load_avg <= 1)                               systemUsageKPIs.dsk_cpu_load_avg            = systemUsage.dsk_cpu_load_avg;
    if (typeof systemUsage.dsk_cpu_load_avg_max !== 'undefined' && !isNaN(systemUsage.dsk_cpu_load_avg_max) && systemUsage.dsk_cpu_load_avg_max <= 1)                   systemUsageKPIs.dsk_cpu_load_avg_max        = systemUsage.dsk_cpu_load_avg_max;
    if (typeof systemUsage.dsk_cpu_load_avg_max_core !== 'undefined' && !isNaN(systemUsage.dsk_cpu_load_avg_max_core) && systemUsage.dsk_cpu_load_avg_max_core <= 1)    systemUsageKPIs.dsk_cpu_load_avg_max_core   = systemUsage.dsk_cpu_load_avg_max_core;
    if (typeof systemUsage.dsk_mem_load_avg !== 'undefined' && !isNaN(systemUsage.dsk_mem_load_avg) && systemUsage.dsk_mem_load_avg <= 1)                               systemUsageKPIs.dsk_mem_load_avg            = systemUsage.dsk_mem_load_avg;
    if (typeof systemUsage.dsk_mem_load_avg_max !== 'undefined' && !isNaN(systemUsage.dsk_mem_load_avg_max) && systemUsage.dsk_mem_load_avg_max <= 1)                   systemUsageKPIs.dsk_mem_load_avg_max        = systemUsage.dsk_mem_load_avg_max;
};

function getLocationKPIsCallback(data)
{
    var location = JSON.parse(data);
    
    if (typeof location.dsk_accuracy !== 'undefined')   locationKPIs.dsk_accuracy   = location.dsk_accuracy;
    if (typeof location.dsk_latitude !== 'undefined')   locationKPIs.dsk_latitude   = location.dsk_latitude;
    if (typeof location.dsk_longitude !== 'undefined')  locationKPIs.dsk_longitude  = location.dsk_longitude;
}

function getRouteToTargetCallback(data)
{
    var routeToTarget = JSON.parse(data);
    
    if (typeof routeToTarget.hops !== 'undefined' && routeToTarget.hops !== '-' && routeToTarget.hops.length !== 0)
    {
        routeKPIs.dsk_client_server_route_hops    = Number(routeToTarget.hops[routeToTarget.hops.length-1].id);
        routeKPIs.dsk_client_server_route         = routeToTarget.hops;
        
        routeToTargetCallbackCalled                 = true;
    }
}

function getRouteToClientCallback(data)
{
    data                                  = JSON.parse(data);
    routeKPIs.server_client_route_hops    = data.server_client_route_hops;
    routeKPIs.server_client_route         = data.server_client_route;

    routeToClientCallbackCalled           = true;
}

function getRttToTargetCallback(data)
{
    var rttToTarget = JSON.parse(data);
    
    if (typeof rttToTarget.avg !== 'undefined' && !isNaN(rttToTarget.avg))
    {
        networkKPIs.dsk_interface_ipv4_gateway_rtt_avg = rttToTarget.avg;
    }
    
    rttToTargetCallbackCalled = true;
}
