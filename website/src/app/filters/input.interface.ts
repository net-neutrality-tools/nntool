import {Filter} from "./filter.interface";

export interface Input extends Filter {
    placeholder ?: string;
    default ?: string;
}