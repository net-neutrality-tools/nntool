import {NetTestComponent, TestGuard} from './test.component';
import {StartScreenComponent} from './start.screen';


export const TEST_DECLARATIONS: any[] = [
    NetTestComponent,
    StartScreenComponent,
];
export const TEST_PROVIDERS: any[] = [
    TestGuard
];
