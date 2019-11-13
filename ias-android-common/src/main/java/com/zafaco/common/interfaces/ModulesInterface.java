package com.zafaco.common.interfaces;

/*!
    \file ModulesInterface.java
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

