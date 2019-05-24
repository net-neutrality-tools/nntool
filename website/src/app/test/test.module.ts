import {NetTestComponent, TestGuard} from "./test.component";
import {StartScreen} from "./start.screen";


export const TEST_DECLARATIONS: any[] = [
    NetTestComponent,
    StartScreen,
];
export const TEST_PROVIDERS: any[] = [
    TestGuard
];