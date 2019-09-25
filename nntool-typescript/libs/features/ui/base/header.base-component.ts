import { Component, EventEmitter, Input, Output } from '@angular/core';

// libs
import { BaseComponent } from '@nntool-typescript/core/base';

export abstract class HeaderBaseComponent extends BaseComponent {
  /**
   * These are just samples to give you an idea of what can be done.
   * Change, remove and customize however you'd like!
   */
  @Input() public title: string;
  @Input() public rightButton: string;
  @Output() public tappedRight: EventEmitter<boolean> = new EventEmitter();
}
