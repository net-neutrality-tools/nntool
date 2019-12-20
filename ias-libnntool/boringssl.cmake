#/*!
#    \file CMakeLists.txt
#    \author alladin-IT GmbH <berec@alladin.at>
#    \date Last update: 2019-11-13
#
#    Copyright (C) 2016 - 2019 zafaco GmbH
#    Copyright (C) 2019 alladin-IT GmbH
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

include (ExternalProject)

set(boringssl_URL https://boringssl.googlesource.com/boringssl)
set(boringssl_TAG origin/master)

set(boringssl_INCLUDE_DIR ${CMAKE_CURRENT_BINARY_DIR}/boringssl/src/boringssl/include)
set(boringssl_BUILD ${CMAKE_CURRENT_BINARY_DIR}/boringssl/src/boringssl-build)

set(boringssl_STATIC_LIBRARIES
    ${boringssl_BUILD}/ssl/libssl.a
    ${boringssl_BUILD}/crypto/libcrypto.a
)

ExternalProject_Add(boringssl
    PREFIX boringssl
    GIT_REPOSITORY ${boringssl_URL}
    GIT_TAG ${boringssl_TAG}
    BUILD_BYPRODUCTS ${boringssl_STATIC_LIBRARIES}
    INSTALL_COMMAND ""
    CMAKE_ARGS
        -DCMAKE_INSTALL_PREFIX=${boringssl_BUILD}
        -DANDROID_ABI=${ANDROID_ABI} 
        -DCMAKE_TOOLCHAIN_FILE=${ANDROID_NDK}/build/cmake/android.toolchain.cmake 
        -DANDROID_NATIVE_API_LEVEL=21 -GNinja ../..
)
