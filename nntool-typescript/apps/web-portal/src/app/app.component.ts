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

import { Location } from '@angular/common';
import { Component, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { NGXLogger } from 'ngx-logger';

import { LangChangeEvent, TranslateService, TranslationChangeEvent } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';

import { FeatureSettings } from 'libs/core/models/settings/features.settings.interface';
import { WebsiteSettings } from 'libs/core/models/settings/settings.interface';
import { Guard } from 'libs/core/services/guard.service';
import { ConfigService } from 'libs/core/services/config.service';
import { BrowserStorageService } from 'libs/core/services/storage.service';
import { AppService } from 'libs/core/services/app.service';
import { UserService, UserInfo } from 'libs/core/services/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit, OnDestroy {
  get showScrollInfo(): boolean {
    return this.appService.showScroll;
  }
  public features: FeatureSettings;
  public config: WebsiteSettings;
  public guard: Guard;

  private subs: Subscription[] = [];
  private localUrlRegex: any = /\/\w+#\w+.*/;

  constructor(
    private logger: NGXLogger,
    private titleService: Title,
    private router: Router,
    private location: Location,
    public translateService: TranslateService,
    private configService: ConfigService,
    private guardService: Guard,
    private storage: BrowserStorageService,
    private renderer: Renderer2,
    public appService: AppService,
    private activatedRoute: ActivatedRoute,
    private userService: UserService
  ) {
    this.guard = guardService;
  }

  public ngOnInit() {
    this.logger.debug('Application component initialized ...');
    this.logger.info(
      'Running website version',
      this.configService.getVersion(),
      ' for ',
      this.configService.getClient(),
      '(',
      this.configService.getBranch(),
      this.configService.getRevision(),
      ')'
    );
    this.config = this.configService.getConfig();
    this.features = this.config.features;
    const langs: string[] = this.config.languages;
    const reg = new RegExp(langs.join('|'));
    const browserLang = this.translateService.getBrowserLang();
    const storedLang = this.storage.load('user_lang', false);
    // TODO: this.renderer.addClass(document.body, this.configService.getClient());

    this.translateService.addLangs(langs);
    this.translateService.setDefaultLang(langs[0]);
    if (storedLang && langs.includes(storedLang)) {
      // Got saved lang and lang is available -> use it
      this.translateService.use(storedLang);
    } else {
      // Use lang based on browser pref
      this.translateService.use(browserLang.match(reg) ? browserLang : langs[0]);
    }

    this.subs.push(
      this.translateService.onLangChange.subscribe((event: LangChangeEvent) => {
        this.updateTitle();
        // Save language for session
        this.storage.save('user_lang', event.lang, false);
      })
    );
    this.subs.push(
      this.translateService.onTranslationChange.subscribe((event: TranslationChangeEvent) => {
        this.updateTitle();
      })
    );
    this.subs.push(
      this.activatedRoute.queryParamMap.subscribe((data: any) => {
        this.logger.debug('Main Query param', data);
        if (data.params) {
          const queryLang: string = data.params.lang;
          const setUuid: string = data.params.set_uuid;
          if (queryLang) {
            if (this.translateService.currentLang !== queryLang && langs.includes(queryLang)) {
              setTimeout(() => {
                this.translateService.use(queryLang);
                this.storage.save('user_lang', queryLang, false);
              }, 0);
            }
          }
          if (this.config.user && this.config.user.allow_set_uuid && setUuid) {
            const user: UserInfo = this.userService.user;
            if (user.uuid !== setUuid) {
              this.logger.debug('Setting client uuid', setUuid);
              // New user - settings
              // user.apply(new UserInfo());
              // TODO: clean/validate
              user.uuid = setUuid;
              user.measurementUUIDs = [];
              this.userService.save();
            }
          }
        }
      })
    );
    this.subs.push(
      this.router.events
        .pipe(filter((event: any) => event instanceof NavigationEnd))
        .subscribe((event: NavigationEnd) => {
          const uri: string = event.url;
          if (this.localUrlRegex.test(uri)) {
            const header: any = document.getElementsByClassName('site-header');
            if (header && header.length > 0) {
              const height: number = 0 - header[0].scrollHeight;
              setTimeout(() => {
                window.scrollBy(0, height);
              }, 0);
            }
          } else {
            window.scrollTo(0, 0);
          }
          const url_location: string = this.location
            .normalize(uri.split('#')[0].split('?')[0])
            .slice(1)
            .split('/')[0];
          if (url_location) {
            let i = 0;
            const rms: string[] = [];
            while (i < document.body.classList.length) {
              const temp: string = document.body.classList.item(i);
              if (temp && temp.startsWith('page-')) {
                rms.push(temp);
              }
              i++;
            }
            for (const r of rms) {
              this.renderer.removeClass(document.body, r);
            }
            this.renderer.addClass(document.body, 'page-' + url_location);
          }
        })
    );
  }

  public ngOnDestroy() {
    while (this.subs.length > 0) {
      this.subs.pop().unsubscribe();
    }
  }

  public menuOpen() {
    // TODO: don't call window.document.body directly
    if (window.document.body.classList.contains('menu-open')) {
      this.menuClose();
    } else {
      this.renderer.addClass(document.body, 'menu-open');
    }
  }

  public menuClose() {
    this.renderer.removeClass(document.body, 'menu-open');
  }

  private updateTitle(): void {
    this.subs.push(
      this.translateService.get('WEBSITE.TITLE').subscribe(res => {
        this.titleService.setTitle(res);
      })
    );
  }
}
