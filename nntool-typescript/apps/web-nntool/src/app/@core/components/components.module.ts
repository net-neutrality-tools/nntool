import { NgModule } from '@angular/core';
import { ResultTableComponent } from './result-table/result-table.component';
import { GroupedResultComponent } from './grouped-result/grouped-result.component';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import { SectionHeaderComponent } from './sections/section-header/section-header.component';
import { SectionContentComponent } from './sections/section-content/section-content.component';
import { SectionIntroComponent } from './sections/section-intro/section-intro.component';
import { SectionArgumentsComponent } from './sections/section-arguments/section-arguments.component';
import { SectionTechnicalComponent } from './sections/section-technical/section-technical.component';
import { CardComponent } from './card/card.component';
import { SectionInteractiveComponent } from './sections/section-interactive/section-interactive.component';
import { SectionMapComponent } from './sections/section-map/section-map.component';
import { SectionFeaturesComponent } from './sections/section-features/section-features.component';
import { QoSResultComponent } from './qos-result/qos-result.component';
import { SharedModule } from '../../shared/shared.module';
import { MeasurementClassifiedValueComponent } from './measurement/classified.value.component';
import { MeasurementFormattedValueComponent } from './measurement/formatted.value.component';
import { FooterComponent } from './footer/footer.component';
import { ShareLinkComponent } from './share-link/share-link.component';
import { DynamicFormComponent } from './dynamic-form/dynamic-form.component';

const COMPONENTS = [
  ResultTableComponent,
  GroupedResultComponent,
  QoSResultComponent,
  SectionHeaderComponent,
  SectionContentComponent,
  SectionIntroComponent,
  SectionArgumentsComponent,
  SectionTechnicalComponent,
  SectionInteractiveComponent,
  SectionFeaturesComponent,
  SectionMapComponent,
  CardComponent,
  MeasurementClassifiedValueComponent,
  MeasurementFormattedValueComponent,
  FooterComponent,
  ShareLinkComponent,
  DynamicFormComponent
];

@NgModule({
  imports: [SharedModule, Ng2SmartTableModule],
  exports: [...COMPONENTS],
  declarations: [...COMPONENTS],
  entryComponents: [...COMPONENTS]
})
export class ComponentsModule { }
