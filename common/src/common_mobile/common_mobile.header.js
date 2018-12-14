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

/* global global require NSNotificationCenter NSDictionary */

require('globals');
require('nativescript-websockets');

var utils = require('utils/utils');
var application = require('application');
var platform = require('platform');
var now = require('performance-now');

var performance = {now: now.bind()};

global.wsMeasurement;




/*-----------------------------------------------------------------------------*/
