/*
*********************************************************************************
*                                                                               *
*       ..--== zafaco GmbH ==--..                                               *
*                                                                               *
*       Website: http://www.zafaco.de                                           *
*                                                                               *
*       Copyright 2017 - 2018                                                   *
*                                                                               *
*********************************************************************************
*/

/*!   \file darwin.js
 *      @description Network Detection Class macOS
 *      \author Tomás Pollak
 *      \editor Mike Kosek <kosek@zafaco.de>
 *      \date Last update: 2018-10-19
 *      \note Copyright (c) 2017 - 2018 zafaco GmbH. All rights reserved.
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

function determine_nic_type(str)
{
    return str.match(/WLAN|W-LAN/i)
        ? 'wireless'
        : str.match(/Ethernet|LAN/i)
        ? 'wired'
        : str.match(/Wi-?Fi|AirPort/i)
          ? 'wireless'
          //: str.match(/FireWire/i)
          //  ? 'FireWire'
          //  : str.match(/Thunderbolt/i)
          //    ? 'Thunderbolt'
              : 'other';
}

exports.get_network_interfaces_list_active = function(cb)
{
    var cmd = 'netstat -rn | grep UG';
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
            list[index] = list[index].replace(/\s+/g, ' ').trim();
            list[index] = list[index].replace(/%/g, ' ');
            list[index] = list[index].split(' ');
        }

        cb(null, list);
    });
};

exports.mac_address_for = function(nic_name, cb) {
  var cmd = "networksetup -getmacaddress " + nic_name + " | awk '{print $3}'";
  trim_exec(cmd, cb);
};

exports.gateway_ip_for = function(nic_name, cb) {
  var cmd = "ipconfig getoption " + nic_name + " router";
  trim_exec(cmd, cb);
};

exports.netmask_for = function(nic_name, cb) {
  var cmd = "ipconfig getoption " + nic_name + " subnet_mask";
  trim_exec(cmd, cb);
};

exports.speed_for = function(nic_name, cb, type)
{
    var cmd;
    if (type === 'wired')
    {
    cmd = "networksetup -getmedia " + nic_name + " | grep Active | awk '{print $2}'";
    }
    else if (type === 'wireless')
    {
    cmd = "/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport " + nic_name + " -I | grep lastTxRate";
    }
    trim_exec(cmd, cb);
};

exports.mtu_for = function(nic_name, cb) {
  var cmd = "networksetup -getmtu " + nic_name + " | awk '{print $3}'";
  trim_exec(cmd, cb);
};

exports.get_network_interfaces_list = function(cb)
{

    var count = 0;
    var list  = [];
    var nics  = os.networkInterfaces();

    function append_data(obj)
    {
        async.parallel([
            function(cb) {
              exports.gateway_ip_for(obj.name, cb)
            },
            function(cb) {
              exports.netmask_for(obj.name, cb)
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
        ], function(err, results) {
          if (results[0]) obj.ipv4_gateway_address  = results[0];
          if (results[1]) obj.ipv4_netmask          = results[1];
          if (results[2])
          {
            var speed = results[2];
            speed = speed.replace('lastTxRate:', '');
            obj.speed = parseInt(speed);
          }
                    if (results[3])
                {
                    var mtu = results[3];
                    obj.mtu = parseInt(mtu);
                    if (isNaN(obj.mtu))
                    {
                        obj.mtu = 0;
                    }
                }

          list.push(obj);
          --count || cb(null, list);
    })
}

  exec('networksetup -listallhardwareports', function(err, out) {
    if (err) return cb(err);

    var blocks = out.toString().split(/Hardware/).slice(1);
    count = blocks.length;

    blocks.forEach(function(block) {
      var parts = block.match(/Port: (.+)/),
          mac   = block.match(/Address: ([A-Fa-f0-9:-]+)/),
          name  = block.match(/Device: (\w+)/);

      if (!parts || !mac || !name) 
        return --count;

      var obj   = {},
      port  = parts[1];

      obj.name  = name[1];
      obj.description  = port;
      obj.type  = determine_nic_type(port);

      obj.ipv4_address  = null;
      obj.mac_address   = mac[1];

      (nics[obj.name] || []).forEach(function(type) {
        if (type.family == 'IPv4') {
          obj.ipv4_address = type.address;
        }
      });

      append_data(obj);
    })

    if (count == 0)
      cb(new Error('No interfaces found.'))
  })

};
