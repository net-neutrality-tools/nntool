## Building ##

### Prerequisites ###
Node.js >= 8 (tested with v8.16.0 and 10.16.0)
npm >= 5
uglify-es >= 3
Linux or macOS (any Version)

### Build ###
With fullfilled prerequisites run *build.sh*

---------------

## UDP Port Blocking

For the UDP port blocking module, a specified set of ports is tested against a defined test-service. The UDP ports 123, 500, 4500 and 5060 are activated on the publicly available measurement peer. The measurement agent configuration features the stated ports as well as UDP port 7000 to show reachable as well as unreachable ports.

---------------

## Execution ##
Open *index.html* from *build/{plain|uglfied}/web/* in a Browser of your choice and choose a test case. The browser developer console should only be used for debugging purposes, as an active developer console can cause performance issues.

To perform rtt, download, or upload measurements, the *ias-server* module has to be deployed on the measurement peer.
To perform port blocking measurements, the *coturn TURN server (https://github.com/coturn/coturn)* has to be deployed on the measurement peer (for detailed instructions on how to setup the *coturn TURN server* reference the documentation in the `docs/build_instructions/servers.adoc` file).
