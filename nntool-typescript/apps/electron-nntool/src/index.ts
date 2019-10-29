import { app, BrowserWindow, ipcMain, screen } from 'electron';
import * as path from 'path';
import * as url from 'url';

let win: BrowserWindow;

const args = process.argv.slice(1);
const serve = args.some(val => val === '--serve');

const getFromEnv = parseInt(process.env.ELECTRON_IS_DEV, 10) === 1;
const isEnvSet = 'ELECTRON_IS_DEV' in process.env;
const debugMode =
  serve || (isEnvSet ? getFromEnv : process.defaultApp || /node_modules[\\/]electron[\\/]/.test(process.execPath));

const mainWindowSettings: Electron.BrowserWindowConstructorOptions = {
  frame: true,
  resizable: true,
  focusable: true,
  fullscreenable: true,
  kiosk: false,
  webPreferences: {
    devTools: debugMode,
    nodeIntegration: true
  }
};

/**
 * Hooks for electron main process
 */
function initMainListener() {
  ipcMain.on('ELECTRON_BRIDGE_HOST', (event, msg) => {
    console.log('msg received', msg);
    if (msg === 'ping') {
      event.sender.send('ELECTRON_BRIDGE_CLIENT', 'pong');
    }
  });
}

/**
 * Create main window presentation
 */
function createWindow() {
  const sizes = screen.getPrimaryDisplay().workAreaSize;

  if (debugMode) {
    process.env['ELECTRON_DISABLE_SECURITY_WARNINGS'] = 'true';

    mainWindowSettings.width = 1280;
    mainWindowSettings.height = 800;
  } else {
    mainWindowSettings.width = Math.trunc(sizes.width * 0.75);
    mainWindowSettings.height = Math.trunc(sizes.height * 0.85);
  }

  win = new BrowserWindow(mainWindowSettings);
  win.center();

  let launchPath;
  if (serve) {
    require('electron-reload')(__dirname, {
      electron: require(`${__dirname}/../../../node_modules/electron`)
    });

    launchPath = 'http://localhost:4200';
  } else {
    launchPath = url.format({
      pathname: path.join(__dirname, 'index.html'),
      protocol: 'file:',
      slashes: true
    });
  }

  win.loadURL(launchPath);

  console.log('launched electron with:', launchPath);

  win.on('closed', () => {
    // Dereference the window object, usually you would store windows
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    win = null;
  });

  initMainListener();

  if (debugMode) {
    win.webContents.openDevTools();
  }
}

try {
  // This method will be called when Electron has finished
  // initialization and is ready to create browser windows.
  // Some APIs can only be used after this event occurs.
  app.on('ready', createWindow);

  // Quit when all windows are closed.
  app.on('window-all-closed', () => {
    // On OS X it is common for applications and their menu bar
    // to stay active until the user quits explicitly with Cmd + Q
    if (process.platform !== 'darwin') {
      app.quit();
    }
  });

  app.on('activate', () => {
    // On OS X it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if (win === null) {
      createWindow();
    }
  });
} catch (e) {
  // Catch Error
  // throw e;
  console.error(e);
}
