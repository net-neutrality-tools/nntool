import { BarUIShowableTestTypeEnum } from './enums/bar-ui-showable-test-type.enum';

export class BarTestResult {
    key: BarUIShowableTestTypeEnum;
    progress?: number;
    ports ?: Array<{
        number: number;
        packets: {
            requested_packets: number,
            lost: number;
            received: number;
            sent: number
        };
    }>;
}