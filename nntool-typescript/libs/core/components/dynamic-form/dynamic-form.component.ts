import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, FormArray } from '@angular/forms';
import { ProviderFilterResponse } from '../../models/provider-filter-response';

@Component({
    selector: 'dynamic-form',
  templateUrl: './dynamic-form.component.html',
})
export class DynamicFormComponent implements OnInit {
    @Input()
    filterResponse: ProviderFilterResponse;

    dynamicForm: FormGroup;

    @Output()
    formChangeCallback = new EventEmitter<FormValue[]>();

    private baseForm: FormValue[];

    constructor(private formBuilder: FormBuilder) { }

    ngOnInit() {
        this.dynamicForm = this.formBuilder.group({
          filters: this.formBuilder.array([this.formBuilder.control('')])
        });

        this.baseForm = new Array<FormValue>();

        for (let filter of this.filterResponse.filters) {
          this.baseForm.push({
            key: filter.key,
            queryString: filter.query_string,
            value: undefined
          });
          let control: FormControl = this.formBuilder.control('');
          (this.dynamicForm.get('filters') as FormArray).push(control);
        }

        this.dynamicForm.valueChanges.subscribe(val => {
          this.baseForm.forEach((formVal, index) => {
            this.baseForm[index].value = val.filters[index];
          });
          this.formChangeCallback.emit(this.baseForm);
        });

        this.filterResponse.filters.forEach((filter, index) => {
          (this.dynamicForm.get("filters") as FormArray).at(index).setValue(filter.default_value);
        });
  }
}

/**
 * The class returned on formChangeCallbackCalls
 */
export class FormValues {
  public filters: any[];

}

export class FormValue {
  public key: string;
  public queryString?: string;
  public value: any;
}
