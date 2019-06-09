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

project(ias-client)

#---------------------------------------------------------------------------------

MESSAGE(STATUS "Looking for pthreads")
FIND_PACKAGE(Threads REQUIRED)
IF (NOT THREADS_FOUND)
	MESSAGE(FATAL_ERROR "ERROR: try -> apt install build-essentials")
ENDIF (NOT THREADS_FOUND)

#---------------------------------------------------------------------------------

if (NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Release)
endif()

set(dir ${CMAKE_CURRENT_SOURCE_DIR}/build)
set(EXECUTABLE_OUTPUT_PATH ${dir} CACHE PATH "Build directory" FORCE)
set(LIBRARY_OUTPUT_PATH ${dir} CACHE PATH "Build directory" FORCE)
 
set ( CMAKE_VERBOSE_MAKEFILE true )
add_definitions(-std=c++14)
set(CMAKE_CXX_FLAGS "-W")
set(CMAKE_CXX_FLAGS_DEBUG "-g")
set(CMAKE_CXX_FLAGS_RELEASE "-O3")
add_definitions(-DNNTOOL)
add_definitions(-DNNTOOL_CLIENT)

set(NNTOOL_CLIENT true)

add_subdirectory (${CMAKE_CURRENT_SOURCE_DIR}/../ias-libnntool ${CMAKE_CURRENT_BINARY_DIR}/ias-libnntool)
add_subdirectory (src)

INSTALL(PROGRAMS build/ias-client DESTINATION bin )
INSTALL(PROGRAMS build/libnntool.so DESTINATION /usr/lib )

INCLUDE (InstallRequiredSystemLibraries)
SET (CPACK_COMPONENTS_ALL Libraries ApplicationData)
        
SET (CPACK_SET_DESTDIR ON)
SET (CPACK_PACKAGING_INSTALL_PREFIX "/tmp")
SET (CPACK_GENERATOR "DEB")

SET (CPACK_DEBIAN_PACKAGE_PRIORITY "high")
SET (CPACK_DEBIAN_PACKAGE_SECTION "measurement")
SET (CPACK_DEBIAN_ARCHITECTURE ${CMAKE_SYSTEM_PROCESSOR})
set (CPACK_DEBIAN_PACKAGE_SHLIBDEPS ON)

SET (CPACK_PACKAGE_DESCRIPTION "ias client cpp (ias-client)")
SET (CPACK_PACKAGE_DESCRIPTION_SUMMARY "ias client cpp (ias-client)")
SET (CPACK_DEBIAN_PACKAGE_MAINTAINER "zafaco GmbH")
SET (CPACK_PACKAGE_FILE_NAME "${CMAKE_PROJECT_NAME}_${CPACK_DEBIAN_ARCHITECTURE}")

SET (CPACK_DEBIAN_PACKAGE_CONTROL_EXTRA debian/postinst;debian/preinst;debian/prerm)

INCLUDE(CPack)