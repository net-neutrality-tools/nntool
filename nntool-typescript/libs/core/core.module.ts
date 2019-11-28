import { NgModule } from '@angular/core';
import { ComponentsModule } from './components/components.module';
import { SharedModule } from '../shared/shared.module';
import { PipesModule } from './pipes/pipes.module';
import { ServicesModule } from './services/services.module';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';

const COMPONENTS = [];

@NgModule({
  imports: [SharedModule, ComponentsModule, PipesModule, ServicesModule, LeafletModule],
  exports: [...COMPONENTS, ComponentsModule, PipesModule, ServicesModule],
  declarations: [...COMPONENTS],
  entryComponents: [...COMPONENTS]
})
export class CoreModule {}
