import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { environment } from '../../environments/environment';
import { ResponseAPI } from './models/api/response.api';
import { SpeedMeasurementPeer } from './models/server-selection/speed-measurement-peer';
import { SpeedMeasurementPeerResponseAPI } from './models/server-selection/speed-measurement-peer-response.api';
import { RequestsService } from '../@core/services/requests.service';
import { RequestInfoService } from '../@core/services/request-info.service';
import { ConfigService } from '../@core/services/config.service';

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
