// ios-app: MeasurementViewController.swift, created on 25.03.19
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

///
class MeasurementViewController: CustomNavigationBarViewController {

    // TODO: QoS view controller is a child view controller

    @IBOutlet private var qosMeasurementViewController: QoSMeasurementViewController?

    @IBOutlet private var progressInfoBar: ProgressInfoBar?
    @IBOutlet private var speedMeasurementGaugeView: SpeedMeasurementGaugeView?
    @IBOutlet private var speedMeasurementBasicResultView: SpeedMeasurementBasicResultView?
    
    private var measurementRunner: MeasurementRunner?

    private var progressAlert: UIAlertController?

    // MARK: - UI Code

    ///
    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()

        startMeasurement()
    }

    @IBAction func viewTapped() {
        let alert = UIAlertController(title: "Abort Measurement?", message: "Do you really want to abort the current measurement?", preferredStyle: .alert)

        alert.addAction(UIAlertAction(title: "Continue", style: .default, handler: nil))

        alert.addAction(UIAlertAction(title: "Abort Measurement", style: .destructive, handler: { _ in
            self.stopMeasurement()
        }))

        present(alert, animated: true, completion: nil)
    }

    @IBAction func measurementResultButtonTapped() {
        performSegue(withIdentifier: "TODO_measurement_result_view", sender: nil)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let identifier = segue.identifier else {
            return
        }

        switch identifier {
            // TODO: populate measurement result view controller

        default: break
        }
    }

    // MARK: - Measurement Code

    ///
    private func startMeasurement() {
        hideNavigationItems()

        progressInfoBar?.reset()
        speedMeasurementGaugeView?.reset()
        speedMeasurementBasicResultView?.reset()
        
        measurementRunner = MEASUREMENT_AGENT.newMeasurementRunner()
        // TODO: fail measurement if runner is nil (could be because agent is not registered)

        measurementRunner?.delegate = self
        measurementRunner?.startMeasurement()
    }

    private func stopMeasurement() {
        measurementRunner?.stopMeasurement()
        returnToHomeScreen()
    }

    private func returnToHomeScreen() {
        navigationController?.popToRootViewController(animated: false)
    }

    private func showMeasurementFailureAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)

        alert.addAction(UIAlertAction(title: "Retry", style: .default, handler: { _ in
            self.startMeasurement()
        }))

        alert.addAction(UIAlertAction(title: "Abort Measurement", style: .destructive, handler: { _ in
            self.returnToHomeScreen()
        }))

        present(alert, animated: true, completion: nil)
    }
}

extension MeasurementViewController: MeasurementRunnerDelegate {

    func measurementWillStartRequestingControlModel(_ runner: MeasurementRunner) {
        print("!^! measurementWillStartRequestingControlModel")

        DispatchQueue.main.async {
            self.progressAlert = UIAlertController.createLoadingAlert(title: "Initiating measurement")
            self.present(self.progressAlert!, animated: true, completion: nil)
        }
    }

    func measurementDidReceiveControlModel(_ runner: MeasurementRunner) {
        print("!^! measurementDidReceiveControlModel")

        DispatchQueue.main.async {
            self.progressAlert?.dismiss(animated: true) {
                self.progressAlert = nil
            }
        }
    }

    func measurementDidStart(_ runner: MeasurementRunner) {
        print("!^! did start")
    }

    func measurementDidStop(_ runner: MeasurementRunner) {
        self.progressAlert?.dismiss(animated: true) {
            self.progressAlert = nil
            self.returnToHomeScreen()
        }
    }

    func measurementDidFinish(_ runner: MeasurementRunner) {
        print("!^! did finish")
        
        DispatchQueue.main.async {
            self.showNavigationItems()
        }
    }

    func measurementDidFail(_ runner: MeasurementRunner) {
        print("!^! did fail")

        let presentFailureAlert = {
            self.showMeasurementFailureAlert(title: "Error", message: "TODO: Measurement Error")
        }

        DispatchQueue.main.async {
            if self.progressAlert != nil {
                self.progressAlert?.dismiss(animated: false) {
                    self.progressAlert = nil
                    presentFailureAlert()
                }
            } else {
                presentFailureAlert()
            }
        }
    }

    func measurementRunner(_ runner: MeasurementRunner, willStartProgramWithName name: String, implementation: /*AnyProgram<Any>*/ProgramProtocol) {
        print("!^! willStart program \(name)")

        (implementation as? IASProgram)?.delegate = self
        (implementation as? QoSProgram)?.delegate = self
    }

    func measurementRunner(_ runner: MeasurementRunner, didFinishProgramWithName name: String, implementation: /*AnyProgram<Any>*/ProgramProtocol) {
        print("!^! didFinish program \(name)")

        (implementation as? IASProgram)?.delegate = nil
        (implementation as? QoSProgram)?.delegate = nil
    }
}

extension MeasurementViewController: IASProgramDelegate {
    
    func iasMeasurement(_ ias: IASProgram, didStartPhase phase: SpeedMeasurementPhase) {
        print("did start phase: \(phase)")
        
        DispatchQueue.main.async {
            self.progressInfoBar?.setRightValue(value: "", newIcon: phase.icon)
            self.speedMeasurementGaugeView?.setActivePhase(phase: phase)
        }
    }
    
    func iasMeasurement(_ ias: IASProgram, didMeasurePrimaryValue value: Double, inPhase phase: SpeedMeasurementPhase) {
        DispatchQueue.main.async {
            
            switch phase {
            case .rtt:
                let msValue = value / Double(NSEC_PER_MSEC)
                let msString = String(format: "%.3f", msValue)
            
                self.progressInfoBar?.setRightValue(value: "\(msString) ms") // TODO: translation, unit from phase enum?
                self.speedMeasurementBasicResultView?.setText(msString, forPhase: phase)
            case .download, .upload:
                let mbpsValue = value / 1_000_000.0
                let mbpsString = String(format: "%.3f", mbpsValue)
                
                self.progressInfoBar?.setRightValue(value: "\(mbpsString) Mbit/s") // TODO: translation, unit from phase enum?
                self.speedMeasurementBasicResultView?.setText(mbpsString, forPhase: phase)
            default: break//reset()
            }
        }
    }
}

extension MeasurementViewController: QoSProgramDelegate {
    
}
