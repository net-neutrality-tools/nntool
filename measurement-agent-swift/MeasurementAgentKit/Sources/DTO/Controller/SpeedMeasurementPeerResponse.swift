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

/// Response DTO that contains a list of speed measurement peers.
public class SpeedMeasurementPeerResponse: Codable {

    /// The list of speed measurement peers.
    public var speedMeasurementPeers: [SpeedMeasurementPeer]

    ///
    enum CodingKeys: String, CodingKey {
        case speedMeasurementPeers = "peers"
    }

    /// This class describes a single speed measurement peer.
    public class SpeedMeasurementPeer: Codable {

        /// The measurement peer's public identifier which is sent back to server by the measurement agent.
        public var identifier: String

        /// The measurement peer's public name.
        public var name: String

        /// The measurement peer's public description.
        public var description: String?

        /// A flag indicating if this measurement peer is the default one.
        public var defaultPeer = false

        ///
        enum CodingKeys: String, CodingKey {
            case identifier
            case name
            case description
            case defaultPeer = "default"
        }
    }
}
