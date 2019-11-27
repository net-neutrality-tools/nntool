import { UIState } from '../ui-state';
import { BarUIShowableTestTypeEnum } from './enums/bar-ui-showable-test-type.enum';

export class BarUIState extends UIState {
  public types: Array<{
    key: BarUIShowableTestTypeEnum;
    ports: Array<{
      number: number;
      reachable: boolean;
      finished: boolean;
    }>;
  }>;
}
