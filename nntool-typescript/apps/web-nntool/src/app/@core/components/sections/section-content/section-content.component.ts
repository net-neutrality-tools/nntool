import { Component } from '@angular/core';

@Component({
  selector: 'nntool-section-content',
  template: `
    <div class="section-content">
      <ng-content></ng-content>
    </div>
  `
})
export class SectionContentComponent {}
