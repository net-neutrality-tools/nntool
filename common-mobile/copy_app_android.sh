#!/bin/bash

cp "${PROJECT_DIR}/../common-mobile/modules/nativescript-websockets/websockets.android.js" "${PROJECT_DIR}/../common-mobile/platforms/android/app/src/main/assets/app/tns_modules/nativescript-websockets/websockets.js"
cp "${PROJECT_DIR}/../common-mobile/modules/nativescript-websockets/websockets.android.js" "${PROJECT_DIR}/../common-mobile/node_modules/nativescript-websockets/websockets.android.js"
cp "${PROJECT_DIR}/../common-mobile/platforms/android/app/libs/runtime-libs/nativescript-optimized-with-inspector.aar" "${PROJECT_DIR}/../android-speed/libs/nativescript-optimized-with-inspector.aar"
cp "${PROJECT_DIR}/../common-mobile/node_modules/tns-core-modules-widgets/platforms/android/widgets-release.aar" "${PROJECT_DIR}/../android-speed/libs/widgets-release.aar"

mkdir -p "${PROJECT_DIR}/src/main/assets/app"
cp -R "${PROJECT_DIR}/../common-mobile/platforms/android/app/src/main/assets/app/tns_modules" "${PROJECT_DIR}/src/main/assets/app"
cp "${PROJECT_DIR}/../common/build/uglified/common_mobile/common_mobile.js" "${PROJECT_DIR}/src/main/assets/app/tns_modules"

cp -R "${PROJECT_DIR}/../common-mobile/platforms/android/app/src/main/java/technology" "${PROJECT_DIR}/src/main/java"
cp -R "${PROJECT_DIR}/../common-mobile/platforms/android/app/src/main/java/com/tns" "${PROJECT_DIR}/src/main/java/com"
