import { BarUIShowableTestTypeEnum } from './enums/bar-ui-showable-test-type.enum';

export class BarTestResult {
    key: BarUIShowableTestTypeEnum;
    progress?: number;
    ports ?: Array<{
        number: number;
        reachable: boolean;
        finished: boolean;
    }>;
}