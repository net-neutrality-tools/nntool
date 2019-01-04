/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2018 - 2019                                                   *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2019-01-04
 *      \note Copyright (c) 2018 - 2019 zafaco GmbH. All rights reserved.
 */

/* global jsTool, jsinterface, getRouteToClientCallback */

var logReports  = true;
var logDebug    = true;




/*-------------------------class JSTool------------------------*/

/**
 * @class JSTool
 * @description Javascript Tool Class
 */
var JSTool = function ()
{ 
    //reference to calling wsMeasurement
    this.wsMeasurement = '';
    
    this.localIPs   = '-';

    return(this);
};

var htPostTool;




/*-------------------------helper------------------------*/

/**
 * @function getDeviceKPIs
 * @description Function to collect Device KPIs
 * @public
 * @param {string} platform Used platform
 */
JSTool.prototype.getDeviceKPIs = function (platform)
{
    var jsClientOS;
    var jsClientOSVersion;
    var jsClientBrowser;
    var jsClientBrowserVersion;

    if (platform !== 'mobile')
    {
        var browserReport                                           = this.getBrowserReport();
        if (browserReport.browser.name)     jsClientBrowser         = browserReport.browser.name + ' ' + browserReport.browser.version;
        if (browserReport.browser.version)  jsClientBrowserVersion  = browserReport.browser.version;
        if (browserReport.os.name)          jsClientOS              = browserReport.os.name;
        if (browserReport.os.version)       jsClientOSVersion       = browserReport.os.version;
    }
    else
    {
        if (typeof require !== 'undefined')
        {
            var nsPlatform          = require('platform');
            var jsClientOS          = nsPlatform.device.os;
            var jsClientOSVersion   = nsPlatform.device.osVersion;
        }
    }

    var deviceKPIs = 
    {
		browser_info:
		{
			name:       jsClientBrowser,
			version:	jsClientBrowserVersion
		},
		os_info:
		{
			name:       jsClientOS,
        	version:	jsClientOSVersion
		}

    };

    console.log('device kpis:');
    console.log('browser name:			' + jsClientBrowser);
	console.log('browser version:		' + jsClientBrowserVersion);
	console.log('os name:			' + jsClientOS);
	console.log('version name:			' + jsClientOSVersion);
    
    return JSON.stringify(deviceKPIs);
};

/**
 * @function generateRandomData
 * @description Function to generate Random ASCII Data
 * @public
 * @param {int} length Random Data Length
 * @param {bool} asString Indicates if the callback should be a string
 */
JSTool.prototype.generateRandomData = function (length, asString)
{
    var mask = '';
    mask += 'abcdefghijklmnopqrstuvwxyz';
    mask += 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    mask += '0123456789';
    mask += '~`!@#$%^&*()_+-={}[]:;?,./|\\';
    var data = '';
    for (var i = length; i > 0; --i)
    {
        data += mask[Math.floor(Math.random() * mask.length)];
    }
    if (asString) return data;
    else return new Blob([data]);
};

/**
 * @function getIPAddressType
 * @description Function to get the IP Address Type (IPv4/IPv6)
 * @public
 * @param {string} ip IP Address
 */
JSTool.prototype.getIPAddressType = function (ip)
{ 
    if (/^\s*((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))\s*$/g.test(ip))
    {
        return 'IPv4';
    }
    
    if (/^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/g.test(ip))
    {
        return 'IPv6';
    }
    
    return false;
};

/**
 * @function getTimestamp
 * @description Function to get the current Timestamp
 * @public
 */
JSTool.prototype.getTimestamp = function()
{
    if (!Date.now)
    {
        Date.now = function()
        { 
            return new Date().getTime(); 
        };
    }
    
    return Date.now();
};

/**
 * @function getFormattedDate
 * @description Function to get the current Timestamp as formatted Date
 * @public
 */
JSTool.prototype.getFormattedDate = function ()
{
    var date = new Date();
    var formattedDate;
    formattedDate = date.getFullYear() + '-' + ('0' + (date.getMonth() + 1)).slice(-2) + '-' + ('0' + date.getDate()).slice(-2) + ' ' + ('0' + date.getHours()).slice(-2) + ':' + ('0' + date.getMinutes()).slice(-2) + ':' + ('0' + date.getSeconds()).slice(-2); 
    
    return formattedDate;
};

/**
 * @function getTimezone
 * @description Function to get the current local timezone
 * @public
 */
JSTool.prototype.getTimezone = function ()
{
    var date = new Date();
    var offset = date.getTimezoneOffset();
    return offset *= -60;
};

/**
 * @function webSocketCloseReasons
 * @description Function to get the WebSocket Close Reason
 * @public
 * @param {string} event WebSocket close event
 */
