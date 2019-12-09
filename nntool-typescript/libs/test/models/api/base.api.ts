export abstract class BaseAPI<T> {
  /**
   * Actual data that is returned for the request/response.
   */
  public data: T;
}
