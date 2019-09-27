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
    transition('true <=> false', animate('500ms ease-in-out'))
  ])
];
