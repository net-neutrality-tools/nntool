import {Component} from "@angular/core";
import {ConfigService} from "../../services/config.service";
import {TranslateService} from "@ngx-translate/core";
import {BarUI} from "../tests-ui/bar/bar-ui";
import {BarUIState} from "../tests-ui/bar/bar-ui-state";
import {PortBlockingTestImplementation} from "../tests-implementation/port-blocking/port-blocking-test-implementation";
import {PortBlockingTestState} from "../tests-implementation/port-blocking/port-blocking-test-state";
import {PortBlockingTestTypeEnum} from "../tests-implementation/port-blocking/enums/port-blocking-test-type";
import {BarUIShowableTestTypeEnum} from "../tests-ui/bar/enums/bar-ui-showable-test-type.enum";

@Component({
    // needs to be mentioned here, but also mentioned in gauge-ui.ts for reference
    templateUrl: "./app/testing/tests-ui/bar/bar-ui.template.html",
    selector: 'port-blocking-test-bar'
})
export class PortBlockingTestBar extends BarUI<PortBlockingTestImplementation, PortBlockingTestState> {


    // TODO: rethink DI in this use case, testImplementation should not be one instance, if there were more than one test at once
    // TODO: Remove this constructor when DI on generic type figured out
    constructor(testImplementation: PortBlockingTestImplementation, configService: ConfigService, translateService: TranslateService) {
        super(testImplementation, configService, translateService);
    }

    protected testStateToUIState = (state: PortBlockingTestState): BarUIState => {
        const barUIState: BarUIState = new BarUIState();
        barUIState.types = [];
        state.types.forEach((type: {
            key: PortBlockingTestTypeEnum;
            ports: {
                number: number;
                reachable: boolean;
                finished: boolean;
            }[]
        }) => {
            const mappedType: {
                key: BarUIShowableTestTypeEnum;
                ports: {
                    number: number;
                    reachable: boolean;
                    finished: boolean;
                }[]
            } = {key: undefined, ports: undefined};
            mappedType.key = BarUIShowableTestTypeEnum[PortBlockingTestTypeEnum[type.key]];
            mappedType.ports = type.ports;
            barUIState.types.push(mappedType);
        });
        return barUIState;
    }
}