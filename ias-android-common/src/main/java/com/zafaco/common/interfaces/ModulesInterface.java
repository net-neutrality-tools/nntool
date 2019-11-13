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
 * Interface ModulesInterface
 */
public interface ModulesInterface
{
    /**
     * Method receiveString
     * @param message
     */
    void receiveString(String message);

    /**
     * Method receiveData
     * @param message
     */
    void receiveData(JSONObject message);
}

