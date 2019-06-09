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
# *      \date Last update: 2019-05-03
# *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
# */


cmake_minimum_required(VERSION 3.6)

project(ias-server)

#---------------------------------------------------------------------------------

set(CMAKE_BUILD_TYPE Release)

#---------------------------------------------------------------------------------

include_directories(${CMAKE_CURRENT_BINARY_DIR})

# include libnopoll headers
include_directories(${libnopoll_INCLUDE_DIR}/nopoll)

add_executable(ias-server
    iasserver.cpp
    tcpserver.cpp
    tcphandler.cpp
    tcptraceroutehandler.cpp
    udpserver.cpp
)

TARGET_LINK_LIBRARIES(ias-server
    ${CMAKE_THREAD_LIBS_INIT}
    nntool
    -llog4cpp
    -lssl
    -lcrypto
    ${libnopoll_BUILD}/libnopoll.a
)

INCLUDE(CPack)
