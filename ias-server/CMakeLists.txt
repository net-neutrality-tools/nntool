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
# *      \date Last update: 2019-04-08
# *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
# */


cmake_minimum_required(VERSION 2.6)

project(ias-server)

#---------------------------------------------------------------------------------

MESSAGE(STATUS "Looking for pthreads")
FIND_PACKAGE(Threads REQUIRED)
IF (NOT THREADS_FOUND)
	MESSAGE(FATAL_ERROR "ERROR: try -> apt install build-essentials")
ENDIF (NOT THREADS_FOUND)

#---------------------------------------------------------------------------------

MESSAGE(STATUS "Looking for pkg-config")
FIND_PROGRAM(PKG_CONFIG NAMES pkg-config PATHS /usr/bin/)
IF (${PKG_CONFIG} STREQUAL PKG_CONFIG-NOTFOUND)
	MESSAGE(FATAL_ERROR "ERROR: try -> apt install pkg-config")
ENDIF (${PKG_CONFIG} STREQUAL PKG_CONFIG-NOTFOUND)

#---------------------------------------------------------------------------------

MESSAGE(STATUS "Looking for libnopoll")
FIND_PROGRAM(LIBNOPOLL NAMES libnopoll.so PATHS /usr/local/lib/)
IF (${LIBNOPOLL} STREQUAL LIBNOPOLL-NOTFOUND)
	MESSAGE(FATAL_ERROR "ERROR: libnopoll missing")
ENDIF (${LIBNOPOLL} STREQUAL LIBNOPOLL-NOTFOUND)

#---------------------------------------------------------------------------------

set(dir ${CMAKE_CURRENT_SOURCE_DIR}/build)
set(EXECUTABLE_OUTPUT_PATH ${dir} CACHE PATH "Build directory" FORCE)
set(LIBRARY_OUTPUT_PATH ${dir} CACHE PATH "Build directory" FORCE)

set ( CMAKE_VERBOSE_MAKEFILE true )
add_definitions(-std=c++11)
add_definitions(-g)
add_definitions(-Wall)
add_definitions("$$(pkg-config nopoll --cflags)")
add_definitions("$$(pkg-config nopoll --libs)")

add_subdirectory (src)

INSTALL(PROGRAMS build/ias-server DESTINATION bin )

INCLUDE (InstallRequiredSystemLibraries)
SET (CPACK_COMPONENTS_ALL Libraries ApplicationData)
        
SET (CPACK_SET_DESTDIR ON)
SET (CPACK_PACKAGING_INSTALL_PREFIX "/tmp")
SET (CPACK_GENERATOR "DEB")

SET (CPACK_DEBIAN_PACKAGE_PRIORITY "high")
SET (CPACK_DEBIAN_PACKAGE_SECTION "measurement")
SET (CPACK_DEBIAN_ARCHITECTURE ${CMAKE_SYSTEM_PROCESSOR})
set (CPACK_DEBIAN_PACKAGE_SHLIBDEPS ON)

SET (CPACK_PACKAGE_DESCRIPTION "WebSocket Measurement Server (ias-server)")
SET (CPACK_PACKAGE_DESCRIPTION_SUMMARY "WebSocket Measurement Server (ias-server)")
SET (CPACK_DEBIAN_PACKAGE_MAINTAINER "zafaco GmbH")
SET (CPACK_PACKAGE_FILE_NAME "${CMAKE_PROJECT_NAME}_${CPACK_DEBIAN_ARCHITECTURE}")

SET (CPACK_DEBIAN_PACKAGE_CONTROL_EXTRA debian/postinst;debian/preinst;debian/prerm)

INCLUDE(CPack)