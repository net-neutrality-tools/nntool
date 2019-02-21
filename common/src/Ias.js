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
 *      \date Last update: 2019-02-21
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

/* global jsinterface, require, global */

var wsControl;
var jsTool = new JSTool();




/**
 * @Class Ias
 * @description IAS Measurement class
 */
function Ias()
{
    this.wsMeasurement;

    var iasVersion                      = '1.0.0';
    var platform;
    var cookieId                        = true;

    //Test Cases
    var performRttMeasurement           = false;
    var performDownloadMeasurement      = false;
    var performUploadMeasurement        = false;

    var performedRttMeasurement         = false;
    var performedDownloadMeasurement    = false;
    var performedUploadMeasurement      = false;

    var performRouteToClientLookup      = false;
    var performedRouteToClientLookup    = false;
    var routeToClientTargetPort         = '8080';

    var wsMeasurementParameters         = {};

    //KPIs
    var globalKPIs                      = {};
    var rttKPIs                         = {};
    var downloadKPIs                    = {};
    var uploadKPIs                      = {};
    var timestampKPIs                   = {};
    var clientKPIs                      = {};
    var deviceKPIs                      = {};
    var routeKPIs                       = {};

    var wsRttTimer                      = 0;
    var wsDownloadTimer                 = 0;
    var wsUploadTimer                   = 0;

    var measurementStopped              = false;

    var gcTimer;
    var gcTimerInterval                 = 10;




    /*-------------------------public functions------------------------*/

    /**
     * @function measurementStart
     * @description API Function to stop a measurement
     * @public
     * @param {string} measurementParameters JSON coded measurement Parameters
     */
    this.measurementStart = function(measurementParameters)
    {
        wsMeasurementParameters = JSON.parse(measurementParameters);

        console.log('Measurement Parameters: \n' + JSON.stringify(wsMeasurementParameters));

        console.log("The browser developer console should only be used for debugging purposes, as an active developer console can cause performance issues");

        resetWsControl();

        if (typeof wsMeasurementParameters.platform !== 'undefined') platform = String(wsMeasurementParameters.platform);

        globalKPIs.start_time           = jsTool.getFormattedDate();

        clientKPIs.timezone             = jsTool.getTimezone();
        clientKPIs.type                 = platform.toUpperCase();
        clientKPIs.ias_version          = iasVersion;

        deviceKPIs                      = JSON.parse(jsTool.getDeviceKPIs(platform));

        if (wsMeasurementParameters.platform === 'mobile')
        {
            //WebSocket streams are natively threaded, so we dont need WebWorkers
            wsMeasurementParameters.singleThread = true;
        }
        else
        {
            //catch Firefox < 38, as it has no WebSocket in WebWorker Support
            if ((deviceKPIs.browser_info.name.search('Firefox') !== -1) && Number(deviceKPIs.browser_info.version) < 38)
            {
                wsMeasurementParameters.singleThread = true;
            }

            //catch ie11 and Edge > 13
            //ie11: no WebSocket in WebWorker Support
            //edge14: no WebSocket in WebWorker Support
            //edge>14: WebSocket in WebWorker Support, but poor (/4) upload performance
            if ((deviceKPIs.browser_info.name.search('Internet Explorer 11') !== -1) || ((deviceKPIs.browser_info.name.search('Edge') !== -1) && (Number(deviceKPIs.browser_info.version) > 13)))
            {
                wsMeasurementParameters.singleThread = true;
            }

            //catch Safari 5, 6 and 7, as they only have partial WebSocket support
            if ((deviceKPIs.browser_info.name.search('Safari 5') !== -1) || (deviceKPIs.browser_info.name.search('Safari 6') !== -1) || (deviceKPIs.browser_info.name.search('Safari 7') !== -1))
            {
                var data                  = {};
                data.cmd                  = 'error';
                data.error_code           = 5;
                data.error_description    = 'WebSockets are only partially supported by your browser';
                data                            = JSON.stringify(data);
                this.controlCallback(data);
                return;
            }
        }

        if (typeof window !== 'undefined' && !window.WebSocket)
        {
            var data                  = {};
            data.cmd                  = 'error';
            data.error_code           = 3;
            data.error_description    = 'WebSockets are not supported by your browser';
            data                      = JSON.stringify(data);
            this.controlCallback(data);
            return;
        }

        if (typeof require !== 'undefined' && platformModule.isIOS)
        {
            gcTimer = setInterval(function ()
            {
                utils.GC();
            }, gcTimerInterval);
        }

        if (typeof wsMeasurementParameters.singleThread !== 'undefined')
        {
            console.log('WebWorkers:        inactive');
            deviceKPIs.web_workers_active = false;
        }
        else
        {
            console.log('WebWorkers:        active');
            deviceKPIs.web_workers_active = true;
        }

        var cookieName = wsMeasurementParameters.wsTLD.split('.', 1);

        if (typeof wsMeasurementParameters.cookieId !== 'undefined')
        {
            cookieId = Boolean(wsMeasurementParameters.cookieId);
        }

        if (typeof require === 'undefined' && cookieId)
        {
            var cookie = jsTool.getCookie(cookieName + '_id');
            if (!cookie)
            {
                cookie = jsTool.generateRandomData(64, true, false);
            }

            jsTool.setCookie(cookieName + '_id', cookie, 365);
            clientKPIs.cookie = cookie;
        }

        if (typeof wsMeasurementParameters.rtt.performMeasurement !== 'undefined')
        {
            performRttMeasurement       = Boolean(wsMeasurementParameters.rtt.performMeasurement);
        }
        if (typeof wsMeasurementParameters.download.performMeasurement !== 'undefined')
        {
            performDownloadMeasurement  = Boolean(wsMeasurementParameters.download.performMeasurement);
        }
        if (typeof wsMeasurementParameters.upload.performMeasurement !== 'undefined')
        {
            performUploadMeasurement    = Boolean(wsMeasurementParameters.upload.performMeasurement);
        }
        if (typeof wsMeasurementParameters.performRouteToClientLookup !== 'undefined')
        {
            performRouteToClientLookup  = Boolean(wsMeasurementParameters.performRouteToClientLookup);
        }
        if (typeof wsMeasurementParameters.routeToClientTargetPort !== 'undefined')
        {
            routeToClientTargetPort     = Number(wsMeasurementParameters.routeToClientTargetPort);
        }

        if (!platform || (!performRttMeasurement && !performDownloadMeasurement && !performUploadMeasurement))
        {
            var data                = {};
            data.cmd                = 'error';
            data.error_code         = 1;
            data.error_description  = 'Measurement Parameters Missing';
            data                    = JSON.stringify(data);
            this.controlCallback(data);
            return;
        }

        delete wsMeasurement;
        wsMeasurement = null;
        wsMeasurement = this;

        measurementCampaign();
    }

    /**
     * @function measurementStop
     * @public
     * @description API Function to stop a measurement
     */
    this.measurementStop = function()
    {
        measurementStopped = true;

        clearTimeout(gcTimer);
        clearTimeout(wsRttTimer);
        clearTimeout(wsDownloadTimer);
        clearTimeout(wsUploadTimer);

        if (wsControl)      wsControl.measurementStop(JSON.stringify(wsMeasurementParameters));
    }

    /**
     * @function controlCallback
     * @description API Function to Callback the JSON coded measurement Results to the implementing client
     * @param {string} data JSON coded measurement Results
     */
    this.controlCallback = function(data)
    {
        data = JSON.parse(data);

        globalKPIs.cmd                  = data.cmd;
        globalKPIs.msg                  = data.msg;
        globalKPIs.test_case            = data.test_case;
        globalKPIs.error_code           = data.error_code;
        globalKPIs.error_description    = data.error_description;

        if(data.test_case === 'routeToClient')
        {
            routeKPIs.server_client       = data.server_client_route;
            routeKPIs.server_client_hops  = data.server_client_route_hops;
        }

        if (data.test_case === 'rtt')
        {
            rttKPIs             = data;
        }

        if (data.test_case === 'download')
        {
            downloadKPIs        = data;
        }

        if (data.test_case === 'upload')
        {
            uploadKPIs          = data;
        }

        /*
        if (data.test_case === 'download' || data.test_case === 'upload')
        {
        /*
            TODO:
            - check bounds
            - if bounds are overflown on first callback, restart campaign with appr. class
                - do this at least n=2 times, i.e., default and two switches
            - add out_of_bounds flag:
                if (typeof downloadThroughputLowerBoundMbps !== 'undefined' && downloadThroughputLowerBoundMbps * 1000 * 1000 > data.throughput_avg_bps)
                {
                    data.out_of_bounds = true;
                }
                else if (typeof downloadThroughputUpperBoundMbps !== 'undefined' && downloadThroughputUpperBoundMbps * 1000 * 1000 < data.throughput_avg_bps)
                {
                    data.out_of_bounds = true;
                }

                if (typeof uploadThroughputLowerBoundMbps !== 'undefined' && uploadThroughputLowerBoundMbps * 1000 * 1000 > data.throughput_avg_bps)
                {
                    data.out_of_bounds = true;
                }
                else if (typeof uploadThroughputUpperBoundMbps !== 'undefined' && uploadThroughputUpperBoundMbps * 1000 * 1000 < data.throughput_avg_bps)
                {
                    data.out_of_bounds = true;
                }
        }
        */

        if (data.cmd === 'error')
        {
            setEndTimestamps(data.test_case);
            setTimeout(resetWsControl, 200);
        }

        if (data.cmd === 'finish')
        {
            if (data.test_case === 'rtt')
            {
                performedRttMeasurement         = true;
            }

            if (data.test_case === 'download')
            {
                performedDownloadMeasurement    = true;
            }

            if (data.test_case === 'upload')
            {
                performedUploadMeasurement      = true;
            }

            measurementCampaign();
        }

        var kpis = getKPIs();

        if (data.cmd !== 'error' && performRttMeasurement === performedRttMeasurement && performDownloadMeasurement === performedDownloadMeasurement && performUploadMeasurement === performedUploadMeasurement)
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
    };




    /*-------------------------private functions------------------------*/

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

        delete wsControl;
        wsControl = null;

        if (typeof wsMeasurementParameters.singleThread !== 'undefined')
        {
            wsControl   = new WSControlSingleThread();
        }
        else
        {
            wsControl   = new WSControl();
        }
        wsControl.wsMeasurement = this;
        wsControl.callback      = 'wsMeasurement';

        var waitTime            = 3000;
        var waitTimeShort       = 1000;

        if (performRttMeasurement && !performedRttMeasurement)
        {
            setEndTimestamps();

            if (!timestampKPIs.rtt_start) timestampKPIs.rtt_start = (jsTool.getTimestamp() + waitTimeShort) * 1000 * 1000;
            wsMeasurementParameters.testCase = 'rtt';
            wsRttTimer = setTimeout(wsControl.measurementStart, waitTimeShort, JSON.stringify(wsMeasurementParameters));

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
            wsDownloadTimer = setTimeout(startDownload, waitTimeShort);

            return;
        }
        else
        if (performUploadMeasurement && !performedUploadMeasurement)
        {
            setEndTimestamps();
            wsUploadTimer = setTimeout(startUpload, waitTime);

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
        if ((performRttMeasurement && performedRttMeasurement && !timestampKPIs.rtt_end) || test_case === 'rtt')
        {
            timestampKPIs.rtt_end = jsTool.getTimestamp() * 1000 * 1000;
        }
        if ((performDownloadMeasurement && performedDownloadMeasurement && !timestampKPIs.download_end)  || test_case === 'download')
        {
            timestampKPIs.download_end = jsTool.getTimestamp() * 1000 * 1000;
        }
        if ((performUploadMeasurement && performedUploadMeasurement && !timestampKPIs.upload_end)  || test_case === 'upload')
        {
            timestampKPIs.upload_end = jsTool.getTimestamp() * 1000 * 1000;
        }
    }

    function startDownload()
    {
        /*
        TODO:
        - handle speed classes if parameterized or default parameters if not
        */

        if (!timestampKPIs.download_start)
        {
            timestampKPIs.download_start = jsTool.getTimestamp() * 1000 * 1000;
        }
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
        wsControl.measurementStart(JSON.stringify(wsMeasurementParameters));
    }

    function startUpload()
    {
        /*
        TODO:
        - handle speed classes if parameterized or default parameters if not
        */

        if (!timestampKPIs.upload_start)
        {
            timestampKPIs.upload_start = jsTool.getTimestamp() * 1000 * 1000;
        }
        wsMeasurementParameters.testCase = 'upload';
        wsControl.measurementStart(JSON.stringify(wsMeasurementParameters));
    }

    /**
     * @function getKPIs
     * @description Return all measurement Results
     */
    function getKPIs()
    {
        var kpis = {};
        kpis = jsTool.extend(globalKPIs);
        if (!jsTool.isEmpty(rttKPIs))         kpis.rtt_info         = rttKPIs;
        if (!jsTool.isEmpty(downloadKPIs))     kpis.download_info    = downloadKPIs;
        if (!jsTool.isEmpty(uploadKPIs))     kpis.upload_info     = uploadKPIs;
        if (!jsTool.isEmpty(timestampKPIs))    kpis.time_info         = timestampKPIs;
        if (!jsTool.isEmpty(clientKPIs))    kpis.client_info    = clientKPIs;
        if (!jsTool.isEmpty(deviceKPIs))    kpis.device_info    = deviceKPIs;
        if (!jsTool.isEmpty(routeKPIs))        kpis.route_info        = routeKPIs;

        return kpis;
    }

    /**
     * @function resetWsControl
     * @description Reset the wsControl object
     * @private
     */
    function resetWsControl()
    {
        clearInterval(gcTimer);
        wsControl = null;
        delete wsControl;
    }

    /**
     * @function controlCallbackToPlatform
     * @description API Function to Callback the JSON coded measurement Results to the implementing client
     * @param {string} kpis JSON coded measurement Results
     */
    function controlCallbackToPlatform(kpis)
    {
        switch (wsMeasurementParameters.platform)
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
     * @function reportToWeb
     * @description Callback to Web
     * @param {string} kpis JSON coded Measurement Results
     */
    function reportToWeb(kpis)
    {
        iasCallback(kpis);
    }

    /**
     * @function reportToMobile
     * @description Callback to Mobile
     * @param {string} kpis JSON coded Measurement Results
     */
    function reportToMobile(kpis)
    {
        global.messageToNative('tnsReportCallback', kpis);
    }
}
