import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [],
  exports: [FormsModule, ReactiveFormsModule, BrowserModule, TranslateModule, RouterModule]
})
export class SharedModule {}
