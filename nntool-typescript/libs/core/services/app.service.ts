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

import { HostListener, Injectable } from '@angular/core';

@Injectable()
export class AppService {
  get trackScroll(): boolean {
    return this._trackScroll;
  }

  set trackScroll(value: boolean) {
    this._trackScroll = value as boolean;
    this.updateScroll();
  }

  get showScroll(): boolean {
    // this.logger.debug("showing scroll", this._showScrollElement, this.trackScroll);
    return this.trackScroll && this._showScrollElement;
  }

  set showScroll(value: boolean) {
    this._showScrollElement = value;
  }

  private _trackScroll: boolean;
  private _showScrollElement: boolean;
  private _disabledNavigation = false;
  // TODO: translate
  private _dialogText = 'Do you want to interrupt this measurment?';

  constructor() {
    this.trackScroll = false;
    this.showScroll = false;
  }

  public disableNavigation(): void {
    // window.onbeforeunload = this.preventNavigation;
    // window.addEventListener('beforeunload', this.preventNavigation, false);
    // window.addEventListener('unload', this.preventNavigation, false);
  }

  public enableNavigation(): void {
    // window.removeEventListener('beforeunload', this.preventNavigation);
    // window.removeEventListener('unload', this.preventNavigation);
  }

  public updateScroll(event?: any): void {
    if (!this._trackScroll) {
      // Not tracking scrolling
      return;
    }
    const pos: number =
      (document.documentElement.scrollTop || document.body.scrollTop) + document.documentElement.offsetHeight;
    const maxPos: number = document.documentElement.scrollHeight;
    // TODO: jquery
    /*const footerHeight: number = $('.section-footer').outerHeight() || 0;
        this.showScroll = (maxPos - footerHeight) * 0.95 >= pos;*/
  }

  // Based on https://stackoverflow.com/a/45451047
  private preventNavigation(e?: any): string {
    if (!this._disabledNavigation) {
      return null;
    }
    if (e) {
      e.returnValue = this._dialogText;
      return this._dialogText;
    }
    const location = window.document.location;
    const originalHashValue = location.hash;

    setTimeout(() => {
      location.hash = 'prevented' + 9999 * Math.random();
      location.hash = originalHashValue;
    }, 0);
  }
}
