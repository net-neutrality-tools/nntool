import { NgModule } from '@angular/core';
import { ResultTableComponent } from './result-table/result-table.component';
import { ResultComponent } from './result/result.component';
import { Ng2SmartTableModule } from '../../../../../node_modules/ng2-smart-table';

@NgModule({
  imports: [Ng2SmartTableModule],
  exports: [ResultTableComponent, ResultComponent],
  declarations: [ResultTableComponent, ResultComponent],
  entryComponents: [ResultTableComponent, ResultComponent]
})
export class ComponentsModule {}
