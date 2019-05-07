/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2019                                                          *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2019-04-08
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

"use strict";

exports.getRouteToTargetCallbackInititator = function(target, error, hops)
{
    var data = {};
    data.target     = target;
    data.hops       = '-';
    
    if (!error)
    {
        var indexId 	= 1;
        var indexHost 	= 5;
        var indexIp 	= 6;
        
        hops = hops[0].split('\n');
		
        hops = hops.splice(3);

        var hopsObject = [];
        hops.forEach(function(hop)
        {
            try
            {
                //console.log(hop);
                hop = hop.replace(/ms/g, '');
                hop = hop.replace(/\s+/g, ' ');
                hop = hop.split(' ');
                var hopData = {};

                if (hop[1] === '' || hop[2] === 'no')
                {
                    return;
                }
                else
                {
                    hopData.id = hop[indexId];
                }

                if (hop[2] === '*' && hop[3] === '*' && hop[4] === '*')
                {
                    hop[5] = '-';
                    hop[6] = '-';
                }

                if (isBlank(hopData.id) || isNaN(hopData.id))
                {
                    return;
                }

                hopData.host = hop[indexHost];

                if (isBlank(hopData.host))
                {
                    hopData.host = '-';
                }

                hopData.ip = hop[indexIp];

                if (isBlank(hopData.ip))
                {
                    hopData.ip = hopData.host;
                }

                hopData.ip      = hopData.ip.replace('[', '');
                hopData.ip      = hopData.ip.replace(']', '');
                hopData.host    = hopData.host.replace('[', '');
                hopData.host    = hopData.host.replace(']', '');

                if (hopsObject.length > 0 && hopsObject[hopsObject.length-1].id === hopData.id)
                {
                    hopsObject.splice(hopsObject.length-1, 1);
                }
                
                delete hopData.host;
                hopsObject.push(hopData);
            }
            catch (exception)
            {
                return;
            }
        });
        
        data.hops   = hopsObject;
    }
    
    getRouteToTargetCallback(JSON.stringify(data));
};