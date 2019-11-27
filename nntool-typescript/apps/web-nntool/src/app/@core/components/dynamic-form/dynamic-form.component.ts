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
    formChangeCallback = new EventEmitter<FormValues>();

    constructor(private formBuilder: FormBuilder) { }

    ngOnInit() {
        this.dynamicForm = this.formBuilder.group({
          filters: this.formBuilder.array([this.formBuilder.control('')])
        });

        for (let filter of this.filterResponse.filters) {
          let control: FormControl = this.formBuilder.control('');
          (this.dynamicForm.get('filters') as FormArray).push(control);
        }

        this.dynamicForm.valueChanges.subscribe(val => {
          this.formChangeCallback.emit(val);
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
