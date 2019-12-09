/*!
    \file AndroidJniCppException.java
    \author zafaco GmbH <info@zafaco.de>
    \author alladin-IT GmbH <info@alladin.at>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH
    Copyright (C) 2019 alladin-IT GmbH

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

package com.zafaco.speed.jni.exception;

public class AndroidJniCppException extends Exception {
    public AndroidJniCppException() {
        super();
    }

    public AndroidJniCppException(String message) {
        super(message);
    }

    public AndroidJniCppException(String message, Throwable cause) {
        super(message, cause);
    }
}
