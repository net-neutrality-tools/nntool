export class ResultGroupResponse {
  public share_measurement_text: string;

  public groups: ResultGroup[];
}

export class ResultGroup {
  public title: string;

  public description: string;

  public icon_character: string;

  public items: ResultGroupItem[];
}

export class ResultGroupItem {
  public key?: string;

  public title: string;

  public unit: string;

  public value: string;
}
