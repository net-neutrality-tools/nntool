import {UIState} from '../ui-state';
import {BarUIShowableTestTypeEnum} from './enums/bar-ui-showable-test-type.enum';

export class BarUIState extends UIState {
    types: {
        key: BarUIShowableTestTypeEnum;
        ports: {
            number: number;
            reachable: boolean;
            finished: boolean;
        }[]
    }[];
}
