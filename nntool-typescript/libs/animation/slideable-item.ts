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

export abstract class SlideableItem {
  // for reasons the method is not always available when it is a class function => constructing it as static method works
  public static toggleSlideAnimation(slideableItem: SlideableItem) {
    slideableItem.showSlideableItem = !slideableItem.showSlideableItem;
  }
  public showSlideableItem = false;

  constructor() {
    this.showSlideableItem = false;
  }
}
