import { NntoolElectronCoreModule } from '@nntool-typescript/electron';
import { AppComponent } from './app.component';
import { AppModule } from './app.module';

import { NgModule } from '@angular/core';

@NgModule({
  imports: [AppModule, NntoolElectronCoreModule],
  declarations: [],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppElectronModule {}
