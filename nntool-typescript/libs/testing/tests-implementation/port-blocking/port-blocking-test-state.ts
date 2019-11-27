import { TestState } from '../test-state';
import { PortBlockingTestTypeEnum } from './enums/port-blocking-test-type';

export class PortBlockingTestState extends TestState {
  public types: Array<{
    key: PortBlockingTestTypeEnum;
    ports: Array<{
      number: number;
      reachable: boolean;
      finished: boolean;
      uid: string;
    }>;
  }>;
}
