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

if (NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Release)
endif()

#---------------------------------------------------------------------------------

if (DEFINED ANDROID_ABI)
    message ("building libnntool for android")
    message("${CMAKE_CURRENT_LIST_DIR}")

    if (NOT DEFINED ANDROID_NDK)
        MESSAGE(FATAL_ERROR "ERROR: 'ANDROID_NDK' not exported; export the path to the android ndk")
    endif()

    include(boringssl.cmake)
    include_directories(${boringssl_INCLUDE_DIR})
endif()

add_library (nntool SHARED
				tool.cpp
				connection.cpp
				configmanager.cpp 
				basisthread.cpp
				trace.cpp
				http.cpp
				json11.cpp
				sha1.cpp
)

if (DEFINED ANDROID_ABI)
    add_dependencies(nntool boringssl)
    target_link_libraries(
        nntool
        ${boringssl_BUILD}/ssl/libssl.a
        ${boringssl_BUILD}/crypto/libcrypto.a
    )
else()
    # TODO: also build non-android version with boringssl (libssl-dev can then be removed from ci image)
    target_link_libraries(
        nntool
        ssl
        crypto
    )
endif()

#---------------------------------------------------------------------------------

#
# Unit Tests with Catch2
#

# TODO: add flag to enable/disable unit tests
# TODO: use something like https://github.com/eranpeer/FakeIt for mocking

set(CMAKE_CXX_FLAGS "-fprofile-arcs -ftest-coverage -O0 ${CMAKE_CXX_FLAGS}")
enable_testing()

include_directories(external)

add_executable(
        nntool_test
        libnntool_test.cpp
        http_test.cpp
        tool_test.cpp
)

target_link_libraries(
    nntool_test
    nntool
)
