import {Injectable} from "@angular/core";
declare let console: any;


export class Logger {

    name: string;

    constructor (name?: string) {
        this.name = name;
    }

    error (...args: any[]) {
        if (this.name) {
            args.unshift(this.name + ":");
        }
        console.error(...args);
    }

    warn (...args: any[]) {
        if (this.name) {
            args.unshift(this.name + ":");
        }
        console.warn(...args);
    }

    info (...args: any[]) {
        console.info(...args);
    }

    debug (...args: any[]) {
        if (this.name) {
            args.unshift(this.name + ":");
        }
        console.debug(...args);
    }

    trace (...args: any[]) {
        console.trace(...args);
    }
}


@Injectable()
export class LoggerService {

    static getLogger (name?: string): Logger {
        return new Logger(name);
    }
}