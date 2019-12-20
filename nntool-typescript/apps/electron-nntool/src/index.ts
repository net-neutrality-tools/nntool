/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * Copyright 2019 zafaco GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import { app, BrowserWindow, ipcMain, screen, powerSaveBlocker, powerMonitor, session } from 'electron';
import * as path from 'path';
import * as url from 'url';
import * as os from 'os';
import * as fs from 'fs';
import unhandled from 'electron-unhandled';

let uiWindow: BrowserWindow;
let measurementWindow: BrowserWindow;

const args = process.argv.slice(1);
const serve = args.some(val => val === '--serve');

const getFromEnv = parseInt(process.env.ELECTRON_IS_DEV, 10) === 1;
const isEnvSet = 'ELECTRON_IS_DEV' in process.env;
let debugMode =
  serve || (isEnvSet ? getFromEnv : process.defaultApp || /node_modules[\\/]electron[\\/]/.test(process.execPath));

/////

let cli = false;
let verbose = false;
let cliOptions;

let appPowerSaveBlockerId;
let workerPIDs = [];

/////

const defaultWindowSettings: Electron.BrowserWindowConstructorOptions = {
  frame: true,
  resizable: true,
  focusable: true,
  fullscreenable: true,
  kiosk: false,
  show: true,
  webPreferences: {
    webSecurity: false,
    allowRunningInsecureContent: true,
    backgroundThrottling: false,
    devTools: debugMode,
    nodeIntegrationInWorker: true,
    nodeIntegration: true
  }
};

/////

