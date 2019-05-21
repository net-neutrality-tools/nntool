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

set(CMAKE_BUILD_TYPE Debug)

#---------------------------------------------------------------------------------

add_library (nntool SHARED 
				typedef.cpp 
				tool.cpp
				connection.cpp
				configmanager.cpp 
				basisthread.cpp
				trace.cpp
				http.cpp
				json11.cpp
				sha1.cpp
) 

target_link_libraries(nntool)
