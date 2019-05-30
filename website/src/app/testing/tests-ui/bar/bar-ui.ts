import {AfterViewInit, Component} from '@angular/core';
import {TestState} from '../../tests-implementation/test-state';
import {TestImplementation} from '../../tests-implementation/test-implementation';
import {Test} from '../../test.component';
import {ConfigService} from '../../../services/config.service';
import {TranslateService} from '@ngx-translate/core';
import {BarUIState} from './bar-ui-state';
import {BarUIShowableTestTypeEnum} from './enums/bar-ui-showable-test-type.enum';
import {TestConfig} from '../../tests-implementation/test-config';

class Point {
    constructor(public x: number, public y: number) {}
}

@Component({
    templateUrl: './bar-ui.template.html',
})
export abstract class BarUIComponent<T extends TestImplementation<TC, TS>, TC extends TestConfig, TS extends TestState>
    extends Test<BarUIState, T, TC, TS>
    implements AfterViewInit {

    private resolutionScaleFactor = 2; // read window.devicePixelRatio (inject window object properly)
    private canvas: HTMLCanvasElement;
    private canvasContext: CanvasRenderingContext2D;
    private currentState: BarUIState;
    private barWidth: number;
    private barLength: number;
    private drawing = false;
    private active: boolean;

    private translations: {[key: string]: any};
    private barColors: {[key: string]: string};
    private font: string;

    protected constructor(testImplementation: T, configService: ConfigService, protected translateService: TranslateService) {
        super(testImplementation);
        this.state.subscribe(this.handleState);
        this.configureBarUI(configService.getConfig());
    }

    private configureBarUI(config: any) {
        this.translations = {
            UDP: 'UDP'
        };

        this.barColors = {
            baseColor: '#EEEEEE',
            valueColor: '#878787',
            progressColor: '#911232',
            fontColor: '#FFFFFF'
        };

        this.font = null;
        if (config.colors.gauge) {
            const gc: {[key: string]: string} = config.colors.gauge;
            this.barColors = {
                baseColor: gc.arc_background,
                valueColor: gc.arc_inner,
                progressColor: gc.arc_outer,
                fontColor: gc.font
            };

            if (gc.fontName) {
                this.font = gc.fontName;
            }
        }
    }

    ngAfterViewInit() {
        this.canvas = document.getElementById('nettest-bar') as HTMLCanvasElement;
        this.canvasContext = this.canvas.getContext('2d');
        this.addResize();
        this.setCanvas();
    }

    private handleState = (state: BarUIState) => {
        this.currentState = state;
        if (this.canvas && this.canvasContext) {
            this.draw(this.currentState);
        }
    }

    public setActive = (active: boolean) => {
        this.active = active;
        if (this.active && this.canvas && this.canvasContext) {
            setTimeout(() => {
                this.resizeEvent();
            }, 0);
        }
    }

    private draw = (state: BarUIState) => {
        if (this.drawing) {
            // console.debug("Already drawing..");
            setTimeout(() => {
                this.draw(state);
            }, 100);
            return;
        }
        this.drawing = true;
        this.clear();
        if (state) {
            state.types.forEach((type: {
                key: BarUIShowableTestTypeEnum;
                ports: {
                    number: number;
                    reachable: boolean;
                    finished: boolean;
                }[]
            }) => {
                if (type.key === BarUIShowableTestTypeEnum.UDP) {
                    this.drawBar(1, this.barColors.baseColor);
                    this.drawBar(this.calculateProgressOfTestType(type), this.barColors.valueColor);
                    this.drawBarName(this.translations[type.key], this.barColors.fontColor);
                }
            });
        }


        this.drawing = false;
    }

    private drawBar = (progress: number, color: string): void => {
        this.canvasContext.lineWidth = this.barWidth;
        this.canvasContext.strokeStyle = color;
        this.canvasContext.lineCap = 'round';

        const center = this.getCenter(this.canvas);

        this.canvasContext.beginPath();
        this.canvasContext.moveTo(center.x - this.barLength / 2, center.y - this.barLength / 3);
        this.canvasContext.lineTo(center.x - this.barLength / 2 + this.barLength * progress, center.y - this.barLength / 3);
        this.canvasContext.stroke();

    }

    private drawBarName = (name: string, color: string): void => {
        const textHeight: number = this.barWidth * 0.6;

        this.canvasContext.font = '100 ' + textHeight + 'px ' + this.font;
        this.canvasContext.fillStyle = color;

        const center = this.getCenter(this.canvas);

        this.canvasContext.fillText(name, center.x - this.barLength / 2, center.y - this.barLength / 3 + textHeight / 2.3);
    }


    private addResize(): void {
        if (this.canvas.addEventListener) {
            // > IE8
            window.addEventListener('resize', () => { this.resizeEvent(); });
        } else {
            (window as any).attachEvent('onresize', () => { this.resizeEvent(); });
        }
    }

    private resizeEvent() {
        if (this.drawing) {
            return;
        }
        this.clear();
        this.setCanvas();
        this.draw(this.currentState);
    }

    private clear() {
        this.canvasContext.clearRect(0, 0, this.canvas.width, this.canvas.height);
    }

    private setCanvas(minVal: number = 400) {
        this.canvas.width = Math.max(this.canvas.clientWidth, minVal) * this.resolutionScaleFactor;
        this.canvas.height = Math.max(this.canvas.clientHeight, minVal) * this.resolutionScaleFactor;

        this.barWidth = this.canvas.width / 13;
        this.barLength = this.canvas.width * 0.9;
    }

    private getCenter(canvas: HTMLCanvasElement): Point {
        return new Point(canvas.width / 2, canvas.height / 2);
    }

    private calculateProgressOfTestType = (type: {
        key: BarUIShowableTestTypeEnum;
        ports: {
            number: number;
            reachable: boolean;
            finished: boolean;
        }[]
    }): number => {
        const numberOfPortsToTest = type.ports.length;
        let numberOfTestedPorts = 0;

        type.ports.forEach((port: {
            number: number;
            reachable: false;
            finished: false;
        }) => {
            if (port.finished) {
                numberOfTestedPorts++;
            }
        });

        return numberOfTestedPorts / numberOfPortsToTest;
    }

}
