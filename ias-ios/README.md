## Building ##

### Prerequisites ###
XCode >= 10
cocoapods
NativeScript >= 5.0.0
macOS (any Version)

### Build ###
With fullfilled prerequisites perform the following steps:
1. Build *ias*, see instructions in *ias/README.md*
2. In dir *ias-mobile*, run *tns build ios* to install NativeScript dependencies
3. In dir *ias-ios*, run *pod install* to install required cocoapods dependencies
4. Open *ias_ios.xcworkspace* in XCode and build target *Demo*

---------------

## Execution ##
Launch the App *ias_ios* in ios-simulator or on an ios-device and select *Start*. Perform another measurement by selecting *Load* followed by *Start*.