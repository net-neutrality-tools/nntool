/***************************************************************************
* Copyright 2017 appscape gmbh
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
***************************************************************************/

import Foundation
import WKWebViewWithURLProtocol

///
class WebsiteTaskResultEntry: CustomStringConvertible {

    ///
    var status = -1

    ///
    var rx = 0

    ///
    init() {

    }

    ///
    init(status: Int, rx: Int) {
        self.status = status
        self.rx = rx
    }

    ///
    var description: String {
        return "[status: \(status), rx: \(rx)]"
    }
}

///
class WebsiteTaskUrlProtocol: URLProtocol, NSURLConnectionDelegate, NSURLConnectionDataDelegate {

    ///
    private static var results: [String: WebsiteTaskResultEntry]!

    ///
    private static let WEBSITE_TEST_TAG_KEY = "tag"

    ///
    private static let WEBSITE_TEST_HANDLED_KEY = "handled"

    ///
    private var _connection: NSURLConnection?

    //

    ///
    class func start() {
        assert(results == nil)

        results = [String: WebsiteTaskResultEntry]()

        let registerBlock = {
            URLProtocol.wk_registerScheme("http")
            URLProtocol.wk_registerScheme("https")
        }

        if Thread.isMainThread {
            registerBlock()
        } else {
            DispatchQueue.main.sync {
                registerBlock()
            }
        }

        URLProtocol.registerClass(WebsiteTaskUrlProtocol.self)
    }

    ///
    class func stop() {
        URLProtocol.unregisterClass(WebsiteTaskUrlProtocol.self)
        results = nil
    }

    ///
    class func tagRequest(request: NSMutableURLRequest, withValue value: String) {
        URLProtocol.setProperty(value, forKey: WEBSITE_TEST_TAG_KEY, in: request)
    }

    ///
    class func queryResultWithTag(tag: String) -> WebsiteTaskResultEntry? {
        //print(results)

        if results == nil {
            return nil
        }

        return results[tag]
    }

    // MARK: URLProtocol

    ///
    override open class func canInit(with request: URLRequest) -> Bool {
        let tag = URLProtocol.property(forKey: WEBSITE_TEST_TAG_KEY, in: request) as? String
        let handled = URLProtocol.property(forKey: WEBSITE_TEST_HANDLED_KEY, in: request) as? Bool ?? false

        if let url = request.mainDocumentURL?.absoluteString {
            if handled {
                return false
            }

            if let t = tag {
                if !results.keys.contains(t) {
                    let entry = WebsiteTaskResultEntry()

                    results[url] = entry
                    results[t] = entry
                }

                return true
            } else {
                return results[url] != nil
            }
        }

        return false
    }

    ///
    override open class func canonicalRequest(for request: URLRequest) -> URLRequest {
        return request
    }

    ///
    override open var cachedResponse: CachedURLResponse? {
        return nil
    }

    ///
    override open class func requestIsCacheEquivalent(_ a: URLRequest, to b: URLRequest) -> Bool {
        return super.requestIsCacheEquivalent(a, to: b)
    }

    //

    ///
    override func stopLoading() {
        _connection?.cancel()
    }

    ///
    override func startLoading() {
        if let handledRequest = (request as NSURLRequest).mutableCopy() as? NSMutableURLRequest {
            handledRequest.cachePolicy = .reloadIgnoringCacheData

            URLProtocol.setProperty(true, forKey: WebsiteTaskUrlProtocol.WEBSITE_TEST_HANDLED_KEY, in: handledRequest)

            _connection = NSURLConnection(request: handledRequest as URLRequest, delegate: self)
        }
    }

    ///
    func connection(_ connection: NSURLConnection, didReceive response: URLResponse) {
        if  let currentUrlString = connection.currentRequest.url?.absoluteString,
            let mainUrlString = connection.currentRequest.mainDocumentURL?.absoluteString,
            let res = WebsiteTaskUrlProtocol.results,
            let entry = res[currentUrlString],
            let r = response as? HTTPURLResponse,
            currentUrlString == mainUrlString {

            entry.status = r.statusCode
        }

        client?.urlProtocol(self, didReceive: response, cacheStoragePolicy: .notAllowed)
    }

    ///
    func connection(_ connection: NSURLConnection, willSend request: URLRequest, redirectResponse response: URLResponse?) -> URLRequest? {
        if let r = response {
            if  let origUrl = connection.originalRequest.mainDocumentURL?.absoluteString,
                let curUrl = request.url?.absoluteString,
                let res = WebsiteTaskUrlProtocol.results,
                let entry = res[origUrl] {

                WebsiteTaskUrlProtocol.results[curUrl] = entry
            }

            if let unhandledRequest = (request as NSURLRequest).mutableCopy() as? NSMutableURLRequest {
                URLProtocol.removeProperty(forKey: WebsiteTaskUrlProtocol.WEBSITE_TEST_HANDLED_KEY, in: unhandledRequest)
                client?.urlProtocol(self, wasRedirectedTo: unhandledRequest as URLRequest, redirectResponse: r)
            }

            _connection?.cancel()
            return nil
        }

        return request
    }

    ///
    func connection(_ connection: NSURLConnection, didReceive data: Data) {
        if  let url = connection.originalRequest.mainDocumentURL?.absoluteString,
            let res = WebsiteTaskUrlProtocol.results,
            let entry = res[url] {

            entry.rx += data.count
        }

        client?.urlProtocol(self, didLoad: data)
        _connection = nil
    }

    ///
    func connection(_ connection: NSURLConnection, didFailWithError error: Error) {
        client?.urlProtocol(self, didFailWithError: error)
    }

    ///
    func connectionDidFinishLoading(_ connection: NSURLConnection) {
        client?.urlProtocolDidFinishLoading(self)
    }
}
