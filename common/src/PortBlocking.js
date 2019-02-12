/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2018 - 2019                                                   *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2019-01-23
 *      \note Copyright (c) 2018 - 2019 zafaco GmbH. All rights reserved.
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
    let portBlockingVersion		= '1.0.0';
	let pbMeasurementParameters	= {};
	let portsToTest				= [];
	
	let timeoutHandler;

	//KPIs
	let globalKPIs				= {};
	let clientKPIs 				= {};
	let deviceKPIs              = {};
	let portBlockingKPIs		= {};
	portBlockingKPIs.results	= [];
	
	
	
	
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

		globalKPIs.start_time				= jsTool.getFormattedDate();
		globalKPIs.test_case				= 'port_blocking';
		
		clientKPIs.timezone					= jsTool.getTimezone();
		clientKPIs.type 					= pbMeasurementParameters.platform.toUpperCase();
		clientKPIs.port_blocking_version	= portBlockingVersion;

		deviceKPIs                      	= JSON.parse(jsTool.getDeviceKPIs(pbMeasurementParameters.platform));

		portBlockingKPIs.peer 		= pbMeasurementParameters.target;
		portBlockingKPIs.user		= pbMeasurementParameters.user;
		portBlockingKPIs.password	= pbMeasurementParameters.password;
		portBlockingKPIs.timeout	= pbMeasurementParameters.timeout;
		
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
					clearTimeout(timeoutHandler);

					console.log('Connection successful to ' + pbMeasurementParameters.target + ':' + port);

					portBlockingKPIs.results.push({"port":port,"reachable":true,"timeout":false});

					measurementStepCompleted();
				}
			};
		}
		else
		{
            let config = { iceServers: [{ urls: 'turn:' + pbMeasurementParameters.target + ':' + port, username: pbMeasurementParameters.user, credential: pbMeasurementParameters.password}] };

            rtcPeerConnection = new RTCPeerConnection(config);

            let dataChannelConfig = { ordered: false, maxRetransmits: 0 };
            dataChannel = rtcPeerConnection.createDataChannel('dc', dataChannelConfig);

            rtcPeerConnection.onicecandidate = function(event)
			{
                if (event.candidate)
				{
                    let str = event.candidate.candidate;
                    if ((str.includes(pbMeasurementParameters.targetIpv4) || str.includes(pbMeasurementParameters.targetIpv6)) && str.includes('relay'))
					{
						clearTimeout(timeoutHandler);
						
						console.log('Connection successful to ' + pbMeasurementParameters.target + ':' + port);
						
						portBlockingKPIs.results.push({"port":port,"reachable":true,"timeout":false});
						
						measurementStepCompleted();
                    }
                }
            };

            rtcPeerConnection.createOffer((description)=>
		  	{
                console.log('Offer Created');

                rtcPeerConnection.setLocalDescription(description);
            }, err => {console.log('Offer creation error: ' + err.message)});
        }
		
		timeoutHandler = setTimeout(function ()
		{
			clearTimeout(timeoutHandler);

			console.log('Connection unsuccessful to ' + pbMeasurementParameters.target + ':' + port);

			portBlockingKPIs.results.push({"port":port,"reachable":false,"timeout":true});

			measurementStepCompleted();
		}, pbMeasurementParameters.timeout);
	}
	
	function measurementStepCompleted()
	{
		globalKPIs.cmd = 'report';
		reportToWeb();
		
		if (portsToTest.length > 0)
		{
			checkUdpPort(portsToTest.shift());
		}
		else
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
		if (!jsTool.isEmpty(clientKPIs))		kpis.client_info	= clientKPIs;
		if (!jsTool.isEmpty(deviceKPIs))		kpis.device_info	= deviceKPIs;
		if (!jsTool.isEmpty(portBlockingKPIs))	kpis.port_blocking	= portBlockingKPIs;
		
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