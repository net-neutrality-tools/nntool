import { APP_BASE_HREF, CommonModule } from '@angular/common';
import { Inject, ModuleWithProviders, NgModule, Optional, SkipSelf } from '@angular/core';

// libs
import { TranslateService } from '@ngx-translate/core';
import { throwIfAlreadyLoaded } from '@nntool-typescript/utils';
import { NxModule } from '@nrwl/nx';

// app
import { environment } from './environments/environment';
import { CORE_PROVIDERS, PlatformLanguageToken } from './services';

export const BASE_PROVIDERS: any[] = [
  ...CORE_PROVIDERS,
  {
    provide: APP_BASE_HREF,
    useValue: '/'
  }
];

@NgModule({
  imports: [CommonModule, NxModule.forRoot()]
})
export class CoreModule {
  // configuredProviders: *required to configure WindowService and others per platform
  public static forRoot(configuredProviders: any[]): ModuleWithProviders {
    return {
      ngModule: CoreModule,
      providers: [...BASE_PROVIDERS, ...configuredProviders]
    };
  }

  constructor(
    @Optional()
    @SkipSelf()
    parentModule: CoreModule,
    @Inject(PlatformLanguageToken) lang: string,
    translate: TranslateService
  ) {
    throwIfAlreadyLoaded(parentModule, 'CoreModule');

    // ensure default platform language is set
    translate.use(lang);
  }
}
