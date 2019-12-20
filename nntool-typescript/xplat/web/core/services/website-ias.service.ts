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

import { Injectable } from '@angular/core';
import { IasService } from '@nntool-typescript/ias/ias.service';

declare var Ias: any;

@Injectable()
export class WebsiteIasService implements IasService {
  private ias: any = undefined;

  constructor() {}

  public start(config: any) {
    if (this.ias !== undefined) {
      return;
    }

    this.ias = new Ias();
    this.ias.measurementStart(JSON.stringify(config));
  }

  public stop() {
    if (this.ias === undefined) {
      return;
    }

    this.ias.measurementStop();
    this.ias = undefined;
  }

  public setCallback(callback: (data: any) => void) {
    (window as any).iasCallback = callback;
  }

  public isRunning(): boolean {
    return this.ias !== undefined;
  }

  public getPlatform(): string {
    return 'web';
  }
}
