import { Component } from '@angular/core';

@Component({
  selector: 'nntool-section-intro',
  template: `
    <section class="section" id="section-intro">
      <div class="wrapper">
        <ng-content></ng-content>
      </div>
    </section>
  `
})
export class SectionIntroComponent {}
