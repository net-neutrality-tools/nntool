import { Component } from '@angular/core';

@Component({
  selector: 'nntool-section-arguments',
  template: `
    <section class="section" id="section-arguments">
      <div class="wrapper">
        <div class="section-content">
          <ng-content></ng-content>
        </div>
      </div>
    </section>
  `
})
export class SectionArgumentsComponent {}
