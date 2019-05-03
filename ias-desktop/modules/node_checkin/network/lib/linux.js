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

/*!   \file darwin.js
 *      @description Network Detection Class linux
 *      \author Tomás Pollak
 *      \editor Mike Kosek <kosek@zafaco.de>
 *      \date Last update: 2017-11-09
 *      \note Copyright (c) 2017 zafaco GmbH. All rights reserved.
 */

"use strict";

var os    = require('os');
var exec  = require('child_process').exec;
var async = require('async');
    
function trim_exec(cmd, cb)
{
    exec(cmd, function(err, out)
    {
        if (out && out.toString() !== '')
        {
            cb(null, out.toString().trim());
        }
        else
        {
            cb(err);
        }  
    });
}

exports.get_network_interfaces_list_active = function(cb)
{
    var cmd = "netstat -46 -rn | grep UG";
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
        list = list.split('\n');

        for (var index in list)
        {
            list[index] = list[index].replace(/\s+/g, ' ');
            list[index] = list[index].replace(/%/g, ' ');
            list[index] = list[index].split(' ');
        }

        cb(null, list);
    });
};

exports.mac_address_for = function(nic_name, cb)
{
    var cmd = 'cat /sys/class/net/' + nic_name + '/address';
    trim_exec(cmd, cb);
};

exports.gateway_ip_for = function(nic_name, cb)
{
    trim_exec("ip r | grep " + nic_name + " | grep default | cut -d ' ' -f 3", cb);
};

exports.netmask_for = function(nic_name, cb)
{
    var cmd = "ifconfig " + nic_name + " 2> /dev/null | egrep 'netmask|Mask:' | awk '{print $4}'";
    trim_exec(cmd, cb);
};

exports.interface_type_for = function(nic_name, cb)
{
    var execSync = require('child_process').execSync;
    try
    {
        var type = execSync('cat /proc/net/wireless | grep ' + nic_name);
        /*{
            return cb(null, err ? 'wired' : 'wireless');
        });*/
        return 'wireless';
    }
    catch(exception)
    {
        return 'wired';
    }
};

exports.speed_for = function(nic_name, cb, type)
{
    var cmd;
    if (type === 'wired')
    {
        cmd = 'cat /sys/class/net/' + nic_name + '/speed';
    }
    else if (type === 'wireless')
    {
        cmd = 'iwconfig ' + nic_name + ' | grep "Rate"';
    }
    trim_exec(cmd, cb);
};

exports.mtu_for = function(nic_name, cb)
{
    var cmd;
    cmd = 'cat /sys/class/net/' + nic_name + '/mtu';
    trim_exec(cmd, cb);
};

exports.get_network_interfaces_list = function(cb)
{
    var count = 0;
    var list = [];
    var nics = os.networkInterfaces();

    function append_data(obj)
    {
        async.parallel
        ([
            function(cb) {
              exports.mac_address_for(obj.name, cb);
            },
            function(cb) {
              exports.gateway_ip_for(obj.name, cb);
            },
            function(cb) {
              exports.netmask_for(obj.name, cb);
            },
            function(cb) 
            {
                if ((obj.type === 'wired') || (obj.type === 'wireless'))
                {
                    exports.speed_for(obj.name, cb, obj.type);
                }
                else
                {
                    obj.speed = '-';
                }
            },
            function(cb)
            {
                exports.mtu_for(obj.name, cb);
                obj.mtu = 0;
            }
        ], 
        function(err, results)
        {
            if (results[0]) obj.mac_address = results[0];
            if (results[1]) obj.ipv4_gateway_address  = results[1];
            if (results[2]) 
            {
                var netmask = results[2];

                netmask = netmask.replace('Mask', '');
                netmask = netmask.replace('netmask', '');  
                netmask = netmask.replace(':', '');

                obj.ipv4_netmask = netmask;
            }
            if (results[3])
            {
                var speed = results[3];
                
                //prefix diff
                //if ':', overall link qualitiy is calculated
                speed           = speed.replace('Bit Rate:', '');
                obj.speed       = parseFloat(speed);
                obj.description = '-';
                
                //if '=', fixed and forced
                if (isNaN(obj.speed))
                {
                    speed       = speed.replace('Bit Rate=', '');
                    obj.description = 'fixed';
                }
                obj.speed   = parseFloat(speed);
            }
            if (results[4])
            {
                var mtu = results[4];
                obj.mtu = parseInt(mtu);
                if (isNaN(obj.mtu))
                {
                    obj.mtu = 0;
                }
            }

            list.push(obj);
            --count || cb(null, list);
        });
    }

    for (var key in nics) {
        if (key !== 'lo0' && key !== 'lo' && !key.match(/^tun/))
        {
            count++;
            var obj = { name: key };

            nics[key].forEach(function(type)
            {
                  if (type.family === 'IPv4')
                  {
                    obj.ipv4_address = type.address;
                  }
            });
            
            obj.type = exports.interface_type_for(obj.name, cb);

            append_data(obj);
        }
    }

    if (count === 0)
    {
        cb(new Error('No interfaces found.'));
    }
};