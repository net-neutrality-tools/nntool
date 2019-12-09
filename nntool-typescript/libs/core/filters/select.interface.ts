import { SelectOption } from '../filters/select_option.interface';
import { Filter } from './filter.interface';

export interface Select extends Filter {
  type: 'select';
  options: SelectOption[];
}
