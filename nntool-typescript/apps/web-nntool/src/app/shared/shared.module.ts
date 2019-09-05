import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { UIModule } from '@nntool-typescript/web';

@NgModule({
  imports: [UIModule],
  exports: [UIModule, TranslateModule]
})
export class SharedModule { }
