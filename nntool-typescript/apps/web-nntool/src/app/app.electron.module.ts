import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { RouterModule } from '@angular/router';
import { AppSharedModule } from 'libs/module';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { NntoolElectronCoreModule } from '@nntool-typescript/electron';
import { AppModule } from './app.module';

@NgModule({
  imports: [
    AppModule,
    NntoolElectronCoreModule,
    RouterModule.forRoot(routes, { /*onSameUrlNavigation: 'reload',*/ useHash: true })
  ],
  providers: [{ provide: LocationStrategy, useClass: HashLocationStrategy }],
  bootstrap: [AppComponent]
})
export class AppElectronModule {}
