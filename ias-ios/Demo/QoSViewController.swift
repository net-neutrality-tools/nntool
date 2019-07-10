import Foundation
import UIKit
import QoSKit

class QoSViewController: UIViewController {

    @IBOutlet private var loadButton: UIButton?
    @IBOutlet private var startButton: UIButton?
    @IBOutlet private var stopButton: UIButton?
    @IBOutlet private var clearButton: UIButton?
    
    @IBOutlet private var statusLabel: UILabel?
    @IBOutlet private var progressLabel: UILabel?
    @IBOutlet private var currentTypeLabel: UILabel?
    
    @IBOutlet private var kpisTextView: UITextView?

    private static let qosServiceAddress = "localhost"
    private static let qosServicePort = "5233"
    
    private static let objectives: QoSObjectives = [
        "TCP": [
            /*[
                "qostest" : "TCP",
                "concurrency_group" : "200",
                "qos_test_uid" : "200",
                "out_port" : "21",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "TCP",
                "concurrency_group" : "200",
                "qos_test_uid" : "201",
                "out_port" : "22",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "TCP",
                "concurrency_group" : "200",
                "qos_test_uid" : "202",
                "out_port" : "80",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "TCP",
                "concurrency_group" : "200",
                "qos_test_uid" : "203",
                "out_port" : "993",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],*/
            [
                "qostest" : "TCP",
                "concurrency_group" : "200",
                "qos_test_uid" : "204",
                "out_port" : "4000",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "TCP",
                "concurrency_group" : "200",
                "qos_test_uid" : "205",
                "out_port" : "5000",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "TCP",
                "concurrency_group" : "200",
                "qos_test_uid" : "206",
                "in_port" : "15333",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ]
        ],
        "ECHO_PROTOCOL": [
            [
                "qostest" : "ECHO_PROTOCOL",
                "echo_protocol_objective_payload" : "UPD payload",
                "concurrency_group" : "301",
                "echo_protocol_objective_protocol" : "udp",
                "server_port" : "7",
                "qos_test_uid" : "301",
                "server_addr" : "127.0.0.1"
            ],
            [
                "qostest" : "ECHO_PROTOCOL",
                "echo_protocol_objective_payload" : "TCP payload",
                "concurrency_group" : "302",
                "echo_protocol_objective_protocol" : "tcp",
                "server_port" : "7",
                "qos_test_uid" : "302",
                "server_addr" : "127.0.0.1"
            ]
        ],
        "UDP": [
            /*[
                "qostest" : "UDP",
                "concurrency_group" : "201",
                "out_num_packets" : "1",
                "qos_test_uid" : "250",
                "out_port" : "123",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "UDP",
                "concurrency_group" : "201",
                "out_num_packets" : "1",
                "qos_test_uid" : "251",
                "out_port" : "500",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],*/
            [
                "qostest" : "UDP",
                "concurrency_group" : "201",
                "out_num_packets" : "1",
                "qos_test_uid" : "252",
                "out_port" : "4500",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "UDP",
                "concurrency_group" : "201",
                "out_num_packets" : "1",
                "qos_test_uid" : "253",
                "out_port" : "5060",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ],
            [
                "qostest" : "UDP",
                "concurrency_group" : "201",
                "in_num_packets" : "1",
                "qos_test_uid" : "254",
                "in_port" : "12241",
                "server_addr" : QoSViewController.qosServiceAddress,
                "server_port" : QoSViewController.qosServicePort
            ]
        ]
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
        statusLabel?.text = ""
        progressLabel?.text = ""
        currentTypeLabel?.text = ""
        kpisTextView?.text = "To run this demo please start a local instance of qos-service on port 5233 and enable UDP ports 123, 500, 4500, 5060 in config.properties. Furthermore, in this example, qos-service needs to bind to privileged TCP ports (21, 22, 80, 993). \nFor the EchoProtocol tests please start a echo server on port 7 (both TCP and UDP)."
    }
}

extension QoSViewController: QoSTaskExecutorDelegate {
    
    func taskExecutorDidStart(_ taskExecutor: QoSTaskExecutor, withTaskGroups groups: [QoSTaskGroup]) {

    }
    
    func taskExecutorDidFail(_ taskExecutor: QoSTaskExecutor, withError error: Error?) {
        DispatchQueue.main.async {
            self.kpisTextView?.text = "failed"
            
            self.toggleButtons(false)
        }
    }
    
    func taskExecutorDidUpdateProgress(_ progress: Double, ofGroup group: QoSTaskGroup, totalProgress: Double) {

    }
    
    func taskExecutorDidFinishWithResult(_ result: [QoSTaskResult]) {
        DispatchQueue.main.async {
            self.kpisTextView?.text = try! String(data: JSONSerialization.data(withJSONObject: result, options: .prettyPrinted), encoding: .utf8)!
            
            self.toggleButtons(false)
        }
    }
}
