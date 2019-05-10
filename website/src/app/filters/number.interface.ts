import {Input} from "./input.interface";

export interface Number extends Input {
    type: "number";
    factor?: number;
}