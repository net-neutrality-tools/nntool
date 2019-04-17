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
            let resultList = self.runTasks()
            
            DispatchQueue.main.async {
                resultList.forEach { print($0) }
                
                self.kpisTextView?.text = "{\n" + resultList.joined(separator: ",\n") + "\n}"
                
                self.toggleButtons(false)
            }
        }
    }
    
    private func runTasks() -> [String] {
        var resultList = [String]()
        
        let controlConnection = ControlConnection(
            host: "localhost",
            port: 5233,
            timeoutS: 3,
            token: "bbd1ee96-0779-4619-b993-bb4bf7089754_1528136454_3gr2gw9lVhtVONV0XO62Vamu/uw="
        )
        
        /////////
        
        // tcp port outgoing example
        resultList.append(contentsOf: runTcpPortTests(controlConnection: controlConnection, ports: [21, 22, 80, 993]))
        
        // tcp port incoming example
        /*resultList.append(
            runTcpPortTest(
                config: TcpPortTaskConfigurationBuilder()
                    .timeoutNs(3 * NSEC_PER_SEC)
                    .serverAddress("localhost")
                    .serverPort(5233)
                    .qosTestUid(2)
                    .portIn(8078)
                    .build(),
                controlConnection: controlConnection
            )
        )*/
        
        /////////
        
        // udp port outgoing example
        resultList.append(contentsOf: runUdpPortTests(controlConnection: controlConnection, ports: [123, 500, 4500, 5060]))

        // udp port incoming example
        /*resultList.append(
            runUdpPortTest(
                config: UdpPortTaskConfigurationBuilder()
                    .timeoutNs(3 * NSEC_PER_SEC)
                    .serverAddress("localhost")
                    .serverPort(5233)
                    .portIn(5061)
                    .qosTestUid(4)
                    .packetCountIn(5)
                    .delayNs(20 * NSEC_PER_MSEC)
                    .build(),
                controlConnection: controlConnection
            )
        )*/
        
        /////////
        
        // echo protocol tcp example
        resultList.append(
            runEchoProtocolTest(config: EchoProtocolTaskConfigurationBuilder()
                .timeoutNs(3 * NSEC_PER_SEC)
                .host("localhost")
                .port(7)
                .protocolType(.tcp)
                .payload("PING_tcp")
                .build()
            )
        )
        
        // echo protocol udp example
        resultList.append(
            runEchoProtocolTest(config: EchoProtocolTaskConfigurationBuilder()
                .timeoutNs(3 * NSEC_PER_SEC)
                .host("localhost")
                .port(7)
                .protocolType(.udp)
                .payload("PING_udp")
                .build()
            )
        )
        
        /////////
        
        return resultList
    }
    
    private func runTcpPortTests(controlConnection: ControlConnection, ports: [UInt16], outgoing: Bool = true) -> [String] {
        var resultList = [String]()
        
        ports.forEach { port in
            let builder = TcpPortTaskConfigurationBuilder()
                .timeoutNs(3 * NSEC_PER_SEC)
                .serverAddress("localhost")
                .serverPort(5233)
                .qosTestUid(Int(port))
                
            if outgoing {
                builder.portOut(port)
            } else {
                builder.portIn(port)
            }

            resultList.append(runTcpPortTest(config: builder.build(), controlConnection: controlConnection))
        }
        
        return resultList
    }
    
    private func runUdpPortTests(controlConnection: ControlConnection, ports: [UInt16], outgoing: Bool = true) -> [String] {
        var resultList = [String]()
        
        ports.forEach { port in
            let builder = UdpPortTaskConfigurationBuilder()
                .timeoutNs(3 * NSEC_PER_SEC)
                .serverAddress("localhost")
                .serverPort(5233)
                .qosTestUid(Int(port))
                .delayNs(20 * NSEC_PER_MSEC)
            
            if outgoing {
                builder.portOut(port)
                builder.packetCountOut(5)
            } else {
                builder.portIn(port)
                builder.packetCountIn(5)
            }
            
            resultList.append(runUdpPortTest(config: builder.build(), controlConnection: controlConnection))
        }
        
        return resultList
    }
    
    private func runTcpPortTest(config: TcpPortTaskConfiguration, controlConnection: ControlConnection) -> String {
        let tcpPortTaskExecutor = TcpPortTaskExecutor(config: config, controlConnection: controlConnection)
        return /*tcpPortTaskExecutor.runTask().toJSONString(prettyPrint: true) ??*/ "{}"
    }
    
    private func runUdpPortTest(config: UdpPortTaskConfiguration, controlConnection: ControlConnection) -> String {
        let udpPortTaskExecutor = UdpPortTaskExecutor(config: config, controlConnection: controlConnection)
        return /*udpPortTaskExecutor.runTask().toJSONString(prettyPrint: true) ??*/ "{}"
    }
    
    private func runEchoProtocolTest(config: EchoProtocolTaskConfiguration) -> String {
        let echoProtocolTaskExecutor = EchoProtocolTaskExecutor(config: config)
        return /*echoProtocolTaskExecutor.runTask().toJSONString(prettyPrint: true) ??*/ "{}"
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
