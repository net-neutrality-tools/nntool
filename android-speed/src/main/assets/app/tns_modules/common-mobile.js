/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2018                                                          *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2018-12-14
 *      \note Copyright (c) 2018 zafaco GmbH. All rights reserved.
 */

/* global global require NSNotificationCenter NSDictionary */

require('globals');
require('nativescript-websockets');

var utils = require('utils/utils');
var application = require('application');
var platform = require('platform');
var now = require('performance-now');

var performance = {now: now.bind()};

global.wsMeasurement;




/*-----------------------------------------------------------------------------*/
/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2018                                                          *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2018-12-14
 *      \note Copyright (c) 2018 zafaco GmbH. All rights reserved.
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
    var jsDate;
    var jsTimezone;
    
    var jsClientOS;
    var jsClientOSVersion;
    var jsClientBrowser;
    var jsClientBrowserVersion;
    
    jsDate                                                  = this.getFormattedDate();
    jsTimezone                                              = this.getTimezone();

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
        date:                   jsDate,
        timezone:               jsTimezone,
        client_browser:         jsClientBrowser,
        client_browser_version: jsClientBrowserVersion,
        client_os:              jsClientOS,
        client_os_version:      jsClientOSVersion
    };

    console.log('device kpis:');
    console.log('date:                  ' + deviceKPIs.date);
    console.log('timezone:              ' + deviceKPIs.timezone);
    console.log('client browser:        ' + deviceKPIs.client_browser);
    console.log('client os:             ' + deviceKPIs.client_os);
    console.log('client os version:     ' + deviceKPIs.client_os_version);
    
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
    mask += '~`!@#$%^&*()_+-={}[]:;<>?,./|\\';
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
}/*
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
 *      \date Last update: 2019-01-02
 *      \note Copyright (c) 2018 - 2019 zafaco GmbH. All rights reserved.
 */

/* global logDebug, logReports, wsControl */

var jsTool = new JSTool();




/*-------------------------class WSControlSingleThread------------------------*/

/**
 * @class WSControlSingleThread
 * @description WebSocket Control Class using a Single Thread
 */
