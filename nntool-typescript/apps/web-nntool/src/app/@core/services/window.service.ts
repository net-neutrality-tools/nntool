import { isPlatformBrowser } from '@angular/common';
import { ClassProvider, FactoryProvider, InjectionToken, PLATFORM_ID } from '@angular/core';

export const WINDOW = new InjectionToken('WindowToken');

export abstract class WindowRef {
  abstract get nativeWindow(): Window;
}

export class BrowserWindowRef extends WindowRef {
  constructor() {
    super();
  }

  get nativeWindow(): Window {
    return window;
  }
}

export function windowFactory(browserWindowRef: BrowserWindowRef, platformId: object): Window | {} {
  if (isPlatformBrowser(platformId)) {
    return browserWindowRef.nativeWindow;
  }
  return {};
}

export const WINDOW_REF_PROVIDER: ClassProvider = {
  provide: WindowRef,
  useClass: BrowserWindowRef
};

export const WINDOW_PROVIDER: FactoryProvider = {
  provide: WINDOW,
  useFactory: windowFactory,
  deps: [WindowRef, PLATFORM_ID]
};
