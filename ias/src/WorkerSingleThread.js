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
 *      \date Last update: 2019-05-29
 *      \note Copyright (c) 2018 -2019 zafaco GmbH. All rights reserved.
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
    var ulIntervalTiming        = 4;
    var ulReportDict            = {};

    var ulDataSize              = 512345;
    var ulBufferSize            = 4096 * 1000;
    var ulStarted               = false;
    
    var uploadFramesPerCall     = 1;

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

                if (wsTestCase === 'download' || wsTestCase === 'upload')
                {
                    wsFrameSize = data.wsFrameSize;
                }
                
                if (wsTestCase === 'upload')
                {
                    uploadFramesPerCall     = data.uploadFramesPerCall;
                }
                
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
                
            case 'uploadStart':
            {
                uploadSM();
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
            if (wsTestCase === 'download')
            {
                wsProtocols.push(wsFrameSize);
            }
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
                ulData = jsTool.generateRandomData(ulDataSize, true, false);
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
                    wsRttValues.avg         = Number(data.avg) * 1000 * 1000;
                    wsRttValues.med         = Number(data.med) * 1000 * 1000;
                    wsRttValues.min         = Number(data.min) * 1000 * 1000;
                    wsRttValues.max         = Number(data.max) * 1000 * 1000;
                    wsRttValues.requests    = Number(data.req);
                    wsRttValues.replies     = Number(data.rep);
                    wsRttValues.errors      = Number(data.err);
                    wsRttValues.missing     = Number(data.mis);
                    wsRttValues.packetsize  = Number(data.pSz);
                    wsRttValues.stDevPop    = Number(data.std_dev_pop) * 1000 * 1000;
                    wsRttValues.server      = String(data.srv);
                }
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
        
        if (typeof require !== 'undefined')
        {
            ulIntervalTiming = 1;
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
};