function WSControlSingleThread()
{
    this.wsMeasurement  = '';
    this.callback       = '';
    
    var wsTestCase;
    var wsData;
    var wsDataTotal;
    var wsFrames;
    var wsFramesTotal;
    var wsSpeedAvgBitS;
    var wsSpeedAvgMBitS;
    var wsSpeedMinBitS;
    var wsSpeedMaxBitS;
    var wsOverhead;
    var wsOverheadTotal;
    var wsStartTime;
    var wsEndTime;
    var wsMeasurementTime;
    var wsMeasurementTimeTotal;
    var wsStartupStartTime;
    var wsOverheadPerFrame;
    var wsWorkers;
    var wsWorkersStatus;
    var wsWorkerTime;
    var wsInterval;
    var wsMeasurementRunningTime;
    var wsCompleted;
    var wsReportInterval;
    var wsTimeoutTimer;
    var wsTimeout;
    var wsStartupTimeout;
    var wsProtocol;
    var wsStreamsStart;
    var wsStreamsEnd;
    var wsFrameSize;
    var wsMeasurementError;
    
    
    var wsStateConnecting   = 0;
    var wsStateOpen         = 1;
    var wsStateClosing      = 2;
    var wsStateClosed       = 3;
    
    var wsAuthToken;
    var wsAuthTimestamp;
    
    
    var rttReportInterval           = 501;
    var rttProtocol                 = 'rtt';
    
   
    var dlReportInterval            = 501;
    var dlWsOverheadPerFrame        = 4;
    var dlData                      = 0;
    var dlFrames                    = 0;
    var dlStartupData               = 0;
    var dlStartupFrames             = 0;
    var dlProtocol                  = 'download';
    
    
    var ulReportInterval            = 501;
    var ulWsOverheadPerFrame        = 8;
    var ulReportDict                = {};
    var ulStartupData               = 0;
    var ulStartupFrames             = 0;
    var ulProtocol                  = 'upload';

	var wsTargets;
    var wsTarget;
    var wsTargetsRtt;
    var wsTargetRtt;
    var wsTLD;
    var wsTargetPort;
    var wsWss;

    //default measurement parameters
    var rttRequests                 = 10;
    var rttRequestTimeout           = 1000;
    var rttRequestWait              = 500;
    var rttTimeout                  = (rttRequests * (rttRequestTimeout + rttRequestWait)) * 1.1;
    var rttPayloadSize              = 64;
    
    var dlStartupTime               = 3000;
    var dlMeasurementRunningTime    = 10000;
    var dlParallelStreams           = 4;
    var dlTimeout                   = 5000;
    
    var ulStartupTime               = 3000;
    var ulMeasurementRunningTime    = 10000;
    var ulParallelStreams           = 4;
    var ulTimeout                   = 5000;
    var ulFrameSize                 = 110000;
    
    var wsParallelStreams;
    var wsStartupTime;




    /*-------------------------KPIs------------------------*/
    
    var wsSystemAvailability;
    var wsServiceAvailability;
    var wsErrorCode;
    var wsErrorDescription;

    var wsRttValues =
    {
        avg:                undefined,
        med:                undefined,
        min:                undefined,
        max:                undefined,
        requests:           undefined,
        replies:            undefined,
        errors:             undefined,
        missing:            undefined,
        packetsize:         undefined,
        stDevPop:           undefined,
        server:             undefined
    };
    
    var wsDownloadValues = 
    {
        rateAvg:          undefined,
        rateMin:          undefined,
        rateMax:          undefined,
        data:             undefined,
        dataTotal:        undefined,
        duration:         undefined,
        durationTotal:    undefined,
        streamsStart:     undefined,
        streamsEnd:       undefined,
        frameSize:        undefined,
        frames:           undefined,
        framesTotal:      undefined,
        overheadPerFrame: undefined,
        overhead:         undefined,
        overheadTotal:    undefined,
        rateAvgMBitS:     undefined
    };
    
    var wsUploadValues = 
    {
        rateAvg:          undefined,
        rateMin:          undefined,
        rateMax:          undefined,
        data:             undefined,
        dataTotal:        undefined,
        duration:         undefined,
        durationTotal:    undefined,
        streamsStart:     undefined,
        streamsEnd:       undefined,
        frameSize:        undefined,
        frames:           undefined,
        framesTotal:      undefined,
        overheadPerFrame: undefined,
        overhead:         undefined,
        overheadTotal:    undefined,
        rateAvgMBitS:     undefined
    };


    
    
    /*-------------------------public functions------------------------*/
    
    /**
     * @function measurementSetup
     * @description Function to Start test cases
     * @public
     * @param {string} measurementParameters JSON coded measurement Parameters
     */
    this.measurementSetup = function(measurementParameters)
    {
        resetValues();
        
        wsMeasurementError      = false;
        measurementParameters   = JSON.parse(measurementParameters);
        wsTestCase              = measurementParameters.testCase;

        switch (wsTestCase)
        {
            case 'rtt':
            {
                if (typeof measurementParameters.wsRttRequests !== 'undefined')             rttRequests             = Number(measurementParameters.wsRttRequests);
                if (typeof measurementParameters.wsRttRequestTimeout !== 'undefined')       rttRequestTimeout       = Number(measurementParameters.wsRttRequestTimeout);
                if (typeof measurementParameters.wsRttRequestWait !== 'undefined')          rttRequestWait          = Number(measurementParameters.wsRttRequestWait);
                if (typeof measurementParameters.wsRttTimeout !== 'undefined')              rttTimeout              = Number(measurementParameters.wsRttTimeout); 
                if (typeof measurementParameters.wsRttPayloadSize !== 'undefined')          rttPayloadSize          = Number(measurementParameters.wsRttPayloadSize);
                wsParallelStreams                                                   = 1;
                wsMeasurementRunningTime                                            = rttTimeout;
                wsReportInterval                                                    = rttReportInterval;
                wsProtocol                                                          = rttProtocol;
                wsTimeout                                                           = dlTimeout;
                break;
            }
            case 'download':
            {
                wsOverheadPerFrame                  = dlWsOverheadPerFrame;
                wsDownloadValues.overheadPerFrame   = dlWsOverheadPerFrame;
                wsParallelStreams                   = dlParallelStreams;
                wsStartupTime                       = dlStartupTime;
                wsMeasurementRunningTime            = dlMeasurementRunningTime;
                wsReportInterval                    = dlReportInterval;
                wsTimeout                           = dlTimeout;
                wsProtocol                          = dlProtocol;
                break;
            }
            case 'upload':
            {
                if (typeof measurementParameters.wsUploadFrameSize !== 'undefined')
                {
                    ulFrameSize             = Number(measurementParameters.wsUploadFrameSize);
                }
                
                wsFrameSize                 = ulFrameSize;
                
                if (ulFrameSize >= 65536)
                {
                    ulWsOverheadPerFrame    = 12;
                }
                else if (ulFrameSize < 126)
                {
                    ulWsOverheadPerFrame    = 6;
                }
                
                wsOverheadPerFrame              = ulWsOverheadPerFrame;
                wsUploadValues.overheadPerFrame = ulWsOverheadPerFrame;
                wsParallelStreams               = ulParallelStreams;
                wsStartupTime                   = ulStartupTime;
                wsMeasurementRunningTime        = ulMeasurementRunningTime;
                wsReportInterval                = ulReportInterval;
                wsTimeout                       = ulTimeout;
                wsProtocol                      = ulProtocol;
                break;
            }
        }
        
        if (typeof measurementParameters.wsTargets !== 'undefined')             wsTargets                   = measurementParameters.wsTargets;
        if (typeof measurementParameters.wsTargetsRtt !== 'undefined')         wsTargetsRtt                = measurementParameters.wsTargetsRtt;   
        if (typeof measurementParameters.wsTLD !== 'undefined')                wsTLD                       = String(measurementParameters.wsTLD);     
        if (typeof measurementParameters.wsTargetPort !== 'undefined')         wsTargetPort                = String(measurementParameters.wsTargetPort);
        if (typeof measurementParameters.wsWss !== 'undefined')                wsWss                       = String(measurementParameters.wsWss);
        if (typeof measurementParameters.wsStartupTime !== 'undefined')        wsStartupTime               = Number(measurementParameters.wsStartupTime);
        if (typeof measurementParameters.wsTimeout !== 'undefined')            wsTimeout                   = Number(measurementParameters.wsTimeout);
        if (typeof measurementParameters.wsParallelStreams  !== 'undefined' && (wsTestCase === 'download' || wsTestCase === 'upload')) wsParallelStreams           = Number(measurementParameters.wsParallelStreams);
        if (typeof measurementParameters.wsMeasureTime      !== 'undefined' && (wsTestCase === 'download' || wsTestCase === 'upload')) wsMeasurementRunningTime    = Number(measurementParameters.wsMeasureTime);
        
        if (typeof measurementParameters.wsWorkerPath !== 'undefined')          wsWorkerPath               = String(measurementParameters.wsWorkerPath);
        
        
        wsAuthToken         = String(measurementParameters.wsAuthToken);
        wsAuthTimestamp     = String(measurementParameters.wsAuthTimestamp);
        
        reportToMeasurement('info', 'starting measurement');
        
        console.log(wsTestCase + ': starting measurement using parameters:');
        var wsWssString                 = 'wss://';
        if (!Number(wsWss)) wsWssString = 'ws://';
        
        if (wsTestCase === 'rtt')
        {
            if (wsTargetsRtt.length > 0)
            {
                wsTargetRtt = wsTargetsRtt[Math.floor(Math.random() * wsTargetsRtt.length)] + '.' + wsTLD;
            }
            console.log('target:            ' + wsWssString + wsTargetRtt + ':' + wsTargetPort);
            console.log('protocol:          ' + rttProtocol);
            console.log('requests:          ' + rttRequests);
            console.log('request timeout:   ' + rttRequestTimeout);
            console.log('request wait:      ' + rttRequestWait);
            console.log('timeout:           ' + rttTimeout);
        }
        
        if (wsTestCase === 'download' || wsTestCase === 'upload')
        {
			if (wsTargets.length > 0)
            {
                wsTarget = wsTargets[Math.floor(Math.random() * wsTargets.length)] + '.' + wsTLD;
            }
            console.log('target:            ' + wsWssString + wsTarget + ':' + wsTargetPort);
            console.log('protocol:          ' + wsProtocol);
            console.log('startup time:      ' + wsStartupTime);
            console.log('measurement time:  ' + wsMeasurementRunningTime);
            console.log('parallel streams:  ' + wsParallelStreams);
            console.log('timeout:           ' + wsTimeout);        
        }
        
        wsWorkers       = new Array(wsParallelStreams);
        wsWorkersStatus = new Array(wsParallelStreams);

        for (var wsID = 0; wsID < wsWorkers.length; wsID++)
        {
            if (wsTestCase === 'upload')
            {
                ulReportDict[wsID]  = {};
            }
            
            var workerData = prepareWorkerData('connect', wsID);
            
            if (wsTestCase === 'download' || wsTestCase === 'upload')
            {
                delete(wsWorkers[wsID]);
                wsWorkers[wsID]                 = new WSWorkerSingleThread();
                wsWorkers[wsID].wsControl       = this;
                wsWorkers[wsID].wsID            = wsID;
                setTimeout(wsWorkers[wsID].onmessageSM, 100,  workerData);
            }
            else
            {
                delete(wsWorkers[wsID]);
                wsWorkers[wsID]                 = new WSWorker();
                wsWorkers[wsID].wsControl       = this;
                setTimeout(wsWorkers[wsID].onmessage, 100,  workerData);
            }
        }
        
        wsTimeoutTimer = setTimeout(measurementTimeout, wsTimeout);  
    };

    /**
     * @function measurementStop
     * @description Function to Stop test cases
     * @public
     * @param {string} data JSON coded measurement Parameters
     */
    this.measurementStop = function(data)
    {
        clearInterval(wsTimeoutTimer);
        clearInterval(wsInterval);
        clearTimeout(wsStartupTimeout);
        
        console.log(wsTestCase + ': stopping measurement');
 
        if (typeof wsWorkers !== 'undefined')
        {
            var workerData = prepareWorkerData('close');
            
            for (var wsID = 0; wsID < wsWorkers.length; wsID++)
            {
                if (wsTestCase === 'download' || wsTestCase === 'upload')
                {
                    wsWorkers[wsID].onmessageSM(workerData);
                }
                else
                {
                    wsWorkers[wsID].onmessage(workerData);
                }
            }
        }
    };

    /**
     * @function workerCallback
     * @description Function to receive callbacks from the WSWorkers
     * @public
     * @param {string} data JSON coded measurement Results
     */
    this.workerCallback = function(data)
    {
        workerCallback(JSON.parse(data));
    };
    
    
    
    
    /*-------------------------private functions------------------------*/
    
    /**
     * @function workerCallback
     * @description Function to receive callbacks from the WSWorkers
     * @private
     * @param {string} data measurement Results
     */
    function workerCallback(data)
    {
        switch (data.cmd)
        {
            case 'info':
            {
                if (typeof wsWorkersStatus !== 'undefined') wsWorkersStatus[data.wsID] = data.wsState;
                if (logDebug)
                {
                    console.log('wsWorker ' + data.wsID + ' command: \'' + data.cmd + '\' message: \'' + data.msg);  
                }
                break;
            }

            case 'open':
            {
                wsWorkersStatus[data.wsID] = data.wsState;
                var allOpen = true;

                for (var wsWorkersStatusID = 0; wsWorkersStatusID < wsWorkersStatus.length; wsWorkersStatusID++)
                {
                    if (wsWorkersStatus[wsWorkersStatusID] !== wsStateOpen)
                    {
                        allOpen = false;
                    }
                }

                if (logDebug)
                {
                    console.log('wsWorker ' + data.wsID + ' websocket open');
                }
                if (allOpen)
                {
                    if (logDebug)
                    {
                        console.log('all websockets open');
                    }
                    
                    if (wsTestCase === 'upload')
                    {
                        for (var wsWorkersStatusID = 0; wsWorkersStatusID < wsWorkersStatus.length; wsWorkersStatusID++)
                        {
                            wsWorkers[wsWorkersStatusID].startUpload();
                        }
                    }

                    clearTimeout(wsTimeoutTimer);

                    if (wsTestCase === 'rtt')
                    {
                        measurementStart(true);
                        break;
                    }

                    if (wsTestCase === 'download')
                    {
                        wsStartupStartTime = performance.now()+500;
                        wsStartupTimeout = setTimeout(measurementStart, wsStartupTime+500);
                        break;
                    }
                    else
                    {
                        wsStartupStartTime = performance.now();
                        wsStartupTimeout = setTimeout(measurementStart, wsStartupTime);
                        break;
                    }
                }
                break;
            }

            case 'close':
            {
                if (typeof wsWorkersStatus !== 'undefined') wsWorkersStatus[data.wsID] = data.wsState;
                break;
            }

            case 'report':
            {
                if (typeof wsWorkersStatus !== 'undefined') wsWorkersStatus[data.wsID] = data.wsState;

                if (wsTestCase === 'rtt')
                {
                    if (data.wsRttValues) wsRttValues = data.wsRttValues;
                    
                    for (key in wsRttValues)
                    {
                        if (typeof wsRttValues[key] === 'undefined' ||  wsRttValues[key] === 'undefined' ||  wsRttValues[key] === null ||  wsRttValues[key] === 'null')
                        {
                            delete wsRttValues[key];
                        }
                    }
                    
                    break;
                }

                if (data.msg === 'startupCompleted')
                {
                    wsFrameSize     = data.wsFrameSize;

                    if (wsTestCase === 'download')
                    {
                        if (wsFrameSize >= 65536)
                        {
                            wsOverheadPerFrame                  = 8;
                            dlWsOverheadPerFrame                = wsOverheadPerFrame;
                            wsDownloadValues.overheadPerFrame   = wsOverheadPerFrame;
                        }
                        else if (wsFrameSize < 126)
                        {
                            wsOverheadPerFrame                  = 2;
                            dlWsOverheadPerFrame                = wsOverheadPerFrame;
                            wsDownloadValues.overheadPerFrame   = wsOverheadPerFrame;
                        }

                        dlStartupData   += data.wsData;
                        dlStartupFrames += data.wsFrames;
                    }

                    if (wsTestCase === 'upload')
                    {
                        ulStartupData           += data.wsData;
                        ulStartupFrames         += data.wsFrames;
                    }

                    break;
                }
                else if (wsTestCase === 'download')
                {
                    dlData          += data.wsData;
                    dlFrames        += data.wsFrames;

                    if (data.wsTime > wsWorkerTime) 
                    {
                        wsWorkerTime = data.wsTime;
                    }

                    break;
                }
                else if (wsTestCase === 'upload')
                {
                    var ulStreamReportDict = ulReportDict[data.wsID];

                    for (var key in data.ulReportDict)
                    {
                        var ulValueDict = {};
                        ulValueDict.bRcv = Number(data.ulReportDict[key].bRcv);
                        ulValueDict.hRcv = Number(data.ulReportDict[key].hRcv);
                        if (typeof ulStreamReportDict !== 'undefined')
                        {
                            ulStreamReportDict[data.ulReportDict[key].time] = ulValueDict;
                        }
                    }
                    ulReportDict[data.wsID] = ulStreamReportDict;

                    break;
                }

                break;
            }

            case 'error':
            {
                wsWorkersStatus[data.wsID] = data.wsState;                   
                if (data.msg === 'authorizationConnection' && !wsMeasurementError && this.wsTestCase !== 'rtt')
                {
                    wsMeasurementError = true;
                    measurementError('webSocket authorization unsuccessful or no connection to measurement server', 4, 1, 0);
                }
                break;
            } 

            default:
            {
                console.log('wsWorker ' + data.wsID + ' unknown command: ' + data.cmd + '\' message: \'' + data.msg);
                break;
            }
        }
    }
    
    /**
     * @function measurementError
     * @description Function to report errors and close active connections
     * @private
     * @param {string} errorDescription Description of the Error
     * @param {string} errorCode Error Code
     * @param {string} systemAvailability System Availability
     * @param {string} serviceAvailability Service Availability
     */
    function measurementError(errorDescription, errorCode, systemAvailability, serviceAvailability)
    {
        clearInterval(wsTimeoutTimer);
        
        if ((errorCode === 2 || errorCode === 4) && wsTestCase === 'rtt')
        {
            wsMeasurementError = true;
            reportToMeasurement('info', 'no connection to measurement server');
            measurementFinish();
            return;
        }
        
        console.log(wsTestCase + ': ' + errorDescription);
        wsErrorCode             = errorCode;
        wsErrorDescription      = errorDescription;
        wsSystemAvailability    = systemAvailability;
        wsServiceAvailability   = serviceAvailability;
        reportToMeasurement('error',  errorDescription);
        for (var wsID = 0; wsID < wsWorkers.length; wsID++)
        {
            var workerData = prepareWorkerData('close', wsID);
         
            if (wsTestCase === 'download' || wsTestCase === 'upload')
            {
                wsWorkers[wsID].onmessageSM(workerData);
            }
            else
            {
                wsWorkers[wsID].onmessage(workerData);
            }
        }
        resetValues();
    }

    /**
     * @function measurementTimeout
     * @description Function to report timeouts
     * @private
     */
    function measurementTimeout()
    {
        wsMeasurementError = true;
        measurementError('webSocket timeout error', 2, 1, 0);
    }
    
    /**
     * @function measurementStart
     * @description Function to start measurements
     * @private
     */
    function measurementStart()
    {
        wsStartTime = performance.now();
        
        if (wsTestCase !== 'rtt')
        {
            for (var wsID = 0; wsID < wsWorkers.length; wsID++)
            {
                if (wsWorkersStatus[wsID] === wsStateOpen) wsStreamsStart++;
                var workerData = prepareWorkerData('resetCounter', wsID);

                if (wsTestCase === 'download' || wsTestCase === 'upload')
                {
                    wsWorkers[wsID].onmessageSM(workerData);
                }
                else
                {
                    wsWorkers[wsID].onmessage(workerData);
                }
            }
        }

        wsInterval = setInterval(measurementReport, wsReportInterval);

        console.log(wsTestCase + ': measurement started');
        reportToMeasurement('info', 'measurement started');
    }
    
    /**
     * @function measurementReport
     * @description Function to report measurement results 
     * @private
     */
    function measurementReport()
    {
        wsEndTime = performance.now();
        if (((wsEndTime - wsStartTime) > wsMeasurementRunningTime) || (wsRttValues.replies + wsRttValues.missing + wsRttValues.errors) === rttRequests)
        {
            clearInterval(wsInterval);
            wsCompleted = true;
            for (var wsID = 0; wsID < wsWorkers.length; wsID++)
            {
                var workerData = prepareWorkerData('close', wsID);
                if (wsTestCase === 'download' || wsTestCase === 'upload') 
                {
                    if (wsWorkersStatus[wsID] === wsStateOpen) wsStreamsEnd++;
                    wsWorkers[wsID].onmessageSM(workerData);
                }
                else
                {
                    wsWorkers[wsID].onmessage(workerData);
                } 
            }
            wsEndTime = performance.now();
            setTimeout(measurementFinish, 100);
        }
        else if ((wsEndTime - wsStartTime) > 500)
        {
            for (var wsID = 0; wsID < wsWorkers.length; wsID++)
            {
                var workerData = prepareWorkerData('report', wsID);
                
                if (wsTestCase === 'download' || wsTestCase === 'upload')
                {
                    wsWorkers[wsID].onmessageSM(workerData);
                }
                else
                {
                    wsWorkers[wsID].onmessage(workerData);
                }
            }
            wsMeasurementTime       = performance.now() - wsStartTime;
            wsMeasurementTimeTotal  = performance.now() - wsStartupStartTime;
            
            if (!wsCompleted)
            {
                setTimeout(report, 100);
            }
        }
    }
    
    /**
     * @function measurementFinish
     * @description Function to finish measurements
     * @private
     */
    function measurementFinish()
    {
        console.log(wsTestCase + ': measurement finished');

        wsMeasurementTime       = wsEndTime - wsStartTime;
        wsMeasurementTimeTotal  = wsEndTime - wsStartupStartTime;
        report(true);
    }




    /*-------------------------helper------------------------*/

    /**
     * @function report
     * @description Function to report measurement results
     * @private
     * @param {bool} finish Indicates if the measurement is finished
     */
    function report(finish)
    {
        if ((wsWorkerTime - wsStartTime) > wsMeasurementTime)
        {
            wsMeasurementTime       = wsWorkerTime - wsStartTime;
            wsMeasurementTimeTotal  = wsWorkerTime - wsStartupStartTime;
        }
        
        var msg;
            
        if (wsTestCase === 'download')
        {
            wsData          = dlData;
            wsFrames        = dlFrames;
            wsDataTotal     = dlData    + dlStartupData;
            wsFramesTotal   = dlFrames  + dlStartupFrames;
            
            msg = 'ok';
        }
        
        if (wsTestCase === 'upload')
        {
            var ulData = 0;
            var ulFrames = 0;
            for (var wsID = 0; wsID < wsWorkers.length; wsID++)
            {
                var ulStreamReportDict = ulReportDict[wsID];
                
                for (var streamKey in ulStreamReportDict)
                {
                    
                    ulData      += ulStreamReportDict[streamKey].bRcv;
                    ulFrames    += ulStreamReportDict[streamKey].hRcv;
                }
            }
            
            wsData          = ulData;
            wsFrames        = ulFrames;
            wsDataTotal     = ulData    + ulStartupData;
            wsFramesTotal   = ulFrames  + ulStartupFrames; 
            
            msg = 'ok';
        }
        
        if (wsTestCase !== 'rtt')
        {    
            wsOverhead           = (wsFrames * wsOverheadPerFrame);
            wsOverheadTotal      = (wsFramesTotal * wsOverheadPerFrame);
            wsSpeedAvgBitS       = (((wsData * 8) + (wsOverhead * 8)) / (Math.round(wsMeasurementTime) / 1000));
            wsSpeedAvgMBitS      = +(wsSpeedAvgBitS / 1000 / 1000).toFixed(2);

            //set min/max rates
            if (wsSpeedAvgBitS < wsSpeedMinBitS)
                wsSpeedMinBitS = wsSpeedAvgBitS;
            else if (wsSpeedMinBitS === 0)
                wsSpeedMinBitS = wsSpeedAvgBitS;
            if (wsSpeedAvgBitS > wsSpeedMaxBitS)
                wsSpeedMaxBitS = wsSpeedAvgBitS;
        }

        var finishString     = '';
        var cmd              = 'report';

        if (finish)
        {
            cmd = 'finish';
            msg = 'measurement finished';
            finishString = 'Overall ';
            console.log('--------------------------------------------------------');
        }
        
        if (logReports && wsTestCase === 'rtt')
        {
            console.log(finishString + 'Time:           ' + Math.round(wsMeasurementTime) + ' ms');
            console.log(finishString + 'RTT Avg:        ' + wsRttValues.avg + ' ms');
            console.log(finishString + 'RTT Median:     ' + wsRttValues.med + ' ms');
            console.log(finishString + 'RTT Min:        ' + wsRttValues.min + ' ms');
            console.log(finishString + 'RTT Max:        ' + wsRttValues.max + ' ms');
            console.log(finishString + 'RTT Requests:   ' + wsRttValues.requests);
            console.log(finishString + 'RTT Replies:    ' + wsRttValues.replies);
            console.log(finishString + 'RTT Errors:     ' + wsRttValues.errors);
            console.log(finishString + 'RTT Missing:    ' + wsRttValues.missing);
            console.log(finishString + 'RTT Packetsize: ' + wsRttValues.packetsize);
            console.log(finishString + 'RTT Std Dev Pop:' + wsRttValues.stDevPop + ' ms');
            console.log(finishString + 'RTT Server:     ' + wsRttValues.server);
        }
        else if (logReports)
        {
            console.log(finishString + 'Time:           ' + Math.round(wsMeasurementTime) + ' ms');
            console.log(finishString + 'Data:           ' + (wsData + wsOverhead) + ' bytes');
            console.log(finishString + 'TCP Throughput: ' + wsSpeedAvgMBitS + ' Mbit/s');
        }
        
        //set KPIs 
        if (wsTestCase === 'download')
        {
            wsDownloadValues.rateAvg       = Math.round(wsSpeedAvgBitS);
            wsDownloadValues.rateMin       = Math.round(wsSpeedMinBitS);
            wsDownloadValues.rateMax       = Math.round(wsSpeedMaxBitS);
            wsDownloadValues.data          = wsData + wsOverhead;
            wsDownloadValues.dataTotal     = wsDataTotal + wsOverheadTotal;  
            wsDownloadValues.duration      = Math.round(wsMeasurementTime);
            wsDownloadValues.durationTotal = Math.round(wsMeasurementTimeTotal);
            wsDownloadValues.streamsStart  = wsStreamsStart;
            wsDownloadValues.streamsEnd    = wsStreamsEnd;
            wsDownloadValues.frameSize     = wsFrameSize;
            wsDownloadValues.frames        = wsFrames;
            wsDownloadValues.framesTotal   = wsFramesTotal;
            wsDownloadValues.overhead      = wsOverhead;
            wsDownloadValues.overheadTotal = wsOverheadTotal;
            wsDownloadValues.rateAvgMBitS  = wsSpeedAvgMBitS;
        }
        
        if (wsTestCase === 'upload')
        {
            wsUploadValues.rateAvg         = Math.round(wsSpeedAvgBitS);
            wsUploadValues.rateMin         = Math.round(wsSpeedMinBitS);
            wsUploadValues.rateMax         = Math.round(wsSpeedMaxBitS);
            wsUploadValues.data            = wsData + wsOverhead;
            wsUploadValues.dataTotal       = wsDataTotal + wsOverheadTotal;  
            wsUploadValues.duration        = Math.round(wsMeasurementTime);
            wsUploadValues.durationTotal   = Math.round(wsMeasurementTimeTotal);
            wsUploadValues.streamsStart    = wsStreamsStart;
            wsUploadValues.streamsEnd      = wsStreamsEnd;
            wsUploadValues.frameSize       = wsFrameSize;
            wsUploadValues.frames          = wsFrames;
            wsUploadValues.framesTotal     = wsFramesTotal;
            wsUploadValues.overhead        = wsOverhead;
            wsUploadValues.overheadTotal   = wsOverheadTotal;  
            wsUploadValues.rateAvgMBitS    = wsSpeedAvgMBitS;
        }
        
        reportToMeasurement(cmd, msg);
        if (finish) resetValues();
    }
    
    /**
     * @function reportToMeasurement
     * @description Function to report measurement results to the WSControl Callback class
     * @private
     * @param {string} cmd Callback Command
     * @param {string} msg Callback Message
     */
    function reportToMeasurement(cmd, msg)
    {
        var report          = {};
        report.cmd          = cmd;
        report.msg          = msg;
        report.test_case    = wsTestCase;

        if (wsTestCase === 'rtt')       report = getKPIsRtt(report);
        if (wsTestCase === 'download')  report = getKPIsDownload(report);
        if (wsTestCase === 'upload')    report = getKPIsUpload(report);
        report = getKPIsAvailability(report);

        if (wsControl.callback === 'wsMeasurement')  this.wsMeasurement.controlCallback(JSON.stringify(report));
    }
    
    /**
     * @function getKPIsRtt
     * @description Function to collect RTT KPIs
     * @private
     * @param {string} report Object to add KPIs to
     */
    function getKPIsRtt(report)
    {
        report.rtt_avg                          = wsRttValues.avg;
        report.rtt_med                          = wsRttValues.med;
        report.rtt_min                          = wsRttValues.min;
        report.rtt_max                          = wsRttValues.max;
        report.rtt_requests                     = wsRttValues.requests;
        report.rtt_replies                      = wsRttValues.replies;
        report.rtt_errors                       = wsRttValues.errors;
        report.rtt_missing                      = wsRttValues.missing;
        report.rtt_packetsize                   = wsRttValues.packetsize;
        report.rtt_std_dev_pop                  = wsRttValues.stDevPop;
        
        if (typeof wsRttValues.server !== 'undefined')
        {
            report.server_rtt                   = wsRttValues.server + '.' + wsTLD;
        }
        
        return report;
    }
    
    /**
     * @function getKPIsDownload
     * @description Function to collect Download KPIs
     * @private
     * @param {string} report Object to add KPIs to
     */
    function getKPIsDownload(report)
    {
        report.download_rate_avg            = wsDownloadValues.rateAvg;
        report.download_rate_min            = wsDownloadValues.rateMin;
        report.download_rate_max            = wsDownloadValues.rateMax;
        report.download_data                = wsDownloadValues.data;
        report.download_data_total          = wsDownloadValues.dataTotal;
        report.download_duration            = wsDownloadValues.duration;
        report.download_duration_total      = wsDownloadValues.durationTotal;
        report.download_streams_start       = wsDownloadValues.streamsStart;
        report.download_streams_end         = wsDownloadValues.streamsEnd;
        report.download_frame_size          = wsDownloadValues.frameSize;
        report.download_frames              = wsDownloadValues.frames;
        report.download_frames_total        = wsDownloadValues.framesTotal;
        report.download_overhead            = wsDownloadValues.overhead;
        report.download_overhead_total      = wsDownloadValues.overheadTotal;
        report.download_overhead_per_frame  = wsDownloadValues.overheadPerFrame;
        report.download_rate_avg_mbits      = wsDownloadValues.rateAvgMBitS;

        return report;
    }
    
    /**
     * @function getKPIsUpload
     * @description Function to collect Upload KPIs
     * @private
     * @param {string} report Object to add KPIs to
     */
    function getKPIsUpload(report)
    {
        report.upload_rate_avg              = wsUploadValues.rateAvg;
        report.upload_rate_min              = wsUploadValues.rateMin;
        report.upload_rate_max              = wsUploadValues.rateMax;
        report.upload_data                  = wsUploadValues.data;
        report.upload_data_total            = wsUploadValues.dataTotal;
        report.upload_duration              = wsUploadValues.duration;
        report.upload_duration_total        = wsUploadValues.durationTotal;
        report.upload_streams_start         = wsUploadValues.streamsStart;
        report.upload_streams_end           = wsUploadValues.streamsEnd;
        report.upload_frame_size            = wsUploadValues.frameSize;
        report.upload_frames                = wsUploadValues.frames;
        report.upload_frames_total          = wsUploadValues.framesTotal;
        report.upload_overhead              = wsUploadValues.overhead;
        report.upload_overhead_total        = wsUploadValues.overheadTotal;
        report.upload_overhead_per_frame    = wsUploadValues.overheadPerFrame;
        report.upload_rate_avg_mbits        = wsUploadValues.rateAvgMBitS;

        return report;
    }
    
    /**
     * @function getKPIsAvailability
     * @description Function to collect Availability KPIs
     * @private
     * @param {string} report Object to add KPIs to
     */
    function getKPIsAvailability(report)
    {
        report.error_code                   = wsErrorCode;
        report.error_description            = wsErrorDescription;
        
        return report;
    }
    
    /**
     * @function prepareWorkerData
     * @description Function to prepare the WSWorker control data
     * @private
     * @param {string} cmd Command to execute
     * @param {int} wsID ID of the WSWorker
     */
    function prepareWorkerData(cmd, wsID)
    {
        var workerData                  = {};
        workerData.cmd                  = cmd;
        workerData.wsTestCase           = wsTestCase;
        workerData.wsID                 = wsID;
        workerData.wsTarget             = wsTarget;
        workerData.wsTargetRtt          = wsTargetRtt;
        workerData.wsTargetPort         = wsTargetPort;
        workerData.wsWss                = wsWss;
        workerData.wsProtocol           = wsProtocol;
        workerData.wsFrameSize          = wsFrameSize;
        workerData.wsAuthToken          = wsAuthToken;
        workerData.wsAuthTimestamp      = wsAuthTimestamp;
        workerData.wsTLD                = wsTLD;
        
        if (wsTestCase === 'rtt')
        {
            workerData.rttRequests              = rttRequests;
            workerData.rttRequestTimeout        = rttRequestTimeout;
            workerData.rttRequestWait           = rttRequestWait;
            workerData.rttTimeout               = rttTimeout;
            workerData.rttPayloadSize           = rttPayloadSize;
        }    
        
        return JSON.stringify(workerData);
    }
    
    /**
     * @function resetValues
     * @description Initialize all variables with default values
     * @private
     */
    function resetValues()
    {
        clearInterval(wsInterval);

        wsTestCase                  = '';
        wsData                      = 0;
        wsDataTotal                 = 0;
        wsFrames                    = 0;
        wsFramesTotal               = 0;
        wsSpeedAvgBitS              = 0;
        wsSpeedAvgMBitS             = 0;
        wsSpeedMinBitS              = 0;
        wsSpeedMaxBitS              = 0;
        wsOverhead                  = 0;
        wsOverheadTotal             = 0;
        wsStartTime                 = performance.now();
        wsEndTime                   = performance.now();
        wsMeasurementTime           = 0;
        wsMeasurementTimeTotal      = 0;
        wsStartupStartTime          = performance.now();
        wsOverheadPerFrame          = 0;
        wsWorkers                   = 0;
        wsWorkersStatus             = 0;
        wsWorkerTime                = 0;
        wsInterval                  = 0;
        wsMeasurementRunningTime    = 0;
        wsCompleted                 = false;
        wsReportInterval            = 0;
        wsTimeoutTimer              = 0;
        wsTimeout                   = 0;
        wsStartupTimeout            = 0;
        wsStreamsStart              = 0;
        wsStreamsEnd                = 0;
        wsFrameSize                 = 0;
        
        wsAuthToken                 = '-';
        wsAuthTimestamp             = '-';
        
        dlData                      = 0;
        dlFrames                    = 0;
        dlStartupData               = 0;
        dlStartupFrames             = 0;
        
        ulReportDict                = {};
        ulStartupData               = 0;
        ulStartupFrames             = 0;

        wsSystemAvailability        = 1;
        wsServiceAvailability       = 1;
    }
};
/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2018                                                          *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2018-12-14
 *      \note Copyright (c) 2018 zafaco GmbH. All rights reserved.
 */

