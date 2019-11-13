package com.zafaco.common.interfaces;

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
 *      \date Last update: 2019-01-03
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

import org.json.JSONObject;

/**
 * Interface GenericInterface
 */
public interface GenericInterface
{
    /**
     * Method reportCallback
     * @param message
     */
    void reportCallback(JSONObject message);

    /**
     * Method consoleCallback
     * @param message
     */
    void consoleCallback(String message);
}

