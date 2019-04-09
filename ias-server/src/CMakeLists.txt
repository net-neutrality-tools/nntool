#/*
# *********************************************************************************
# *                                                                               *
# *       ..--== zafaco GmbH ==--..                                               *
# *                                                                               *
# *       Website: http://www.zafaco.de                                           *
# *                                                                               *
# *       Copyright 2019                                                          *
# *                                                                               *
# *********************************************************************************
# */

#/*!
# *      \author zafaco GmbH <info@zafaco.de>
# *      \date Last update: 2019-04-08
# *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
# */


cmake_minimum_required(VERSION 2.6)

project(ias-server)

#---------------------------------------------------------------------------------

set(CMAKE_BUILD_TYPE Release)

#---------------------------------------------------------------------------------

include_directories(${CMAKE_CURRENT_BINARY_DIR})

add_executable(ias-server
    basisthread.cpp
    trace.cpp
    connection.cpp
    hthandler.cpp
    libs/json11.cpp
    libs/sha1.cpp
    tool.cpp
    wsserver.cpp
    wshandler.cpp
    wslistener.cpp
    htlistener.cpp
)

TARGET_LINK_LIBRARIES(ias-server
    ${CMAKE_THREAD_LIBS_INIT}
    -llog4cpp
    -lssl
    -lcrypto
    -lnopoll
)

INCLUDE(CPack)