import { Component, OnInit } from '@angular/core';
import { ConfigService } from '../services/config.service';
import { Logger, LoggerService } from '../services/log.service';
import { UserInfo, UserService } from '../services/user.service';
import { WebsiteSettings } from '../settings/settings.interface';

@Component({
  templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {
  // TODO i18n
  private logger: Logger = LoggerService.getLogger('SettingsComponent');
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

  constructor(private configService: ConfigService, private userService: UserService) {}

  public ngOnInit() {
    this.settings = this.configService.getConfig();
    this.user = this.userService.user;
    console.log(this.user);
    this.userService.loadMeasurements(this.user).subscribe(
      () => {},
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
