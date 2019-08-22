import { Location } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable, Observer, throwError } from 'rxjs';

import { ResultGroupResponse } from '../history/model/result.groups';
import { WebsiteSettings } from '../settings/settings.interface';
import { ConfigService } from './config.service';
import { Logger, LoggerService } from './log.service';
import { RequestsService } from './requests.service';
import { BrowserStorageService } from './storage.service';

export class UserInfo {
  public static fromJson(json: string): UserInfo {
    const obj: any = JSON.parse(json);
    const userInfo: UserInfo = new UserInfo();
    if (typeof obj.disassociated !== 'undefined') {
      userInfo.disassociated = obj.disassociated;
    }
    if (typeof obj.invisible !== 'undefined') {
      userInfo.invisible = obj.invisible;
    }
    if (typeof obj.forceIp !== 'undefined') {
      userInfo.forceIp = obj.forceIp;
    }
    if (typeof obj.disassociateBeforeDelete !== 'undefined') {
      userInfo.disassociateBeforeDelete = obj.disassociateBeforeDelete;
    }
    if (typeof obj.uuid !== 'undefined') {
      userInfo.uuid = obj.uuid;
    }
    if (typeof obj.measurementUUIDs !== 'undefined') {
      userInfo.measurementUUIDs = obj.measurementUUIDs;
    }
    if (typeof obj.acceptTC !== 'undefined') {
      userInfo.acceptTC = obj.acceptTC;
    }
    return userInfo;
  }
  /**
   * Disassociate measurements after taking them
   * - keep user id
   */
  public disassociated = false;

  /**
   * Don't track me
   * - includes disassociate?
   * - delete user id after each test
   */
  public invisible = false;

  /**
   * Useful?
   */
  public forceIp = false;

  /**
   *
   */
  public disassociateBeforeDelete = false;

  /**
   * Client uuid
   */
  public uuid: string = null;

  public measurementUUIDs: string[] = null;

  /**
   * Accepted terms and conditions
   */
  public acceptTC = false;

  public apply(other: UserInfo): void {
    this.disassociated = other.disassociated;
    this.invisible = other.invisible;
    this.forceIp = other.forceIp;
    this.disassociateBeforeDelete = other.disassociateBeforeDelete;
    this.uuid = other.uuid;
    this.measurementUUIDs = other.measurementUUIDs;
    this.acceptTC = other.acceptTC;
  }

  public toJson(): string {
    const obj: any = {
      disassociated: this.disassociated,
      invisible: this.invisible,
      forceIp: this.forceIp,
      disassociateBeforeDelete: this.disassociateBeforeDelete,
      uuid: this.uuid,
      measurementUUIDs: this.measurementUUIDs,
      acceptTC: this.acceptTC
    };

    return JSON.stringify(obj);
  }
}

@Injectable()
export class UserService {
  get user(): UserInfo {
    if (this.userInfo === null) {
      this.userInfo = new UserInfo();
      this.load(this.userInfo);
    }
    return this.userInfo;
  }

  set user(other: UserInfo) {
    if (this.userInfo === null) {
      this.userInfo = new UserInfo();
      this.load(this.userInfo);
    }
    this.userInfo.apply(other);
  }

  private logger: Logger = LoggerService.getLogger('UserService');
  private userInfo: UserInfo = null;
  private config: WebsiteSettings;

  constructor(
    private storage: BrowserStorageService,
    private requests: RequestsService,
    private configService: ConfigService,
    private translateService: TranslateService
  ) {
    this.config = this.configService.getConfig();
  }

  /**
   * Load user information from local storage and if found apply it to user
   *
   * @param user  Object to load info into (if not set -> use local userInfo)
   */
  public load(user: UserInfo = null): void {
    /*
        user.disassociated = this.getKeyDefault("disassociated", user.disassociated);
        user.invisible = this.getKeyDefault("invisible", user.invisible);
        user.forceIp = this.getKeyDefault("forceIp", user.forceIp);
        user.disassociateBeforeDelete = this.getKeyDefault("disassociateBeforeDelete", user.disassociateBeforeDelete);
        */
    if (!user) {
      user = this.user;
    }
    const userInfoSt: string = this.storage.load('user_info', true);
    if (userInfoSt) {
      user.apply(UserInfo.fromJson(userInfoSt));
    }
  }

  /**
   * Save user information to local storage
   *
   * @param user  User to save (if not set -> use local userInfo)
   */
  public save(user: UserInfo = null): void {
    /*
        this.storage.save("disassociated", this.userInfo.disassociated, true);
        this.storage.save("invisible", this.userInfo.invisible, true);
        this.storage.save("forceIp", this.userInfo.forceIp, true);
        this.storage.save("disassociateBeforeDelete", this.userInfo.disassociateBeforeDelete, true);
        */
    if (!user) {
      user = this.user;
    }
    this.storage.save('user_info', user.toJson(), true);
  }