/* global jsinterface, require, global */

var wsControl;
var jsTool = new JSTool();




/*-------------------------class WSMeasurement------------------------*/

/**
 * @class WSMeasurement
 * @description WebSocket Measurement Class
 */
function WSMeasurement()
{
    //private variables
    var speedVersion                       	= '1.0.0';
    
    var platform;
    
    var cookieId                            = true;
    
    //Test Cases
    var performRttMeasurement               = false;
    var performDownloadMeasurement          = false;
    var performUploadMeasurement            = false;
    
    var performedRttMeasurement             = false;
    var performedDownloadMeasurement        = false;
    var performedUploadMeasurement          = false;
    
    var performRouteToClientLookup          = false;
    var performedRouteToClientLookup        = false;
    var routeToClientTargetPort             = '8080';

    var wsMeasurementParameters             = {};
    
    //KPIs
    var deviceKPIs                          = {};
    var availabilityKPIs                    = {};
    var rttKPIs                             = {};
    var downloadKPIs                        = {};
    var uploadKPIs                          = {};
    var timestampKPIs                       = {};
    var routeKPIs                           = {};
    
    var wsRttTimer                          = 0;
    var wsDownloadTimer                     = 0;
    var wsUploadTimer                       = 0;
    
    var measurementStopped                  = false;
    
    
    
    
    /*-------------------------public functions------------------------*/
    
    /**
     * @function measurementControl
     * @description API Function to Start and Stop measurements
     * @public
     * @param {string} measurementParameters JSON coded measurement Parameters
     */
    this.measurementControl = function(measurementParameters)
    {
        wsMeasurementParameters = JSON.parse(measurementParameters);
        
        if (wsMeasurementParameters.cmd === 'start')
        {
			console.log("The browser developer console should only be used for debugging purposes, as an active developer console can cause performance issues");
			
            resetWsControl();

            if (typeof wsMeasurementParameters.platform !== 'undefined') platform = String(wsMeasurementParameters.platform);
         
            deviceKPIs                          = JSON.parse(jsTool.getDeviceKPIs(platform));
            deviceKPIs.client_speed_version   = speedVersion;
            
            if (wsMeasurementParameters.platform === 'mobile')
            {
                //WebSocket streams are natively threaded, so we dont need WebWorkers
                wsMeasurementParameters.singleThread = true;
            }
            else
            {
                //catch Firefox < 38, as it has no WebSocket in WebWorker Support
                if ((deviceKPIs.client_browser.search('Firefox') !== -1) && Number(deviceKPIs.client_browser_version) < 38)
                {
                    wsMeasurementParameters.singleThread = true;
                }

                //catch ie11 and Edge > 13
                //ie11: no WebSocket in WebWorker Support
                //edge14: no WebSocket in WebWorker Support
                //edge>14: WebSocket in WebWorker Support, but poor (/4) upload performance
                if ((deviceKPIs.client_browser.search('Internet Explorer 11') !== -1) || ((deviceKPIs.client_browser.search('Edge') !== -1) && (Number(deviceKPIs.client_browser_version) > 13)))
                {
                    wsMeasurementParameters.singleThread = true;
                }

                //catch Safari 5, 6 and 7, as they only have partial WebSocket support
                if ((deviceKPIs.client_browser.search('Safari 5') !== -1) || (deviceKPIs.client_browser.search('Safari 6') !== -1) || (deviceKPIs.client_browser.search('Safari 7') !== -1))
                {
                    var data = {};
                    data.cmd                                = 'error';
                    availabilityKPIs.cmd                    = 'error';     
                    availabilityKPIs.error_code             = 5;
                    availabilityKPIs.error_description      = 'WebSockets are only partially supported by your browser';
                    data                                    = JSON.stringify(data);
                    this.controlCallback(data);
                    return;
                }
            }
            
            //log measurement mode
            if (typeof wsMeasurementParameters.singleThread !== 'undefined')
            {
                console.log('Measurement Mode: Single Thread');
                deviceKPIs.measurement_mode = 'st';
            }
            else
            {
                console.log('Measurement Mode: Multi Thread');
                deviceKPIs.measurement_mode = 'mt';
            }
            
            var cookieName = wsMeasurementParameters.wsTLD.split('.', 1);
            
            if (typeof wsMeasurementParameters.cookieId !== 'undefined') cookieId = Boolean(wsMeasurementParameters.cookieId);
            
            if (cookieId)
            {
                var cookie = jsTool.getCookie(cookieName + '_id');
                if (!cookie) 
                {
                    cookie = jsTool.generateRandomData(64, true);
                }

                jsTool.setCookie(cookieName + '_id', cookie, 365);
                deviceKPIs.client_cookie = cookie;
            }
        }

        if (typeof wsMeasurementParameters.performRttMeasurement !== 'undefined')           performRttMeasurement           = Boolean(wsMeasurementParameters.performRttMeasurement);
        if (typeof wsMeasurementParameters.performDownloadMeasurement !== 'undefined')      performDownloadMeasurement      = Boolean(wsMeasurementParameters.performDownloadMeasurement);
        if (typeof wsMeasurementParameters.performUploadMeasurement !== 'undefined')        performUploadMeasurement        = Boolean(wsMeasurementParameters.performUploadMeasurement);
        if (typeof wsMeasurementParameters.performRouteToClientLookup !== 'undefined')      performRouteToClientLookup      = Boolean(wsMeasurementParameters.performRouteToClientLookup);
        if (typeof wsMeasurementParameters.routeToClientTargetPort !== 'undefined')         routeToClientTargetPort         = Number(wsMeasurementParameters.routeToClientTargetPort);

        if (typeof window !== 'undefined' && !window.WebSocket)
        {
            var data = {};
            data.cmd                                = 'error';
            availabilityKPIs.cmd                    = 'error';     
            availabilityKPIs.error_code             = 3;
            availabilityKPIs.error_description      = 'WebSockets are not supported by your browser';
            data                                    = JSON.stringify(data);
            this.controlCallback(data);
            return;
        }
        
        if (!wsMeasurementParameters.cmd || !platform || (!performRttMeasurement && !performDownloadMeasurement && !performUploadMeasurement))
        {
            var data = {};
            data.cmd                                = 'error';
            availabilityKPIs.cmd                    = 'error';     
            availabilityKPIs.error_code             = 1;
            availabilityKPIs.error_description      = 'Measurement Parameters Missing';
            data                                    = JSON.stringify(data);
            this.controlCallback(data);
            return;
        }

        switch (wsMeasurementParameters.cmd)
        {
            case 'start':
            {
                measurementCampaign();
                
                break;
            }
            case 'stop':
            {
                measurementStopped = true;
                
                clearTimeout(wsRttTimer);
                clearTimeout(wsDownloadTimer);
                clearTimeout(wsUploadTimer);
   
                if (wsControl)      wsControl.measurementStop(JSON.stringify(wsMeasurementParameters));
                
                break;
            }
        }
    };

    /**
     * @function controlCallback
     * @description API Function to Callback the JSON coded measurement Results to the implementing client
     * @param {string} data JSON coded measurement Results
     */
    this.controlCallback = function(data)
    {
        data = JSON.parse(data);
        
        if(data.test_case === 'routeToClient')
        {
            routeKPIs.server_client_route       = data.server_client_route;
            routeKPIs.server_client_route_hops  = data.server_client_route_hops;
        }
        
        if (data.test_case === 'rtt')           rttKPIs             = data;
        if (data.test_case === 'download')
        {
            downloadKPIs        = data;
        }
        if (data.test_case === 'upload')        uploadKPIs          = data;

        if (data.cmd === 'error')
        {
            setEndTimestamps(data.test_case);
            setTimeout(resetWsControl, 200);
        }

        if (data.cmd === 'finish')
        {
            if (data.test_case === 'rtt')           
            {
                performedRttMeasurement                 = true;
            }

            if (data.test_case === 'download')
            {
                performedDownloadMeasurement            = true;
            }
            
            if (data.test_case === 'upload')
            {
                performedUploadMeasurement              = true;
            }
            
            measurementCampaign();
        }
        
        var kpis = getKPIs();
        
        if (performRttMeasurement === performedRttMeasurement && performDownloadMeasurement === performedDownloadMeasurement && performUploadMeasurement === performedUploadMeasurement)
        {
            var kpisCompleted = jsTool.extend(kpis);
            kpisCompleted.cmd = 'completed';
            kpisCompleted = JSON.stringify(kpisCompleted);
            setTimeout(controlCallbackToPlatform, 50, kpisCompleted);
        }
         
        kpis = JSON.stringify(kpis);
        
        controlCallbackToPlatform(kpis);
    };
    
    /**
     * @function setDeviceKPIs
     * @description API Function to set additional Device KPIs
     * @public
     * @param {string} data JSON coded Device KPIs
     */
    this.setDeviceKPIs = function(data)
    {
        data = JSON.parse(data);
        deviceKPIs = jsTool.extend(deviceKPIs, data);
        //$.extend(deviceKPIs, data);
    };

    
    
    
    /*-------------------------private functions------------------------*/
    
    /**
     * @function controlCallbackToPlatform
     * @description API Function to Callback the JSON coded measurement Results to the implementing client
     * @private
     * @param {string} kpis JSON coded measurement Results
     */
    function controlCallbackToPlatform(kpis)
    {
        switch (platform)
        {
            case 'web':
            {
                reportToWeb(kpis);
                break;
            }
            
            case 'desktop':
            {
                reportToWeb(kpis);
                break;
            }
            
            case 'mobile':
            {
                reportToMobile(kpis);
                break;
            }
        }
    }

    /**
     * @function measurementCampaign
     * @description Perform a measurement Campaign containing one or more Test Cases
     * @private
     */
    function measurementCampaign()
    {
        if (measurementStopped)
        {
            return;
        }

        wsControl               = null;
        delete wsControl;
        if (typeof wsMeasurementParameters.singleThread !== 'undefined')
        {
            wsControl               = new WSControlSingleThread();
        }
        else
        {
            wsControl               = new WSControl();
        }
        wsControl.wsMeasurement = this;
        wsControl.callback      = 'wsMeasurement';
        
        var waitTime            = 3000;
        var waitTimeShort       = 1000;
        
        if (performRttMeasurement && !performedRttMeasurement)
        {
            setEndTimestamps();
            if (!timestampKPIs.timestamp_rtt_start) timestampKPIs.timestamp_rtt_start = jsTool.getTimestamp() + (waitTimeShort);
            wsMeasurementParameters.testCase = 'rtt';
            wsRttTimer = setTimeout(wsControl.measurementSetup, waitTimeShort, JSON.stringify(wsMeasurementParameters));
            
            var stats = {};
            stats.timestamp_rtt_start   = timestampKPIs.timestamp_rtt_start; 
            
            return;
        }

        if (performRouteToClientLookup && !performedRouteToClientLookup && (performDownloadMeasurement || performUploadMeasurement))
        {
            jsTool.performRouteToClientLookup(wsMeasurementParameters.wsTarget + '.' + wsMeasurementParameters.wsTLD, routeToClientTargetPort);
            performedRouteToClientLookup = true;
        }
        
        if (performDownloadMeasurement && !performedDownloadMeasurement)
        {
            setEndTimestamps();
            
            var waitTimeDownload = waitTimeShort;
            
            if (!timestampKPIs.timestamp_download_start) timestampKPIs.timestamp_download_start = jsTool.getTimestamp() + waitTimeDownload;
            wsMeasurementParameters.testCase = 'download';
            if (typeof require !== 'undefined')
            {
				/*
                if (v6)
                {
                    wsMeasurementParameters.ndServerFamily = 6;
                }
                else
                {
                    wsMeasurementParameters.ndServerFamily = 4;
                }
				*/
                
            }
            wsDownloadTimer = setTimeout(wsControl.measurementSetup, waitTimeDownload, JSON.stringify(wsMeasurementParameters));
            
            var stats = {};
            stats.timestamp_download_start   = timestampKPIs.timestamp_download_start; 
            
            return;
        }
        else
        if (performUploadMeasurement && !performedUploadMeasurement)
        {
            setEndTimestamps();
            if (!timestampKPIs.timestamp_upload_start) timestampKPIs.timestamp_upload_start = jsTool.getTimestamp() + waitTime;
            wsMeasurementParameters.testCase = 'upload';
            wsUploadTimer = setTimeout(wsControl.measurementSetup, waitTime, JSON.stringify(wsMeasurementParameters));
            
            var stats = {};
            stats.timestamp_upload_start   = timestampKPIs.timestamp_upload_start; 
            
            return;
        }  
        else setTimeout(resetWsControl, 200);
        
        setEndTimestamps();
    }
    
    /**
     * @function setEndTimestamps
     * @description Set the Timestamp of the completion of the current Test Case
     * @private
     * @param {string} test_case Test Case
     */
    function setEndTimestamps(test_case)
    {
        if ((performRttMeasurement && performedRttMeasurement && !timestampKPIs.timestamp_rtt_end) || test_case === 'rtt')
        {
            timestampKPIs.timestamp_rtt_end = jsTool.getTimestamp();
        }
        if ((performDownloadMeasurement && performedDownloadMeasurement && !timestampKPIs.timestamp_download_end)  || test_case === 'download')
        {
            timestampKPIs.timestamp_download_end = jsTool.getTimestamp();
        }
        if ((performUploadMeasurement && performedUploadMeasurement && !timestampKPIs.timestamp_upload_end)  || test_case === 'upload')
        {
			timestampKPIs.timestamp_upload_end = jsTool.getTimestamp();
        }
    }
    
    /**
     * @function getKPIs
     * @description Return all measurement Results
     */
    function getKPIs()
    {
        var kpis = jsTool.extend(availabilityKPIs, rttKPIs, downloadKPIs, uploadKPIs, timestampKPIs, deviceKPIs, routeKPIs);
        
        return kpis;
    }

    /**
     * @function resetWsControl
     * @description Reset the wsControl object
     * @private
     */
    function resetWsControl()
    {
        wsControl = null;
        delete wsControl;
    }
    
    /**
     * @function reportToWeb
     * @description Callback to Browser
     * @private
     * @param {string} kpis JSON coded Measurement Results
     */
    function reportToWeb(kpis)
    {
        measurementCallback(kpis);
    }
    
    /**
     * @function reportToMobile
     * @description Callback to Mobile
     * @private
     * @param {string} kpis JSON coded Measurement Results
     */
    function reportToMobile(kpis)
    {
        global.messageToNative('tnsReportCallback', kpis);
    }
};
/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2018                                                          *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2018-12-14
 *      \note Copyright (c) 2018 zafaco GmbH. All rights reserved.
 */

