/*!
    \file win32.js
    \author Tomás Pollak
    \editor zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13
*/

"use strict";

var wmic        = require('wmic');
var exec        = require('child_process').exec;
var execSync    = require('child_process').execSync;
var os          = require('os');

function get_wmic_ip_value(what, nic_name, cb){
  exports.mac_address_for(nic_name, function(err, mac){
    if (err || !mac)
      return cb(err || new Error('No MAC Address found.'));

    wmic.get_value('nicconfig', what, 'MACAddress = \'' + mac + '\'', function(err, out){
      if (err) return cb(err);

      cb(null, out.split(',')[0].replace(/[^0-9\.]/g, ''));
    });
  })
}

function get_mtu(stdout, nic_name)
{
	var raw = stdout.toString().trim().split('\n');
	if (raw.length !== 0 && raw !== [''])
	{
		var next = false;
		
		for (var i=0; i<raw.length;i++)
		{
			if (raw[i].toLowerCase().indexOf(nic_name.toLowerCase()) !== -1)
			{
				return raw[i].trim().split(" ")[0];
			}
		}
	}
}

exports.get_network_interfaces_list_active = function(cb, interfaces)
{
	var cmd = 'netstat -rn';
    exec(cmd, function(err, stdout)
    {
        if (err)
        {
            return cb(err);
        }

        var list = stdout.toString().trim();
        if (list.length === 0)
        {
            return cb(new Error('No active network interface found.'));
        }

        var interfaceList = list.split('IPv4')[0].split('\n');
		var ipv4List = list.split('IPv4')[1].split('IPv6')[0].split('\n');
		var ipv6List = list.split('IPv6')[1].split('\n');
		list = [];
		
		var interfaceArray = [];
		var ipv4Array = [];
		var ipv6Array = [];
		
		for (var index in interfaceList)
        {
			interfaceList[index] = interfaceList[index].replace(/\s+/g, ' ');
			interfaceList[index] = interfaceList[index].replace(' ', '');
			interfaceList[index] = interfaceList[index].replace(/[^a-zA-Z\d\s\.:]/g, '');
			
			for (var iface in interfaces)
			{
				if (interfaceList[index].match(interfaces[iface].description.replace(/[^a-zA-Z\d\s\.:]/g, '')))
				{
					interfaceArray.push({'id' : interfaceList[index].split('.')[0], 'description' : interfaces[iface].description, 'name' : interfaces[iface].name,'ipv4_address' : interfaces[iface].ipv4_address});
				}
			}
        }
		for (var index in ipv4List)
        {
			ipv4List[index] = ipv4List[index].replace(/\s+/g, ' ');
			ipv4List[index] = ipv4List[index].replace(' ', '');
			
			for (var iface in interfaceArray)
			{
				if (ipv4List[index].match(interfaceArray[iface].ipv4_address))
				{
					if (!ipv4List[index].match('On-link') && !ipv4List[index].match('Verbindung'))
					{
						ipv4Array.push({'ip' : ipv4List[index].split(' ')[2], 'metric' : ipv4List[index].split(' ')[4], 'name' : interfaceArray[iface].name});
					}
				}
			}
			
        }
		for (var index in ipv6List)
        {
			ipv6List[index] = ipv6List[index].replace(/\s+/g, ' ');
			ipv6List[index] = ipv6List[index].replace(' ', '');
			
			for (var iface in interfaceArray)
			{
				if (ipv6List[index].split(' ')[0].match(interfaceArray[iface].id))
				{
					if (ipv6List[index].split(' ')[3] !== '' && ipv6List[index].split(' ')[3] !== 'On-link' && ipv6List[index].split(' ')[3] !== 'Verbindung')
					{
						ipv6Array.push({'ip' : ipv6List[index].split(' ')[3], 'metric' : ipv6List[index].split(' ')[1], 'name' : interfaceArray[iface].name});
					}
				}
			}
        }
		
		ipv4Array.sort(function(a, b)
		{
			return parseFloat(a.metric) - parseFloat(b.metric);
		});
		ipv6Array.sort(function(a, b)
		{
			return parseFloat(a.metric) - parseFloat(b.metric);
		});
		
		for (var index in ipv4Array)
		{
			list.push([ipv4Array[index].metric, ipv4Array[index].ip, ipv4Array[index].name]);
		}
		
		for (var index in ipv6Array)
		{
			list.push([ipv6Array[index].metric, ipv6Array[index].ip, ipv6Array[index].name]);
		}

        cb(null, list);
    });
};

exports.netmask_for = function(nic_name, cb) {
  get_wmic_ip_value('IPSubnet', nic_name, cb);
};

exports.gateway_ip_for = function(nic_name, cb) {
  get_wmic_ip_value('DefaultIPGateway', nic_name, cb);
};

exports.mac_address_for = function(nic_name, cb) {
  var cond = 'NetConnectionID = \'' + nic_name + '\'';
  wmic.get_value('nic', 'MACAddress', cond, cb);
}

exports.get_network_interfaces_list = function(callback) {

    var list = [];
    var node_nics = os.networkInterfaces();

    wmic.get_list('nic', function(err, nics)
    {
        if (err) return callback(err);

        nics.forEach(function(nic)
        {
            if (nic.Name && nic.NetConnectionID != '' && nic.MACAddress != '') {

                var obj = {
                    name: nic.NetConnectionID,
                    description: nic.Name,
                    mac_address: nic.MACAddress,
                    speed: 0,
                    ipv4_address: nic.IPAddress,
                    vendor: nic.Manufacturer,
                    model: nic.Description,
                    type: nic.Name.match(/802.11|wi-?fi|wireless|wlan|w-lan/i) ? 'wireless' : 'wired'
                }
				
                var cmd = "netsh interface ip show subinterfaces";
                try
                {
                    obj.mtu = get_mtu(execSync(cmd), obj.name)
                }
                catch(e)
                {
                    
                }
                
                if (obj.type === 'wired')
                {
                    obj.speed = parseInt(nic.Speed / 1000000);
                }

                var node_nic = node_nics[obj.name] || [];

                node_nic.forEach(function(type)
                {
                    if (type.family == 'IPv4')
                    {
                        obj.ipv4_address = type.address;
                    }
                });

                list.push(obj);
            }
        })

        callback(null, list);
    });
};
