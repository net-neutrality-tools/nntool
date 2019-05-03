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
 *      \date Last update: 2019-03-20
 *      \note Copyright (c) 2018 - 2019 zafaco GmbH. All rights reserved.
 */

/* global logDebug, logReports, wsControl, __dirname */

var jsTool = new JSTool();




/*-------------------------class WSControl------------------------*/

/**
 * @class WSControl
 * @description WebSocket Control Class
 */
function WSControl()
{
    this.wsMeasurement  = '';
    this.callback       = '';

    var wsTestCase;
    var wsData;
    var wsDataTotal;
    var wsFrames;
    var wsFramesTotal;
    var wsSpeedAvgBitS;
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
    var rttRequestTimeout           = 2000;
    var rttRequestWait              = 500;
    var rttTimeout                  = (rttRequests * (rttRequestTimeout + rttRequestWait)) * 1.1;
    var rttPayloadSize              = 64;

    var dlStartupTime               = 3000;
    var dlMeasurementRunningTime    = 10000;
    var dlParallelStreams           = 4;
    var dlTimeout                   = 10000;
    var dlFrameSize                 = 32768;

    var ulStartupTime               = 3000;
    var ulMeasurementRunningTime    = 10000;
    var ulParallelStreams           = 4;
    var ulTimeout                   = 10000;
    var ulFrameSize                 = 65535;
    var uploadFramesPerCall         = 1;

    var wsParallelStreams;
    var wsStartupTime;

    var singleThread                = false;

    var wsWorkerPath                = 'Worker.js';




    /*-------------------------KPIs------------------------*/

    var wsSystemAvailability;
    var wsServiceAvailability;
    var wsErrorCode;
    var wsErrorDescription;

    var wsRttValues =
    {
        duration:           undefined,
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
        overheadTotal:    undefined
    };

    var wsUploadValues =
    {
        rateAvg:          undefined,
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
        framePerCall:     undefined
    };




    /*-------------------------public functions------------------------*/

    /**
     * @function measurementStart
     * @description Function to Start test cases
     * @public
     * @param {string} measurementParameters JSON coded measurement Parameters
     */
    this.measurementStart = function(measurementParameters)
    {
        resetValues();

        wsMeasurementError      = false;
        measurementParameters   = JSON.parse(measurementParameters);
        wsTestCase              = measurementParameters.testCase;

        if (typeof measurementParameters.download.streams !== 'undefined' && wsTestCase === 'download')
        {
            dlParallelStreams   = Number(measurementParameters.download.streams );
        }
        if (typeof measurementParameters.upload.streams !== 'undefined' && wsTestCase === 'upload')
        {
            ulParallelStreams   = Number(measurementParameters.upload.streams );
        }

        if (typeof measurementParameters.download.frameSize !== 'undefined' && wsTestCase === 'download')
        {
            dlFrameSize         = Number(measurementParameters.download.frameSize);
        }
        if (typeof measurementParameters.upload.frameSize !== 'undefined' && wsTestCase === 'upload')
        {
            ulFrameSize         = Number(measurementParameters.upload.frameSize);
        }
        if (typeof measurementParameters.upload.framesPerCall !== 'undefined' && wsTestCase === 'upload')
        {
            uploadFramesPerCall = Number(measurementParameters.upload.framesPerCall);
        }

        switch (wsTestCase)
        {
            case 'rtt':
            {
                if (typeof measurementParameters.rttRequests !== 'undefined')
                {
                    rttRequests         = Number(measurementParameters.rttRequests);
                }
                if (typeof measurementParameters.rttRequestTimeout !== 'undefined')
                {
                    rttRequestTimeout   = Number(measurementParameters.rttRequestTimeout);
                }
                if (typeof measurementParameters.rttRequestWait !== 'undefined')
                {
                    rttRequestWait      = Number(measurementParameters.rttRequestWait);
                }
                if (typeof measurementParameters.rttTimeout !== 'undefined')
                {
                    rttTimeout          = Number(measurementParameters.rttTimeout);
                }
                if (typeof measurementParameters.rttPayloadSize !== 'undefined')
                {
                    rttPayloadSize      = Number(measurementParameters.rttPayloadSize);
                }
                wsParallelStreams           = 1;
                wsMeasurementRunningTime    = rttTimeout;
                wsReportInterval            = rttReportInterval;
                wsProtocol                  = rttProtocol;
                wsTimeout                   = rttTimeout;

                break;
            }
            case 'download':
            {
                wsParallelStreams                   = dlParallelStreams;
                wsFrameSize                         = dlFrameSize;

                if (wsFrameSize >= 65536)
                {
                    dlWsOverheadPerFrame    = 8;
                }
                else if (wsFrameSize < 126)
                {
                    dlWsOverheadPerFrame    = 2;
                }

                wsOverheadPerFrame                  = dlWsOverheadPerFrame;
                wsDownloadValues.overheadPerFrame   = dlWsOverheadPerFrame;
                wsStartupTime                       = dlStartupTime;
                wsMeasurementRunningTime            = dlMeasurementRunningTime;
                wsReportInterval                    = dlReportInterval;
                wsTimeout                           = dlTimeout;
                wsProtocol                          = dlProtocol;

                break;
            }
            case 'upload':
            {
                wsParallelStreams               = ulParallelStreams;
                wsFrameSize                     = ulFrameSize;

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
                wsStartupTime                   = ulStartupTime;
                wsMeasurementRunningTime        = ulMeasurementRunningTime;
                wsReportInterval                = ulReportInterval;
                wsTimeout                       = ulTimeout;
                wsProtocol                      = ulProtocol;

                break;
            }
        }

        if (typeof measurementParameters.wsTargets !== 'undefined')
        {
            wsTargets                   = measurementParameters.wsTargets;
        }
        if (typeof measurementParameters.wsTargetsRtt !== 'undefined')
        {
            wsTargetsRtt                = measurementParameters.wsTargetsRtt;
        }
        if (typeof measurementParameters.wsTLD !== 'undefined')
        {
            wsTLD                       = String(measurementParameters.wsTLD);
        }
        if (typeof measurementParameters.wsTargetPort !== 'undefined')
        {
            wsTargetPort                = String(measurementParameters.wsTargetPort);
        }
        if (typeof measurementParameters.wsWss !== 'undefined')
        {
            wsWss                       = String(measurementParameters.wsWss);
        }
        if (typeof measurementParameters.wsStartupTime !== 'undefined')
        {
            wsStartupTime               = Number(measurementParameters.wsStartupTime);
        }
        if (typeof measurementParameters.wsTimeout !== 'undefined')
        {
            wsTimeout                   = Number(measurementParameters.wsTimeout);
        }
        if (typeof measurementParameters.wsMeasureTime      !== 'undefined' && (wsTestCase === 'download' || wsTestCase === 'upload'))
        {
            wsMeasurementRunningTime    = Number(measurementParameters.wsMeasureTime);
        }

        if (typeof measurementParameters.wsWorkerPath !== 'undefined')
        {
            wsWorkerPath               = String(measurementParameters.wsWorkerPath);
        }

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
            console.log('frame size:        ' + wsFrameSize);
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

            if (typeof measurementParameters.singleThread !== 'undefined')
            {
                singleThread                    = true;
                delete(wsWorkers[wsID]);
                wsWorkers[wsID]                 = new WSWorker();
                wsWorkers[wsID].wsControl       = this;
                setTimeout(wsWorkers[wsID].onmessage, 100,  workerData);
            }
            else
            {
                if (typeof require !== 'undefined' && measurementParameters.platform === 'desktop')
                {
                    var WorkerNode              = require("tiny-worker");
                    var path                    = require('path');
                    var ipcRendererMeasurement  = require('electron').ipcRenderer;

                    wsWorkersStatus[wsID]       = wsStateClosed;
                    wsWorkers[wsID]             = new WorkerNode(path.join(__dirname, 'modules/Worker.js'));

                    ipcRendererMeasurement.send('iasSetWorkerPID', wsWorkers[wsID].child.pid),

                    workerData = JSON.parse(workerData);
                    workerData.ndServerFamily = measurementParameters.ndServerFamily;
                    workerData = JSON.stringify(workerData);
                }
                else
                {
                    wsWorkersStatus[wsID]   = wsStateClosed;
                    wsWorkers[wsID]         = new Worker(wsWorkerPath);
                }

                wsWorkers[wsID].onmessage = function (event)
                {
                    workerCallback(JSON.parse(event.data));
                };

                wsWorkers[wsID].postMessage(workerData);
            }
        }

        wsTimeoutTimer = setTimeout(measurementTimeout, wsTimeout);
    };

    /**
     * @function measurementStop
     * @description Function to Stop test cases
     * @public
     */
    this.measurementStop = function()
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
                wsWorkers[wsID].postMessage(workerData);
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
                wsWorkersStatus[data.wsID] = data.wsState;
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
                        var workerData = prepareWorkerData('uploadStart');

                        for (var wsID = 0; wsID < wsWorkers.length; wsID++)
                        {
                            wsWorkers[wsID].postMessage(workerData);
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
                    if (data.wsRttValues)
                    {
                        wsRttValues = data.wsRttValues;
                        wsRttValues.duration = Math.round(wsMeasurementTime * 1000 * 1000);
                    }

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
                        dlStartupData   += data.wsData;
                        dlStartupFrames += data.wsFrames;
                    }

                    if (wsTestCase === 'upload')
                    {
                        ulStartupData   += data.wsData;
                        ulStartupFrames    += data.wsFrames;
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

            if (!singleThread)
            {
                wsWorkers[wsID].postMessage(workerData);
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

                if (!singleThread)
                {
                    wsWorkers[wsID].postMessage(workerData);
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
                if (wsWorkersStatus[wsID] === wsStateOpen) wsStreamsEnd++;
                var workerData = prepareWorkerData('close', wsID);

                if (!singleThread)
                {
                    wsWorkers[wsID].postMessage(workerData);
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

                if (!singleThread)
                {
                    wsWorkers[wsID].postMessage(workerData);
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
            console.log(finishString + 'Time:                   ' + wsRttValues.duration + ' ns');
            console.log(finishString + 'RTT Avgerage:           ' + wsRttValues.avg + ' ns');
            console.log(finishString + 'RTT Median:             ' + wsRttValues.med + ' ns');
            console.log(finishString + 'RTT Min:                ' + wsRttValues.min + ' ns');
            console.log(finishString + 'RTT Max:                ' + wsRttValues.max + ' ns');
            console.log(finishString + 'RTT Sent:               ' + wsRttValues.requests);
            console.log(finishString + 'RTT Received:           ' + wsRttValues.replies);
            console.log(finishString + 'RTT Errors:             ' + wsRttValues.errors);
            console.log(finishString + 'RTT Missing:            ' + wsRttValues.missing);
            console.log(finishString + 'RTT Packet Size:        ' + wsRttValues.packetsize);
            console.log(finishString + 'RTT Standard Deviation: ' + wsRttValues.stDevPop + ' ns');
            console.log(finishString + 'RTT Peer:               ' + wsRttValues.server);
        }
        else if (logReports)
        {
            console.log(finishString + 'Time:                   ' + Math.round(wsMeasurementTime * 1000 * 1000) + ' ns');
            console.log(finishString + 'Data:                   ' + (wsData + wsOverhead) + ' bytes');
            console.log(finishString + 'TCP Throughput:         ' + (wsSpeedAvgBitS / 1000 / 1000).toFixed(2) + ' MBit/s');
        }

        //set KPIs
        if (wsTestCase === 'download')
        {
            wsDownloadValues.rateAvg       = Math.round(wsSpeedAvgBitS);
            wsDownloadValues.data          = wsData + wsOverhead;
            wsDownloadValues.dataTotal     = wsDataTotal + wsOverheadTotal;
            wsDownloadValues.duration      = Math.round(wsMeasurementTime) * 1000 * 1000;
            wsDownloadValues.durationTotal = Math.round(wsMeasurementTimeTotal) * 1000 * 1000;
            wsDownloadValues.streamsStart  = wsStreamsStart;
            wsDownloadValues.streamsEnd    = wsStreamsEnd;
            wsDownloadValues.frameSize     = wsFrameSize;
            wsDownloadValues.frames        = wsFrames;
            wsDownloadValues.framesTotal   = wsFramesTotal;
            wsDownloadValues.overhead      = wsOverhead;
            wsDownloadValues.overheadTotal = wsOverheadTotal;
        }

        if (wsTestCase === 'upload')
        {
            wsUploadValues.rateAvg         = Math.round(wsSpeedAvgBitS);
            wsUploadValues.data            = wsData + wsOverhead;
            wsUploadValues.dataTotal       = wsDataTotal + wsOverheadTotal;
            wsUploadValues.duration        = Math.round(wsMeasurementTime) * 1000 * 1000;
            wsUploadValues.durationTotal   = Math.round(wsMeasurementTimeTotal) * 1000 * 1000;
            wsUploadValues.streamsStart    = wsStreamsStart;
            wsUploadValues.streamsEnd      = wsStreamsEnd;
            wsUploadValues.frameSize       = wsFrameSize;
            wsUploadValues.frames          = wsFrames;
            wsUploadValues.framesTotal     = wsFramesTotal;
            wsUploadValues.overhead        = wsOverhead;
            wsUploadValues.overheadTotal   = wsOverheadTotal;
            wsUploadValues.framePerCall    = uploadFramesPerCall;
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

        if (wsControl !== null && wsControl.callback !== null && wsControl.callback === 'wsMeasurement' && typeof this.wsMeasurement !== 'undefined')  this.wsMeasurement.controlCallback(JSON.stringify(report));
    }

    /**
     * @function getKPIsRtt
     * @description Function to collect RTT KPIs
     * @private
     * @param {string} report Object to add KPIs to
     */
    function getKPIsRtt(report)
    {
        report.duration_ns              = wsRttValues.duration;
        report.average_ns               = wsRttValues.avg;
        report.median_ms                = wsRttValues.med;
        report.min_ns                   = wsRttValues.min;
        report.max_ns                   = wsRttValues.max;
        report.num_sent                 = wsRttValues.requests;
        report.num_received             = wsRttValues.replies;
        report.num_error                = wsRttValues.errors;
        report.num_missing              = wsRttValues.missing;
        report.packet_size              = wsRttValues.packetsize;
        report.standard_deviation_ns    = wsRttValues.stDevPop;

        if (typeof wsRttValues.server !== 'undefined')
        {
            report.peer                 = wsRttValues.server + '.' + wsTLD;
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
        report.throughput_avg_bps                       = wsDownloadValues.rateAvg;
        report.bytes                                    = wsDownloadValues.data;
        report.bytes_including_slow_start               = wsDownloadValues.dataTotal;
        report.duration_ns                              = wsDownloadValues.duration;
        report.duration_ns_total                        = wsDownloadValues.durationTotal;
        report.num_streams_start                        = wsDownloadValues.streamsStart;
        report.num_streams_end                          = wsDownloadValues.streamsEnd;
        report.frame_size                               = wsDownloadValues.frameSize;
        report.frame_count                              = wsDownloadValues.frames;
        report.frame_count_including_slow_start         = wsDownloadValues.framesTotal;
        report.overhead                                 = wsDownloadValues.overhead;
        report.overhead_per_frame_including_slow_start  = wsDownloadValues.overheadTotal;
        report.overhead_per_frame                       = wsDownloadValues.overheadPerFrame;

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
        report.throughput_avg_bps                       = wsUploadValues.rateAvg;
        report.bytes                                    = wsUploadValues.data;
        report.bytes_including_slow_start               = wsUploadValues.dataTotal;
        report.duration_ns                              = wsUploadValues.duration;
        report.duration_ns_total                        = wsUploadValues.durationTotal;
        report.num_streams_start                        = wsUploadValues.streamsStart;
        report.num_streams_end                          = wsUploadValues.streamsEnd;
        report.frame_size                               = wsUploadValues.frameSize;
        report.frame_count                              = wsUploadValues.frames;
        report.frame_count_including_slow_start         = wsUploadValues.framesTotal;
        report.overhead                                 = wsUploadValues.overhead;
        report.overhead_per_frame_including_slow_start  = wsUploadValues.overheadTotal;
        report.overhead_per_frame                       = wsUploadValues.overheadPerFrame;
        report.frames_per_call                          = wsUploadValues.framePerCall;

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
        workerData.uploadFramesPerCall  = uploadFramesPerCall;
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
