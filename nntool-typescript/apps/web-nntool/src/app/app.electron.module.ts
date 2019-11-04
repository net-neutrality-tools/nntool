import { NntoolElectronCoreModule } from '@nntool-typescript/electron';
import { AppComponent } from './app.component';
import { NgModule } from '@angular/core';
import { routes } from './app.routes';
import { RouterModule } from '@angular/router';
import { AppSharedModule } from './app.shared.module';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';

@NgModule({
  imports: [
    AppSharedModule,
    NntoolElectronCoreModule,
    RouterModule.forRoot(routes, { /*onSameUrlNavigation: 'reload',*/ useHash: true })
  ],
  providers: [{ provide: LocationStrategy, useClass: HashLocationStrategy }],
  exports: [RouterModule],
  bootstrap: [AppComponent]
})
export class AppElectronModule {}
