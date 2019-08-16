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
 *      \date Last update: 2019-07-29
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */


var jsTool = new JSTool();




/*-------------------------class WSWorker------------------------*/

/**
 * @class WSWorker
 * @description WebSocket Worker Class
 */
function WSWorker()
{
    this.wsControl      = 'undefined';
    this.wsID;

    var ulDataSize		= 512345;

    var  useWebWorkers	= false;
