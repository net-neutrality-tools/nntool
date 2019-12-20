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

import { animate, state, style, transition, trigger } from '@angular/animations';

export const SlideAnimation = [
  // animation help from https://stackoverflow.com/questions/43497431/angular-2-4-animation-on-div-height
  // link to variable (that is either shown or hidden) by using [@slide]="your.variable.here" in your html
  trigger('slide', [
    state(
      'true',
      style({
        overflow: 'hidden',
        height: '*',
        width: '*',
        'margin-top': '*',
        'margin-bottom': '*',
        'padding-top': '*',
        'padding-bottom': '*'
      })
    ),
    state(
      'false',
      style({
        opacity: '0',
        overflow: 'hidden',
        height: '0px',
        width: '0px',
        'margin-top': '0',
        'margin-bottom': '0',
        'padding-top': '0',
        'padding-bottom': '0'
      })
    ),
    transition('true <=> false', animate('137ms ease-in-out'))
  ])
];
