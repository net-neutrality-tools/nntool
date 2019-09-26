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
# *      \date Last update: 2019-04-30
# *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
# */

cmake_minimum_required(VERSION 3.6)

project(ias-client)

#---------------------------------------------------------------------------------

if (NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Release)
endif()

#---------------------------------------------------------------------------------

include_directories(${CMAKE_CURRENT_BINARY_DIR})

set(libnntooldir ${CMAKE_CURRENT_SOURCE_DIR}/../../ias-libnntool/)

include_directories(${libnntooldir})

if (DEFINED ANDROID_ABI)
    message("Building library for android client")

    include_directories(${CMAKE_CURRENT_BINARY_DIR}/../ias-libnntool/boringssl/src/boringssl/include)

    add_library(ias-client SHARED
        iasclient.cpp
        callback.cpp
        timer.cpp
        measurement.cpp
        android_connector.cpp
        tests/ping.cpp
        tests/download.cpp
        tests/upload.cpp
        tests/upload_sender.cpp
    )

    TARGET_LINK_LIBRARIES(ias-client
        ${CMAKE_THREAD_LIBS_INIT}
        nntool
        android
        log
    )
else()
    message("Building executable")

    add_executable(ias-client
        iasclient.cpp
        #type.cpp
        callback.cpp
        timer.cpp
        measurement.cpp
        tests/ping.cpp
        tests/download.cpp
        tests/upload.cpp
        tests/upload_sender.cpp
    )

    TARGET_LINK_LIBRARIES(ias-client
        ${CMAKE_THREAD_LIBS_INIT}
        nntool
        -lssl
    )
endif()

INCLUDE(CPack)

#---------------------------------------------------------------------------------

#
# Unit Tests with Catch2
#

IF(CMAKE_BUILD_TYPE MATCHES DEBUG)

    # TODO: add flag to enable/disable unit tests
    # TODO: use something like https://github.com/eranpeer/FakeIt for mocking

    set(CMAKE_CXX_FLAGS "-fprofile-arcs -ftest-coverage -O0 ${CMAKE_CXX_FLAGS}")
    enable_testing()

    include_directories(external)

    add_executable(
        ias_client_test
        iasclient_test.cpp
        timer_test.cpp
        download_test.cpp
        upload_test.cpp
        ping_test.cpp
        
    )

    if (DEFINED ANDROID_ABI)

        target_link_libraries(
            ias_client_test
            ias-client
        )

    else()

        add_library(ias-client-lib SHARED
            iasclient.cpp
            #type.cpp
            callback.cpp
            timer.cpp
            measurement.cpp
            tests/ping.cpp
            tests/download.cpp
            tests/upload.cpp
            tests/upload_sender.cpp
        )

        TARGET_LINK_LIBRARIES(ias-client-lib
            ${CMAKE_THREAD_LIBS_INIT}
            nntool
        )

        target_link_libraries(
            ias_client_test
            ias-client-lib
        )

    endif()

else ()
    set(CMAKE_CXX_FLAGS "-O3 ${CMAKE_CXX_FLAGS}")
ENDIF(CMAKE_BUILD_TYPE MATCHES DEBUG)
