/*!
    \file main.js
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


/* global __dirname, process */

const {app, BrowserWindow, powerSaveBlocker, contentTracing} = require('electron');
const electron      = require('electron');
const path          = require('path');
const url           = require('url');
const nativeImage   = require('electron').nativeImage;
const ipcMain       = require('electron').ipcMain;
const fs            = require('fs');
const unhandled     = require('electron-unhandled');
const os            = require('os');
const reload        = require('electron-reload')(__dirname);
const lsbRelease    = require('bizzby-lsb-release');

var debug           = false;
var cli             = false;
var verbose         = false;
var cliOptions;

var windowUi;
var windowMeasurement;
var appPowerSaveBlockerId;
var appForceQuit    = true;
var workerPIDs      = [];

if(os.arch() === 'x64')
{
    app.commandLine.appendSwitch('js-flags', '--max-old-space-size=4096');
}




/*-------------------------app handling------------------------*/

app.requestSingleInstanceLock()

app.on('second-instance', (event, argv, cwd) =>
{
    app.quit();
})

app.on('ready', function()
{
    unhandled({'showDialog':false});
    
    handleArgv(process.argv);
    
    createWindowUi();
    createWindowMeasurement();
    
    appPowerSaveBlockerId = powerSaveBlocker.start('prevent-app-suspension');
    console.log('PowerSaveBlocker: Enabled');
    
    electron.powerMonitor.on('suspend', () => 
    {
        console.log('PowerMonitor: System suspended');
        windowMeasurement.reload();
    });
    
    electron.powerMonitor.on('resume', () => 
    {
        console.log('PowerMonitor: System resumed');
        windowMeasurement.reload();
    });
	
	electron.session.defaultSession.webRequest.onBeforeSendHeaders((details, callback) =>
	{
            details.requestHeaders['User-Agent'] = app.getName() + '/' + app.getVersion();
            details.requestHeaders['Origin'] = 'https://net-neutrality.tools/';
            callback({cancel: false, requestHeaders: details.requestHeaders});
	});
});

app.on('activate', function()
{
    if (!cli)
    {
        if (windowUi === null)
        {
            createWindow();
        }
        else
        {
            windowUi.show();
            windowUi.focus();
        }
    }
});

app.on('before-quit', function (e)
{
    appForceQuit = true;
});

app.on('activate-with-no-open-windows', function()
{
    if (!cli)
    {
        windowUi.show();
    }
});

app.on('quit', function()
{
    killWorkerPIDs();
    powerSaveBlocker.stop(appPowerSaveBlockerId);
    console.log('PowerSaveBlocker: Disabled');
}); 




/*-------------------------window handling------------------------*/

function createWindowUi()
{
    var width = 1120;
    var height = 720;
    var show = true;

    if (cli)
    {
        width = 0;
        heigth = 0;
        show = false;
    }

    windowUi = new BrowserWindow(
    {
        width: 1120,
        height: 720,
        x: 20,
        y: 20,
        resizable: true,
        show: show,
        webPreferences:
        {
            webSecurity: false, 
            allowDisplayingInsecureContent: true, 
            allowRunningInsecureContent: true,
            backgroundThrottling: false,
            nodeIntegration: true
        }
    });

    windowUi.loadFile('modules/index.html');
    
    if (debug && !cli)
    {
        windowUi.webContents.openDevTools();
    }
    
    windowUi.setMenu(null);
    
    windowUi.on('close', function(e)
    {
        if(!appForceQuit)
        {
            e.preventDefault();
            windowUi.hide();
        }
        else
        {
            app.quit();
        }
    });

    windowUi.webContents.session.setCertificateVerifyProc((request, callback) => 
    {
        callback(verifyCertificate(request));
    });
}

function createWindowMeasurement()
{
    var width = 900;
    var height = 700;
    var show = true;

    if (!debug || cli)
    {
        width = 0;
        heigth = 0;
        show = false;
    }

    windowMeasurement = new BrowserWindow(
    {
        width: width,
        height: height,
        x: 1200,
        y: 200,
        resizable: true,
        show: show,
        webPreferences:
        {
            webSecurity: false, 
            allowDisplayingInsecureContent: true, 
            allowRunningInsecureContent: true,
            backgroundThrottling: false,
            nodeIntegration: true
        }
    });

    windowMeasurement.loadFile('ias-desktop.html');

    if (debug && !cli)
    {
        windowMeasurement.webContents.openDevTools();
    }

    windowMeasurement.setMenu(null);
    
    windowMeasurement.webContents.session.setCertificateVerifyProc((request, callback) => 
    {
        callback(verifyCertificate(request));
    });
    
    windowMeasurement.webContents.on('did-finish-load', () =>
    {
        windowUi.send('iasCallback', 'iasDidLoad');

        if (cli)
        {
            console.log('Starting Measurement');
            if (typeof cliOptions !== 'undefined')
            {
                windowUi.send('cli', 'start', JSON.stringify(cliOptions));
            }
            else
            {
                windowUi.send('cli', 'start');
            }
        }
    });
}




/*-------------------------ipc handling------------------------*/

ipcMain.on('iasControl', function(event, cmd, data)
{
    windowMeasurement.send('iasControl', cmd, data);
    
    if (cmd === 'load')
    {
        killWorkerPIDs();
    }
});

