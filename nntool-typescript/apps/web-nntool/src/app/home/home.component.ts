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

import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { environment } from '@env/environment'

declare var window;

@Component({
  templateUrl: './home.component.html'
})
export class HomeComponent {
  public isElectron = typeof window !== 'undefined' && window.process && window.process.type;

  public features = environment.features;

  constructor(public translateService: TranslateService) {}
}
