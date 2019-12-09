import { BaseAPI } from './base.api';
import { RequestInfoAPI } from './request-info.api';

export class RequestAPI<T> extends BaseAPI<T> {
  public request_info: RequestInfoAPI;
}
