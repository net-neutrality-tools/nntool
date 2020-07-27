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

export const isString = (arg: any) => {
  return typeof arg === 'string';
};

export const isObject = (arg: any) => {
  return arg && typeof arg === 'object';
};

declare global {
  interface Date {
    toLocalISOString: () => string;    
  }
};

Date.prototype.toLocalISOString = function() {
  return this.getFullYear() + '-'
    + ("" + (this.getMonth() + 1)).padStart(2, '0') + '-'
    + ("" + (this.getDate())).padStart(2, '0') + 'T'
    + ("" + (this.getHours())).padStart(2, '0') + ':'
    + ("" + (this.getMinutes())).padStart(2, '0') + ':'
    + ("" + (this.getSeconds())).padStart(2, '0') + '.'
    + ("" + (this.getMilliseconds())).padStart(3, '0') + 'Z';
};