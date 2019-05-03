/*
*********************************************************************************
*                                                                               *
*       ..--== zafaco GmbH ==--..                                               *
*                                                                               *
*       Website: http://www.zafaco.de                                           *
*                                                                               *
*       Copyright 2017                                                          *
*                                                                               *
*********************************************************************************
*/

/*!   \file index.js
 *      @description Network Detection Class
 *      \author Tomás Pollak
 *      \editor Mike Kosek <kosek@zafaco.de>
 *      \date Last update: 2017-11-10
 *      \note Copyright (c) 2017 zafaco GmbH. All rights reserved.
 */

"use strict";

//var needle       = require('needle');
var os_functions = require('./' + process.platform);

// var ip_regex = /((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})/;
//var ip_regex = /(\b\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\b)/;
/*
function is_ip_address(str) {
  return ip_regex.test(str);
};*/

/*
function nic_by_name(name, cb)
{
	os_functions.get_network_interfaces_list(function(err, list)
	{
		if (err) return cb(err);

		var nics = list.filter(function(nic) { return nic.name === name; });

		if (nics.length > 0)
		{
			cb(null, nics[0]);
		} 
		else
		{
			cb(new Error('No network interface named ' + name));
		}  
  	});
};*/

/*
exports.get_public_ip = function(options, cb) {

  var default_urls = [
    'checkip.dyndns.org',
    'http://wtfismyip.com/text',
    'http://ipecho.net/plain',
    'http://ifconfig.me/ip'
  ];

  if (typeof options == 'function') { // no options passed
    cb = options;
    options = {};
  }

  var urls = options.urls || default_urls;

  function get(i) {
    var url = urls[i];
    if (!url) return cb(new Error('Unable to fetch IP address.'));

    needle.get(url, function(err, resp) {
      var body = resp && resp.body.toString();
      if (body && body.match(ip_regex)) {
        return cb(null, body.match(ip_regex)[1]);
      }

      get(i+1);
    })
  };

  get(0);
}
*/

/*
exports.get_private_ip = function(cb) {

  os_functions.get_network_interfaces_list(function(err, list) {
    if (err || !list)
      return cb(err || new Error('No network interfaces found.'));

    os_functions.get_active_network_interface_name(function(err, active_nic) {
      if (err) return cb(err);

      var ips = list.filter(function(nic) {
        if (is_ip_address(nic.ip_address))
          return active_nic ? active_nic == nic.name : true;
      });

      if (ips.length > 0)
        cb(null, ips[0].ip_address);
      else
        cb(new Error('No private IPs found (' + list.length + ' interfaces)'));
    });
  });
};
*/
/*
exports.get_gateway_ip = function(cb) {

  os_functions.get_active_network_interface_name(function(err, nic_name) {
    if (err || nic_name.trim() == '')
      return cb(err || new Error('No active network interface found.'));

    os_functions.gateway_ip_for(nic_name, function(err, out) {
      if (err || !out || out.toString() == '')
        return cb(err || new Error('No gateway IP assigned to ' + nic_name));

      cb(null, out.toString().trim())
    })
  });

};*/
/*
function get_active_interface (cb)
{
	os_functions.get_active_network_interface_name(function(err, nic_name)
	{
		if (err || !nic_name) return cb(err || new Error("No active interfaces detected."));

		nic_by_name(nic_name, function(err, nic)
		{
			if (err) return cb(err);

			os_functions.netmask_for(nic_name, function(err, netmask)
			{
				if (!err && netmask)
				{
					netmask = netmask.replace('Mask', '');
					netmask = netmask.replace('netmask', '');  
					netmask = netmask.replace(':', '');
					nic.ipv4_netmask = netmask.trim();
				}
				os_functions.gateway_ip_for(nic_name, function(err, ip)
				{
				  if (!err && ip)
				  {
					  nic.ipv4_gateway_address = ip.toString().trim();
				  }
				  cb(null, nic);
				})
			});
    	});
  	});
};*/

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


//exports.get_interfaces_list = os_functions.get_network_interfaces_list;
//exports.mac_address_for = os_functions.mac_address_for;