## Building ##

### Prerequisites ###
Android Studio >= 3
NativeScript >= 5.0.0
Windows,macOS, Linux (any Version)

### Build ###
With fullfilled prerequisites perform the following steps:

1. Build *common*, see instructions in *common/README.md*
2. In dir *common-mobile*, run *tns build android* to install NativeScript dependencies
3. Open *android-demo* in Android Studio and build target *android-demo*

---------------

## Port Blocking

For the port blocking module a specified set of ports is tested against a defined test-service.

### Settings

Available settings are located in the src/main/res/values/defaults.xml file (of the android-demo project).
They are:

* default\_qos\_control\_host: which defines the location of the qos-service to test against
* default\_qos\_control\_port: the port to be used when connecting to that qos-service (which is not the same as the ports to be tested)
* qos\_tcp\_test\_port\_list: which is a list of TCP ports to be tested against during the execution of the port-blocking module
* qos\_udp\_test\_port\_list: which is a list of UDP ports to be tested against during the execution of the port-blocking module
(Note that all UDP ports that shall be successfully tested against need be enabled in the qos-service config.properties file. 
The corresponding setting is "server.udp.ports", which takes a list of ports to be enabled for UDP blocking tests)

### Results

The results in the demo-application of the port-blocking module are only displayed as Json array 
(for more information see [the JSON standard](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf)). 
This will not be the case for later versions of the app.

In case of a failed connection to the qos-service (as defined in the corresponding Settings) the result
is a simple text providing information about the currently input configuration.

The test type (e.g: UDP) can be found under the "test\_type" key.
For TCP tests, the port is reachable if the result under "tcp\_result\_out" reads "OK".
For udp results, the port is reachable if the value under "udp\_objective\_out\_num\_packets" (the number of UDP packets expected)
 equals the value under "udp\_result\_out\_num\_packets" (the number of UDP packets actually received).
 UDP tests additionally carry information about the round-trip-time under the "udp\_result\_out\_rtts\_ns" key, 
 wherein the entry after "0" contains the round-trip-time for that UDP packet in nano-seconds. 
 Note that the round-trip-time is not set for blocked ports.

---------------

## Execution ##
Launch the App *android-demo* in android-simulator or on an android-device and select *Start Test*. Perform another measurement by clicking *Start* again.