/* global logDebug, self, wsControl, global */




/*-------------------------class WSWorker------------------------*/

/**
 * @class WSWorker
 * @description WebSocket Worker Class
 */
var WSWorker = function ()
{
    this.wsControl      = 'undefined';

    return(this);
};

var webSocket;

var wsTarget;
var wsTargetRtt;
var wsTargetPort;
var wsWss;
var wsProtocol;
var wsTestCase;
var wsID;
var wsFrameSize;
var wsData;
var wsFrames;
var wsCompleted;
var wsServer;
var wsClient;
var wsTLD;

var wsStateConnecting   = 0;
var wsStateOpen         = 1;
var wsStateClosing      = 2;
var wsStateClosed       = 3;

var wsConnected         = false;

var wsRttValues =
{
    avg:                undefined,
    med:                undefined,
    min:                undefined,
    max:                undefined,
    requests:           undefined,
    replies:            undefined,
    errors:             undefined,
    missing:            undefined,
    packetsize:         undefined,
    stDevPop:           undefined,
    server:             undefined
};

var wsAuthToken;
var wsAuthTimestamp;

var rttRequests             = 10;
var rttRequestTimeout       = 1000;
var rttRequestWait          = 500;
var rttTimeout              = ((rttRequests * rttRequestTimeout) + rttRequestWait) * 1.3;
var rttPayloadSize          = 64;

