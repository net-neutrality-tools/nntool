import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';

import { environment } from './environments/environment'; // TODO
// libs
//import { environment } from '@nntool-typescript/core';

// app
import { AppElectronModule } from './app/app.electron.module';

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic()
  .bootstrapModule(AppElectronModule)
  .catch(err => console.log(err));
