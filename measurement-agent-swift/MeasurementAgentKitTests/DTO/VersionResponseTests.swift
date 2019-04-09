// MeasurementAgentKit: VersionResponseTests.swift, created on 29.03.19
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

import XCTest
import Nimble
@testable import MeasurementAgentKit

class VersionResponseTests: XCTestCase {

    override func setUp() {

    }

    override func tearDown() {

    }

    func testSerialize() {
        let vr = VersionResponse()

        vr.controllerServiceVersion = "1.0.0"
        vr.collectorServiceVersion = "2.1.0"
        vr.resultServiceVersion = "3.0.4"
        vr.mapServiceVersion = "4.2.2"
        vr.statisticServiceVersion = "0.1.0"

        guard let jsonObject = try? JSONSerialization.jsonObject(with: JSONEncoder().encode(vr), options: []) as? [String: String] else {
            XCTFail("encoding and decoding failed")
            return
        }

        expect(jsonObject["controller_service_version"]).to(equal(vr.controllerServiceVersion!))
        expect(jsonObject["collector_service_version"]).to(equal(vr.collectorServiceVersion!))
        expect(jsonObject["result_service_version"]).to(equal(vr.resultServiceVersion!))
        expect(jsonObject["map_service_version"]).to(equal(vr.mapServiceVersion!))
        expect(jsonObject["statistic_service_version"]).to(equal(vr.statisticServiceVersion!))
    }

    func testDeserialize() {
        let controllerServiceVersion = "0.0.1"
        let collectorServiceVersion = "1.2.3"
        let resultServiceVersion = "17.22.10"
        let mapServiceVersion = "0.20.1"
        let statisticServiceVersion = "10.1.2"

        let jsonString = """
        {
            "controller_service_version": "\(controllerServiceVersion)",
            "collector_service_version": "\(collectorServiceVersion)",
            "result_service_version": "\(resultServiceVersion)",
            "map_service_version": "\(mapServiceVersion)",
            "statistic_service_version": "\(statisticServiceVersion)"
        }
        """

        do {
            let vr = try JSONDecoder().decode(VersionResponse.self, from: jsonString.data(using: .utf8)!)

            expect(vr).notTo(beNil())

            expect(vr.controllerServiceVersion).to(equal(controllerServiceVersion))
            expect(vr.collectorServiceVersion).to(equal(collectorServiceVersion))
            expect(vr.resultServiceVersion).to(equal(resultServiceVersion))
            expect(vr.mapServiceVersion).to(equal(mapServiceVersion))
            expect(vr.statisticServiceVersion).to(equal(statisticServiceVersion))
        } catch {
            print(error)
            XCTFail("decoding failed")
        }
    }
}
