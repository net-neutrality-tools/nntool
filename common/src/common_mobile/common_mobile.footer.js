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

function getMeasurementParameters(testCase, platform)
{
    var wsConfig            = new WSConfig();
    measurementParameters   = null;
    measurementParameters   = wsConfig.getSpeedParameters(testCase, platform);

    if (typeof measurementParameters.removeKPIs !== 'undefined')
    {
        for (var indexToRemove in measurementParameters.removeKPIs)
        {
            removeKPIs.push(measurementParameters.removeKPIs[indexToRemove]);
        }
    }

    return measurementParameters;
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
