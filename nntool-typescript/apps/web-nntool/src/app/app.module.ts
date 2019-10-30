import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { RouterModule } from '@angular/router';
import { AppSharedModule } from './app.shared.module';

@NgModule({
  imports: [AppSharedModule, RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload', useHash: false })],
  bootstrap: [AppComponent]
})
export class AppModule {}
