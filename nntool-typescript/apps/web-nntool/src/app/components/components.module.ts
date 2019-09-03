import { NgModule } from '@angular/core';
import { ResultTableComponent } from './result-table/result-table.component';
import { ResultComponent } from './result/result.component';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { SharedModule } from '../features/shared/shared.module';
import { SectionHeaderComponent } from './sections/section-header/section-header.component';
import { SectionContentComponent } from './sections/section-content/section-content.component';
import { SectionIntroComponent } from './sections/section-intro/section-intro.component';
import { SectionArgumentsComponent } from './sections/section-arguments/section-arguments.component';
import { SectionTechnicalComponent } from './sections/section-technical/section-technical.component';
import { CardComponent } from './card/card.component';
import { SectionInteractiveComponent } from './sections/section-interactive/section-interactive.component';
import { SectionMapComponent } from './sections/section-map/section-map.component';
import { SectionFeaturesComponent } from './sections/section-features/section-features.component';

const COMPONENTS = [
  ResultTableComponent,
  ResultComponent,
  SectionHeaderComponent,
  SectionContentComponent,
  SectionIntroComponent,
  SectionArgumentsComponent,
  SectionTechnicalComponent,
  SectionInteractiveComponent,
  SectionFeaturesComponent,
  SectionMapComponent,
  CardComponent
];

@NgModule({
  imports: [SharedModule, Ng2SmartTableModule],
  exports: [...COMPONENTS],
  declarations: [...COMPONENTS],
  entryComponents: [...COMPONENTS]
})
export class ComponentsModule {}