var ulData;
var ulDataPointer;
var ulInterval;
var ulReportDict            = {};

var ulDataSize              = 2246915;
var ulBufferSize            = 4096 * 1000;
var ulStarted               = false;

var singleThread            = false;

var jsTool;

var ndServerFamily;




/*-------------------------WSWorker------------------------*/

/**
 * @function onmessage
 * @description Function to Receive Commands from WSControl Single Thread 
 * @public
 * @param {string} data JSON coded measurement Parameters
 */
WSWorker.prototype.onmessage = function(data)
{
    singleThread = true;
    onmessage(data);
};

/**
 * @function onmessage
 * @description Function to Receive Commands from WSControl Multi Thread 
 * @public
 * @param {string} event JSON coded measurement Parameters
 */
onmessage = function (event)
{
    var data;

    try 
    {
        if (!singleThread) 
        {
            data = JSON.parse(event.data);
        }
        else
        {
            data = JSON.parse(event);
            wsTestCase = data.wsTestCase;
        }
    }
    catch (error)
    {
        return;
    }

    wsID    = data.wsID;

    switch (data.cmd)
    {
        case 'connect':
        {
            reportToControl('info', 'websocket connecting');
            setWsParameters(data);
            ndServerFamily = data.ndServerFamily;

            if (wsTestCase === 'upload') wsFrameSize = data.wsFrameSize;

            if (wsTestCase === 'rtt')
            {
                rttRequests             = data.rttRequests;
                rttRequestTimeout       = data.rttRequestTimeout;
                rttRequestWait          = data.rttRequestWait;
                rttTimeout              = data.rttTimeout;
                rttPayloadSize          = data.rttPayloadSize;
            }

            wsAuthToken           = data.wsAuthToken;
            wsAuthTimestamp       = data.wsAuthTimestamp;  

            connect();
            break;
        }

        case 'resetCounter':
        {
            //console.log('reset received');
            
            if (wsTestCase === 'upload')
            {
                for (var key in ulReportDict)
                {
                    wsData      += ulReportDict[key].bRcv;
                    wsFrames    += ulReportDict[key].hRcv;
                }
            }

            reportToControl('report', 'startupCompleted');
            resetCounter();
            reportToControl('info', 'counter reseted');
            break;
        }

        case 'report':
        {
            reportToControl('report');
            resetCounter();
            break;
        }

        case 'stop':
        {
            break;
        }

        case 'close':
        {   
            reportToControl('report');

            wsCompleted = true;

            if (webSocket.readyState === wsStateClosed)
            {
                webSocket.close();
                if (!singleThread) this.close();
            }
            else
            {
                webSocket.close();
                websocketClose('close received');
                reportToControl('info', 'websocket closing');   
            }
            break;
        }

        default:
        {
            reportToControl('error', 'Unknown command: ' + data.msg);
            break;
        }
    }
};

