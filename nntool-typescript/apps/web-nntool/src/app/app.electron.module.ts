import { NntoolElectronCoreModule } from '@nntool-typescript/electron';
import { AppModule } from './app.module';
import {AppComponent} from './app.component';

import {NgModule} from '@angular/core';

@NgModule({
    imports: [
        AppModule, NntoolElectronCoreModule
    ],
    declarations: [

    ],
    providers: [

    ],
    bootstrap: [AppComponent]
})
export class AppElectronModule {
}
