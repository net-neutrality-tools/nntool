## Building ##

### Prerequisites ###
* XCode >= 11
* cocoapods
* NativeScript = 5.x
* macOS (any Version)

### Build ###
With fullfilled prerequisites perform the following steps:
1. Build *ias*, see instructions in *ias/README.md*
2. Build *ias-mobile*, see instructions in *ias-mobile/README.md*
3. In directory *qos-client-swift* run *pod install* to install MeasurementKit 
4. In directory *ias-ios*, run *pod install* to install required cocoapods dependencies
5. Open *ias_ios.xcworkspace* in XCode and build target *Demo*

---------------

### Demo Parameters ###

Modify *Demo/SpeedViewController.m* according to Code-Documentation to edit measurement parameters before *Build*

---------------

### Demo Execution ###
Launch the App *ias_ios* in ios-simulator or on an ios-device and select *Start*. Perform another measurement by selecting *Load* followed by *Start*.