/**
 * @function connect
 * @description Function to initiate a WebSocket Connection
 * @private
 */
function connect()
{   
    resetValues();

    if (!singleThread && typeof require === 'undefined') importScripts('Tool.js');
    
    if (typeof require === 'undefined')
    {
        jsTool = new JSTool();
    }

    var wsWssString                 = 'wss://';
    if (!Number(wsWss)) wsWssString = 'ws://';

    var target = wsWssString + wsTarget + ':' + wsTargetPort;    

    if (wsTestCase === 'rtt')
    {
        target = wsWssString + wsTargetRtt + ':' + wsTargetPort;
    }

    try 
    {
        if (typeof require !== 'undefined' && wsTestCase === 'download')
        {
            var logDebug = false;
            var WebSocketNode = require('ws');
            var wsProtocols = [wsProtocol, wsAuthToken, wsAuthTimestamp];
            webSocket = new WebSocketNode(target, wsProtocols,
            {
                origin: 'http://' + wsTLD,
                perMessageDeflate: false,
                family: ndServerFamily,
                headers:
                    {
                        'Pragma': 'no-cache',
                        'Cache-Control': 'no-cache' 
                    }
            });
        }
        else
        {
            if (wsTestCase === 'rtt'  || wsTestCase === 'download' || wsTestCase === 'upload')
            {
                var wsProtocols = [wsProtocol, wsAuthToken, wsAuthTimestamp];
                webSocket = new WebSocket(target, wsProtocols);
            }
            else
            {
                webSocket = new WebSocket(target, wsProtocol);
            }
        }
    }
    catch (error)
    {
        //console.log('webSocket error: try/catch: ' + error);
        if (logDebug) reportToControl('info', 'webSocket error: try/catch: ' + error);
    }

    webSocket.onopen = function (event)
    {
        reportToControl('open', 'websocket open');
        wsConnected = true;

        if (wsTestCase === 'rtt')
        {
            roundTripTime();
        }
        else if (wsTestCase === 'download')
        {
            download();
        }
        else if (wsTestCase === 'upload')
        {
            setTimeout(upload, 200);
            wsCompleted = false;
            ulData = jsTool.generateRandomData(ulDataSize, true);
        }
    };

    webSocket.onerror = function (event)
    {
        //console.log('webSocket error: onError: ' + event.type);
        if (logDebug) reportToControl('info', 'webSocket error: onError: ' + event.type);
        if (!wsConnected && webSocket.readyState === wsStateClosed) 
        {
            reportToControl('error', 'authorizationConnection'); 
        }
        else
        if (singleThread && webSocket.readyState === wsStateClosed)
        {
            reportToControl('error', 'authorizationConnection');
        }
    };

    webSocket.onmessage = function (event)
    {
        if (wsTestCase === 'download')
        {
            wsData      += event.data.size;
            wsFrameSize = event.data.size;
            wsFrames++;
        }
        else
        {
            //console.log('Server: response text received: ' + event.data);
            //reportToControl('info', 'Server: response text received: ' + event.data);
            try 
            {
                var data = JSON.parse(event.data);
            }
            catch (error)
            {
                //console.log('webSocket on Message: json parse error');
                //reportToControl('info', 'webSocket on Message: json parse error');
                return;
            }

            if (data.cmd === 'rttReport')
            {
                wsRttValues.avg         = Number(data.avg);
                wsRttValues.med         = Number(data.med);
                wsRttValues.min         = Number(data.min);
                wsRttValues.max         = Number(data.max);
                wsRttValues.requests    = Number(data.req);
                wsRttValues.replies     = Number(data.rep);
                wsRttValues.errors      = Number(data.err);
                wsRttValues.missing     = Number(data.mis);
                wsRttValues.packetsize  = Number(data.pSz);
                wsRttValues.stDevPop    = Number(data.std_dev_pop);
                wsRttValues.server      = String(data.srv);
            }

            if (data.cmd === 'ulReport')
            {
                var length = 0;
                for (var key in ulReportDict)
                {
                    length++;
                }
                ulReportDict[length]        = {};
                ulReportDict[length].time   = Number(data.time);
                ulReportDict[length].bRcv   = Number(data.bRcv);
                ulReportDict[length].hRcv   = Number(data.hRcv);
            }
        }
    };

    webSocket.onclose = function (event)
    {
        wsTestCase = '';
        var closeReason = '';
        if (typeof require === 'undefined')
        {
            closeReason = ', reason: ' + jsTool.webSocketCloseReasons(event);
        }
        reportToControl('close', 'websocket closed' + closeReason);
    };
};

