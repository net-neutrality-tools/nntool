#/*!
#    \file CMakeLists.txt
#    \author zafaco GmbH <info@zafaco.de>
#    \author alladin-IT GmbH <berec@alladin.at>
#    \date Last update: 2019-11-13
#
#    Copyright (C) 2016 - 2019 zafaco GmbH
#
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU Affero General Public License version 3 
#    as published by the Free Software Foundation.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU Affero General Public License for more details.
#
#    You should have received a copy of the GNU Affero General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
#*/

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

add_library (
    nntool SHARED
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

IF(CMAKE_BUILD_TYPE MATCHES DEBUG)

    # Build tests only if library is not included in other modules (e.g. ias-cpp or ias-server).
    # Therefore, check if both NNTOOL_SERVER and NNTOOL_CLIENT are not defined before adding tests.
    # TODO: add flag to enable/disable unit tests

    if (NOT DEFINED NNTOOL_SERVER AND NOT DEFINED NNTOOL_CLIENT)

        # TODO: use something like https://github.com/eranpeer/FakeIt for mocking

        set(CMAKE_CXX_FLAGS "-fprofile-arcs -ftest-coverage -O0 ${CMAKE_CXX_FLAGS}")
        enable_testing()

        include_directories(external)

        add_executable(
            nntool_test
            libnntool_test.cpp
            http_test.cpp
            tool_test.cpp
            connection_test.cpp
            configmanager_test.cpp
            json11_test.cpp
            sha1_test.cpp
        )

        target_link_libraries(
            nntool_test
            nntool
        )

    endif()

ENDIF(CMAKE_BUILD_TYPE MATCHES DEBUG)
