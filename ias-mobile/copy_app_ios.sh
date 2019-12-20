#!/bin/bash

#/*!
#    \file copy_app_ios.sh
#    \author zafaco GmbH <info@zafaco.de>
#    \date Last update: 2019-12-20
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

cp "${PROJECT_DIR}/../ias-mobile/modules/nativescript-websockets/websockets.ios.js" "${PROJECT_DIR}/../ias-mobile/platforms/ios/iasmobile/app/tns_modules/nativescript-websockets/websockets.js"
cp "${PROJECT_DIR}/../ias-mobile/modules/nativescript-websockets/websockets.ios.js" "${PROJECT_DIR}/../ias-mobile/node_modules/nativescript-websockets/websockets.ios.js"
mkdir -p "${BUILT_PRODUCTS_DIR}/${UNLOCALIZED_RESOURCES_FOLDER_PATH}/app"
cp -R "${PROJECT_DIR}/../ias-mobile/platforms/ios/iasmobile/app/tns_modules" "${BUILT_PRODUCTS_DIR}/${UNLOCALIZED_RESOURCES_FOLDER_PATH}/app"
