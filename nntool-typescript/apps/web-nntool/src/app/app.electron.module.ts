import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { RouterModule } from '@angular/router';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { NntoolElectronCoreModule } from '@nntool-typescript/electron';
import { ElectronIasService } from '@nntool-typescript/electron/core/services';
import { IasService } from '@nntool-typescript/ias/ias.service';
import { AppNntoolSharedModule } from './app.nntool.shared.module';

@NgModule({
  imports: [
    AppNntoolSharedModule,
    NntoolElectronCoreModule,
    RouterModule.forRoot(routes, { /*onSameUrlNavigation: 'reload',*/ useHash: true })
  ],
  providers: [
    { provide: LocationStrategy, useClass: HashLocationStrategy },
    { provide: IasService, useClass: ElectronIasService }
  ],
  bootstrap: [AppComponent]
})
export class AppElectronModule {}
