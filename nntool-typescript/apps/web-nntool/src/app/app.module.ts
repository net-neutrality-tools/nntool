import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { RouterModule } from '@angular/router';
import { NntoolWebsiteCoreModule } from '@nntool-typescript/web';
import { WebsiteIasService } from '@nntool-typescript/web/core/services';
import { IasService } from '@nntool-typescript/ias/ias.service';
import { AppNntoolSharedModule } from './app.nntool.shared.module';

@NgModule({
  imports: [
    AppNntoolSharedModule,
    NntoolWebsiteCoreModule,
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload', useHash: false })
  ],
  providers: [{ provide: IasService, useClass: WebsiteIasService }],
  bootstrap: [AppComponent]
})
export class AppModule {}