/**
 * @function roundTripTime
 * @description Function to perform the Round-Trip Time Test Case
 * @private
 */
function roundTripTime()
{
    sendToWebSocket('rttStart');
    reportToControl('info', 'start round trip time');
};

/**
 * @function download
 * @description Function to perform the Download Test Case
 * @private
 */
function download()
{
    reportToControl('info', 'start download');
};

/**
 * @function upload
 * @description Function to perform the Upload Test Case
 * @private
 */
function upload()
{
    if (!ulStarted)
    {
        wsCompleted = false;
        reportToControl('info', 'start upload'); 
        ulStarted = true;
    }

    ulInterval = setInterval(function ()
    {
        if (webSocket.bufferedAmount <= ulBufferSize)
        {
            if (wsCompleted)
            {
                clearInterval(ulInterval);
            }
                
            var ulPayload = ulData.slice(ulDataPointer, ulDataPointer + wsFrameSize);
            ulDataPointer += wsFrameSize;
            if (ulDataPointer > ulDataSize)
            {
                ulDataPointer = ulDataPointer - ulDataSize;
                ulPayload = ulPayload + ulData.slice(0, ulDataPointer);
            }
            
            if (webSocket.readyState === wsStateOpen) 
            {
                webSocket.send(ulPayload);
            }
        }
    }, 4);
};

/**
 * @function reportToControl
 * @description Function to report measurement results to the WSControl Callback class
 * @private
 * @param {string} cmd Callback Command
 * @param {string} msg Callback Message
 */
function reportToControl(cmd, msg)
{
    var data            = {};
    data.cmd            = cmd;
    data.msg            = '';
    if (msg) data.msg   = msg;
    data.wsID           = wsID;
    data.wsFrameSize    = wsFrameSize;

    if (webSocket) data.wsState    = webSocket.readyState;

    if (wsTestCase === 'rtt')
    {
        data.wsRttValues            = wsRttValues;
    }

    if (wsTestCase === 'upload')
    {
        data.ulReportDict = ulReportDict;
    }

    data.wsData         = wsData;
    data.wsFrames       = wsFrames;

    if (typeof performance !== 'undefined')
    {
        data.wsTime = performance.now();
    }
    else 
    {
        data.wsTime = 0;
    }

    if (!singleThread)
    {
        self.postMessage(JSON.stringify(data));
    }
    else
    {
        if (wsControl !== 'undefined' && wsControl)
        {
            wsControl.workerCallback(JSON.stringify(data));
            return;
        }
    }
};

/**
 * @function sendToWebSocket
 * @description Function to send data to an active WebSocket connection
 * @private
 * @param {string} cmd Command
 * @param {string} msg Message
 */
function sendToWebSocket(cmd, msg)
{
    var data    = {};
    data.cmd    = cmd;
    data.msg    = msg;

    if (wsTestCase === 'rtt')
    {
        data.rttRequests       = rttRequests;
        data.rttRequestTimeout = rttRequestTimeout;
        data.rttRequestWait    = rttRequestWait;
        data.rttTimeout        = rttTimeout;
        data.rttPayloadSize    = rttPayloadSize;
    }

    if (webSocket.readyState === wsStateOpen) webSocket.send(JSON.stringify(data));
};

/**
 * @function setWsParameters
 * @description Function to set WebSocket parameters
 * @private
 * @param {string} data Parameters
 */
function setWsParameters(data)
{
    wsTarget        = data.wsTarget;
    wsTargetRtt     = data.wsTargetRtt;
    wsTargetPort    = data.wsTargetPort;
    wsWss           = data.wsWss;
    wsProtocol      = data.wsProtocol;
    wsTestCase      = data.wsTestCase;
    wsTLD           = data.wsTLD;
};

/**
 * @function resetCounter
 * @description Function to reset the Download/Upload Counter
 * @private
 */
function resetCounter()
{
    wsData          = 0;
    wsFrames        = 0;

    ulReportDict    = {};
};

/**
 * @function websocketClose
 * @description Function to close an active WebSocket connection
 * @private
 * @param {string} reason Close reason
 */
function websocketClose(reason)
{
    webSocket.close();
    //console.log('close websocket: reason: ' + reason);
    if (!singleThread) setTimeout(this.close, 200);
};

/**
 * @function resetValues
 * @description Initialize all variables with default values
 * @private
 */
function resetValues()
{
    clearInterval(ulInterval);

    delete(webSocket);

    wsCompleted     = false;

    wsData          = 0;
    wsFrames        = 0;

    ulData          = 0;
    ulDataPointer   = 0;
    ulReportDict    = {};
};/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2018                                                          *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2018-12-14
 *      \note Copyright (c) 2018 zafaco GmbH. All rights reserved.
 */

/* global logDebug, wsControl, global */

var jsTool = new JSTool();




/*-------------------------class WSWorkerSingleThread------------------------*/

/**
 * @class WSWorkerSingleThread
 * @description WebSocket Worker Class using a Single Thread
 */