  /**
   * Load measurements (uuids) of user
   *
   * @param user  User to load measurements for (if not set -> use local userInfo)
   * @returns Was the call successful
   */
  public loadMeasurements(user: UserInfo = null): Observable<any> {
    if (!user) {
      user = this.user;
    }
    if (!user || !user.uuid) {
      return throwError('No user set');
    }
    // const lang: string = this.translateService.currentLang;
    return new Observable((observer: any) => {
      this.requests
        .getJson<any>(
          // TODO: take result-service url from settings request
          Location.joinWithSlash(this.config.servers.result, 'measurement-agents/' + user.uuid + '/measurements'),
          {}
        )
        .subscribe(
          (data: any) => {
            this.logger.debug('User Mes', data);
            user.measurementUUIDs = [];

            for (const mes of data.data.content) {
              user.measurementUUIDs.push(mes.test_uuid);
            }
            observer.next(data);
          },
          (err: HttpErrorResponse) => {
            this.logger.error('Error retrieving measurements', err);
            observer.error(err);
          },
          () => {
            observer.complete();
          }
        );
    });
  }

  public loadMeasurementDetail(measurementUuid: string, user: UserInfo = null): Observable<any> {
    if (!user) {
      user = this.user;
    }
    if (!user || !user.uuid) {
      return throwError('No user set');
    }

    return new Observable((observer: any) => {
      this.requests
        .getJson<any>(
          // TODO: take result-service url from settings request
          Location.joinWithSlash(
            this.config.servers.result,
            'measurement-agents/' + user.uuid + '/measurements/' + measurementUuid + '/details'
          ),
          {}
        )
        .subscribe(
          (data: any) => {
            this.logger.debug('User measurement details: ', data);

            observer.next(data);
          },
          (error: HttpErrorResponse) => {
            this.logger.error('Error retrieving measurement', error);
            observer.error();
          },
          () => observer.complete()
        );
    });
  }

  public loadFullMeasurement(measurementUuid: string, user: UserInfo = null): Observable<any> {
    if (!user) {
      user = this.user;
    }
    if (!user || !user.uuid) {
      return throwError('No user set');
    }

    return new Observable((observer: any) => {
      this.requests
        .getJson<any>(
          // TODO: take result-service url from settings request
          Location.joinWithSlash(
            this.config.servers.result,
            'measurement-agents/' + user.uuid + '/measurements/' + measurementUuid
          ),
          {}
        )
        .subscribe(
          (data: any) => {
            this.logger.debug('User full measurement: ', data);

            observer.next(data);
          },
          (error: HttpErrorResponse) => {
            this.logger.error('Error retrieving measurement', error);
            observer.error();
          },
          () => observer.complete()
        );
    });
  }

  /**
   * Disassociate a measurement from user
   *
   * @param clientUUID        UUID of client to use
   * @param measurementUUID   UUID of measurement to use
   * @param wait              Wait for completion of api call or return immediatelly
   * @returns Was the call successfull
   */
  public disassociate(clientUUID: string, measurementUUID: string, wait: boolean = true): Observable<any> {
    if (clientUUID == null || !clientUUID) {
      this.logger.error('No client uuid');
      return throwError('No client uuid');
    }
    if (measurementUUID == null || !measurementUUID) {
      this.logger.error('No measurement uuid');
      return throwError('No measurement uuid');
    }

    return this.requests.deleteJson<any>(
      Location.joinWithSlash(
        this.config.servers.result,
        'measurement-agents/' + clientUUID + '/measurements/' + measurementUUID
      )
    );
  }

  /**
   * Disassociate all measurements for user
   *
   * (Uses only measurement uuid present in user.measurementUUIDs)
   *
   * @param user  User to disassociate
   */
  public disassociateAll(user: UserInfo = null): Observable<any> {
    if (!user) {
      user = this.user;
    }

    if (!user || !user.uuid) {
      this.logger.error('No valid user given');
      return throwError('No valid user given');
    }

    return new Observable((observer: Observer<any>) => {
      this.loadMeasurements(user).subscribe(
        () => {
          const obs: Array<Observable<any>> = [];

          for (const mesID of user.measurementUUIDs) {
            obs.push(this.disassociate(user.uuid, mesID, true));
          }
          forkJoin(obs).subscribe(
            () => {
              observer.next(null);
            },
            (err: string | HttpErrorResponse) => {
              // if (err instanceof Error) {
              this.logger.error('Disassociate failed', err);
              observer.error('Disassociate failed');
            },
            () => {
              observer.complete();
            }
          );
        },
        (error: any) => {
          this.logger.error('Failed to load measurements', error);
          observer.error(error);
        }
      );
    });
  }

  private getKeyDefault(key: string, defaultValue: boolean): boolean {
    let res: any = this.storage.load(key);
    if (res === null || typeof res === 'undefined') {
      res = defaultValue;
    } else if (typeof res === 'string') {
      res = res === 'true';
    }
    return res;
  }
}