try {
  if (os.arch() === 'x64') {
    app.commandLine.appendSwitch('js-flags', '--max-old-space-size=4096');
  }

  app.requestSingleInstanceLock();

  app.on('second-instance', (event, argv, cwd) => {
    app.quit();
  });

  // This method will be called when Electron has finished
  // initialization and is ready to create browser windows.
  // Some APIs can only be used after this event occurs.
  app.on('ready', onAppReady);

  // Quit when all windows are closed.
  app.on('window-all-closed', () => {
    // On OS X it is common for applications and their menu bar
    // to stay active until the user quits explicitly with Cmd + Q
    if (process.platform !== 'darwin') {
      app.quit();
    }
  });

  app.on('activate', () => {
    if (cli) {
      return;
    }

    // On OS X it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if (uiWindow === null) {
      createUiWindow();
    } else {
      uiWindow.show();
      uiWindow.focus();
    }
  });

  app.on('quit', () => {
    killWorkerPIDs();
    powerSaveBlocker.stop(appPowerSaveBlockerId);
  });
} catch (e) {
  // Catch Error
  // throw e;
  console.error(e);
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function initMainListener() {
  ipcMain.on('iasControl', (event, cmd, data) => {
    measurementWindow.webContents.send('iasControl', cmd, data);

    if (cmd === 'load') {
      killWorkerPIDs();
    }
  });

  ipcMain.on('iasCallback', (event, cmd, report, error) => {
    uiWindow.webContents.send('iasCallback', cmd, report, error);

    if (report) {
      report = JSON.parse(report);

      /*if (cli) {
        if (report.cmd === 'report' || report.cmd === 'finish' || report.cmd === 'completed') {
          if (report.test_case === 'rtt' && report.rtt_info.average_ns !== 0) {
            console.log('RTT: ' + report.rtt_info.average_ns + ' ns');
          }
          if (
            report.test_case === 'download' &&
            report.download_info[report.download_info.length - 1].throughput_avg_bps !== 0
          ) {
            console.log(
              'DOWNLOAD: ' +
                (report.download_info[report.download_info.length - 1].throughput_avg_bps / 1000 / 1000).toFixed(2) +
                ' Mbit/s'
            );
          }
          if (
            report.test_case === 'upload' &&
            report.upload_info[report.upload_info.length - 1].throughput_avg_bps !== 0
          ) {
            console.log(
              'UPLOAD: ' +
                (report.upload_info[report.upload_info.length - 1].throughput_avg_bps / 1000 / 1000).toFixed(2) +
                ' Mbit/s'
            );
          }
        }

        if (verbose) {
          console.log('VERBOSE REPORT: \n' + JSON.stringify(report) + '\n\n');
        }

        if (report.cmd === 'error' || report.cmd === 'completed') {
          console.log('Mesaurement ' + report.cmd);
          killWorkerPIDs();
          app.quit();
        }
      }*/

      if (report.cmd === 'finish') {
        killWorkerPIDs();
      }
    }
  });

  ipcMain.on('iasSetWorkerPID', (event, pid) => {
    workerPIDs.push(pid);
  });
}

function onAppReady() {
  unhandled({ showDialog: false });

  handleArgv(process.argv);

  createUiWindow();
  createMeasurementWindow();

  initMainListener();

  appPowerSaveBlockerId = powerSaveBlocker.start('prevent-app-suspension');

  powerMonitor.on('suspend', () => {
    console.log('PowerMonitor: System suspended');
    uiWindow.reload();
  });

  powerMonitor.on('resume', () => {
    console.log('PowerMonitor: System resumed');
    uiWindow.reload();
  });

  session.defaultSession.webRequest.onBeforeSendHeaders((details, callback) => {
    details.requestHeaders['User-Agent'] = app.getName() + '/' + app.getVersion();
    //details.requestHeaders['Origin'] = 'https://net-neutrality.tools/';
    callback({ cancel: false, requestHeaders: details.requestHeaders });
  });
}

/**
 * Create main window presentation
 */
function createUiWindow() {
  const uiWindowSettings = Object.assign({}, defaultWindowSettings);

  if (debugMode) {
    process.env['ELECTRON_DISABLE_SECURITY_WARNINGS'] = 'true';

    uiWindowSettings.width = 1280;
    uiWindowSettings.height = 800;
  } else if (cli) {
    uiWindowSettings.width = 0;
    uiWindowSettings.height = 0;
    uiWindowSettings.show = false;
  } else {
    const sizes = screen.getPrimaryDisplay().workAreaSize;

    uiWindowSettings.width = Math.trunc(sizes.width * 0.75);
    uiWindowSettings.height = Math.trunc(sizes.height * 0.85);
  }

  uiWindow = new BrowserWindow(uiWindowSettings);
  uiWindow.center();

  const launchPath = getLaunchPath('index.html');
  console.log('launched electron with:', launchPath);

  uiWindow.loadURL(launchPath);

  uiWindow.on('closed', () => {
    uiWindow = null;
  });

  if (debugMode && !cli) {
    uiWindow.webContents.openDevTools();
  }

  /*uiWindow.webContents.session.setCertificateVerifyProc((request, callback) => {
    callback(verifyCertificate(request));
  });*/
}

function createMeasurementWindow() {
  const measurementWindowSettings = Object.assign({}, defaultWindowSettings);

  if (!debugMode || cli) {
    measurementWindowSettings.width = 0;
    measurementWindowSettings.height = 0;
    measurementWindowSettings.show = false;
  }

  measurementWindow = new BrowserWindow(measurementWindowSettings);
  measurementWindow.loadURL(getLaunchPath('ias-desktop.html'));

  measurementWindow.on('closed', () => {
    measurementWindow = null;
  });

  if (debugMode && !cli) {
    measurementWindow.webContents.openDevTools();
  }

  measurementWindow.setMenu(null);

  /*measurementWindow.webContents.session.setCertificateVerifyProc((request, callback) => {
    callback(verifyCertificate(request));
  });*/

  measurementWindow.webContents.on('did-finish-load', () => {
    uiWindow.webContents.send('iasCallback', 'iasDidLoad');

    if (cli) {
      console.log('Starting Measurement');
      if (typeof cliOptions !== 'undefined') {
        uiWindow.webContents.send('cli', 'start', JSON.stringify(cliOptions));
      } else {
        uiWindow.webContents.send('cli', 'start');
      }
    }
  });
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function getLaunchPath(file: string): string {
  let launchPath;
  if (serve) {
    require('electron-reload')(__dirname, {
      electron: require(`${__dirname}/../../../node_modules/electron`)
    });

    launchPath = 'http://localhost:4200/' + file;
  } else {
    launchPath = url.format({
      pathname: path.join(__dirname, file),
      protocol: 'file:',
      slashes: true
    });
  }

  return launchPath;
}

function handleArgv(argv) {
  let argsprefix = '--';

  argv.forEach((element, index) => {
    if (element === '--dev') {
      argsprefix = '';
    }
    if (element === '--debug') {
      debugMode = true;
    }
    if (element === argsprefix + 'cli') {
      cli = true;
    }
    if (element === argsprefix + 'verbose') {
      verbose = true;
    }
    if (element.indexOf('=') !== 0) {
      const split = element.split('=');
      const rtt = argsprefix + 'rtt';
      const download = argsprefix + 'download';
      const upload = argsprefix + 'upload';

      if (typeof split[0] !== 'undefined' && typeof split[1] !== 'undefined') {
        switch (split[0]) {
          case rtt: {
            if (typeof cliOptions === 'undefined') {
              cliOptions = {};
            }
            cliOptions.rtt = {};
            cliOptions.rtt.performMeasurement = split[1];
            break;
          }
          case download: {
            if (typeof cliOptions === 'undefined') {
              cliOptions = {};
            }
            cliOptions.download = {};
            cliOptions.download.performMeasurement = split[1];
            break;
          }
          case upload: {
            if (typeof cliOptions === 'undefined') {
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

function verifyCertificate(request) {
  //Uncomment the following line if TLS-certificate check should be deactivated
  return 0;

  var certificateFound = false;
  var dir = path.join(__dirname, '/certificates/.');

  var certificates = fs.readdirSync(dir);
  for (var i = 0; i < certificates.length; i++) {
    var hostnameStored = certificates[i].substring(0, certificates[i].lastIndexOf('.'));

    hostnameStored = hostnameStored.replace(/.wildcard/g, '');

    if (hostnameStored === request.hostname) {
      var certificateRequested = request.certificate.data.replace(/\s/g, '');
      var certificateStored = fs
        .readFileSync(path.join(__dirname, '/certificates/' + certificates[i]), 'utf-8')
        .replace(/\s/g, '')
        .replace(/.wildcard/g, '');

      if (certificateRequested !== certificateStored) {
        console.log(
          'SetCertificateVerifyProc: Certificate Rejected for ' + request.hostname + ', Reason: Certificate mismatch'
        );
        continue;
      } else {
        certificateFound = true;
        console.log('SetCertificateVerifyProc: Certificate Accepted for ' + request.hostname);
        return 0;
      }
    }
  }

  if (!certificateFound) {
    console.log('SetCertificateVerifyProc: ' + request.hostname + ' not in list, denied');
    return -2;
  }
}

function killWorkerPIDs() {
  if (!workerPIDs.length) {
    if (!cli) {
      console.log('killWorkerPIDs: no Workers running');
    }
  } else {
    try {
      workerPIDs.forEach(element => {
        if (element !== 0) {
          process.kill(element);
        }
      });
    } catch (exception) {
      if (!cli) {
        console.log('killWorkerPIDs: exception catched');
      }
    } finally {
      workerPIDs = null;
      workerPIDs = [];
      if (!cli) {
        console.log('killWorkerPIDs: Workers killed');
      }
    }
  }
}
