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

set(CMAKE_BUILD_TYPE Release)

#---------------------------------------------------------------------------------

if (DEFINED ANDROID_ABI)


    file (MAKE_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}/libs/boringssl/build)

    set(build_dir ${CMAKE_CURRENT_LIST_DIR}/libs/boringssl/build/${ANDROID_ABI})

    file(MAKE_DIRECTORY ${build_dir})

    include_directories(${CMAKE_CURRENT_LIST_DIR}/libs/boringssl/include)

if (NOT EXISTS "${build_dir}/ssl/libssl.a" OR NOT EXISTS "${build_dir}/crypto/libcrypto.a")
    execute_process(
        COMMAND cmake -DANDROID_ABI=${ANDROID_ABI} -DCMAKE_TOOLCHAIN_FILE=${ANDROID_NDK}/build/cmake/android.toolchain.cmake -DANDROID_NATIVE_API_LEVEL=21 -GNinja ../..
        WORKING_DIRECTORY ${build_dir}
    )
    execute_process(
        COMMAND ninja
        WORKING_DIRECTORY ${build_dir}
    )
endif()

    add_library(boringssl STATIC IMPORTED)
    set_target_properties(
        boringssl
        PROPERTIES IMPORTED_LOCATION
        ${build_dir}/ssl/libssl.a
    )

    add_library(crypto STATIC IMPORTED)
    set_target_properties(
        crypto
        PROPERTIES IMPORTED_LOCATION
        ${build_dir}/crypto/libcrypto.a
    )

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
    target_link_libraries(
        nntool
        boringssl
        crypto
    )
else()
    target_link_libraries(nntool)
endif()