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
# *      \date Last update: 2019-07-30
# *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
# */


cmake_minimum_required(VERSION 3.6)

project(ias-server)

#---------------------------------------------------------------------------------

if (NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Release)
endif()

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
    load_monitoring.cpp
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

#---------------------------------------------------------------------------------

#
# Unit Tests with Catch2
#

IF(CMAKE_BUILD_TYPE MATCHES DEBUG)

    # TODO: add flag to enable/disable testing
    # TODO: use something like https://github.com/eranpeer/FakeIt for mocking

    set(CMAKE_CXX_FLAGS "-fprofile-arcs -ftest-coverage -O0 ${CMAKE_CXX_FLAGS}")
    enable_testing()

    include_directories(external)

    add_library(ias-server-lib SHARED
        iasserver.cpp
        tcpserver.cpp
        tcphandler.cpp
        tcptraceroutehandler.cpp
        udpserver.cpp
    )

    TARGET_LINK_LIBRARIES(ias-server-lib
        ${CMAKE_THREAD_LIBS_INIT}
        nntool
        -llog4cpp
        -lssl
        -lcrypto
        ${libnopoll_BUILD}/libnopoll.so
    )

    add_executable(
        ias_server_test
        iasserver_test.cpp
        tcphandler_test.cpp
        udpserver_test.cpp
    )

    target_link_libraries(
        ias_server_test
        ias-server-lib
    )

ENDIF(CMAKE_BUILD_TYPE MATCHES DEBUG)
