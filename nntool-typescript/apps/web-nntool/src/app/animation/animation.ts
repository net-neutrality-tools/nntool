import { trigger, state, style, transition, animate } from '@angular/animations';

export const SlideAnimation = [
    //animation help from https://stackoverflow.com/questions/43497431/angular-2-4-animation-on-div-height
    //link to variable (that is either shown or hidden) by using [@slide]="your.variable.here" in your html
    trigger('slide', [
        state('shown', style({
            overflow: 'hidden',
            height: '*',
            width: '*'
        })),
        state('hidden', style({
            opacity: '0',
            overflow: 'hidden',
            height: '0px',
            width: '0px'
        })),
        transition('shown => hidden', animate('500ms ease-in-out')),
        transition('hidden => shown', animate('500ms ease-in-out'))
        ])
]