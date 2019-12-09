import { NGXLogger } from 'ngx-logger';
import { BaseMeasurementGauge, Point, ProgressType, StateView } from './base.gauge.ui';

export class MeasurementGauge extends BaseMeasurementGauge {
  private resolutionScaleFactor = 2;
  private arcWidth = 0;
  private arcSpace = 0;
  private buttonWidth = 75;

  private angle = 240;
  private startAngle: number = 90 + (360 - this.angle) / 2;

  private angleRad: number = this.deg2rad(this.angle + this.startAngle);
  private startAngleRad: number = this.deg2rad(this.startAngle);

  private canvasContext: CanvasRenderingContext2D;
  private canvasPartitionContext: CanvasRenderingContext2D;

  constructor(
    protected logger: NGXLogger,
    private canvas: HTMLCanvasElement,
    private canvasPartition: HTMLCanvasElement,
    private stateView: HTMLElement,
    private pingView: HTMLElement,
    private upView: HTMLElement,
    private downView: HTMLElement,
    private positionView: HTMLElement,
    private providerView: HTMLElement,
    private deviceView: HTMLElement,
    private technologyView: HTMLElement,
    private serverView: HTMLElement,
    private window: Window,
    translations: { [key: string]: any },
    gaugeColors: { [key: string]: string } = null,
    public gaugeFont: string = null,
    public hasQos: boolean = false
  ) {
    super(logger, translations, gaugeColors);
    this.canvasContext = this.canvas.getContext('2d');
    this.canvasPartitionContext = this.canvasPartition.getContext('2d');

    if (this.window && this.window.devicePixelRatio) {
      this.resolutionScaleFactor = this.window.devicePixelRatio;
    }

    // set canvas width and height to css values
    this.setCanvas();
    this.addResize();

    if (!this.translations) {
      this.translations = {
        SPEED_MBPS: 'Mbps',
        DURATION_MS: 'ms',
        INNER_TEXTS: ['0Mbps', '1Mbps', '10Mbps', '100Mbps', '1Gbps'],
        OUTER_TEXTS: ['init', 'ping', 'down', 'up', 'qos']
      };
    }
    if (!this.gaugeColors) {
      this.gaugeColors = {
        baseColor: '#EEEEEE',
        valueColor: '#878787',
        progressColor: '#911232',
        fontColor: '#FFFFFF'
      };
    }
    if (!this.gaugeFont) {
      this.gaugeFont = 'times';
    }

    //in order to display the GO correctly in a non-icon font, set the icon font to initial at first
    this.stateView.style.fontFamily = "initial";
  }

  public resizeEvent() {
    if (this.drawing) {
      // console.debug("Resize already drawing");
      return;
    }
    super.resizeEvent();
    this.clear();
    this.setCanvas();
    this.draw();
  }

  public draw(): void {
    if (this.drawing) {
      // console.debug("Already drawing..");
      setTimeout(() => {
        this.draw();
      }, 100);
      return;
    }
    this.drawing = true;
    // this.canvasContext.translate(0, +80);
    // this.canvasPartitionContext.translate(0, +80);
    this.clear();

    const center = this.getCenter(this.canvas);
    const [radiusOuter, radiusInner, radiusButton] = this.getRadii();

    this.canvasContext.lineWidth = this.arcWidth;
    this.canvasContext.strokeStyle = this.gaugeColors.baseColor;

    // draw outer arc background
    this.drawArc(center, radiusOuter);

    // draw inner arc background
    this.drawArc(center, radiusInner);

    this.drawPartitions();

    // draw progress
    this.canvasContext.strokeStyle = this.gaugeColors.progressColor;

    this.drawArc(center, radiusOuter, this.progress, this.progressType === ProgressType.QOS);

    // draw value
    this.canvasContext.strokeStyle = this.gaugeColors.valueColor;

    // draw inner arc
    this.drawArc(center, radiusInner, this.value, false);

    // draw labels
    const txtHeight: number = this.arcWidth * 0.6;
    this.drawTextList(
      this.translations.OUTER_TEXTS,
      radiusOuter - txtHeight / 3,
      1.6,
      '100 ' + txtHeight + 'px ' + this.gaugeFont,
      this.gaugeColors.fontColor,
      false
    );
    this.drawTextList(
      this.translations.INNER_TEXTS,
      radiusInner,
      1.7,
      '100 ' + txtHeight / 2 + 'px ' + this.gaugeFont,
      this.gaugeColors.fontColor,
      false,
      1.13
    );

    // this.canvasContext.translate(0, -80);
    // this.canvasPartitionContext.translate(0, -80);
    this.drawing = false;
  }

