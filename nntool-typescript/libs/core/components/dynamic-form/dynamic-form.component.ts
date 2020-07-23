/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import { Component, Input, OnInit, Output, EventEmitter, AfterViewInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, FormArray } from '@angular/forms';
import { ProviderFilterResponse } from '../../models/provider-filter-response';

@Component({
    selector: 'dynamic-form',
  templateUrl: './dynamic-form.component.html',
})
export class DynamicFormComponent implements OnInit, AfterViewInit {
    @Input()
    filterResponse: ProviderFilterResponse;

    //the translation prefix is prepended to the filter translation keys coming from the server
    @Input()
    translationPrefix: string;

    dynamicForm: FormGroup;

    @Output()
    formChangeCallback = new EventEmitter<FormValue[]>();

    private baseForm: FormValue[];

    constructor(private formBuilder: FormBuilder) { }

  ngAfterViewInit() {

    this.dynamicForm.valueChanges.subscribe(val => {
      this.baseForm.forEach((formVal, index) => {
        this.baseForm[index].value = formVal.valueMultiplier ? val.filters[index] * formVal.valueMultiplier : val.filters[index];
      });
      this.formChangeCallback.emit(this.baseForm);
    });
  }

    ngOnInit() {
        this.dynamicForm = this.formBuilder.group({
          filters: this.formBuilder.array([this.formBuilder.control('')])
        });

        this.baseForm = new Array<FormValue>();

        for (let filter of this.filterResponse.filters) {
          this.baseForm.push({
            key: filter.key,
            queryString: filter.query_string,
            valueMultiplier: filter.value_multiplier,
            value: undefined
          });
          let control: FormControl = this.formBuilder.control('');
          (this.dynamicForm.get('filters') as FormArray).push(control);
        }

        this.filterResponse.filters.forEach((filter, index) => {
          (this.dynamicForm.get("filters") as FormArray).at(index).setValue(filter.default_value);
        });
  }
}

/**
 * The class returned on formChangeCallbackCalls
 */
export class FormValue {
  public key: string;
  public queryString?: string;
  public valueMultiplier?: number;
  public value: any;
}
