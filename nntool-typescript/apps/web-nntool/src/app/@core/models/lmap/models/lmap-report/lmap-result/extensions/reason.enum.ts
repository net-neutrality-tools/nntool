export enum Reason {
  /**
   * Use this if the connection to the measurement server couldn't be established.
   */
  UNABLE_TO_CONNECT = 'UNABLE_TO_CONNECT',

  /**
   * Use this if the connection was lost during a measurement.
   */
  CONNECTION_LOST = 'CONNECTION_LOST',

  /**
   * Use this if the network category changed (e.g. from MOBILE to WIFI).
   */
  NETWORK_CATEGORY_CHANGED = 'NETWORK_CATEGORY_CHANGED',

  /**
   * Use this if the App was put to background on mobile devices.
   */
  APP_BACKGROUNDED = 'APP_BACKGROUNDED',

  /**
   * Use this if the user aborted the measurement.
   */
  USER_ABORTED = 'USER_ABORTED'
}
