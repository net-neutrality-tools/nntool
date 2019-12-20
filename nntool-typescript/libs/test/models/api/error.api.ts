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
