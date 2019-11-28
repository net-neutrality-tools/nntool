import { Component } from '@angular/core';

@Component({
  selector: 'nntool-section-arguments',
  template: `
    <section class="section" id="section-arguments">
      <div class="wrapper">
        <ng-content></ng-content>
      </div>
    </section>
  `
})
export class SectionArgumentsComponent {}