function WSWorkerSingleThread()
{
    this.wsControl      = 'undefined';
    this.wsID;

    var webSocket;

    var wsTarget;
    var wsTargetRtt;
    var wsTargetPort;
    var wsWss;
    var wsProtocol;
    var wsTestCase;
    var wsID;
    var wsFrameSize;
    var wsData;
    var wsFrames;
    var wsCompleted;

    var wsStateConnecting   = 0;
    var wsStateOpen         = 1;
    var wsStateClosing      = 2;
    var wsStateClosed       = 3;

    var wsConnected         = false;

    var wsAuthToken;
    var wsAuthTimestamp;

    var ulData;
    var ulDataPointer           = 0;
    var ulInterval;
    var ulReportDict            = {};

    var ulDataSize              = 512345;
    var ulBufferSize            = 4096 * 1000;
    var ulStarted               = false;

    /**
     * @function onmessageSM
     * @description Function to Receive Commands from WSControl Single Thread 
     * @public
     * @param {string} data JSON coded measurement Parameters
     */
    this.onmessageSM = function(data)
    {
        try 
        {
            data = JSON.parse(data);
            wsTestCase = data.wsTestCase;
        }
        catch (error)
        {
            return;
        }

        wsID = data.wsID;

        switch (data.cmd)
        {
            case 'connect':
            {
                reportToControlSM('info', 'websocket connecting');
                setWsParametersSM(data);

                if (wsTestCase === 'upload') wsFrameSize = data.wsFrameSize;

                wsAuthToken           = data.wsAuthToken;
                wsAuthTimestamp       = data.wsAuthTimestamp;  

                connectSM();
                break;
            }

            case 'resetCounter':
            {
                if (wsTestCase === 'upload')
                {
                    for (var key in ulReportDict)
                    {
                        wsData      += ulReportDict[key].bRcv;
                        wsFrames    += ulReportDict[key].hRcv;
                    }
                }

                reportToControlSM('report', 'startupCompleted');
                resetCounterSM();
                reportToControlSM('info', 'counter reseted');
                break;
            }

            case 'report':
            {
                reportToControlSM('report');
                resetCounterSM();
                break;
            }

            case 'close':
            {   
                reportToControlSM('report');

                wsCompleted = true;

                if (webSocket.readyState === wsStateClosed)
                {
                    webSocket.close();
                }
                else
                {
                    webSocket.close();
                    websocketCloseSM('close received');
                    reportToControlSM('info', 'websocket closing');   
                }
                break;
            }

            default:
            {
                reportToControlSM('error', 'Unknown command: ' + data.msg);
                break;
            }
        }
    };

    /**
    * @function startUpload
    * @description Function to perform the Upload Test Case
    * @private
    */
    this.startUpload = function()
    {
        setTimeout(uploadSM, 200);
    };
    
    /**
    * @function connectSM
    * @description Function to initiate a WebSocket Connection
    * @private
    */
    function connectSM()
    {   
        resetValuesSM();

        jsTool = new JSTool();

        var wsWssString                 = 'wss://';
        if (!Number(wsWss)) wsWssString = 'ws://';

        var target = wsWssString + wsTarget + ':' + wsTargetPort;    

        try 
        {
            var wsProtocols = [wsProtocol, wsAuthToken, wsAuthTimestamp];
            webSocket = new WebSocket(target, wsProtocols);
        }
        catch (error)
        {
            //console.log('webSocket error: try/catch: ' + error);
            if (logDebug) reportToControlSM('info', 'webSocket error: try/catch: ' + error);
        }

        webSocket.onopen = function (event)
        {
            reportToControlSM('open', 'websocket open');
            wsConnected = true;

            if (wsTestCase === 'download')
            {
                downloadSM();
            } 
            else if (wsTestCase === 'upload')
            {
                wsCompleted = false;
                ulData = jsTool.generateRandomData(ulDataSize, true);
            }
        };

        webSocket.onerror = function (event)
        {
            //console.log('webSocket error: onError: ' + event.type);
            if (logDebug) reportToControlSM('info', 'webSocket error: onError: ' + event.type);
            if (!wsConnected && webSocket.readyState === wsStateClosed) 
            {
                reportToControlSM('error', 'authorizationConnection'); 
            }
        };

        webSocket.onmessage = function (event)
        {  
            if (wsTestCase === 'download')
            {
                wsData      += event.data.size;
                wsFrameSize = event.data.size;
                wsFrames++;
            }
            else
            {
                //reportToControl('info', 'Server: response text received: ' + event.data);
                try 
                {
                    var data = JSON.parse(event.data);
                }
                catch (error)
                {
                    return;
                }

                if (data.cmd === 'ulReport')
                {
                    var length = 0;
                    for (var key in ulReportDict)
                    {
                        length++;
                    }
                    ulReportDict[length]        = {};
                    ulReportDict[length].time   = Number(data.time);
                    ulReportDict[length].bRcv   = Number(data.bRcv);
                    ulReportDict[length].hRcv   = Number(data.hRcv);
                }
            }
        };

        webSocket.onclose = function (event)
        {
            wsTestCase = '';
            reportToControlSM('close', 'websocket closed, reason: ' + jsTool.webSocketCloseReasons(event));
        };
    };

    /**
     * @function downloadSM
     * @description Function to perform the Download Test Case
     * @private
     */
    function downloadSM()
    {
        reportToControlSM('info', 'start download');
    };

    /**
     * @function uploadSM
     * @description Function to perform the Upload Test Case
     * @private
     */
    function uploadSM()
    {
        if (!ulStarted)
        {
            wsCompleted = false;
            reportToControlSM('info', 'start upload'); 
            ulStarted = true;
        }

        ulInterval = setInterval(function ()
        {
            if (webSocket.bufferedAmount <= ulBufferSize)
            {
                if (wsCompleted)
                {
                    clearInterval(ulInterval);
                }

                var ulPayload = ulData.slice(ulDataPointer, ulDataPointer + wsFrameSize);
                ulDataPointer += wsFrameSize;
                if (ulDataPointer > ulDataSize)
                {
                    ulDataPointer = ulDataPointer - ulDataSize;
                    ulPayload = ulPayload + ulData.slice(0, ulDataPointer);
                }

                if (webSocket.readyState === wsStateOpen) 
                {
                    webSocket.send(ulPayload);
                }
            }
        }, 4);
    };

    /**
     * @function reportToControlSM
     * @description Function to report measurement results to the WSControl Callback class
     * @private
     * @param {string} cmd Callback Command
     * @param {string} msg Callback Message
     */
    function reportToControlSM(cmd, msg)
    {
        var data            = {};
        data.cmd            = cmd;
        data.msg            = '';
        if (msg) data.msg   = msg;
        data.wsID           = wsID;
        data.wsFrameSize    = wsFrameSize;

        if (webSocket) data.wsState    = webSocket.readyState;

        if (wsTestCase === 'upload')
        {
            data.ulReportDict = ulReportDict;
        }

        data.wsData         = wsData;
        data.wsFrames       = wsFrames;

        if (typeof performance !== 'undefined')
        {
            data.wsTime = performance.now();
        }
        else 
        {
            data.wsTime = 0;
        }

        if (wsControl !== 'undefined' && wsControl)
        {
            wsControl.workerCallback(JSON.stringify(data));
            return;
        }
    };

    /**
     * @function sendToWebSocketSM
     * @description Function to send data to an active WebSocket connection
     * @private
     * @param {string} cmd Command
     * @param {string} msg Message
     */
    function sendToWebSocketSM(cmd, msg)
    {
        var data    = {};
        data.cmd    = cmd;
        data.msg    = msg;

        if (webSocket.readyState === wsStateOpen) webSocket.send(JSON.stringify(data));
    };

    /**
     * @function setWsParametersSM
     * @description Function to set WebSocket parameters
     * @private
     * @param {string} data Parameters
     */
    function setWsParametersSM(data)
    {
        wsTarget        = data.wsTarget;
        wsTargetRtt     = data.wsTargetRtt;
        wsTargetPort    = data.wsTargetPort;
        wsWss           = data.wsWss;
        wsProtocol      = data.wsProtocol;
        wsTestCase      = data.wsTestCase;
    };

    /**
     * @function resetCounterSM
     * @description Function to reset the Download/Upload Counter
     * @private
     */
    function resetCounterSM()
    {
        wsData          = 0;
        wsFrames        = 0;

        ulReportDict    = {};
    };

    /**
     * @function websocketCloseSM
     * @description Function to close an active WebSocket connection
     * @private
     * @param {string} reason Close reason
     */
    function websocketCloseSM(reason)
    {
        webSocket.close();
        //console.log('close websocket: reason: ' + reason);
    };

    /**
     * @function resetValuesSM
     * @description Initialize all variables with default values
     * @private
     */
    function resetValuesSM()
    {
        clearInterval(ulInterval);

        delete(webSocket);

        wsCompleted     = false;

        wsData          = 0;
        wsFrames        = 0;

        ulData          = 0;
        ulDataPointer   = 0;
        ulReportDict    = {};
    };
};/*
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
 *      \date Last update: 2019-01-02
 *      \note Copyright (c) 2018 - 2019 zafaco GmbH. All rights reserved.
 */




global.measurementStart = function(measurementParameters)
{
	console.log('Measurement Parameters: \n' + JSON.stringify(measurementParameters));

	delete wsMeasurement;
	wsMeasurement = null;
	wsMeasurement = new WSMeasurement();
	wsMeasurement.measurementControl(JSON.stringify(measurementParameters));
}

global.measurementStop = function()
{
    wsMeasurement.measurementControl(JSON.stringify({cmd:'stop'}));
}

global.console.log = function(message)
{
    global.messageToNative('tnsConsoleLogCallback', message)
};

global.messageToNative = function(channel, message)
{
    if (platform.isAndroid)
    {
        var broadcastManager = android.support.v4.content.LocalBroadcastManager.getInstance(utils.ad.getApplicationContext());
        var intent = new android.content.Intent(channel);
        intent.putExtra('message', message);
        broadcastManager.sendBroadcast(intent);
    }

    if (platform.isIOS)
    {
        var notificationCenter = utils.ios.getter(NSNotificationCenter, NSNotificationCenter.defaultCenter);
        var dataDictionary = NSDictionary.dictionaryWithDictionary(
        {
                message: message
        })
        notificationCenter.postNotificationNameObjectUserInfo(channel, null, dataDictionary);
    }
};
