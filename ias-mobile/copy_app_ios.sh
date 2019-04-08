#!/bin/bash

cp "${PROJECT_DIR}/../common-mobile/modules/nativescript-websockets/websockets.ios.js" "${PROJECT_DIR}/../common-mobile/platforms/ios/commonmobile/app/tns_modules/nativescript-websockets/websockets.js"
cp "${PROJECT_DIR}/../common-mobile/modules/nativescript-websockets/websockets.ios.js" "${PROJECT_DIR}/../common-mobile/node_modules/nativescript-websockets/websockets.ios.js"
mkdir -p "${BUILT_PRODUCTS_DIR}/${UNLOCALIZED_RESOURCES_FOLDER_PATH}/app"
cp -R "${PROJECT_DIR}/../common-mobile/platforms/ios/commonmobile/app/tns_modules" "${BUILT_PRODUCTS_DIR}/${UNLOCALIZED_RESOURCES_FOLDER_PATH}/app"
