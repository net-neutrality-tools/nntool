import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { RouterModule } from '@angular/router';
import { AppSharedModule } from 'libs/module';
import { TestGuard } from '@nntool-typescript/test/base_test.component';

@NgModule({
  imports: [AppSharedModule, RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload', useHash: false })],
  providers: [TestGuard],
  declarations: [AppComponent],
  bootstrap: [AppComponent]
})
export class AppModule {}
