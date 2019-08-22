import { NGXLogger } from 'ngx-logger';
import { GaugeUIStateEnum } from '../enums/gauge-ui-state.enum';
import { GaugeUIState } from '../gauge-ui-state';

export class Point {
  constructor(public x: number, public y: number) {}
}

export enum ProgressType {
  SPEED,
  QOS
}

export enum StateView {
  ERROR = 'error',
  READY = 'ready',
  INIT = 'init',
  PING = 'ping',
  UPLOAD = 'upload',
  DOWNLOAD = 'download',
  QOS = 'qos',
  COMPLETE = 'complete'
}

/**
 * Lg of max speed in Mbit/s
 */
export const MAX_SPEED_LOG: number = Math.log10(1e3);
// GAUGE_PARTS: number = 4.25;
export const GAUGE_PARTS: number = 2 * (MAX_SPEED_LOG - 1);

export abstract class BaseMeasurementGauge {
  public get value(): number {
    return this._value;
  }

  public set value(value: number) {
    if (this._value !== value) {
      this._value = value;
      this.dirty = true;
    }
  }

  public get progress() {
    return this._progress;
  }

  public set progress(progress: number) {
    if (this._progress !== progress) {
      this._progress = progress;
      this.dirty = true;
    }
  }

  public set progressType(progressType: ProgressType) {
    this._progressType = progressType;
    this._progress = 0;
    this.dirty = true;
  }
  public currentState: GaugeUIState = null;

  protected drawing = false;

  protected _value = 0;
  protected _progress = 0;
  protected _progressType: ProgressType = ProgressType.SPEED;
  protected stateContent = '';
  protected valueContent = '';
  protected positionContent = '';
  protected providerContent = '';
  protected deviceContent = '';
  protected technologyContent = '';
  protected serverContent = '';
  protected pingContent = '';
  protected upContent = '';
  protected downContent = '';
  protected dirty = false;

  constructor(
    protected logger: NGXLogger,
    public translations: { [key: string]: any },
    public gaugeColors: { [key: string]: string } = null
  ) {}

  public resizeEvent(): void {}

  public abstract draw(): void;

  public onStateChange(state: GaugeUIState): void {
    this.updateState(state);
  }

  public onProgressChange(state: GaugeUIState): void {
    this.updateState(state);
  }

  public onValueChange(state: GaugeUIState): void {
    this.updateState(state);
  }

  protected setStateView(value: StateView): void {
    let newState = '';
    switch (value) {
      case StateView.READY:
      case StateView.COMPLETE:
        newState = 'h';
        break;
      case StateView.ERROR:
        newState = 'w';
        break;
      case StateView.INIT:
        newState = 'q';
        break;
      case StateView.PING:
        newState = 'q';
        break;
      case StateView.DOWNLOAD:
        newState = 'r';
        break;
      case StateView.UPLOAD:
        newState = 's';
        break;
      case StateView.QOS:
        newState = '\u00EB';
        break;
      default:
        this.logger.warn('setStateView: unknown state');
    }
    if (this.stateContent !== newState) {
      this.dirty = true;
      this.stateContent = newState;
    }
  }

  protected setValueView(value: string): void {
    if (this.valueContent !== value) {
      this.dirty = true;
      this.valueContent = value;
    }
  }

  protected setPingView(value: string): void {
    if (this.pingContent !== value) {
      this.dirty = true;
      this.pingContent = value;
    }
  }

  protected setDownloadView(value: string): void {
    if (this.downContent !== value) {
      this.dirty = true;
      this.downContent = value;
    }
  }

  protected setUploadView(value: string): void {
    if (this.upContent !== value) {
      this.dirty = true;
      this.upContent = value;
    }
  }

  protected setPositionView(value: string): void {
    if (this.positionContent !== value) {
      this.dirty = true;
      this.positionContent = value;
    }
  }

  protected setProviderView(value: string): void {
    if (this.providerContent !== value) {
      this.dirty = true;
      this.providerContent = value;
    }
  }

  protected setDeviceView(value: string): void {
    if (this.deviceContent !== value) {
      this.dirty = true;
      this.deviceContent = value;
    }
  }

  protected setTechnologyView(value: string): void {
    if (this.technologyContent !== value) {
      this.dirty = true;
      this.technologyContent = value;
    }
  }

