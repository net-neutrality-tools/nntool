import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {enableProdMode} from '@angular/core';
import {AppModule} from './app.module';
import {environment} from "./settings/env";

if (environment === <string>"prod") {
    enableProdMode();
}


const platform = platformBrowserDynamic();


platform.bootstrapModule(AppModule);