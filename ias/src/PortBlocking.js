/*!
    \file PortBlocking.js
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH

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




/*-------------------------class PortBlocking------------------------*/

/**
 * @class PortBlocking
 * @description Port Blocking Measurement Class
 */
function PortBlocking()
{
    //private variables
    let portBlockingVersion         = '1.0.0';
    let pbMeasurementParameters     = {};
    let portsToTest                 = [];
    let portsTested                 = [];

    let rtcPeerConnections          = [];
    let timeoutHandler              = [];
    let timeoutHandlerActivePorts   = {};

    //KPIs
    let globalKPIs                  = {};
    let clientKPIs                  = {};
    let deviceKPIs                  = {};
    let portBlockingKPIs            = {};
    portBlockingKPIs.results        = [];




    /*-------------------------public functions------------------------*/

    /**
     * @function measurementControl
     * @description API Function to Start measurements
     * @public
     * @param {string} measurementParameters JSON coded measurement Parameters
     */
    this.measurementStart = function(measurementParameters)
    {
        pbMeasurementParameters = measurementParameters;
        portsToTest = pbMeasurementParameters.ports;

        globalKPIs.start_time               = jsTool.getFormattedDate();
        globalKPIs.test_case                = 'port_blocking';

        clientKPIs.timezone                 = jsTool.getTimezone();
        clientKPIs.type                     = pbMeasurementParameters.platform.toUpperCase();
        clientKPIs.port_blocking_version    = portBlockingVersion;

        deviceKPIs                          = JSON.parse(jsTool.getDeviceKPIs(pbMeasurementParameters.platform));

        portBlockingKPIs.peer       = pbMeasurementParameters.target;
        portBlockingKPIs.user       = pbMeasurementParameters.user;
        portBlockingKPIs.password   = pbMeasurementParameters.password;
        portBlockingKPIs.timeout    = pbMeasurementParameters.timeout;

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
            let gathererOptions = {gatherPolicy : "all", iceServers : [{ urls: 'turn:' + pbMeasurementParameters.target + ':' + port, username: pbMeasurementParameters.user, credential: pbMeasurementParameters.password}]};
            let iceGatherer = new RTCIceGatherer(gathererOptions);

            iceGatherer.onlocalcandidate = function (event)
            {
                if (event.candidate.ip === pbMeasurementParameters.targetIpv4 || event.candidate.ip === pbMeasurementParameters.targetIpv6)
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

            console.log('Connection unsuccessful to ' + pbMeasurementParameters.target + ':' + port);

            portBlockingKPIs.results.push({"port":port,"reachable":false,"timeout":true});

            measurementStepCompleted();
        }, pbMeasurementParameters.timeout);
    }

    async function handleRtcPeerConnection(port)
    {
        let config = { iceServers: [{ urls: 'turn:' + pbMeasurementParameters.target + ':' + port, username: pbMeasurementParameters.user, credential: pbMeasurementParameters.password}] };

        rtcPeerConnections[port] = new RTCPeerConnection(config);

        let dataChannelConfig = { ordered: false, maxRetransmits: 0 };
        dataChannel = rtcPeerConnections[port].createDataChannel('dc', dataChannelConfig);

        rtcPeerConnections[port].onicecandidate = function(event)
        {
            if (event.candidate)
            {
                let candidate = event.candidate.candidate;
                if ((candidate.includes(pbMeasurementParameters.targetIpv4) || candidate.includes(pbMeasurementParameters.targetIpv6)) && candidate.includes('relay'))
                {
                    validCandidateReceived(candidate, port);
                }
            }
        };

        try
        {
            let offer = await rtcPeerConnections[port].createOffer();
            rtcPeerConnections[port].setLocalDescription(offer);
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

        console.log('Connection successful to ' + pbMeasurementParameters.target + ':' + port);

        let ipVersion = candidate.includes(pbMeasurementParameters.targetIpv4) ? '4' : '6';

        let portMatched         = false;
        let ipVersionMatched    = false;
        for (var i = 0; i < portBlockingKPIs.results.length; i++)
        {
            if (portBlockingKPIs.results[i].port === port)
            {
                portMatched = true;
                if (portBlockingKPIs.results[i].ip_version === ipVersion) ipVersionMatched = true;
            }
        }

        if (!portMatched || !ipVersionMatched)
        {
           portsTested.push(port); portBlockingKPIs.results.push({"port":port,"reachable":true,"timeout":false,"ip_version":ipVersion});
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
        if (!jsTool.isEmpty(clientKPIs))        kpis.client_info    = clientKPIs;
        if (!jsTool.isEmpty(deviceKPIs))        kpis.device_info    = deviceKPIs;
        if (!jsTool.isEmpty(portBlockingKPIs))  kpis.port_blocking  = portBlockingKPIs;

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
