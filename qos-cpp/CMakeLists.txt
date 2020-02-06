#/*!
#    \file CMakeLists.txt
#    \author alladin-IT GmbH <berec@alladin.at>
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

project(qos-client)

include (ExternalProject)

if (NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Release)
endif()

#set(dir ${CMAKE_CURRENT_SOURCE_DIR}/build)
#set(EXECUTABLE_OUTPUT_PATH ${dir} CACHE PATH "Build directory" FORCE)
#set(LIBRARY_OUTPUT_PATH ${dir} CACHE PATH "Build directory" FORCE)

#set ( CMAKE_VERBOSE_MAKEFILE true )
add_definitions(-std=c++14)
set(CMAKE_CXX_FLAGS "-W")
set(CMAKE_CXX_FLAGS_DEBUG "-g")
set(CMAKE_CXX_FLAGS_RELEASE "-O3")
add_definitions(-DNNTOOL)
add_definitions(-DNNTOOL_CLIENT)

set(gstreamer_URL https://gitlab.freedesktop.org/gstreamer/gstreamer)
set(gstreamer_TAG origin/master)

set(gstreamer_BUILD ${CMAKE_CURRENT_BINARY_DIR}/gstreamer/src/gstreamer-build)


ExternalProject_Add(gstreamer
        PREFIX gstreamer
        GIT_REPOSITORY ${gstreamer_URL}
        GIT_TAG ${gstreamer_TAG}
        INSTALL_COMMAND ""
        CMAKE_ARGS
        -DCMAKE_INSTALL_PREFIX=${gstreamer_BUILD}
        -DANDROID_ABI=${ANDROID_ABI}
        -DCMAKE_TOOLCHAIN_FILE=${ANDROID_NDK}/build/cmake/android.toolchain.cmake
        -DANDROID_NATIVE_API_LEVEL=21 -GNinja ../..
        )

add_library(qos-client SHARED
        src/main.cpp
        )

add_dependencies(
    qos-client
    gstreamer
)
