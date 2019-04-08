#!/bin/bash

cp "${PROJECT_DIR}/../ias-mobile/modules/nativescript-websockets/websockets.ios.js" "${PROJECT_DIR}/../ias-mobile/platforms/ios/iasmobile/app/tns_modules/nativescript-websockets/websockets.js"
cp "${PROJECT_DIR}/../ias-mobile/modules/nativescript-websockets/websockets.ios.js" "${PROJECT_DIR}/../ias-mobile/node_modules/nativescript-websockets/websockets.ios.js"
mkdir -p "${BUILT_PRODUCTS_DIR}/${UNLOCALIZED_RESOURCES_FOLDER_PATH}/app"
cp -R "${PROJECT_DIR}/../ias-mobile/platforms/ios/iasmobile/app/tns_modules" "${BUILT_PRODUCTS_DIR}/${UNLOCALIZED_RESOURCES_FOLDER_PATH}/app"
