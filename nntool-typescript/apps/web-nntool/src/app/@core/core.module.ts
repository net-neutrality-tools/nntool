import { NgModule } from '@angular/core';
import { ComponentsModule } from './components/components.module';
import { NntoolCoreModule } from '@nntool-typescript/web';
import { SharedModule } from '../shared/shared.module';
import { PipesModule } from './pipes/pipes.module';


const COMPONENTS = [

];

@NgModule({
  imports: [NntoolCoreModule, SharedModule, ComponentsModule, PipesModule],
  exports: [...COMPONENTS, ComponentsModule, PipesModule],
  declarations: [...COMPONENTS],
  entryComponents: [...COMPONENTS]
})
export class CoreModule { }
