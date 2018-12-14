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
};