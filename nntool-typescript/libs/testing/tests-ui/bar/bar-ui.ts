/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import { AfterViewInit, Component, Inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Test } from '../../test.component';
import { TestConfig } from '../../tests-implementation/test-config';
import { TestImplementation } from '../../tests-implementation/test-implementation';
import { TestState } from '../../tests-implementation/test-state';
import { BarUIState } from './bar-ui-state';
import { BarUIShowableTestTypeEnum } from './enums/bar-ui-showable-test-type.enum';
import { ConfigService } from '../../../core/services/config.service';
import { WINDOW } from '../../../core/services/window.service';
import { BarTestResult } from './bar-test-result';

class Point {
  constructor(public x: number, public y: number) {}
}

/*@Component({
  templateUrl: './bar-ui.template.html'
})*/
export abstract class BarUIComponent<T extends TestImplementation<TC, TS>, TC extends TestConfig, TS extends TestState>
  extends Test<BarUIState, T, TC, TS>
  implements AfterViewInit {
  public active: boolean;
  private resolutionScaleFactor = 2;
  private canvas: HTMLCanvasElement;
  private canvasContext: CanvasRenderingContext2D;
  private currentState: BarUIState;
  private barWidth: number;
  private barLength: number;
  private drawing = false;

  private translations: { [key: string]: any };
  private barColors: { [key: string]: string };
  private font: string;

  protected constructor(
    testImplementation: T,
    configService: ConfigService,
    protected translateService: TranslateService,
    @Inject(WINDOW) private window: Window
  ) {
    super(testImplementation);

    if (this.window && this.window.devicePixelRatio) {
      this.resolutionScaleFactor = this.window.devicePixelRatio;
    }

    this.state.subscribe(this.handleState);
    this.configureBarUI(configService.getConfig());
  }

  public ngAfterViewInit() {
    this.canvas = document.getElementById('nettest-bar') as HTMLCanvasElement;
    this.canvasContext = this.canvas.getContext('2d');
    this.addResize();
    this.setCanvas();
  }

  public setActive = (active: boolean) => {
    this.active = active;
    if (this.active && this.canvas && this.canvasContext) {
      setTimeout(() => {
        this.resizeEvent();
      }, 0);
    }
  };

  public onOnlyReInit() {}

  private configureBarUI(config: any) {
    this.translations = {
      UDP_TURN: 'UDP_TURN',
      TRACEROUTE: 'TRACEROUTE'
    };

    this.barColors = {
      baseColor: '#EEEEEE',
      valueColor: '#878787',
      progressColor: '#911232',
      fontColor: '#FFFFFF'
    };

    this.font = null;
    if (config.colors.gauge) {
      const gc: { [key: string]: string } = config.colors.gauge;
      this.barColors = {
        baseColor: gc.arc_background,
        valueColor: gc.arc_inner,
        progressColor: gc.arc_outer,
        fontColor: gc.font
      };

      if (gc.fontName) {
        this.font = gc.fontName;
      }
    }
  }

  private handleState = (state: BarUIState) => {
    this.currentState = state;
    if (this.canvas && this.canvasContext) {
      this.draw(this.currentState);
    }
  };

  private draw = (state: BarUIState) => {
    if (this.drawing) {
      // console.debug("Already drawing..");
      setTimeout(() => {
        this.draw(state);
      }, 100);
      return;
    }
    this.drawing = true;
    this.clear();
    if (state && state.types) {
      state.types.forEach(
        (type: BarTestResult) => {
          if (type.progress) {
            this.drawBar(1, this.barColors.baseColor);
            this.drawBar(type.progress, this.barColors.valueColor);
            this.drawBarName(this.translations[type.key], this.barColors.fontColor);
          }
          else if (type.key === BarUIShowableTestTypeEnum.UDP_TURN) {
            this.drawBar(1, this.barColors.baseColor);
            this.drawBar(this.calculateProgressOfTestType(type), this.barColors.valueColor);
            this.drawBarName(this.translations[type.key], this.barColors.fontColor);
          }
        }
      );
    }

    this.drawing = false;
  };

  private drawBar = (progress: number, color: string): void => {
    this.canvasContext.lineWidth = this.barWidth;
    this.canvasContext.strokeStyle = color;
    this.canvasContext.lineCap = 'round';

    const center = this.getCenter(this.canvas);

    this.canvasContext.beginPath();
    this.canvasContext.moveTo(center.x - this.barLength / 2, center.y - this.barLength / 3);
    this.canvasContext.lineTo(center.x - this.barLength / 2 + this.barLength * progress, center.y - this.barLength / 3);
    this.canvasContext.stroke();
  };

  private drawBarName = (name: string, color: string): void => {
    const textHeight: number = this.barWidth * 0.6;

    this.canvasContext.font = '100 ' + textHeight + 'px ' + this.font;
    this.canvasContext.fillStyle = color;

    const center = this.getCenter(this.canvas);

    this.canvasContext.fillText(name, center.x - this.barLength / 2, center.y - this.barLength / 3 + textHeight / 2.3);
  };

  private addResize(): void {
    if (this.canvas.addEventListener) {
      // > IE8
      window.addEventListener('resize', () => {
        this.resizeEvent();
      });
    } else {
      (window as any).attachEvent('onresize', () => {
        this.resizeEvent();
      });
    }
  }

  private resizeEvent() {
    if (this.drawing) {
      return;
    }
    this.clear();
    this.setCanvas();
    this.draw(this.currentState);
  }

  private clear() {
    this.canvasContext.clearRect(0, 0, this.canvas.width, this.canvas.height);
  }

  private setCanvas(minVal: number = 400) {
    this.canvas.width = Math.max(this.canvas.clientWidth, minVal) * this.resolutionScaleFactor;
    this.canvas.height = Math.max(this.canvas.clientHeight, minVal) * this.resolutionScaleFactor;

    this.barWidth = this.canvas.width / 13;
    this.barLength = this.canvas.width * 0.9;
  }

  private getCenter(canvas: HTMLCanvasElement): Point {
    return new Point(canvas.width / 2, canvas.height / 2);
  }

  private calculateProgressOfTestType = (type: BarTestResult): number => {
    let numberOfPortsToTest = 0;
    let numberOfTestedPorts = 0;
    type.ports.forEach((port: {
      number: number;
      packets: {
        requested_packets: number,
        lost: number;
        received: number;
        sent: number
      };
    }) => {

        numberOfTestedPorts += port.packets.sent + port.packets.received;
        numberOfPortsToTest += port.packets.requested_packets;
    });

    return Math.min(1, numberOfTestedPorts / numberOfPortsToTest);
  };
}
