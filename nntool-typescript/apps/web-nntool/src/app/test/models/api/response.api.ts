import { BaseAPI } from './base.api';
import { ErrorAPI } from './error.api';

export class ResponseAPI<T> extends BaseAPI<T> {
  /**
   * Optional list of errors that occurred during request processing.
   */
  public errors: ErrorAPI[];
}
