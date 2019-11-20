// ios-app: QoSGroupOverviewCell.swift, created on 05.08.19
/*******************************************************************************
 * Copyright 2019 Benjamin Pucher (alladin-IT GmbH)
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
import MeasurementAgentKit

class QoSGroupOverviewCell: UITableViewCell {

    private static let reuseIdentifier = "qos_group_collectionview_cell"

    @IBOutlet private var collectionView: UICollectionView?

    var flowLayoutConfig: CollectionViewFlowLayoutConfig?

    var evaluatedQoSResults: [QoSGroupResult]?

    override func awakeFromNib() {
        super.awakeFromNib()

        collectionView?.dataSource = self
        collectionView?.delegate = self

        collectionView?.reloadData()
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        // Remove separator views from this cell
        for view in subviews where view != contentView {
            view.removeFromSuperview()
        }
    }
}

extension QoSGroupOverviewCell: UICollectionViewDataSource {

    func numberOfSections(in collectionView: UICollectionView) -> Int {
        return 1
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return evaluatedQoSResults?.count ?? 0
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let item = evaluatedQoSResults?[indexPath.row] else {
            return UICollectionViewCell()
        }

        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: QoSGroupOverviewCell.reuseIdentifier, for: indexPath) as? QoSGroupCollectionViewCell else {
            return UICollectionViewCell()
        }

        cell.type = item.type

        cell.iconLabel?.text = item.icon
        cell.groupNameLabel?.text = item.localizedName

        let successfulTasks = item.tasks.filter { $0.isSuccessful() }.count

        if successfulTasks == item.tasks.count {
            cell.statusIconLabel?.text = IconFont.check.rawValue
            cell.statusIconLabel?.textColor = UIColor.green
        } else if successfulTasks == 0 {
            cell.statusIconLabel?.text = IconFont.cross.rawValue
            cell.statusIconLabel?.textColor = UIColor.red
        } else {
            cell.statusIconLabel?.text = IconFont.check.rawValue
            cell.statusIconLabel?.textColor = UIColor.yellow
        }

        cell.statusLabel?.text = "\(successfulTasks)/\(item.tasks.count)"

        return cell
    }
}

extension QoSGroupOverviewCell: UICollectionViewDelegate {

}

///
extension QoSGroupOverviewCell: UICollectionViewDelegateFlowLayout {

    ///
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return flowLayoutConfig?.getItemDimensions(viewWidth: frame.width) ?? CGSize(width: 0, height: 0)
    }

    ///
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, insetForSectionAt section: Int) -> UIEdgeInsets {
        return flowLayoutConfig?.sectionInsets ?? UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
    }

    ///
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return flowLayoutConfig?.sectionInsets.left ?? 0
    }
}
