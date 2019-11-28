/*!
    \file QoSViewController.swift
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

import Foundation
import UIKit
import QoSKit
import CodableJSON

class QoSViewController: UIViewController {

    @IBOutlet private var loadButton: UIButton?
    @IBOutlet private var startButton: UIButton?
    @IBOutlet private var stopButton: UIButton?
    @IBOutlet private var clearButton: UIButton?
    
    @IBOutlet private var statusLabel: UILabel?
    @IBOutlet private var progressLabel: UILabel?
    @IBOutlet private var currentTypeLabel: UILabel?
    
    @IBOutlet private var kpisTextView: UITextView?

    private static let qosServiceAddress = JSON("peer-qos-de-01.net-neutrality.tools") // localhost
    private static let qosServicePort = JSON(5233) // 5233
    
    private static let objectives: QoSObjectives = [
        "TCP": [
            [
                "qostest" : "TCP",
                "concurrency_group" : 200,
                "qos_test_uid" : "201",
                "out_port" : 21,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            /*[ // qos-service on peer-qos-de-01.net-neutrality.tools doesn't bind to port 22
                "qostest" : "TCP",
                "concurrency_group" : 200,
                "qos_test_uid" : "202",
                "out_port" : 22,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],*/
            [
                "qostest" : "TCP",
                "concurrency_group" : 200,
                "qos_test_uid" : "203",
                "out_port" : 81,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "TCP",
                "concurrency_group" : 200,
                "qos_test_uid" : "204",
                "out_port" : 993,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "TCP",
                "concurrency_group" : 200,
                "qos_test_uid" : "205",
                "out_port" : 8077,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            /*[
                "qostest" : "TCP",
                "concurrency_group" : 200,
                "qos_test_uid" : "206",
                "in_port" : 15333,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ]*/
        ],
        "UDP": [
            [
                "qostest" : "UDP",
                "concurrency_group" : 201,
                "out_num_packets" : 1,
                "qos_test_uid" : "251",
                "out_port" : 53,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "UDP",
                "concurrency_group" : 201,
                "out_num_packets" : 2,
                "qos_test_uid" : "252",
                "out_port" : 123,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "UDP",
                "concurrency_group" : 201,
                "out_num_packets" : 3,
                "qos_test_uid" : "253",
                "out_port" : 500,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "UDP",
                "concurrency_group" : 201,
                "out_num_packets" : 4,
                "qos_test_uid" : "254",
                "out_port" : 4500,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "UDP",
                "concurrency_group" : 201,
                "out_num_packets" : 10,
                "qos_test_uid" : "255",
                "out_port" : 5060,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            /*[
                "qostest" : "UDP",
                "concurrency_group" : 201,
                "in_num_packets" : 5,
                "qos_test_uid" : "256",
                "in_port" : 12241,
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ]*/
        ],
        "ECHO_PROTOCOL": [
            [
                "qostest" : "ECHO_PROTOCOL",
                "concurrency_group" : 301,
                "qos_test_uid" : "301",
                "host" : QoSViewController.qosServiceAddress,
                "port" : "7",
                "payload" : "UDP payload",
                "protocol" : "udp"
            ],
            [
                "qostest" : "ECHO_PROTOCOL",
                "concurrency_group" : 302,
                "qos_test_uid" : "302",
                "host" : QoSViewController.qosServiceAddress,
                "port" : "7",
                "payload" : "TCP payload",
                "protocol" : "tcp"
            ]
        ],
    ]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        clearUi()
    }
    
    // MARK: UI
    
    @IBAction func loadButtonTouched(sender: AnyObject?) {
        // unused
    }
    
    @IBAction func startButtonTouched() {
        toggleButtons(true)
        clearUi()
        
        DispatchQueue.global(qos: .background).async {
            self.run()
        }
    }
    
    private func run() {
        let qosTaskExecutor = QoSTaskExecutor()
        qosTaskExecutor.delegate = self
        
        qosTaskExecutor.startWithObjectives(QoSViewController.objectives, token: "bbd1ee96-0779-4619-b993-bb4bf7089754_1528136454_3gr2gw9lVhtVONV0XO62Vamu/uw=")
    }
    
    @IBAction func stopButtonTouched(sender: AnyObject?) {
        // unused
    }
    
    @IBAction func clearButtonTouched(sender: AnyObject?) {
        toggleButtons(false)
        clearUi()
    }
    
    func toggleButtons(_ enable: Bool) {
        startButton?.isEnabled = !enable
        stopButton?.isEnabled = false //enable
        clearButton?.isEnabled = !enable
    }
    
    func clearUi() {
        statusLabel?.text = "-"
        progressLabel?.text = "-"
        currentTypeLabel?.text = "-"
        kpisTextView?.text = "This demo uses the demo qos-service deployed on peer-qos-de-01.net-neutrality.tools:5233. If you want to execute the QoS tests to a local instance, change the variables qosServiceAddress and qosServicePort accordining to your local setup. Furthermore, in this example, qos-service needs to bind to privileged TCP ports (21, 80, 993) and UDP ports (53, 123, 4500, 5060). \nFor the EchoProtocol tests please start a echo server on port 7 (both TCP and UDP)."
    }
}

extension QoSViewController: QoSTaskExecutorDelegate {
    
    func taskExecutorDidStop(_ taskExecutor: QoSTaskExecutor) {
        
    }
    
    func taskExecutorDidStart(_ taskExecutor: QoSTaskExecutor, withTaskGroups groups: [QoSTaskGroup]) {

    }
    
    func taskExecutorDidFail(_ taskExecutor: QoSTaskExecutor, withError error: Error?) {
        DispatchQueue.main.async {
            self.kpisTextView?.text = "failed"
            
            self.toggleButtons(false)
        }
    }
    
    func taskExecutorDidUpdateProgress(_ progress: Double, ofGroup group: QoSTaskGroup, totalProgress: Double) {
        DispatchQueue.main.async {
            self.statusLabel?.text = String(format: "Group %@: %d%%", group.key, Int(progress * 100))
            self.progressLabel?.text = String(format: "%d%%", Int(totalProgress * 100))
            self.currentTypeLabel?.text = group.key
        }
    }
    
    func taskExecutorDidFinishWithResult(_ result: [QoSTaskResult]) {
        let encoder = JSONEncoder()
        encoder.outputFormatting = .prettyPrinted
        
        var jsonString: String?
        if let jsonData = try? encoder.encode(result) {
            jsonString = String(data: jsonData, encoding: .utf8)
        }
        
        DispatchQueue.main.async {
            self.kpisTextView?.text = jsonString ?? "Error"
            
            self.toggleButtons(false)
        }
    }
}
