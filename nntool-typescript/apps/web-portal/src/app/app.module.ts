import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { RouterModule } from '@angular/router';
import { AppSharedModule } from 'libs/module';
import { PortalHomeComponent } from './portal-home/portal-home.component';

@NgModule({
  imports: [AppSharedModule, RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload', useHash: false })],
  declarations: [AppComponent, PortalHomeComponent],
  bootstrap: [AppComponent]
})
export class AppModule {}
