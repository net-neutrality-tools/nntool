import {Injectable} from "@angular/core";
import {ConfigService} from "./config.service";
import {WebsiteSettings} from "../settings/settings.interface";


export class ColorList {

    private index: number;
    private colors: string[];

    constructor (colors: string[]) {
        this.index = 0;
        this.colors = colors;
    }

    getNext (): string {
        if (!this.colors || this.colors.length === 0) {
            return null;
        }

        let res: string = this.colors[this.index];
        this.index = (this.index + 1) % this.colors.length;
        return res;
    }
}


@Injectable()
export class ColorService {

    private config: WebsiteSettings;


    constructor (
        private configService: ConfigService
    ) {}

    private shuffleArray<T> (toShuffle: T[], inPlace?: boolean): T[] {
        let a: T[];
        if (inPlace === undefined || !inPlace) {
            a = toShuffle.slice();
        } else {
            a = toShuffle;
        }
        let j: number, x: T, i: number;

        for (i = a.length; i; i--) {
            j = Math.floor(Math.random() * i);
            x = a[i - 1];
            a[i - 1] = a[j];
            a[j] = x;
        }
        return a;
    }

    private randomIntFromInterval(minVal: number, maxVal: number): number {
        return Math.floor(Math.random() * (maxVal - minVal + 1) + minVal);
    }

    private randomIntFromMax(maxVal: number) {
        return this.randomIntFromInterval(0, maxVal);
    }

    get groups (): {[key: string]: string[]} {
        if (!this.config) {
            this.config = this.configService.getConfig();
        }
        return this.config.colors.groups;
    }

    randomGroup (): string {
        let groups: any = this.groups;
        let keys: string[] = Object.keys(groups);
        if (keys.length === 0) {
            return null;
        }
        let index: number = this.randomIntFromMax(keys.length - 1);
        return keys[index];
    }

    randomColor (key: string, excludes?: string[]): string {
        if (excludes === undefined) {
            excludes = [];
        }

        // TODO: finish
        return "red";
    }

    colorList (): ColorList {
        let colors: string[] = [];
        let groups: {[key: string]: string[]} = Object.assign({}, this.groups);
        let keys: string[] = Object.keys(this.groups);
        keys = this.shuffleArray(keys);
        let done: boolean = false;

        while (!done) {
            done = true;
            for (let key of keys) {
                if (groups[key].length === 0) {
                    continue;
                }
                done = false;
                groups[key] = this.shuffleArray(groups[key]);
                colors.push(groups[key].pop());
            }
        }
        return new ColorList(colors);
    }
}