import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { AppSharedModule } from 'libs/module';
import { TestGuard } from '@nntool-typescript/test/base_test.component';
import { CoreModule } from '@nntool-typescript/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateModule } from '@ngx-translate/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';

@NgModule({
  imports: [AppSharedModule, RouterModule],
  exports: [TranslateModule, BrowserModule, BrowserAnimationsModule, CoreModule],
  providers: [TestGuard],
  declarations: [AppComponent, HomeComponent]
})
export class AppNntoolSharedModule {}
