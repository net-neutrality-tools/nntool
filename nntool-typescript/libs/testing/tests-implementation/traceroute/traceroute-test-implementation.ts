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

import { Injectable, NgZone } from '@angular/core';
import { Subject } from 'rxjs';
import { BasicTestState } from '../../enums/basic-test-state.enum';
import { TestSchedulerService } from '../../test-scheduler.service';
import { TestImplementation } from '../test-implementation';
import { TracerouteTestConfig } from './traceroute-test-config';
import { TracerouteTestState } from './traceroute-test-state';
import { RequestsService } from '@nntool-typescript/core/services/requests.service';

@Injectable({
    providedIn: 'root'
})
export class TracerouteTestImplementation extends TestImplementation<TracerouteTestConfig, TracerouteTestState> {

    private $state: Subject<TracerouteTestState>;

    private timeoutHolder: any;
    private progressUpdateHolder: any;
    private startTime: number;
    private endTime: number;
    
    constructor(
        testSchedulerService: TestSchedulerService,
        private zone: NgZone,
        private requestsService: RequestsService
         ) {
        super(testSchedulerService);
    }

    public start = (config: TracerouteTestConfig, $state: Subject<TracerouteTestState>): void => {
        this.$state = $state;

        if (!config.TRACEROUTE) {
            const endState = this.prepareFinalState(config);
            this.$state.next(endState);
            return;
        }

        for (const test of config.TRACEROUTE) {
            if (!test.is_reverse || !test.host) {
                continue;
            }

            if (!test.timeout) {
                test.timeout = 10000;
            }

            if (!test.max_hops) {
                test.max_hops = 30;
            }

            const tracerouteBody = {
                cmd: "traceroute",
                max_hops: test.max_hops
            };

            this.zone.runOutsideAngular(() => {
                this.startTime = new Date().getTime();
                this.requestsService
                    .postJson(test.host + (test.port ? ":" + test.port : ""), tracerouteBody)
                    .subscribe((res: any) => {
                        this.endTime = new Date().getTime();
                        clearTimeout(this.timeoutHolder);
                        let state = this.prepareFinalState(test);
                        state.result.hops = res.hops;
                        this.$state.next(state);
                    });

                this.timeoutHolder = setTimeout(() => {
                    console.log("traceroute - timeout");
                    this.endTime = new Date().getTime();
                    const state = this.prepareFinalState(test);
                    this.$state.next(state);
                }, test.timeout / 1e6);

                this.progressUpdateHolder = setInterval(() => {
                    const state = this.generateInitState();
                    state.progress = (new Date().getTime() - this.startTime) / (test.timeout / 1e6);
                    this.$state.next(state);
                }, 1000)

                const state = this.generateInitState();
                state.basicState = BasicTestState.RUNNING;
                this.$state.next(state);

            });
        }
    };

    private prepareFinalState(config: any): TracerouteTestState {
        if (this.progressUpdateHolder) {
            clearInterval(this.progressUpdateHolder);
        }
        if (this.timeoutHolder) {
            clearInterval(this.timeoutHolder);
        }
        
        const ret: TracerouteTestState = this.generateInitState();
        
        if (config.port) {
            ret.result.port = config.port;
        }
        if (config.qos_test_uid) {
            ret.result.qos_test_uid = config.qos_test_uid;
        }
        if (config.max_hops) {
            ret.result.max_hops = config.max_hops;
        }
        if (this.startTime) {
            ret.result.startTimeNs = this.startTime * 1000;
        }
        if (this.endTime) {
            ret.result.endTimeNs = this.endTime * 1000;
        }
        ret.result.timeout = config.timeout;
        ret.result.host = config.host;
        ret.result.is_reverse = config.is_reverse;
        ret.progress = 1;
        ret.basicState = BasicTestState.ENDED;

        return ret;
    }

    protected generateInitState = () => {
        const state: TracerouteTestState = new TracerouteTestState();

        state.result = {};
        state.basicState = BasicTestState.INITIALIZED;

        return state;
    };

    protected clean = (): void => {
        this.$state = null;
        if (this.timeoutHolder) {
            clearTimeout(this.timeoutHolder);
        }
        if (this.progressUpdateHolder) {
            clearInterval(this.progressUpdateHolder);
        }
    };

    public onDestroy= (): void => {
        //if the component is closed by the client, stop still ongoing tasks
        this.clean();
    }
}
