import { Injectable } from '@angular/core';
declare let console: any;

export class Logger {
  public name: string;

  constructor(name?: string) {
    this.name = name;
  }

  public error(...args: any[]) {
    if (this.name) {
      args.unshift(this.name + ':');
    }
    console.error(...args);
  }

  public warn(...args: any[]) {
    if (this.name) {
      args.unshift(this.name + ':');
    }
    console.warn(...args);
  }

  public info(...args: any[]) {
    console.info(...args);
  }

  public debug(...args: any[]) {
    if (this.name) {
      args.unshift(this.name + ':');
    }
    console.debug(...args);
  }

  public trace(...args: any[]) {
    console.trace(...args);
  }
}

@Injectable()
export class LoggerService {
  public static getLogger(name?: string): Logger {
    return new Logger(name);
  }
}
