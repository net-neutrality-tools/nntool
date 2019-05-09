import {Filter} from "./filter.interface";
import {Input} from "./input.interface";

export interface Range extends Filter {
    from: Input;
    to: Input;
}