import {TestState} from "../test-state";
import {PortBlockingTestTypeEnum} from "./enums/port-blocking-test-type";

export class PortBlockingTestState extends TestState {
    types: {
        key: PortBlockingTestTypeEnum;
        ports: {
            number: number;
            reachable: boolean;
            finished: boolean;
            uid: string;
        }[]
    }[];
}