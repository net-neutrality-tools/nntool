import Foundation

class DnsTask: QoSTask {

    override var result: QoSTaskResult {
        var r = super.result

        r["dns"] = "1"

        return r
    }

    override func main() {
        print("run DNS \(uid)")
        sleep(5)
        print("finished DNS \(uid)")
    }
}
