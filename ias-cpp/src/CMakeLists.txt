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


cmake_minimum_required(VERSION 2.8.2)

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

    include_directories(${libnntooldir}/build/boringssl/include)

    add_library(boringssl STATIC IMPORTED)
    set_target_properties(
        boringssl
        PROPERTIES IMPORTED_LOCATION
        ${libnntooldir}/build/boringssl/build/${ANDROID_ABI}/ssl/libssl.a
    )

    add_library(crypto STATIC IMPORTED)
    set_target_properties(
        crypto
        PROPERTIES IMPORTED_LOCATION
        ${libnntooldir}/build/boringssl/build/${ANDROID_ABI}/crypto/libcrypto.a
    )

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
        boringssl
        crypto
        android
        log
    )
else()
    message("Building executable")

    add_executable(ias-client
        iasclient.cpp
        type.cpp
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