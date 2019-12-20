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

import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'nntool-result-table',
  templateUrl: './result-table.component.html',
  styleUrls: ['./result-table.component.less']
})
export class ResultTableComponent {
  @Input() source: any;
  @Input() settings: Object = {};

  @Output() rowSelect = new EventEmitter<any>();

  tableSource: any;
  tableSettings: Object = {};

  private readonly defaultSettings = {
    // TODO: get from environment
    hideSubHeader: true,
    //noDataMessage: , // TODO: translate
    /*attr: {
      //class: 'no-stack'
    },*/
    pager: {
      display: true,
      perPage: 25
    },
    mode: 'external',
    actions: {
      add: false,
      edit: false,
      delete: false
    }
  };

  constructor(protected logger: NGXLogger) { }

  public ngOnInit() {
    this.tableSource = this.source;
    this.tableSettings = Object.assign({}, this.defaultSettings, this.settings);
    //this.logger.debug(this.tableSettings);
  }

  onRowSelected(event: any) {
    let item = event.data;
    this.rowSelect.emit(item);
  }
}
