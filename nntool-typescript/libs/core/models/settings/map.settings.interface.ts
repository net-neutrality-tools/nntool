import { SelectOption } from '../../filters/select_option.interface';

export interface MapSelectMapOptions extends SelectOption {
  legend: {
    colors: string;
    captions: string[];
  };

  thresholds?: {
    [key: string]: {
      colors: string;
      captions: string[];
    };
  };
}

export interface MapOptions {
  statistical_method?: SelectOption[];
  map_options: MapSelectMapOptions[];
  period?: SelectOption[];
  technology?: SelectOption[];
  operator?: SelectOption[];
  layer_type: SelectOption[];
}

export interface MapSettings {
  view: any;
  showLegend?: boolean;
  options: MapOptions;
  filter_defaults?: {
    statistical_method?: any;
    map_options?: any;
    period?: any;
    technology?: any;
    operator?: any;
  };
}
