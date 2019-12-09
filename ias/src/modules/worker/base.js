/*!
    \file base.js
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-26

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

/* global logDebug, self, wsControl, global */

var webSocket;

var wsTarget;
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
    server:             undefined,
    rtts:               undefined
};

var wsAuthToken;
var wsAuthTimestamp;

var rttRequests             = 10;
var rttRequestTimeout       = 2000;
var rttRequestWait          = 500;
var rttTimeout              = ((rttRequests * rttRequestTimeout) + rttRequestWait) * 1.3;
var rttPayloadSize          = 64;

var ulData;
var ulDataPointer;
var ulInterval;
var ulIntervalTiming        = 4;
var ulReportDict            = {};

var ulBufferSize            = 4096 * 1000;
var ulStarted               = false;

var uploadFramesPerCall     = 1;

var ndServerFamily;

/**
 * @function onmessage
 * @description Function to Receive Commands from WSControl
 * @public
 * @param {string} event JSON coded measurement Parameters
 */
onmessage = function (event)
{
    var data;

    try 
    {
        if (useWebWorkers) 
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

            if (wsTestCase === 'download' || wsTestCase === 'upload')
            {
                wsFrameSize = data.wsFrameSize;
            }
            
            if (wsTestCase === 'upload')
            {
                uploadFramesPerCall     = data.uploadFramesPerCall;
            }

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

        case 'fetchCounter':
        {
            if (wsTestCase === 'upload')
            {
                for (var key in ulReportDict)
                {
                    wsData      += ulReportDict[key].bRcv;
                    wsFrames    += ulReportDict[key].hRcv;
                }
            }
            reportToControl('info', 'counter');

            if (wsTestCase === 'upload')
            {
                wsData          = 0;
                wsFrames        = 0;
            }
            
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

        case 'uploadStart':
        {
            upload();
            break;
        }

        case 'close':
        {   
            reportToControl('report');

            wsCompleted = true;

            if (webSocket.readyState === wsStateClosed)
            {
                webSocket.close();
                if (useWebWorkers) this.close();
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

    if (useWebWorkers && typeof require === 'undefined') importScripts('Tool.js');
    
    if (!useWebWorkers || typeof require === 'undefined')
    {
        jsTool = new JSTool();
    }

    var wsWssString                 = 'wss://';
    if (!Number(wsWss)) wsWssString = 'ws://';

    var target = wsWssString + wsTarget + ':' + wsTargetPort;    

    try
    {
        var wsProtocols = [wsProtocol, 'overload', wsAuthToken, wsAuthTimestamp];

        if (typeof require !== 'undefined' && typeof platformModule === 'undefined')
        {
            var logDebug = false;
            var WebSocketNode = require('ws');
            
            if (wsTestCase === 'download')
            {
                wsProtocols.push(wsFrameSize);
            }
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
                if (wsTestCase === 'download')
                {
                    wsProtocols.push(wsFrameSize);
                }
                webSocket = new WebSocket(target, wsProtocols);
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

        if (webSocket.protocol === 'overload')
        {
            reportToControl('error', 'overload'); 
        }

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
            wsCompleted = false;
            ulData = generateRandomData(ulDataSize, true, false);
        }
    };

    webSocket.onerror = function (event)
    {
        //console.log('webSocket error: onError: ' + event.type);
        if (logDebug) reportToControl('info', 'webSocket error: onError: ' + event.type);
        if ((!useWebWorkers || !wsConnected) && webSocket.readyState === wsStateClosed) 
        {
            reportToControl('error', 'authorizationConnection'); 
        }
    };

    webSocket.onmessage = function (event)
    {
        if (wsTestCase === 'download')
        {
            wsData      += event.data.size;
            wsFrames++;
        }
        else
        {
            //console.log('Server: response text received: ' + event.data);
            //reportToControl('info', 'Server: response text received: ' + event.data);

            if (wsTestCase === 'upload')
            {
                var length = 0;
                for (var key in ulReportDict)
                {
                    length++;
                }

                var data = event.data.split(';')[0].split(',');
                if (data.length > 4)
                {
                    ulReportDict[length]        = {};
                    ulReportDict[length].bRcv   = Number(data[0]);
                    ulReportDict[length].time   = Number(data[3]);
                    ulReportDict[length].hRcv   = Number(data[4]);
                }
                else
                {
                    return;
                }
            }
            else if (wsTestCase === 'rtt')
            {
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
                    wsRttValues.avg         = Number(data.avg) * 1000;
                    wsRttValues.med         = Number(data.med) * 1000;
                    wsRttValues.min         = Number(data.min) * 1000;
                    wsRttValues.max         = Number(data.max) * 1000;
                    wsRttValues.requests    = Number(data.req);
                    wsRttValues.replies     = Number(data.rep);
                    wsRttValues.errors      = Number(data.err);
                    wsRttValues.missing     = Number(data.mis);
                    wsRttValues.packetsize  = Number(data.pSz);
                    wsRttValues.stDevPop    = Number(data.std_dev_pop) * 1000;
                    wsRttValues.server      = String(data.srv);

                    if (typeof data.rtts !== 'undefined')
                    {
                        wsRttValues.rtts = [];
                        for (var rtt in data.rtts)
                        {
                            wsRttValues.rtts.push({"rtt_ns": data.rtts[rtt].rtt_ns * 1000, "id": data.rtts[rtt].id});
                        }
                    }
                }
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
        if (wsCompleted)
        {
            clearInterval(ulInterval);
        }
        
        if (webSocket.bufferedAmount <= ulBufferSize && webSocket.readyState === wsStateOpen)
        {       
            for (var i=0;i<uploadFramesPerCall;i++)
            {
                var ulPayload = ulData.slice(ulDataPointer, ulDataPointer + wsFrameSize);
                ulDataPointer += wsFrameSize;
                if (ulDataPointer > ulDataSize)
                {
                    ulDataPointer = ulDataPointer - ulDataSize;
                    ulPayload = ulPayload + ulData.slice(0, ulDataPointer);
                }

                webSocket.send(ulPayload);
            }
        }
    }, ulIntervalTiming);
};

/**
 * @function generateRandomData
 * @description Function to generate Random ASCII Data
 * @public
 * @param {int} length Random Data Length
 * @param {bool} asString Indicates if the callback should be a string
 * @param {bool} asArrayBuffer Indicates if the callback should be a n ArrayBuffer
 */
function generateRandomData(length, asString, asArrayBuffer)
{
    var mask = '';
    mask += 'abcdefghijklmnopqrstuvwxyz';
    mask += 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    mask += '0123456789';
    mask += '~`!@#$%^&*()_+-={}[]:;?,./|\\';

    if (asArrayBuffer)
    {
          var    arrayBuffer = new ArrayBuffer(length);
          var bufferView = new Uint8Array(arrayBuffer);
        for (var i = length; i > 0; --i)
        {
            bufferView[i] = mask.charCodeAt(Math.floor(Math.random() * mask.length));
        }
        return arrayBuffer;
    }
    else
    {
        var data = '';
        for (var i = length; i > 0; --i)
        {
            data += mask[Math.floor(Math.random() * mask.length)];
        }
        if (asString)
        {
            return data;
        }
        else
        {
            return new Blob([data]);
        }
    }
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

    if (webSocket) data.wsState = webSocket.readyState;

    if (wsTestCase === 'rtt')
    {
        data.wsRttValues = wsRttValues;
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

    if (useWebWorkers)
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
    if (useWebWorkers) setTimeout(this.close, 200);
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