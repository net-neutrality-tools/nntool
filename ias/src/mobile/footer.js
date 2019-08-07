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
 *      \date Last update: 2019-07-25
 *      \note Copyright (c) 2018 - 2019 zafaco GmbH. All rights reserved.
 */




global.measurementStart = function(measurementParameters)
{
    console.log('Measurement Parameters: \n' + JSON.stringify(measurementParameters));

    delete ias;
    ias = null;
    ias = new Ias();
    ias.measurementStart(JSON.stringify(measurementParameters));
};

global.measurementStop = function()
{
    ias.measurementStop();
};

global.console.log = function(message)
{
    global.messageToNative('tnsConsoleLogCallback', message)
};

global.messageToNative = function(channel, message)
{
    if (platformModule.isIOS)
    {
        var notificationCenter = utils.ios.getter(NSNotificationCenter, NSNotificationCenter.defaultCenter);
        var dataDictionary = NSDictionary.dictionaryWithDictionary(
        {
                message: message
        })
        notificationCenter.postNotificationNameObjectUserInfo(channel, null, dataDictionary);
    }
};
