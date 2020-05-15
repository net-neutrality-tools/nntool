/*!
    \file UdpPortBlocking.js
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2020-05-15

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
    let portsTested                 = [];

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
        portsToTest = udpPbMeasurementParameters.ports;

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

            udpPortBlockingKPIs.results.push({"port":port,"reachable":false,"timeout":true});

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
        clearTimeout(timeoutHandler[port]);
        delete timeoutHandlerActivePorts[port];

        console.log('Connection successful to ' + udpPbMeasurementParameters.target + ':' + port);

        let ipVersion = candidate.includes(udpPbMeasurementParameters.targetIpv4) ? '4' : '6';

        let portMatched         = false;
        let ipVersionMatched    = false;
        for (var i = 0; i < udpPortBlockingKPIs.results.length; i++)
        {
            if (udpPortBlockingKPIs.results[i].port === port)
            {
                portMatched = true;
                if (udpPortBlockingKPIs.results[i].ip_version === ipVersion) ipVersionMatched = true;
            }
        }

        if (!portMatched || !ipVersionMatched)
        {
           portsTested.push(port); udpPortBlockingKPIs.results.push({"port":port,"reachable":true,"timeout":false,"ip_version":ipVersion});
        }

        measurementStepCompleted(portMatched);
    }

    function measurementStepCompleted(performNextStep, completed)
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
