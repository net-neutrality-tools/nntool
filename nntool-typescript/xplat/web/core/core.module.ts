import { NgModule, Optional, SkipSelf } from '@angular/core';

import { throwIfAlreadyLoaded } from '@nntool-typescript/utils';
import { WEBSITE_PROVIDERS, WebsiteIasService } from './services';

@NgModule({
  providers: [...WEBSITE_PROVIDERS]
})
export class NntoolWebsiteCoreModule {
  constructor(
    @Optional()
    @SkipSelf()
    parentModule: NntoolWebsiteCoreModule,
    private _iasService: WebsiteIasService
  ) {
    throwIfAlreadyLoaded(parentModule, 'NntoolWebsiteCoreModule');
  }
}
