export class ResultGroupResponse {

    share_measurement_text: string;

    groups: ResultGroup[];
}

export class ResultGroup {

    title: string;

    description: string;

    icon_character: string;

    items: ResultGroupItem[];

}

export class ResultGroupItem {

    key?: string;

    title: string;

    unit: string;

    value: string;
}