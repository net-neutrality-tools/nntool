// inspired by https://github.com/appscape/open-rmbt-ios/blob/master/Sources/RMBTQoSWebTest.m

import Foundation
import UIKit
import nntool_shared_swift

///
class WebsiteTaskRunner: NSObject {

    ///
    private var status: QoSTaskStatus = .unknown

    ///
    private var webView: UIWebView?

    ///
    private var startedAt: UInt64 = 0

    ///
    private var duration: UInt64 = 0

    ///
    private var semaphore = DispatchSemaphore(value: 0)

    ///
    private var requestCount = 0

    ///
    private var uid: UInt?

    //

    ///
    init(uid: UInt) {
        super.init()

        self.uid = uid
    }

    ///
    func run(urlObj: URL, timeout: UInt64) -> (WebsiteTaskResultEntry?, UInt64?, QoSTaskStatus) {
        DispatchQueue.main.sync {
            self.webView = UIWebView()
            self.webView?.delegate = self

            let request = NSMutableURLRequest(url: urlObj)
            self.tagRequest(request: request)

            self.webView?.loadRequest(request as URLRequest)

            logger.debug("AFTER LOAD REQUEST")
        }

        let semaphoneTimeout = DispatchTime.now() + DispatchTimeInterval.nanoseconds(Int(truncatingIfNeeded: timeout))

        status = semaphore.wait(timeout: semaphoneTimeout) == .timedOut ? .timeout : .ok

        duration = TimeHelper.currentTimeNs() - startedAt

        let protocolResult = WebsiteTaskUrlProtocol.queryResultWithTag(tag: "\(String(describing: uid))")

        DispatchQueue.main.sync {
            webView?.stopLoading()
            webView = nil
        }

        return (protocolResult, duration, status)
    }

    ///
    func tagRequest(request: NSMutableURLRequest) {
        WebsiteTaskUrlProtocol.tagRequest(request: request, withValue: "\(String(describing: uid))")
    }
}

// MARK: UIWebViewDelegate methods

extension WebsiteTaskRunner: UIWebViewDelegate {

    ///
    func webView(_ webView: UIWebView, shouldStartLoadWith request: URLRequest, navigationType: UIWebView.NavigationType) -> Bool {
        assert(status == .unknown)

        if let r = request as? NSMutableURLRequest {
            tagRequest(request: r)
        }

        if startedAt == 0 {
            startedAt = TimeHelper.currentTimeNs()
        }

        return true
    }

    ///
    func webViewDidStartLoad(_ webView: UIWebView) {
        requestCount += 1
    }

    ///
    func webViewDidFinishLoad(_ webView: UIWebView) {
        maybeDone()
    }

    ///
    func webView(_ webView: UIWebView, didFailLoadWithError error: Error) {
        status = .error
        maybeDone()
    }

    ///
    func maybeDone() {
        if status == .timeout {
            // Already timed out
            return
        }

        assert(requestCount > 0)

        requestCount -= 1

        if requestCount == 0 {
            semaphore.signal()
        }
    }
}
