/*!
    \file index.js
    \author Tomás Pollak
    \editor zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13
*/

"use strict";

var os_functions = require('./' + process.platform);

exports.get_network_interfaces = function(cb)
{
    os_functions.get_network_interfaces_list(function(err, interfaces)
    {
        if (err || !interfaces)
        {
            return cb(err);
        }

        var interfaces = interfaces.filter(function(nic) 
        {
            if (typeof nic.ipv4_address === 'undefined' || nic.ipv4_address === null || nic.ipv4_address === '')
            {
                return;
            }
            if (nic.type === 'wireless' || nic.type === 'wired')
            {
                return nic.type;
            }
            return;
        });

        if (interfaces.length === 0)
        {
            return cb(new Error('No network interfaces found'));
        }

        os_functions.get_network_interfaces_list_active(function(err, interfaces_active)
        {
            if (err || !interfaces_active)
            {
                return cb(err || new Error("No active interfaces detected."));
            }

            interfaces_active = interfaces_active.filter(function(nic) 
            {
                var ipType = getIPAddressType(nic[1]);
                if (nic[1] === 'fe80::') return;
                if (ipType === false) return;
                return ipType;
            });

            var ipv4PrimaryFound = false;
            var ipv6PrimaryFound = false;

            for (var iface_active in interfaces_active)
            {
                var ipv4Primary = false;
                var ipv6Primary = false;
                var ipType = getIPAddressType(interfaces_active[iface_active][1]);
                if (ipType === 'IPv4' && !ipv4PrimaryFound)
                {
                    ipv4PrimaryFound = true;
                    ipv4Primary = true;
                }
                else if (ipType === 'IPv6' && !ipv6PrimaryFound)
                {
                    ipv6PrimaryFound = true;
                    ipv6Primary = true;
                }

                for (var iface in interfaces)
                {
                    if (interfaces[iface].name === interfaces_active[iface_active][interfaces_active[iface_active].length-1])
                    {
                        if (!interfaces[iface].ipv4Primary)
                        {
                            interfaces[iface].ipv4Primary = ipv4Primary;
                        }
                        if (!interfaces[iface].ipv6Primary)
                        {
                            interfaces[iface].ipv6Primary = ipv6Primary;
                        }
						if (!interfaces[iface].ipv4_gateway_address)
						{
							interfaces[iface].ipv4_gateway_address = interfaces_active[iface_active][1];
						}
                    }
                }
            }

            cb(null, interfaces);
        }, interfaces);
    });
}

function getIPAddressType(ip)
{ 
    if (/^\s*((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))\s*$/g.test(ip))
    {
        return 'IPv4';
    }
    
    if (/^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$/g.test(ip))
    {
        return 'IPv6';
    }
    
    return false;
};
