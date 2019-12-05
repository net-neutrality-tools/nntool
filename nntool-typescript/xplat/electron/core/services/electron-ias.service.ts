import { Injectable } from '@angular/core';
import * as childProcess from 'child_process';
import { ipcRenderer } from 'electron';
import { IasService } from '@nntool-typescript/ias/ias.service';

@Injectable()
export class ElectronIasService implements IasService {
  private _ipc: typeof ipcRenderer;
  private _childProcess: typeof childProcess;

  private callback: (data: any) => void;
  private running = false;

  constructor() {
    const win = window as any;

    this._ipc = win.require('electron').ipcRenderer;
    const unhandled = require('electron-unhandled');

    unhandled({ showDialog: false });

    const reallyThis = this;

    this._ipc.on('iasCallback', (event, cmd, report, error) => {
      //console.log(event);
      //console.log(cmd);
      //console.log(report);
      //console.log(error);

      if (reallyThis.callback) {
        reallyThis.callback(report);
      }
      /*switch (cmd) {
        case 'iasDidLoad': {
          iasDidLoad();
          break;
        }

        case 'iasCallbackWithResponse': {
          iasCallbackWithResponse(report);
          break;
        }

        case 'iasDidCompleteWithResponse': {
          iasDidCompleteWithResponse(report, error);
          break;
        }

        case 'iasDidStop': {
          iasDidStop();
          break;
        }
      }*/
    });

    this._ipc.on('cli', (event, cmd, data) => {
      if (cmd === 'start') {
        if (data) {
          const cliOptions = JSON.parse(data);
          console.log('CLI CLI CLI CLI CLI');
        }
      }
    });
  }

  public start(config: any) {
    this.running = true;
    this.iasSendToControl('start', JSON.stringify(config));
  }

  public stop() {
    this.running = false;
    this.iasSendToControl('stop');
  }

  public setCallback(callback: (data: any) => void) {
    this.callback = callback;
  }

  public isRunning(): boolean {
    return this.running;
  }

  public getPlatform(): string {
    return 'desktop';
  }

  private iasSendToControl(cmd: any, data?: any) {
    this._ipc.send('iasControl', cmd, data);
  }
}
