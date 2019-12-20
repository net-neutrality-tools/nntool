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

---------------

## License ##

ias-ios is released under the AGPLv3 <https://www.gnu.org/licenses/agpl-3.0.txt>

Copyright (C) 2016-2019 zafaco GmbH

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3 
as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.