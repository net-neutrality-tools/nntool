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
import { NGXLogger } from 'ngx-logger';

export interface StorageService {
  save(key: string, value: any, clientOnly?: boolean): void;
  load(key: string): any;
  delete(key: string): void;
}

@Injectable()
export class BrowserStorageService implements StorageService {
  private cookie: string;

  constructor(private logger: NGXLogger) {}

  public save(key: string, value: any, clientOnly: boolean = true): void {
    if (typeof Storage === 'undefined') {
      // Can't use anything but cookies
      clientOnly = false;
    }
    if (clientOnly) {
      this.saveLocalStorage(key, value);
    } else {
      this.saveCookie(key, value);
    }
  }

  public load(key: string, clientOnly: boolean = true): any {
    if (clientOnly) {
      return this.loadLocalStorage(key);
    } else {
      return this.loadCookie(key);
    }
  }

  public delete(key: string): void {
    this.deleteCookie(key);
    this.deleteLocalStorage(key);
  }

  private saveCookie(key: string, value: any): void {
    this.logger.debug('save cookie', key);
    // No time -> deleted on browser close
    const cookieStr: string = key + '=' + encodeURIComponent(value) + ';path=/'; // + "; secure";
    document.cookie = cookieStr;
    this.cookie = cookieStr;
  }

  private loadCookie(key: string): any {
    for (const cookie of document.cookie.split(';')) {
      const parts: string[] = cookie.split('=');
      if (parts.length !== 2) {
        continue;
      }
      if (parts[0] && parts[0].trim() === key) {
        return decodeURIComponent(parts[1]);
      }
    }
    return null;
  }

  private deleteCookie(key: string): void {
    document.cookie = key + '=; expires=Thu, 01 Jan 1970 00:00:00 GMT';
  }

  private saveLocalStorage(key: string, value: string): void {
    this.logger.debug('save local', key);
    window.localStorage.setItem(key, value);
  }

  private loadLocalStorage(key: string): any {
    this.logger.debug('load local', key);
    return localStorage.getItem(key);
  }

  private deleteLocalStorage(key: string): void {
    window.localStorage.removeItem(key);
  }
}
