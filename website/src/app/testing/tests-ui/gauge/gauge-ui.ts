import {AfterViewInit, Component} from "@angular/core";
import {GaugeUIState} from "./gauge-ui-state";
import {TestState} from "../../tests-implementation/test-state";
import {TestImplementation} from "../../tests-implementation/test-implementation";
import {Test} from "../../test.component";
import {forkJoin, Observable} from "rxjs";
import {first} from "rxjs/operators";
import {WebsiteSettings} from "../../../settings/settings.interface";
import {ConfigService} from "../../../services/config.service";
import {TranslateService} from "@ngx-translate/core";
import {BaseMeasurementGauge} from "./existing-gauge-ui/base.gauge.ui";
import {MeasurementGauge} from "./existing-gauge-ui/gauge.ui";
import {TestConfig} from "../../tests-implementation/test-config";

@Component({
    templateUrl: "./app/testing/tests-ui/gauge/gauge-ui.template.html",
})
export abstract class GaugeUI<T extends TestImplementation<TC, TS>, TC extends TestConfig, TS extends TestState>
    extends Test<GaugeUIState, T, TC, TS>
    implements AfterViewInit {

    private active: boolean;

    private renderingConfig: WebsiteSettings;
    protected testGauge: BaseMeasurementGauge;

    protected constructor(testImplementation: T, configService: ConfigService, protected translateService: TranslateService) {
        super(testImplementation);
        this.state.subscribe(this.handleState);
        this.renderingConfig = configService.getConfig();
    }

    private handleState = (state: GaugeUIState) => {
        if (this.testGauge) {
            this.testGauge.onStateChange(state);
        }
    }

    public setActive = (active: boolean) => {
        this.active = active;
        if (this.active && this.testGauge) {
            setTimeout(() => {
                this.testGauge.resizeEvent();
            }, 0);
        }
    }

    ngAfterViewInit () {
        this.testGauge = this.getGauge();
        this.testGauge.draw();
    }

    private getGauge (): any {
        let hasQos: boolean = this.renderingConfig.nettest && this.renderingConfig.nettest.tests && this.renderingConfig.nettest.tests.qos;
        const translations: {[key: string]: any} = {
            SPEED_MBPS: "Mbps",
            DURATION_MS: "ms",
            INNER_TEXTS: ['0MBPS', '1MBPS', '10MBPS', '100MBPS', '1GBPS'],
            OUTER_TEXTS: ['INIT', 'PING', 'DOWN', 'UP']
        };
        if (hasQos) {
            translations['OUTER_TEXTS'].push('QOS');
        }
        let gaugeColors: {[key: string]: string} = {
            baseColor: "#EEEEEE",
            valueColor: "#878787",
            progressColor: "#911232",
            fontColor: "#FFFFFF"
        };
        let font: string = null;
        if (this.renderingConfig.colors.gauge) {
            const gc: {[key: string]: string} = this.renderingConfig.colors.gauge;
            gaugeColors = {
                baseColor: gc.arc_background,
                valueColor: gc.arc_inner,
                progressColor: gc.arc_outer,
                fontColor: gc.font
            };

            if (gc.fontName) {
                font = gc.fontName;
            }
        }
        const gauge: any = new MeasurementGauge(
            <HTMLCanvasElement>document.getElementById("nettest-gauge"),
            <HTMLCanvasElement>document.getElementById("nettest-gauge-2"),
            document.getElementById("nettest-state"),
            document.getElementById("nettest-ping"),
            document.getElementById("nettest-up"),
            document.getElementById("nettest-down"),
            document.getElementById("nettest-position"),
            document.getElementById("nettest-provider"),
            document.getElementById("nettest-device"),
            document.getElementById("nettest-technology"),
            document.getElementById("nettest-server"),
            translations, gaugeColors, font, hasQos,
        );

        let firstRun: boolean = true;
        const translate: any = () => {
            let obs: Observable<string>[] = [];
            for (let key in translations) {
                if (key === "INNER_TEXTS" || key === "OUTER_TEXTS") {
                    for (let gaugeKey of translations[key]) {
                        obs.push(this.translateService.get("NETTEST.GAUGE." + gaugeKey));
                    }
                } else {
                    obs.push(this.translateService.get("WEBSITE.UNITS." + key));
                }
            }

            forkJoin(obs).pipe( first() ).subscribe(
                (data: any) => {
                    let i: number = 0;
                    let res: any = {};

                    for (let key in translations) {
                        if (!translations.hasOwnProperty(key)) {
                            continue;
                        }
                        if (i >= data.length) {
                            break;
                        }
                        if (key === "INNER_TEXTS" || key === "OUTER_TEXTS") {
                            res[key] = [];
                            for (let gaugeKey of translations[key]) {
                                res[key].push(data[i]);
                                i++;
                            }
                        } else {
                            res[key] = data[i];
                            i++;
                        }
                    }

                    if (res) {
                        gauge.translations = res;
                        gauge.draw();
                    }
                }
            );
            if (firstRun) {
                this.translateService.onLangChange.subscribe(
                    translate
                );
                firstRun = false;
            }
        };
        translate();
        return gauge;
    }
}