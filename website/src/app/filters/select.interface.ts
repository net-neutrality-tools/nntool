import {Filter} from "./filter.interface";
import {SelectOption} from "../filters/select_option.interface";

export interface Select extends Filter {
    type: "select";
    options: SelectOption[];
}