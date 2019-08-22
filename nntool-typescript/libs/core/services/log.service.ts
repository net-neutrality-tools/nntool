import { Injectable } from '@angular/core';

export interface DebugLevel {
  LEVEL_1: boolean;
  LEVEL_2: boolean;
  LEVEL_3: boolean;
  LEVEL_4: boolean;
  LEVEL_5: boolean;
}

@Injectable()
export class LogService {
  public static DEBUG: DebugLevel = {
    LEVEL_1: false, // .warn only
    LEVEL_2: false, // .error only
    LEVEL_3: false, // .log + all the above
    LEVEL_4: false, // .log + all the above + info
    LEVEL_5: false // just info (excluding all else)
  };

  // info (extra messages like analytics)
  // use LEVEL_5 to see only these
  public info(...msg: any[]) {
    if (LogService.DEBUG.LEVEL_5 || LogService.DEBUG.LEVEL_4) {
      // extra messages
      // tslint:disable-next-line
      console.info(msg);
    }
  }

  // debug (standard output)
  public debug(...msg: any[]) {
    if (LogService.DEBUG.LEVEL_4 || LogService.DEBUG.LEVEL_3) {
      // console.debug does not work on {N} apps... use `log`
      // tslint:disable-next-line
      console.log(msg);
    }
  }

  // error
  public error(...err: any[]) {
    if (LogService.DEBUG.LEVEL_4 || LogService.DEBUG.LEVEL_3 || LogService.DEBUG.LEVEL_2) {
      // tslint:disable-next-line
      console.error(err);
    }
  }

  // warn
  public warn(...warn: any[]) {
    if (LogService.DEBUG.LEVEL_4 || LogService.DEBUG.LEVEL_3 || LogService.DEBUG.LEVEL_1) {
      // tslint:disable-next-line
      console.warn(warn);
    }
  }
}
