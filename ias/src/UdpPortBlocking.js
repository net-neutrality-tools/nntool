/*!
    \file UdpPortBlocking.js
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2020-07-01

    Copyright (C) 2016 - 2020 zafaco GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

var jsTool = new JSTool();




/*-------------------------class UdpPortBlocking------------------------*/

/**
 * @class UdpPortBlocking
 * @description UDP Port Blocking Measurement Class
 */
function UdpPortBlocking()
{
    //private variables
    let udpPortBlockingVersion      = '1.0.0';
    let udpPbMeasurementParameters  = {};
    let portsToTest                 = [];
    let timings                     = [];

    let rtcPeerConnections          = [];
    let timeoutHandler              = [];
    let timeoutHandlerActivePorts   = {};

    //KPIs
    let globalKPIs                  = {};
    let clientKPIs                  = {};
    let deviceKPIs                  = {};
    let udpPortBlockingKPIs         = {};
    udpPortBlockingKPIs.results     = [];




    /*-------------------------public functions------------------------*/

    /**
     * @function measurementControl
     * @description API Function to Start measurements
     * @public
     * @param {string} measurementParameters JSON coded measurement Parameters
     */
    this.measurementStart = function(measurementParameters)
    {
        udpPbMeasurementParameters = measurementParameters;

        for (port of udpPbMeasurementParameters.ports)
        {
            if (typeof port.packets !== "undefined" && !isNaN(port.packets))
            {
                for (var i=0; i<port.packets; i++)
                {
                    //limit to a maximum of 20 probes per port, as tests can only be performed sequentially
                    portsToTest.push(port.port);
                    if (i>=19) break;
                }
            }
            else
            {
                portsToTest.push(port.port);
            }
        }

        globalKPIs.start_time               = jsTool.getFormattedDate();
        globalKPIs.test_case                = 'udp_port_blocking';

        clientKPIs.timezone                 = jsTool.getTimezone();
        clientKPIs.type                     = udpPbMeasurementParameters.platform.toUpperCase();
        clientKPIs.udp_port_blocking_version= udpPortBlockingVersion;

        deviceKPIs                          = JSON.parse(jsTool.getDeviceKPIs(udpPbMeasurementParameters.platform));

        udpPortBlockingKPIs.peer       = udpPbMeasurementParameters.target;
        udpPortBlockingKPIs.user       = udpPbMeasurementParameters.user;
        udpPortBlockingKPIs.password   = udpPbMeasurementParameters.password;
        udpPortBlockingKPIs.timeout    = udpPbMeasurementParameters.timeout;

        checkUdpPort(portsToTest.shift());
    }




    /*-------------------------private functions------------------------*/

    /**
     * @function checkUdpPort
     * @description check reachability of udp port
     * @private
     * @param {int} port udp port to test
     */
    function checkUdpPort(port)
    {
        if (typeof timings[port] === "undefined")
        {
            timings[port] = [];
        }
        timings[port].push({"sent":jsTool.getTimestamp()});
        if (window.RTCIceGatherer !== undefined)
        {
            let gathererOptions = {gatherPolicy : "all", iceServers : [{ urls: 'turn:' + udpPbMeasurementParameters.target + ':' + port, username: udpPbMeasurementParameters.user, credential: udpPbMeasurementParameters.password}]};
            let iceGatherer = new RTCIceGatherer(gathererOptions);

            iceGatherer.onlocalcandidate = function (event)
            {
                if (event.candidate.ip === udpPbMeasurementParameters.targetIpv4 || event.candidate.ip === udpPbMeasurementParameters.targetIpv6)
                {
                    validCandidateReceived(event.candidate.ip, port);
                }
            };
        }
        else
        {
            handleRtcPeerConnection(port);
        }

        timeoutHandlerActivePorts[port] = true;
        timeoutHandler[port] = setTimeout(function ()
        {
            clearTimeout(timeoutHandler[port]);
            delete timeoutHandlerActivePorts[port];

            console.log('Connection unsuccessful to ' + udpPbMeasurementParameters.target + ':' + port);

            setResult(port, false);

            measurementStepCompleted();
        }, udpPbMeasurementParameters.timeout);
    }

    async function handleRtcPeerConnection(port)
    {
        let config = { iceServers: [{ urls: 'turn:' + udpPbMeasurementParameters.target + ':' + port, username: udpPbMeasurementParameters.user, credential: udpPbMeasurementParameters.password}] };

        rtcPeerConnections[port] = new RTCPeerConnection(config);

        let dataChannelConfig = { ordered: false, maxRetransmits: 0 };
        dataChannel = rtcPeerConnections[port].createDataChannel('dc', dataChannelConfig);

        rtcPeerConnections[port].onicecandidate = function(event)
        {
            if (event.candidate)
            {
                let candidate = event.candidate.candidate;
                if ((candidate.includes(udpPbMeasurementParameters.targetIpv4) || candidate.includes(udpPbMeasurementParameters.targetIpv6)) && candidate.includes('relay'))
                {
                    validCandidateReceived(candidate, port);
                }
            }
        };

        try
        {
            let offer = await rtcPeerConnections[port].createOffer();
            rtcPeerConnections[port].setLocalDescription(offer);
            console.warn(JSON.stringify(offer));
            console.log('Offer Created');
        }
        catch(e)
        {
            console.log('Offer creation error: ' + e.message);
        }
    }

    function validCandidateReceived(candidate, port)
    {
        timings[port][timings[port].length-1] = {"sent": timings[port][timings[port].length-1].sent, "received":jsTool.getTimestamp()};

        clearTimeout(timeoutHandler[port]);
        delete timeoutHandlerActivePorts[port];

        console.log('Connection successful to ' + udpPbMeasurementParameters.target + ':' + port);
        setResult(port, true, candidate.includes(udpPbMeasurementParameters.targetIpv4) ? '4' : '6');

        measurementStepCompleted();
    }

    function setResult(port, successful, ipVersion)
    {
        var delays = [];
        var received = 0;
        for (timing of timings[port])
        {
            if (!isNaN(timing.sent) && !isNaN(timing.received))
            {
                received++;
                delays.push(timing.received*1e6 - timing.sent*1e6);
            }
        }

        var delayAvg;
        var delayStandardDeviation;
        if (delays.length > 0)
        {
            var sum = 0.0;
            for (delay of delays)
            {
                sum += delay;
            }
            delayAvg = sum / received;

            sum = 0.0;
            for (delay of delays)
            {
                sum += Math.pow((delay-delayAvg), 2);
            }
            delayStandardDeviation = Math.sqrt(sum/received);
        }

        var delayResult;
        if (typeof delayAvg !== "undefined")
        {
            delayResult={};
            delayResult.average_ns = parseInt(delayAvg);
            delayResult.standard_deviation_ns = parseInt(delayStandardDeviation);
        }


        var matched = false;
        for (var i = 0; i < udpPortBlockingKPIs.results.length; i++)
        {
            if (udpPortBlockingKPIs.results[i].port === port)
            {
                matched= true;
                udpPortBlockingKPIs.results[i].packets.sent++;
                successful ? udpPortBlockingKPIs.results[i].packets.received++ : udpPortBlockingKPIs.results[i].packets.lost++;
                udpPortBlockingKPIs.results[i].delay=delayResult;
                udpPortBlockingKPIs.results[i].delays=delays;
                break;
            }
        }

        if (!matched)
        {
            var packets = {"sent": 1, "received": successful ? 1:0, "lost": successful ? 0:1};
            udpPortBlockingKPIs.results.push({"port":port,"packets": packets, "ip_version":ipVersion, "delay": delayResult, "delays": delays});
        }
    }

    function measurementStepCompleted()
    {
        globalKPIs.cmd = 'report';
        reportToWeb();

        if (portsToTest.length > 0)
        {
            checkUdpPort(portsToTest.shift());
        }
        else if (Object.keys(timeoutHandlerActivePorts).length === 0)
        {
            globalKPIs.cmd = 'completed';
            reportToWeb();
        }
    }

    /**
     * @function getKPIs
     * @description Return port blocking measurement Results
     */
    function getKPIs()
    {
        var kpis = {};
        kpis = jsTool.extend(globalKPIs);
        if (!jsTool.isEmpty(clientKPIs))            kpis.client_info        = clientKPIs;
        if (!jsTool.isEmpty(deviceKPIs))            kpis.device_info        = deviceKPIs;
        if (!jsTool.isEmpty(udpPortBlockingKPIs))   kpis.udp_port_blocking  = udpPortBlockingKPIs;

        return kpis;
    }

    /**
     * @function reportToWeb
     * @description Callback to Browser
     * @private
     * @param {string} kpis JSON coded Measurement Results
     */
    function reportToWeb()
    {
        measurementCallback(JSON.stringify(getKPIs()));
    }
}