  protected setServerView(value: string): void {
    if (this.serverContent !== value) {
      this.dirty = true;
      this.serverContent = value;
    }
  }

  protected logSpeed(value: number): number {
    // nettest/nettest-ios/blob/master/rmbt-ios-client/Sources/Speed.swift
    if (value < 1e5) {
      return 0;
    }
    return (GAUGE_PARTS - MAX_SPEED_LOG + Math.log10(value / 1e6)) / GAUGE_PARTS;
  }

  protected deg2rad(value: number): number {
    return value * (Math.PI / 180);
  }

  protected rad2deg(value: number): number {
    return value / (Math.PI / 180);
  }

  protected updateState(state: GaugeUIState): void {
    // this.logger.debug('updateState', state, this.currentState);
    if (state == null) {
      // Reset values
      this.setValueView('');
      this.setPingView('-');
      this.setDownloadView('-');
      this.setUploadView('-');
      this.setPositionView('-');
      this.setProviderView('-');
      this.setDeviceView('-');
      this.setTechnologyView('-');
      this.setServerView('-');
      return;
    }
    state.technology = 'BROWSER';
    const noState: boolean = this.currentState == null;
    if (noState) {
      this.currentState = new GaugeUIState();
    }
    let progress: number = this.progress;
    let value: number = this.value;

    if (
      noState ||
      (this.currentState.gaugeUIState !== state.gaugeUIState && state.gaugeUIState === GaugeUIStateEnum.READY)
    ) {
      // Reset values
      this.setValueView('');
      this.setPingView('-');
      this.setDownloadView('-');
      this.setUploadView('-');
      this.setPositionView('-');
      this.setProviderView('-');
      this.setDeviceView('-');
      this.setTechnologyView('-');
      this.setServerView('-');
    }

    if (progress == null) {
      progress = 0.0;
    }
    /*
     * 0..init
     * 1..ping
     * 2..down
     * 3..up
     * (4..qos)
     */
    const sectionPercent = 0.25;
    let section: number = Math.floor(progress / sectionPercent);
    let partProgress: number = null;
    let numParts = 0;
    if (section === 0) {
      numParts = 7;
    } else if (section !== 3) {
      numParts = 1;
    } else if (section === 3) {
      numParts = 2;
    }

    if (noState || this.currentState.gaugeUIState !== GaugeUIStateEnum.ERROR) {
      // Only for no error
      switch (state.gaugeUIState) {
        case GaugeUIStateEnum.WAITING:
          section = 0;
          partProgress = 1 / numParts;
          break;
        case GaugeUIStateEnum.CONNECTED:
          section = 0;
          partProgress = 2 / numParts;
          break;
        case GaugeUIStateEnum.STARTING:
          section = 0;
          partProgress = 3 / numParts;
          break;
        case GaugeUIStateEnum.TOKEN:
          section = 0;
          partProgress = 4 / numParts;
          break;
        case GaugeUIStateEnum.TOKEN_OK:
          section = 0;
          partProgress = 5 / numParts;
          break;
        case GaugeUIStateEnum.DOWN_PRE:
          section = 0;
          partProgress = 6 / numParts;
          partProgress += state.progress / numParts;
          break;
        case GaugeUIStateEnum.PING:
          section = 1;
          partProgress = state.progress;
          break;
        case GaugeUIStateEnum.DOWN:
          section = 2;
          partProgress = state.progress;
          break;
        case GaugeUIStateEnum.UP_PRE:
          section = 3;
          partProgress = state.progress / numParts;
          // value = 0.0;
          break;
        case GaugeUIStateEnum.UP:
          section = 3;
          partProgress = state.progress;
          break;
        case GaugeUIStateEnum.SUBMIT:
        case GaugeUIStateEnum.SUBMIT_OK:
          state.upMBit = null;
          break;
        case GaugeUIStateEnum.COMPLETE:
          // value = 0.0;
          // progress = 1.0;
          break;
      }
    }

    if (partProgress != null) {
      progress = Math.min(sectionPercent * (partProgress + section), 1.0);
    }

    if (noState || this.currentState.ping !== state.ping) {
      if (state.ping != null) {
        this.setPingView('' + state.ping + ' ' + this.translations.DURATION_MS);
        this.setValueView('' + state.ping + ' ' + this.translations.DURATION_MS);
      }
    }
    if (noState || (this.currentState.downMBit !== state.downMBit && state.gaugeUIState === GaugeUIStateEnum.DOWN)) {
      if (state.downMBit != null) {
        this.setDownloadView('' + state.downMBit + ' ' + this.translations.SPEED_MBPS);
        this.setValueView('' + state.downMBit + ' ' + this.translations.SPEED_MBPS);
        value = this.logSpeed(state.downBit);
      }
    }
    if (noState || (this.currentState.upMBit !== state.upMBit && state.gaugeUIState === GaugeUIStateEnum.UP)) {
      if (state.upMBit != null) {
        this.setUploadView('' + state.upMBit + ' ' + this.translations.SPEED_MBPS);
        this.setValueView('' + state.upMBit + ' ' + this.translations.SPEED_MBPS);
        value = this.logSpeed(state.upBit);
      }
    }
    if (noState || this.currentState.serverName !== state.serverName) {
      if (state.serverName != null) {
        this.setServerView('' + state.serverName);
      }
    }
    if (noState || this.currentState.location !== state.location) {
      if (state.location != null) {
        this.setPositionView(state.location.latitude + '\n' + state.location.longitude);
      }
    }
    if (noState || this.currentState.device !== state.device) {
      if (state.device != null) {
        this.setDeviceView('' + state.device);
      }
    }
    if (noState || this.currentState.technology !== state.technology) {
      if (state.technology != null) {
        this.setTechnologyView('' + state.technology);
      }
    }
    if (noState || this.currentState.provider !== state.provider) {
      if (state.provider != null) {
        this.setTechnologyView('' + state.provider);
      }
    }

    let info = '';
    if (this.currentState.uuid != null) {
      info += this.currentState.uuid + '<br />';
    }
    if (this.currentState.remoteIp != null) {
      info += this.currentState.remoteIp + '<br />';
    }
    /*
        if (noState || this.currentState.totalProgress !== state.totalProgress) {
            if (state.totalProgress !== null) {
                progress = state.totalProgress;
            }
        }
        */

    if (noState || this.currentState.gaugeUIState !== state.gaugeUIState) {
      switch (state.gaugeUIState) {
        case GaugeUIStateEnum.READY:
          this.setStateView(StateView.READY);
          value = 0.0;
          progress = 0.0;
          break;
        case GaugeUIStateEnum.WAITING:
        case GaugeUIStateEnum.CONNECTED:
        case GaugeUIStateEnum.STARTING:
        case GaugeUIStateEnum.TOKEN:
        case GaugeUIStateEnum.TOKEN_OK:
        case GaugeUIStateEnum.DOWN_PRE:
          this.setStateView(StateView.INIT);
          value = 0;
          break;
        case GaugeUIStateEnum.DOWN_PRE_OK:
          progress = sectionPercent * 1; // TODO: is this the correct behaviour?
        /* falls through */ case GaugeUIStateEnum.PING:
          this.setStateView(StateView.PING);
          break;
        case GaugeUIStateEnum.DOWN:
          this.setStateView(StateView.DOWNLOAD);
          progress = sectionPercent * 2;
          break;
        case GaugeUIStateEnum.DOWN_OK:
          this.setStateView(StateView.DOWNLOAD);
          value = 0.0;
          break;
        case GaugeUIStateEnum.UP_PRE:
          this.setStateView(StateView.UPLOAD);
          this.setValueView('');
          progress = sectionPercent * 3;
          break;
        case GaugeUIStateEnum.UP_OK:
          this.setStateView(StateView.UPLOAD);
          value = 0.0; // TODO: is this the correct behaviour?
        /* falls through */ case GaugeUIStateEnum.SUBMIT:
          this.setValueView('');
          progress = sectionPercent * 4;
          break;
        case GaugeUIStateEnum.SUBMIT_OK:
        case GaugeUIStateEnum.COMPLETE:
          this.setStateView(StateView.COMPLETE);
          value = 0.0;
          progress = 1.0;
          break;
        case GaugeUIStateEnum.ERROR:
          this.setStateView(StateView.ERROR);
          progress = 1.0;
          value = 1.0;
          break;
      }
    }

    this.progress = progress;
    this.value = value;
    this.currentState.apply(state);
    if (this.dirty) {
      this.draw();
    }
  }
}
