## Building ##

### Prerequisites ###
Node.js >= 8
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
To perform port blocking measurements, the *coturn TURN server (https://github.com/coturn/coturn)* has to be deployed on the measurement peer.

*coturn TURN server* can be installed via *apt install coturn* on debian-based systems. The current version 4.5.0.7 was verified for port blocking measurements.
*coturn TURN server* can be executed with the following bash-script:

```
#!/bin/bash

credentials=berec:berec
realmname=berec

ports=(123 500 4500 5060)

for port in "${ports[@]}"
do
    turnserver -n -v -o --no-dtls --no-tls -u $credentials -r $realmname -p $port [-L <first-ip-address> -L <second-ip-address> ...]
done
```

Please note, that the stated credentials and realmname must match the configured values in the ias-module.