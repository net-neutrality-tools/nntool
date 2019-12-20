#/*!
#    \file CMakeLists.txt
#    \author alladin-IT GmbH <berec@alladin.at>
#    \date Last update: 2019-12-20
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

include(ExternalProject)

set(libnopoll_URL https://github.com/net-neutrality-tools/nopoll)
set(libnopoll_TAG origin/master)

set(libnopoll_BUILD ${CMAKE_CURRENT_BINARY_DIR}/libnopoll/src/libnopoll-build)
set(libnopoll_INCLUDE_DIR ${libnopoll_BUILD}/include)

ExternalProject_Add(libnopoll
	PREFIX libnopoll
  GIT_REPOSITORY ${libnopoll_URL}
  GIT_TAG ${libnopoll_TAG}
	BUILD_IN_SOURCE 1
  CONFIGURE_COMMAND COMMAND <SOURCE_DIR>/autogen.sh --prefix=${libnopoll_BUILD}
													  --includedir=${libnopoll_INCLUDE_DIR}
													  --libdir=${libnopoll_BUILD}
)

