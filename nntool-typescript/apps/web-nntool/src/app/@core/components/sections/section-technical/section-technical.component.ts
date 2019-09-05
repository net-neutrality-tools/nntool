import { Component } from '@angular/core';

@Component({
  selector: 'nntool-section-technical',
  template: `
    <section class="section" id="section-technical">
      <div class="wrapper">
        <ng-content></ng-content>
      </div>
    </section>
  `
})
export class SectionTechnicalComponent {}
