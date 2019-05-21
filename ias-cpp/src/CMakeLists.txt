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

set(CMAKE_BUILD_TYPE Debug)

#---------------------------------------------------------------------------------

include_directories(${CMAKE_CURRENT_BINARY_DIR})

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../../ias-libnntool/)

add_library(ias-client SHARED
	iasclient.cpp 
	type.cpp
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
	#-lssl
	android
	log
)

INCLUDE(CPack)