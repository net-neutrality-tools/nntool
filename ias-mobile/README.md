## Building ##

### Prerequisites ###
* XCode >= 10
* cocoapods
* NativeScript = 5.x
* macOS (any Version)

### Build ###
With fullfilled prerequisites perform the following steps:
1. Run *npm i* to install dependencies
2. Run *tns build ios*
2.1 If *tns build ios* fails, check if the following files have the executable bit set:
* ias-mobile/platforms/ios/internal/nativescript-post-build
* ias-mobile/platforms/ios/internal/nativescript-pre-build
* ias-mobile/platforms/ios/internal/nativescript-pre-link
* ias-mobile/platforms/ios/internal/strip-dynamic-framework-architectures.sh
* ias-mobile/platforms/ios/internal/metadata-generator/bin/build-step-metadata-generator.py
* ias-mobile/platforms/ios/internal/metadata-generator/bin/objc-metadata-generator