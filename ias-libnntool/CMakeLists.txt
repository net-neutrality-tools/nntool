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
    target_link_libraries(nntool)
endif()
