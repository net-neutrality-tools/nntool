/*!
    \file linux.js
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

"use strict";

exports.getRouteToTargetCallbackInititator = function(target, error, hops)
{
    var data = {};
    data.target     = target;
    data.hops       = '-';
    
    if (!error)
    {
        var indexId 	= 1;
        var indexHost 	= 2;
        var indexIp 	= 3;

        hops = hops[0].split('\n');
		
        hops = hops.splice(2); 

        var hopsObject = [];
        hops.forEach(function(hop)
        {
            try
            {
                //console.log(hop);
                hop = hop.replace(/\s+/g, ' ');
                hop = hop.split(' ');
                var hopData = {};

                hopData.id = hop[indexId];
                hopData.id = hopData.id.replace(/:/g, '');

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

                hopData.ip = hopData.ip.replace('(', '');
                hopData.ip = hopData.ip.replace(')', '');

                if (hopData.host === 'no' || hopData.ip === 'reply')
                {
                    return;
                }

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