import {Component, EventEmitter, Input, Output, OnInit} from '@angular/core';
import { RequestsService } from '../services/requests.service';
import {RequestInfoService} from '../services/request-info.service';
import { environment } from '../../environments/environment';
import { SpeedMeasurementPeerResponseAPI } from './models/server-selection/speed-measurement-peer-response.api';
import { ResponseAPI } from './models/api/response.api';
import { ConfigService } from '../services/config.service';
import { SpeedMeasurementPeer } from './models/server-selection/speed-measurement-peer';
import { tap } from 'rxjs/operators';
import { LoggerService, Logger } from '../services/log.service';

@Component({
    selector: 'app-test-server-selection',
    templateUrl: './test.server_selection.html'
})
export class ServerSelectionComponent implements OnInit {

    private static logger: Logger = LoggerService.getLogger('ServerSelectionComponent');

    private speedServerList: SpeedMeasurementPeer[];

    private currentSpeedServer: SpeedMeasurementPeer;

    constructor (private readonly requestService: RequestsService,
                private readonly requestInfoService: RequestInfoService,
                private readonly configService: ConfigService) {}

    ngOnInit(): void {
        //request measurement peer

        const measurementPeerRequest = {
            data: {
                deserialize_type: environment.deserializeTypes.speedMeasurementPeerRequestDeserializeType
            },
            request_info: this.requestInfoService.getRequestInfo()
        };


        this.requestService.getJson<ResponseAPI<SpeedMeasurementPeerResponseAPI>>(
            `${this.configService.getServerControl()}speed-measurement-peers`,
             measurementPeerRequest).subscribe( (response: ResponseAPI<SpeedMeasurementPeerResponseAPI>) => {
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

    public getCurrentSpeedServer () : SpeedMeasurementPeer {
        return this.currentSpeedServer;
    }

}
