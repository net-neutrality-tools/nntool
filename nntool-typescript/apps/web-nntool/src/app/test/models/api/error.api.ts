export class ErrorAPI {
  /**
   * Date and time at which the error occurred.
   */
  public time: Date;

  /**
   * URI path/resource that caused the error.
   */
  public path: string;

  /**
   * Status code for the error. Example: 400, 404, 500, ...
   */
  public status: number;

  /**
   * String representation of the status. Example: "Internal Server Error, "Not Found", ...
   */
  public error: string;

  /**
   * The error or exception message. Example: "java.lang.RuntimeException".
   */
  public message: string;

  /**
   * Exception class name.
   */
  public exception: string;

  /**
   * Exception stack trace.
   */
  public trace: string;
}
