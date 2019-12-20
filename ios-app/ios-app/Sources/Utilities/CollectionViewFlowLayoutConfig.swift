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

import Foundation
import UIKit

///
struct CollectionViewFlowLayoutConfig {

    let sectionInsets: UIEdgeInsets
    let itemsPerRow: Int
    let heightFactor: CGFloat

    ///
    func getItemDimensions(viewWidth: CGFloat) -> CGSize {
        let paddingSpace = sectionInsets.left * CGFloat(itemsPerRow + 1)
        let availableWidth = viewWidth - paddingSpace
        let widthPerItem = availableWidth / CGFloat(itemsPerRow)

        return CGSize(width: widthPerItem, height: widthPerItem * heightFactor)
    }

    func getCollectionViewDimensions(viewWidth: CGFloat, count: Int) -> CGSize {
        let itemDimensions = getItemDimensions(viewWidth: viewWidth)

        let height =
            sectionInsets.top + sectionInsets.bottom +
            (itemDimensions.height + sectionInsets.left) * CGFloat(ceil(Double(count) / Double(itemsPerRow)))
            - sectionInsets.left

        return CGSize(width: viewWidth, height: height)
    }
}
