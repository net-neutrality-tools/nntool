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

import { StartScreenComponent } from './start.screen';
import { NetTestComponent, TestGuard } from './test.component';
import { ServerSelectionComponent } from './test.server_selection';

export const TEST_DECLARATIONS: any[] = [NetTestComponent, StartScreenComponent, ServerSelectionComponent];
export const TEST_PROVIDERS: any[] = [TestGuard];
