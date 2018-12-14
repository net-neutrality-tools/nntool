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




function Speed()
{
    this.wsMeasurement;

    this.measurementStart = function(measurementParameters)
    {
        console.log('Measurement Parameters: \n' + JSON.stringify(measurementParameters));

        delete wsMeasurement;
        wsMeasurement = null;
        wsMeasurement = new WSMeasurement();
        wsMeasurement.measurementControl(JSON.stringify(measurementParameters));
    }

    this.measurementStop = function()
    {
        wsMeasurement.measurementControl(JSON.stringify({cmd:'stop'}));
    }
}