import { Component, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { WebsiteSettings } from '../../@core/models/settings/settings.interface';
import { UserInfo, UserService } from '../../@core/services/user.service';
import { ConfigService } from '../../@core/services/config.service';

@Component({
  templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {
  // TODO i18n
  private settings: WebsiteSettings;

  private user: UserInfo;

  get shown(): any {
    return this.settings.user.shown;
  }

  get disassociated(): boolean {
    return this.user.disassociated;
  }

  set disassociated(value: boolean) {
    this.user.disassociated = value;
    this.save();
  }

  get invisible(): boolean {
    return this.user.invisible;
  }

  set invisible(value: boolean) {
    this.user.invisible = value;
    this.save();
  }

  get forceIp(): boolean {
    return this.user.forceIp;
  }

  set forceIp(value: boolean) {
    this.user.forceIp = value;
    this.save();
  }

  get executePingMeasurement(): boolean {
    return this.user.executePingMeasurement;
  }

  set executePingMeasurement(value: boolean) {
    this.user.executePingMeasurement = value;
    this.save();
  }

  get executeDownloadMeasurement(): boolean {
    return this.user.executeDownloadMeasurement;
  }

  set executeDownloadMeasurement(value: boolean) {
    this.user.executeDownloadMeasurement = value;
    this.save();
  }

  get executeUploadMeasurement(): boolean {
    return this.user.executeUploadMeasurement;
  }

  set executeUploadMeasurement(value: boolean) {
    this.user.executeUploadMeasurement = value;
    this.save();
  }

  get executeQosMeasurement(): boolean {
    return this.user.executeQosMeasurement;
  }

  set executeQosMeasurement(value: boolean) {
    this.user.executeQosMeasurement = value;
    this.save();
  }

  get disassociateBeforeDelete(): boolean {
    return this.user.disassociateBeforeDelete;
  }

  set disassociateBeforeDelete(value: boolean) {
    this.user.disassociateBeforeDelete = value;
    this.save();
  }

  get clientUuid(): string {
    return this.user.uuid;
  }

  constructor(private logger: NGXLogger, private configService: ConfigService, private userService: UserService) { }

  public ngOnInit() {
    this.settings = this.configService.getConfig();
    this.user = this.userService.user;
    console.log(this.user);
    this.userService.loadMeasurements(this.user).subscribe(
      () => { },
      (error: any) => {
        this.logger.error('Failed to load settings', error);
      }
    );
  }

  /**
   * Save/Apply settings
   */
  public save(): void {
    // Not necessary - same object?
    this.userService.user = this.user;
    this.userService.save();
  }

  /**
   * Clear all user data (Settings to default?)
   */
  public clear(): void {
    this.logger.info('Deleting user');
    if (this.user.disassociateBeforeDelete) {
      this.logger.info('Disassociating measurements before deletion');
      this.userService.disassociateAll(this.user).subscribe(
        () => {
          this.logger.info('Disassociate for user ' + this.user.uuid + ' complete');
          this.userService.user = new UserInfo();
          this.userService.save();
          this.user = this.userService.user;
        },
        (error: any) => {
          this.logger.error('Failed to disassociate all', error);
        }
      );
    } else {
      this.userService.user = new UserInfo();
      this.userService.save();
      this.user = this.userService.user;
    }
  }
}
