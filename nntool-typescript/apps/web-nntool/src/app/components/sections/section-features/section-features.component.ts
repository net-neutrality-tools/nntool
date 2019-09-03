import { Component } from '@angular/core';

@Component({
  selector: 'nntool-section-features',
  template: `
    <section class="section" id="section-features">
      <div class="wrapper">
        <ng-content></ng-content>
      </div>
    </section>
  `
})
export class SectionFeaturesComponent {}
