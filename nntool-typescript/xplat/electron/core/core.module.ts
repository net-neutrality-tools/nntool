import { NgModule, Optional, SkipSelf } from '@angular/core';

import { throwIfAlreadyLoaded } from '@nntool-typescript/utils';
import { ELECTRON_PROVIDERS, ElectronService } from './services';

@NgModule({
  providers: [...ELECTRON_PROVIDERS]
})
export class NntoolElectronCoreModule {
  constructor(
    @Optional()
    @SkipSelf()
    parentModule: NntoolElectronCoreModule,
    private _electronService: ElectronService
  ) {
    throwIfAlreadyLoaded(parentModule, 'NntoolElectronCoreModule');
  }
}
