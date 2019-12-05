import { Injectable } from '@angular/core';

@Injectable()
export abstract class IasService {
  public abstract start(config: any);
  public abstract stop();

  public abstract setCallback(callback: (data: any) => void);

  public abstract isRunning(): boolean;
  public abstract getPlatform(): string;
}
