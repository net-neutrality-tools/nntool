export enum GroupTypes {}

export interface GroupedValue {
  key?: string;
  type?: string;
}

export interface FormattedValue extends GroupedValue {
  title: string;
  value: any;
  unit?: string;
  icon?: string;
}

export interface ClassifiedValue extends FormattedValue {
  classification: number;
  color?: string;
}

export interface Grouped {
  title: string;
  icon?: string;
  key?: string;
  type: string;
}

export interface SpeedGraphGroup extends Grouped {
  values: Array<{
    title: string;
    curveType: 'upload' | 'download' | 'signal';
    values: Array<{
      x: number;
      y: number;
    }>;
  }>;
}

export interface ListGrouped extends Grouped {
  values?: Array<FormattedValue | ClassifiedValue | SpeedGraphGroup>;
}

export interface QosGroup extends Grouped {
  description: string;
  tests: any[];
  successes: number;
  failures: number;
  shown: boolean;
}

interface Measurement {
  uuid?: string;
  openUuid?: string;
  time?: number;
  location?: {
    latitude: number;
    longitude: number;
    value?: string;
  };
  locationPath?: Array<{
    latitude: number;
    longitude: number;
    accuracy?: number;
    timeElapsed?: number;
  }>;

  overview?: ListGrouped;
  more?: ListGrouped[];
  details?: Array<ListGrouped | SpeedGraphGroup>;
  qos?: QosGroup[];
  hasQos?: boolean;
}

export default Measurement;
