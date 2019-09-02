import { NgModule } from '@angular/core';
import { ResultTableComponent } from './result-table/result-table.component';
import { ResultComponent } from './result/result.component';
import { Ng2SmartTableModule } from '../../../../../node_modules/ng2-smart-table';
import { StandardPageComponent } from './standard-page/standard-page.component';
import { SharedModule } from '../features/shared/shared.module';

const COMPONENTS = [ResultTableComponent, ResultComponent, StandardPageComponent];

@NgModule({
  imports: [SharedModule, Ng2SmartTableModule],
  exports: [...COMPONENTS],
  declarations: [...COMPONENTS],
  entryComponents: [...COMPONENTS]
})
export class ComponentsModule {}
