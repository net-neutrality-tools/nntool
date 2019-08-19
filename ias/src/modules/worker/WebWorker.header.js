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
 *      \date Last update: 2019-07-30
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */




/*-------------------------class WSWebWorker------------------------*/

/**
 * @class WSWebWorker
 * @description WebSocket Web Worker Class
 */
var WSWebWorker = function ()
{
    this.wsControl	= 'undefined';

    return(this);
};

var ulDataSize		= 2246915;

var useWebWorkers	= true;
var jsTool;
