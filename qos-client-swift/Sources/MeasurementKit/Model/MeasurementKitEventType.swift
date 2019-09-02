import Foundation

enum MeasurementKitEventType: String {
    case statusQueued = "status.queued"
    case statusStarted = "status.started"
    case statusMeasurementStart = "status.measurement_start"
    case statusMeasurementDone = "status.measurement_done"
    case statusMeasurementSubmission = "status.measurement_submission"
    case statusEnd = "status.end"
    case statusReportCreate = "status.report_create"

    case statusUpdateWebsites = "status.update.websites"

    case failureStartup = "failure.startup"
    case failureMeasurementSubmission = "failure.measurement_submission"
    case failureReportCreate = "failure.report_create"

    case measurement// = "measurement"
    case progress = "status.progress"
    case geoIpLookup = "status.geoip_lookup"
    case log// = "log"

    case bugJsonDump = "bug.json_dump"
}
