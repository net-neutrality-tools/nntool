

export interface ResultSettings {
    keys: {
        header: string;
        icon: string;
        values : {[key: string]: any}[];
    }[];
    view: any;
    graph: {
        step_ms: number;
        reposition?: boolean;
    };
    detailScroll?: boolean;
}