JSTool.prototype.webSocketCloseReasons = function (event)
{
    switch (event.code)
    {
        case 1000: 
            return 'Normal closure, meaning that the purpose for which the connection was established has been fulfilled.';
            break;
        case 1001: 
            return 'An endpoint is \"going away\", such as a server going down or a browser having navigated away from a page.';
            break;
        case 1002: 
            return 'An endpoint is terminating the connection due to a protocol error';
            break;
        case 1003:
            return 'An endpoint is terminating the connection because it has received a type of data it cannot accept (e.g., an endpoint that understands only text data MAY send this if it receives a binary message).';
            break;
        case 1004:
            return 'Reserved. The specific meaning might be defined in the future.';
            break;
        case 1005:
            return 'No status code was actually present.';
            break;
        case 1006:
            return 'The connection was closed abnormally, e.g., without sending or receiving a Close control frame';
            break;
        case 1007:
            return 'An endpoint is terminating the connection because it has received data within a message that was not consistent with the type of the message (e.g., non-UTF-8 [http://tools.ietf.org/html/rfc3629] data within a text message).';
            break;
        case 1008:
            return  'An endpoint is terminating the connection because it has received a message that \'violates its policy\'. This reason is given either if there is no other sutible reason, or if there is a need to hide specific details about the policy.';
            break;
        case 1009:
            return 'An endpoint is terminating the connection because it has received a message that is too big for it to process.';
            break;
        case 1010:
            return 'An endpoint (client) is terminating the connection because it has expected the server to negotiate one or more extension, but the server didn\'t return them in the response message of the WebSocket handshake. <br /> Specifically, the extensions that are needed are: ' + event.reason;
            break;
        case 1011:
            return 'A server is terminating the connection because it encountered an unexpected condition that prevented it from fulfilling the request.';
            break;
        case 1015:
            return 'The connection was closed due to a failure to perform a TLS handshake (e.g., the server certificate can\'t be verified).';
            break;    
        default:
            return 'Unknown reason';
            break;
    }
};

/**
 * @function getBrowserReport
 * @description Function to get browser information
 * @public
 */
JSTool.prototype.getBrowserReport = function()
{
    return browserReportSync();
};

/**
 * @function setCookie
 * @description Function to set a cookie
 * @public
 * @param {string} key Cookie Key
 * @param {string} value Cookie Value
 * @param {int} daysValid Lifetime in Days
 */
JSTool.prototype.setCookie = function(key, value, daysValid)
{
    var expiresString = '';
    if (typeof require === 'undefined')
    {
        var date = new Date();
        date.setTime(date.getTime() + (daysValid * 24 * 60 * 60 * 1000));
        expiresString = 'expires=' + date.toUTCString();
    }

    document.cookie = key + '=' + value + '; ' + expiresString;
};

/**
 * @function getCookie
 * @description Function to get a cookie
 * @public
 * @param {string} key Cookie Key
 */
JSTool.prototype.getCookie = function(key)
{
    var name = key + '=';
    var cookieString = document.cookie.split(';');
    for(var i = 0; i <cookieString.length; i++) {
        var c = cookieString[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length,c.length);
        }
    }
    return false;
};

/**
 * @function deleteCookie
 * @description Function to delete a cookie
 * @public
 * @param {string} key Cookie Key
 */
JSTool.prototype.deleteCookie = function(key)
{
    document.cookie = key +'=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
};

/**
 * @function extend
 * @description Function to merge two or more objects
 * @public
 * @params {object} objects to merge
 */
JSTool.prototype.extend = function ()
{
    var extended = {};
    var length = arguments.length;

    var merge = function (obj)
    {
        for (var prop in obj)
        {
            if (Object.prototype.hasOwnProperty.call(obj, prop))
            {
                extended[prop] = obj[prop];
            }
        }
    };

    for (var i=0; i < length; i++)
    {
        var obj = arguments[i];
        merge(obj);
    }

    return extended;
};

JSTool.prototype.performRouteToClientLookup = function(target, port)
{
    htPostTool = null;
    htPostTool = new HTPost();
    htPostTool.setURL('http://' + target + ':' + port);
    htPostTool.setValues(JSON.stringify({cmd:'traceroute'}));
    htPostTool.setContentType('application/json');
    htPostTool.setTimeout(15000);
    htPostTool.setMaxTries(1);
    htPostTool.executeRequest();
};

JSTool.prototype.isEmpty = function(obj)
{
	for (var key in obj)
	{
		if (obj.hasOwnProperty(key))
		{
			return false;
		}
	}
	return true;
};




/*-------------------------private function------------------------*/

function htPostCallbackTool(data)
{
    try
    {
       data = JSON.parse(data); 
    }
    catch(e)
    {
        return;
    }
    
    if (typeof data.hops !== 'undefined')
    {
        reportRouteToClientToMeasurement(data);
    }
}

function reportRouteToClientToMeasurement(data)
{
    var report            = {};
    report.cmd            = 'report';
    report.msg            = '';
    report.test_case      = 'routeToClient';

    try
    {
        data.hops.splice(data.hops.length-2, 2);
    }
    catch(e)
    {
        data.hops = [];
    }

    try
    {
        report.server_client_route_hops = Number(data.hops[data.hops.length-1].id);
    }
    catch (error)
    {
        report.server_client_route_hops = 0;
    }
    
    report.server_client_route      = JSON.stringify(data.hops);

    if (typeof getRouteToClientCallback !== 'undefined')
    {
        getRouteToClientCallback(JSON.stringify(report));
    }
    else if (typeof this.wsMeasurement !== 'undefined')
    {
        this.wsMeasurement.controlCallback(JSON.stringify(report));
    }
}