ipcMain.on('iasCallback', function(event, cmd, report, error)
{
    windowUi.send('iasCallback', cmd, report, error);
    
    if(report)
    {
        report = JSON.parse(report);

        if (cli)
        {
            if (report.cmd === 'report' || report.cmd === 'finish' || report.cmd === 'completed')
            {
                if (report.test_case === 'rtt' && report.rtt_info.average_ns !== 0)
                {
                    console.log('RTT: ' + report.rtt_info.average_ns + ' ns');
                }
                if (report.test_case === 'download' && report.download_info[report.download_info.length-1].throughput_avg_bps !== 0)
                {
                    console.log('DOWNLOAD: ' + (report.download_info[report.download_info.length-1].throughput_avg_bps / 1000 / 1000).toFixed(2) + ' Mbit/s');
                }
                if (report.test_case === 'upload' && report.upload_info[report.upload_info.length-1].throughput_avg_bps !== 0)
                {
                    console.log('UPLOAD: ' + (report.upload_info[report.upload_info.length-1].throughput_avg_bps / 1000 / 1000).toFixed(2) + ' Mbit/s');
                }
            }

            if (verbose)
            {
                console.log('VERBOSE REPORT: \n' + JSON.stringify(report) + '\n\n');
            }

            if (report.cmd === 'error' || report.cmd === 'completed')
            {
                console.log('Mesaurement ' + report.cmd);
                killWorkerPIDs();
                app.quit();
            }
        }

        if (report.cmd === 'finish')
        {
            killWorkerPIDs();
        }
    }
});

ipcMain.on('iasSetWorkerPID', function(event, pid)
{
    workerPIDs.push(pid);
});




/*-------------------------other functions------------------------*/

function handleArgv(argv)
{
    var argsprefix = '--';

    argv.forEach(function(element, index)
    {
        if (element === '--dev')
        {
            argsprefix = '';
        }
        if (element === '--debug')
        {
            debug = true;
        }
        if (element === argsprefix + 'cli')
        {
            cli = true;
        }
        if (element === argsprefix + 'verbose')
        {
            verbose = true;
        }
        if (element.indexOf('=') !== 0)
        {
            var split = element.split('=');
            var rtt = argsprefix + 'rtt';
            var download = argsprefix + 'download';
            var upload = argsprefix + 'upload';

            if (typeof split[0] !== 'undefined' && typeof split[1] !== 'undefined')
            {
                switch (split[0])
                {
                    case rtt:
                    {
                        if (typeof cliOptions === 'undefined')
                        {
                            cliOptions = {};
                        }
                        cliOptions.rtt = {};
                        cliOptions.rtt.performMeasurement = split[1];
                        break;
                    }
                    case download:
                    {
                        if (typeof cliOptions === 'undefined')
                        {
                            cliOptions = {};
                        }
                        cliOptions.download = {};
                        cliOptions.download.performMeasurement = split[1];
                        break;
                    }
                    case upload:
                    {
                        if (typeof cliOptions === 'undefined')
                        {
                            cliOptions = {};
                        }
                        cliOptions.upload = {};
                        cliOptions.upload.performMeasurement = split[1];
                        break;
                    }
                }
            }
        }
    });
}

function verifyCertificate(request)
{
    //Uncomment the following line if TLS-certificate check should be deactivated
    //return 0;

    var certificateFound = false;
    var dir = path.join(__dirname, '/certificates/.');

    var certificates = fs.readdirSync(dir);
    for (var i=0; i<certificates.length;i++)
    {
        var hostnameStored = certificates[i].substring(0, certificates[i].lastIndexOf("."));

        hostnameStored = hostnameStored.replace(/.wildcard/g, '');

        if (hostnameStored === request.hostname)
        {
            var certificateRequested = request.certificate.data.replace(/\s/g, '');
            var certificateStored = fs.readFileSync(path.join(__dirname, '/certificates/' + certificates[i]), 'utf-8').replace(/\s/g, '').replace(/.wildcard/g, '');

            if (certificateRequested !== certificateStored)
            {
                console.log('SetCertificateVerifyProc: Certificate Rejected for ' + request.hostname + ', Reason: Certificate mismatch');
                continue;
            }
            else
            {
                certificateFound = true;
                console.log('SetCertificateVerifyProc: Certificate Accepted for ' + request.hostname);
                return 0;
            }
        }
    }

    if (!certificateFound)
    {
        console.log('SetCertificateVerifyProc: ' + request.hostname + ' not in list, denied');
        return -2;
    }
}

function killWorkerPIDs()
{
    if (!workerPIDs.length)
    {
        if (!cli) console.log('killWorkerPIDs: no Workers running')
    }
    else
    {
        try
        {
            workerPIDs.forEach(function(element)
            {
                if (element !== 0) 
                {
                    process.kill(element);
                }
            });
        }
        catch(exception)
        {
            if (!cli) console.log('killWorkerPIDs: exception catched')
        }
        finally
        {
            workerPIDs = null;
            workerPIDs = [];
            if (!cli) console.log('killWorkerPIDs: Workers killed')
        } 
    }
}
