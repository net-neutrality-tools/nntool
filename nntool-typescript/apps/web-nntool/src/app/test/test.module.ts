import {NetTestComponent, TestGuard} from './test.component';
import {StartScreenComponent} from './start.screen';
import {ServerSelectionComponent} from './test.server_selection'


export const TEST_DECLARATIONS: any[] = [
    NetTestComponent,
    StartScreenComponent,
    ServerSelectionComponent,
];
export const TEST_PROVIDERS: any[] = [
    TestGuard
];
