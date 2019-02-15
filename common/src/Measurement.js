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
 *      \date Last update: 2019-02-11
 *      \note Copyright (c) 2018 - 2019 zafaco GmbH. All rights reserved.
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
    var speedVersion                        = '1.0.0';
    
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
    var globalKPIs                          = {};
    var rttKPIs                             = {};
    var downloadKPIs                        = {};
    var uploadKPIs                          = {};
    var timestampKPIs                       = {};
    var clientKPIs                          = {};
    var deviceKPIs                          = {};
    var routeKPIs                           = {};
    
    var wsRttTimer                          = 0;
    var wsDownloadTimer                     = 0;
    var wsUploadTimer                       = 0;
    
    var measurementStopped                  = false;
    
    var gcTimer;
    var gcTimerInterval                     = 10;
    
    var downloadThroughputLowerBoundMbps    = 'undefined';
    var downloadThroughputUpperBoundMbps    = 'undefined';
    var uploadThroughputLowerBoundMbps      = 'undefined';
    var uploadThroughputUpperBoundMbps      = 'undefined';
    
    
    
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
         
            globalKPIs.start_time            = jsTool.getFormattedDate();
            
            clientKPIs.timezone                = jsTool.getTimezone();
            clientKPIs.type                 = platform.toUpperCase();
            clientKPIs.speed_version        = speedVersion;
            
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
                    var data = {};
                    data.cmd                                = 'error';
                    globalKPIs.cmd                            = 'error';     
                    globalKPIs.error_code                     = 5;
                    globalKPIs.error_description              = 'WebSockets are only partially supported by your browser';
                    data                                    = JSON.stringify(data);
                    this.controlCallback(data);
                    return;
                }
            }
            
            if (typeof require !== 'undefined' && platformModule.isIOS)
            {
                gcTimer = setInterval(function ()
                {
                    utils.GC();
                }, gcTimerInterval);
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
                    cookie = jsTool.generateRandomData(64, true, false);
                }

                jsTool.setCookie(cookieName + '_id', cookie, 365);
                clientKPIs.cookie = cookie;
            }
        }

        if (typeof wsMeasurementParameters.performRttMeasurement !== 'undefined')           performRttMeasurement           = Boolean(wsMeasurementParameters.performRttMeasurement);
        if (typeof wsMeasurementParameters.performDownloadMeasurement !== 'undefined')      performDownloadMeasurement      = Boolean(wsMeasurementParameters.performDownloadMeasurement);
        if (typeof wsMeasurementParameters.performUploadMeasurement !== 'undefined')        performUploadMeasurement        = Boolean(wsMeasurementParameters.performUploadMeasurement);
        if (typeof wsMeasurementParameters.performRouteToClientLookup !== 'undefined')      performRouteToClientLookup      = Boolean(wsMeasurementParameters.performRouteToClientLookup);
        if (typeof wsMeasurementParameters.routeToClientTargetPort !== 'undefined')         routeToClientTargetPort         = Number(wsMeasurementParameters.routeToClientTargetPort);
        
        if (typeof wsMeasurementParameters.downloadThroughputLowerBoundMbps !== 'undefined') downloadThroughputLowerBoundMbps = Number(wsMeasurementParameters.downloadThroughputLowerBoundMbps);
        if (typeof wsMeasurementParameters.downloadThroughputUpperBoundMbps !== 'undefined') downloadThroughputUpperBoundMbps = Number(wsMeasurementParameters.downloadThroughputUpperBoundMbps);
        if (typeof wsMeasurementParameters.uploadThroughputLowerBoundMbps !== 'undefined') uploadThroughputLowerBoundMbps = Number(wsMeasurementParameters.uploadThroughputLowerBoundMbps);
        if (typeof wsMeasurementParameters.uploadThroughputUpperBoundMbps !== 'undefined') uploadThroughputUpperBoundMbps = Number(wsMeasurementParameters.uploadThroughputUpperBoundMbps);

        if (typeof window !== 'undefined' && !window.WebSocket)
        {
            var data = {};
            data.cmd                          = 'error';
            globalKPIs.cmd                    = 'error';     
            globalKPIs.error_code             = 3;
            globalKPIs.error_description      = 'WebSockets are not supported by your browser';
            data                              = JSON.stringify(data);
            this.controlCallback(data);
            return;
        }
        
        if (!wsMeasurementParameters.cmd || !platform || (!performRttMeasurement && !performDownloadMeasurement && !performUploadMeasurement))
        {
            var data = {};
            data.cmd                          = 'error';
            globalKPIs.cmd                    = 'error';     
            globalKPIs.error_code             = 1;
            globalKPIs.error_description      = 'Measurement Parameters Missing';
            data                              = JSON.stringify(data);
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
                
                clearTimeout(gcTimer);
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
                
                if (typeof downloadThroughputLowerBoundMbps !== 'undefined' && downloadThroughputLowerBoundMbps * 1000 * 1000 > data.throughput_avg_bps)
                {
                    data.out_of_bounds = true;
                }
                else if (typeof downloadThroughputUpperBoundMbps !== 'undefined' && downloadThroughputUpperBoundMbps * 1000 * 1000 < data.throughput_avg_bps)
                {
                    data.out_of_bounds = true;
                }
            }
            
            if (data.test_case === 'upload')
            {
                performedUploadMeasurement              = true;
                
                if (typeof uploadThroughputLowerBoundMbps !== 'undefined' && uploadThroughputLowerBoundMbps * 1000 * 1000 > data.throughput_avg_bps)
                {
                    data.out_of_bounds = true;
                }
                else if (typeof uploadThroughputUpperBoundMbps !== 'undefined' && uploadThroughputUpperBoundMbps * 1000 * 1000 < data.throughput_avg_bps)
                {
                    data.out_of_bounds = true;
                }
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
            if (!timestampKPIs.rtt_start) timestampKPIs.rtt_start = (jsTool.getTimestamp() + waitTimeShort) * 1000 * 1000;
            wsMeasurementParameters.testCase = 'rtt';
            wsRttTimer = setTimeout(wsControl.measurementSetup, waitTimeShort, JSON.stringify(wsMeasurementParameters));
            
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
            
            if (!timestampKPIs.download_start) timestampKPIs.download_start = (jsTool.getTimestamp() + waitTimeDownload) * 1000 * 1000;
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
            
            return;
        }
        else
        if (performUploadMeasurement && !performedUploadMeasurement)
        {
            setEndTimestamps();
            if (!timestampKPIs.upload_start) timestampKPIs.upload_start = (jsTool.getTimestamp() + waitTime) * 1000 * 1000;
            wsMeasurementParameters.testCase = 'upload';
            wsUploadTimer = setTimeout(wsControl.measurementSetup, waitTime, JSON.stringify(wsMeasurementParameters));
            
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
