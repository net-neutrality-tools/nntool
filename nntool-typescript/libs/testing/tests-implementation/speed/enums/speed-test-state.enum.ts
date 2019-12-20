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

export enum SpeedTestStateEnum {
  ERROR = 'ERROR',
  READY = 'READY',
  WAITING = 'WAITING',
  STARTING = 'STARTING',
  CONNECTED = 'CONNECTED',
  TOKEN = 'TOKEN',
  TOKEN_OK = 'TOKEN_OK',
  DOWN_PRE = 'DOWN_PRE',
  DOWN_PRE_OK = 'DOWN_PRE_OK',
  PING = 'PING',
  PING_OK = 'PING_OK',
  DOWN = 'DOWN',
  DOWN_OK = 'DOWN_OK',
  UP_PRE = 'UP_PRE',
  UP_PRE_OK = 'UP_PRE_OK',
  UP = 'UP',
  UP_OK = 'UP_OK',
  SUBMIT = 'SUBMIT',
  SUBMIT_OK = 'SUBMIT_OK',
  QOS = 'QOS',
  QOS_OK = 'QOS_OK',
  QOS_SUBMIT = 'QOS_SUBMIT',
  QOS_SUBMIT_OK = 'QOS_SUBMIT_OK',
  COMPLETE = 'COMPLETE'
}
