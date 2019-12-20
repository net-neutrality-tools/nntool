/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
