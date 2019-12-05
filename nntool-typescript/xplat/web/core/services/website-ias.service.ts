import { Injectable } from '@angular/core';
import { IasService } from '@nntool-typescript/ias/ias.service';

declare var Ias: any;

@Injectable()
export class WebsiteIasService implements IasService {
  private ias: any = undefined;

  constructor() {}

  public start(config: any) {
    if (this.ias !== undefined) {
      return;
    }

    this.ias = new Ias();
    this.ias.measurementStart(JSON.stringify(config));
  }

  public stop() {
    if (this.ias === undefined) {
      return;
    }

    this.ias.measurementStop();
    this.ias = undefined;
  }

  public setCallback(callback: (data: any) => void) {
    (window as any).iasCallback = callback;
  }

  public isRunning(): boolean {
    return this.ias !== undefined;
  }

  public getPlatform(): string {
    return 'web';
  }
}
