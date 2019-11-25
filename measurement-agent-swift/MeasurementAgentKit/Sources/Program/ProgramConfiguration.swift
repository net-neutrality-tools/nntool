// MeasurementAgentKit: ProgramConfiguration.swift, created on 16.04.19
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

public /*struct*/class ProgramTask {
    public var name: String
    
    public var localizedName: String?
    public var localizedDescription: String?
    
    public init(name: String, localizedName: String? = nil, localizedDescription: String? = nil) {
        self.name = name
        self.localizedName = localizedName
        self.localizedDescription = localizedDescription
    }
    
    public /*mutating*/ func updateLocalization(localizedName: String? = nil, localizedDescription: String? = nil) {
        self.localizedName = localizedName
        self.localizedDescription = localizedDescription
    }
}

///
public struct ProgramConfiguration {

    public typealias InstantiationBlock = (LmapTaskDto, ProgramConfiguration /* TODO: use other object(s) */) throws -> (ProgramProtocol)

    public var name: String
    public var version: String

    public var isEnabled: Bool! {
        didSet {
            logger.debug("isEnabled changed: \(oldValue) -> \(isEnabled), storing")
            UserDefaults.standard.set(isEnabled, forKey: settingsPrefix + "isEnabled")
            logger.debug("isEnabled after storing \(isEnabled)")
        }
    }

    public var availableTasks: [ProgramTask]
    public var enabledTasks: Set<String>! {
        didSet {
            logger.debug("enabledTasks changed: \(oldValue) -> \(enabledTasks), storing")
            UserDefaults.standard.set([String](enabledTasks), forKey: settingsPrefix + "enabledTasks")
            logger.debug("enabledTasks after storing \(enabledTasks)")
        }
    }

    public var newInstance: InstantiationBlock

    private var settingsPrefix: String {
        return "program.\(name.lowercased())."
    }

    public init(name: String, version: String, isEnabled: Bool, availableTasks: [ProgramTask] = [], enabledTasks: Set<String> = [], newInstance: @escaping InstantiationBlock) {
        self.name = name
        self.version = version
        //self.isEnabled = isEnabled

        self.availableTasks = availableTasks

        self.newInstance = newInstance

        if let en = UserDefaults.standard.object(forKey: settingsPrefix + "isEnabled") as? Bool {
            self.isEnabled = en
        } else {
            self.isEnabled = true
        }

        if let et = UserDefaults.standard.stringArray(forKey: settingsPrefix + "enabledTasks") {
            self.enabledTasks = Set<String>(et)
        } else {
            self.enabledTasks = Set<String>(availableTasks.map { $0.name })
        }
    }

    public func isTaskEnabled(_ name: String) -> Bool {
        guard availableTasks.map({ $0.name }).contains(name) else {
            return false
        }

        return enabledTasks.contains(name)
    }

    public mutating func enableTask(name: String, enable: Bool = true) {
        guard availableTasks.map({ $0.name }).contains(name) else {
            return
        }

        if enable {
            enabledTasks.insert(name)
        } else {
            enabledTasks.remove(name)
        }
    }

    public mutating func toggleTask(_ name: String) {
        enableTask(name: name, enable: !isTaskEnabled(name))
    }
}
