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

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { environment } from '@env/environment';
import { ResponseAPI } from './models/api/response.api';
import { SpeedMeasurementPeer } from './models/server-selection/speed-measurement-peer';
import { SpeedMeasurementPeerResponseAPI } from './models/server-selection/speed-measurement-peer-response.api';
import { RequestsService } from '../core/services/requests.service';
import { RequestInfoService } from '../core/services/request-info.service';
import { ConfigService } from '../core/services/config.service';

@Component({
  selector: 'app-test-server-selection',
  templateUrl: './test.server_selection.html'
})
export class ServerSelectionComponent implements OnInit {
  public speedServerList: SpeedMeasurementPeer[];

  private currentSpeedServer: SpeedMeasurementPeer;

  constructor(
    private readonly requestService: RequestsService,
    private readonly requestInfoService: RequestInfoService,
    private readonly configService: ConfigService
  ) {}

  public ngOnInit(): void {
    // Request measurement peer
    const measurementPeerRequest = {
      data: {
        deserialize_type: environment.deserializeTypes.speedMeasurementPeerRequestDeserializeType
      },
      request_info: this.requestInfoService.getRequestInfo()
    };

    this.requestService
      .getJson<ResponseAPI<SpeedMeasurementPeerResponseAPI>>(
        `${this.configService.getServerControl()}speed-measurement-peers`,
        measurementPeerRequest
      )
      .subscribe((response: ResponseAPI<SpeedMeasurementPeerResponseAPI>) => {
        this.speedServerList = response.data.peers;
        let foundDefault = false;
        for (const server of this.speedServerList) {
          if (server.default) {
            this.currentSpeedServer = server;
            foundDefault = true;
            break;
          }
        }
        if (!foundDefault && this.speedServerList.length > 0) {
          this.currentSpeedServer = this.speedServerList[0];
        }
      });
  }

  public getCurrentSpeedServer(): SpeedMeasurementPeer {
    return this.currentSpeedServer;
  }
}