  protected setStateView(value: StateView): void {
    const old: string = this.stateContent;
    const oldDirty: boolean = this.dirty;
    super.setStateView(value);
    // Not drawn
    this.dirty = oldDirty;

    /*if (value === StateView.READY) {
            this.stateView.className = "btn__nettest-state";
        }
        if (value === StateView.INIT) {
            this.stateView.className = "btn__nettest-state inprogress";
        }
        if (value === StateView.COMPLETE) {
            this.stateView.className = "btn__nettest-state complete";
        }
        if (value === StateView.ERROR) {
            this.stateView.className = "btn__nettest-state complete";
        }*/

    if (this.stateContent !== old) {
      this.stateView.innerText = this.stateContent;
      //reset icon font usage based on displayed state
      if (value === StateView.READY || value === StateView.COMPLETE) {
        this.stateView.style.fontFamily = "initial";
      } else {
        this.stateView.style.fontFamily = "";
      }
    }
  }

  protected setValueView(value: string): void {
    super.setValueView(value);
  }

  protected setPingView(value: string): void {
    if (this.pingContent !== value) {
      this.pingContent = value;
      this.pingView.innerText = value;
    }
  }

  protected setDownloadView(value: string): void {
    if (this.downContent !== value) {
      this.downContent = value;
      this.downView.innerText = value;
    }
  }

  protected setUploadView(value: string): void {
    if (this.upContent !== value) {
      this.upContent = value;
      this.upView.innerText = value;
    }
  }

  protected setPositionView(value: string): void {
    if (value) {
      value.replace('\n', '<br />');
    }
    if (this.positionContent !== value) {
      this.positionContent = value;
      this.positionView.innerText = value;
    }
  }

  protected setProviderView(value: string): void {
    if (this.providerContent !== value) {
      this.providerContent = value;
      this.providerView.innerText = value;
    }
  }

  protected setDeviceView(value: string): void {
    if (this.deviceContent !== value) {
      this.deviceContent = value;
      this.deviceView.innerText = value;
    }
  }

  protected setTechnologyView(value: string): void {
    if (this.technologyContent !== value) {
      this.technologyContent = value;
      this.technologyView.innerText = value;
    }
  }

  protected setServerView(value: string): void {
    if (this.serverContent !== value) {
      this.serverContent = value;
      this.serverView.innerText = value;
    }
  }

  private setCanvas(minVal: number = 400) {
    this.canvas.width = Math.max(this.canvas.clientWidth, minVal) * this.resolutionScaleFactor;
    this.canvas.height = Math.max(this.canvas.clientHeight, minVal) * this.resolutionScaleFactor;
    this.canvasPartition.width = Math.max(this.canvasPartition.clientWidth, minVal) * this.resolutionScaleFactor;
    this.canvasPartition.height = Math.max(this.canvasPartition.clientHeight, minVal) * this.resolutionScaleFactor;

    this.arcWidth = this.canvas.width / 13;
    this.arcSpace = this.arcWidth * 0.8;
  }

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

  private removeResize(): void {
    if (this.canvas.addEventListener) {
      // > IE8
      this.canvas.removeEventListener('resize', this.resizeEvent);
    } else {
      (this.canvas as any).detachEvent('onresize', this.resizeEvent);
    }
  }

  private getCenter(canvas: HTMLCanvasElement): Point {
    return new Point(canvas.width / 2, canvas.height / 2);
  }

  private getRadii(): number[] {
    const outerArcRadius = (this.canvas.width - this.arcWidth) / 2;
    const innerArcRadius = outerArcRadius - this.arcWidth - this.arcSpace;
    const buttonRadius = this.buttonWidth;

    return [outerArcRadius, innerArcRadius, buttonRadius];
  }

  private clear() {
    this.canvasContext.clearRect(0, 0, this.canvas.width, this.canvas.height);
    this.canvasPartitionContext.clearRect(0, 0, this.canvasPartition.width, this.canvasPartition.height);
  }

  private drawArc(center: Point, radius: number, factor: number = 1.0, reverse: boolean = false) {
    this.canvasContext.beginPath();

    const aStart = reverse ? this.angleRad : this.startAngleRad;
    const aEnd = this.deg2rad(this.startAngle + this.angle * factor);

    this.canvasContext.arc(center.x, center.y, radius, aStart, aEnd, reverse);

    this.canvasContext.stroke();
  }

