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

import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { NGXLogger } from 'ngx-logger';

import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { ADocService } from './adoc.service';
import { ConfigService } from '../../core/services/config.service';

@Component({
  templateUrl: './adoc.component.html'
})
export class ADocComponent implements AfterViewInit, OnDestroy, OnInit {
  public key = '';
  public loading = false;

  protected injectedVars: { [key: string]: string };
  private subs: Subscription[] = [];

  constructor(
    private logger: NGXLogger,
    private elementRef: ElementRef,
    private renderer: Renderer2,
    private translateService: TranslateService,
    private adoc: ADocService,
    protected configService: ConfigService
  ) {}

  public ngOnInit() {
    this.loading = true;
    this.adoc
      .setPage(this.key)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe(
        () => {
          this.inject(this.injectedVars);
        },
        (err: any) => {
          this.logger.error('Adoc setPage', err);
        }
      );
  }

  public ngAfterViewInit() {
    this.subs.push(
      this.translateService.onLangChange.subscribe((event: LangChangeEvent) => {
        if (!this.loading || this.translateService.currentLang !== event.lang) {
          this.loading = true;
          this.adoc
            .setPage(this.key, event.lang)
            .pipe(
              finalize(() => {
                this.loading = false;
              })
            )
            .subscribe(
              () => {
                this.inject(this.injectedVars);
              },
              (err: any) => {
                this.logger.error('Adoc setPage', err);
              }
            );
        }
      })
    );
  }

  public ngOnDestroy(): void {
    while (this.subs.length > 0) {
      try {
        this.subs.pop().unsubscribe();
      } catch (e) {
        this.logger.error('Failed to unsubscribe', e);
      }
    }
  }

  private inject(vars: { [key: string]: string }): void {
    setTimeout(() => {
      for (const key in vars) {
        if (!vars.hasOwnProperty(key)) {
          continue;
        }
        this.logger.debug(key, vars[key]);
        while (true) {
          let el: any;
          try {
            el = this.renderer.selectRootElement('.' + key);
            this.renderer.appendChild(el, this.renderer.createText(vars[key]));
            this.renderer.removeClass(el, key);
            this.renderer.addClass(el, 'injected-' + key);
          } catch (e) {
            // Not found
            break;
          }
        }
      }
    }, 0);
  }
}
