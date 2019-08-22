export interface ResultSettings {
  keys: Array<{
    header: string;
    icon: string;
    values: Array<{ [key: string]: any }>;
  }>;
  view: any;
  graph: {
    step_ms: number;
    reposition?: boolean;
  };
  detailScroll?: boolean;
}