  private drawTextAlongArc(
    text: string,
    center: Point,
    radius: number,
    startRotate: number,
    maxAngle: number,
    factor: number,
    font: string,
    align: 'LEFT' | 'CENTER' | 'RIGHT'
  ) {
    this.canvasPartitionContext.save();
    this.canvasPartitionContext.font = font;
    this.canvasPartitionContext.textAlign = 'center';

    const len: number = text.length;
    const txtWidth: number = this.canvasPartitionContext.measureText(text).width;
    const angle: number = Math.min((txtWidth / radius) * factor, maxAngle);
    // const angle: number = Math.min(this.deg2rad(Math.ceil(180 / Math.PI * txtWidth / radius)), maxAngle);

    this.canvasPartitionContext.translate(center.x, center.y);
    this.canvasPartitionContext.rotate(startRotate);
    if (align === 'CENTER') {
      this.canvasPartitionContext.rotate((-1 * angle) / 2);
    } else if (align === 'LEFT') {
      this.canvasPartitionContext.rotate((-1 * maxAngle) / 2);
    } else if (align === 'RIGHT') {
    }
    this.canvasPartitionContext.rotate((-1 * (angle / len)) / 2);

    for (let n = 0; n < len; n++) {
      this.canvasPartitionContext.rotate(angle / len);
      this.canvasPartitionContext.save();
      this.canvasPartitionContext.translate(0, -1 * radius);
      const letter: string = text[n];
      this.canvasPartitionContext.fillText(letter, 0, 0);
      this.canvasPartitionContext.restore();
    }
    this.canvasPartitionContext.restore();
  }

  private drawPartitions() {
    const center = this.getCenter(this.canvasPartition);
    const [radiusOuter, radiusInner, radiusButton] = this.getRadii();
    const parts: number = this.hasQos ? 5 : 4;

    this.canvasPartitionContext.beginPath();
    this.canvasPartitionContext.strokeStyle = '#00f';
    /*this.canvasPartitionContext.arc(
            center.x, center.y, radiusOuter,
            this.startAngleRad, this.deg2rad(this.startAngle + this.angle), false
        );*/
    this.canvasPartitionContext.lineWidth = 2;
    this.canvasPartitionContext.stroke();
    this.canvasPartitionContext.beginPath();
    this.canvasPartitionContext.strokeStyle = '#fff';
    this.canvasPartitionContext.translate(center.x, center.y);

    // Outer
    for (let i = 1; i < parts; i++) {
      const angle: number = this.deg2rad(this.startAngle - 270 + (this.angle / parts) * i);
      const sineAngle: number = Math.sin(angle);
      const cosAngle: number = -Math.cos(angle);
      const iPoint: Point = new Point(
        sineAngle * (radiusOuter + this.arcWidth / 2),
        cosAngle * (radiusOuter + this.arcWidth / 2)
      );
      const oPoint: Point = new Point(
        sineAngle * (radiusOuter - this.arcWidth / 2),
        cosAngle * (radiusOuter - this.arcWidth / 2)
      );

      this.canvasPartitionContext.beginPath();
      this.canvasPartitionContext.moveTo(iPoint.x, iPoint.y);
      this.canvasPartitionContext.lineTo(oPoint.x, oPoint.y);
      this.canvasPartitionContext.stroke();
    }

    // Inner
    for (let i = -1; i <= 1; i++) {
      const angle: number = (Math.PI / 3) * i;
      const sineAngle: number = Math.sin(angle);
      const cosAngle: number = -Math.cos(angle);
      const iPoint: Point = new Point(
        sineAngle * (radiusInner - this.arcWidth * 0.2),
        cosAngle * (radiusInner - this.arcWidth * 0.2)
      );
      const oPoint: Point = new Point(
        sineAngle * (radiusInner - this.arcWidth / 2),
        cosAngle * (radiusInner - this.arcWidth / 2)
      );

      this.canvasPartitionContext.strokeStyle = '#0f0';
      this.canvasPartitionContext.arc(iPoint.x, iPoint.y, 4, 0, this.deg2rad(360));
      this.canvasPartitionContext.beginPath();
      this.canvasPartitionContext.strokeStyle = '#fff';
      this.canvasPartitionContext.moveTo(iPoint.x, iPoint.y);
      this.canvasPartitionContext.lineTo(oPoint.x, oPoint.y);
      this.canvasPartitionContext.stroke();
    }
    // Reset canvas
    this.canvasPartitionContext.translate(-center.x, -center.y);
  }

  private drawTextList(
    texts: string[],
    radius: number,
    factor: number,
    font: string,
    fontColor: string,
    stretch: boolean = false,
    angleFactor: number = 1.0
  ): void {
    const parts: number = texts.length;
    const center: Point = this.getCenter(this.canvas);
    const angleStep: number = (this.angle * angleFactor) / parts;

    this.canvasPartitionContext.save();
    this.canvasPartitionContext.fillStyle = fontColor;
    const start: number = this.startAngle + (this.angle - this.angle * angleFactor) / 2 - 270 - angleStep / 2;
    let i = 0;

    for (const text of texts) {
      const angle: number = this.deg2rad(start + angleStep * (i + 1));
      let align: 'LEFT' | 'CENTER' | 'RIGHT' = 'CENTER';
      if (stretch) {
        if (i === 0) {
          align = 'LEFT';
        }
        if (i === texts.length - 1) {
          align = 'RIGHT';
        }
      }
      this.drawTextAlongArc(text, center, radius, angle, this.deg2rad(Math.floor(angleStep)), factor, font, align);
      i++;
    }

    this.canvasPartitionContext.stroke();
    this.canvasPartitionContext.restore();
  }
}
