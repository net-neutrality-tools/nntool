import Foundation
import QoSKitDNS
import CodableJSON
import nntool_shared_swift

class DnsTask: QoSTask {

    private var host: String
    private var record: String
    private var resolver: String?

    private var dnsResolutionTimeNs: UInt64?
    private var rcodeStr: String?
    private var entries: [JSON]?

    ///
    enum CodingKeys4: String, CodingKey {
        case host
        case record
        case resolver
    }

    override var statusKey: String? {
        return "dns_result_info"
    }

    override var objectiveTimeoutKey: String? {
        return "dns_objective_timeout"
    }

    private static let recordDict = [
        "A": ns_t_a,
        "AAAA": ns_t_aaaa,
        "MX": ns_t_mx,
        "CNAME": ns_t_cname
    ]

    override var result: QoSTaskResult {
        var r = super.result

        r["dns_objective_host"] = JSON(host)
        r["dns_objective_dns_record"] = JSON(record)
        r["dns_objective_resolver"] = JSON(resolver)

        r["dns_result_status"] = JSON(rcodeStr)
        r["dns_result_entries_found"] = JSON(entries?.count)
        r["dns_result_entries"] = JSON(entries)

        return r
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        host = try container.decode(String.self, forKey: .host)
        resolver = try container.decodeIfPresent(String.self, forKey: .resolver)
        record = try container.decode(String.self, forKey: .record)

        try super.init(from: decoder)
    }

    override func taskMain() {
        guard let dnsRecord = DnsTask.recordDict[record] else {
            status = .error
            return
        }

        var res = __res_9_state()
        guard res_9_ninit(&res) == 0 else {
            status = .error
            return
        }

        res.retry = 1
        res.retrans = max(1, Int32(timeoutS))

        // set custom resolver if present
        if let r = resolver {
            var addr = in_addr()
            inet_aton(r.cString(using: .ascii), &addr)

            res.nsaddr_list.0.sin_addr = addr
            res.nsaddr_list.0.sin_family = sa_family_t(AF_INET) // TODO: support ipv6 name servers
            res.nsaddr_list.0.sin_port = in_port_t(53).bigEndian // TODO: support other dns server port
            res.nscount = 1
        }

        if isCancelled {
            return
        }

        var answer = [CUnsignedChar](repeating: 0, count: Int(NS_PACKETSZ))

        let preDnsResolutionTimeNs = TimeHelper.currentTimeNs()

        let len: CInt = res_9_nquery(&res, host.cString(using: .ascii), Int32(ns_c_in.rawValue), Int32(dnsRecord.rawValue), &answer, Int32(answer.count))

        dnsResolutionTimeNs = TimeHelper.currentTimeNs() - preDnsResolutionTimeNs

        if len == -1 {
            if h_errno == HOST_NOT_FOUND {
                rcodeStr = "NXDOMAIN"
            } else if h_errno == TRY_AGAIN {
                status = .timeout
            }
        } else {
            var handle = res_9_ns_msg()
            res_9_ns_initparse(&answer, len, &handle)

            let rcode = res_9_ns_msg_getflag(handle, Int32(ns_f_rcode.rawValue))
            if let rcodeCStr = res_9_p_rcode(rcode), let rcodeSwiftString = String(cString: rcodeCStr, encoding: .ascii) {
                rcodeStr = rcodeSwiftString
            } else {
                rcodeStr = "UNKNOWN" // or ERROR?
            }

            let answerCount = handle._counts.1
            if answerCount > 0 {
                var rr = res_9_ns_rr()

                entries = [JSON]()

                for i in 0..<answerCount {
                    if isCancelled {
                        return
                    }

                    if res_9_ns_parserr(&handle, ns_s_an, Int32(i), &rr) == 0 {
                        var entry = [String: JSON]()

                        entry["dns_result_ttl"] = JSON(rr.ttl)

                        let uint32_rr_type = UInt32(rr.type)

                        if uint32_rr_type == ns_t_a.rawValue {
                            var buf = [Int8](repeating: 0, count: Int(INET_ADDRSTRLEN + 1)) // why +1?

                            if inet_ntop(AF_INET, rr.rdata, &buf, socklen_t(INET_ADDRSTRLEN)) != nil {
                                entry["dns_result_address"] = JSON(String(cString: buf, encoding: .ascii))
                            }
                        } else if uint32_rr_type == ns_t_aaaa.rawValue {
                            var buf = [Int8](repeating: 0, count: Int(INET6_ADDRSTRLEN + 1)) // why +1?

                            if inet_ntop(AF_INET6, rr.rdata, &buf, socklen_t(INET6_ADDRSTRLEN)) != nil {
                                entry["dns_result_address"] = JSON(String(cString: buf, encoding: .ascii))
                            }
                        } else if uint32_rr_type == ns_t_mx.rawValue || uint32_rr_type == ns_t_cname.rawValue {
                            var buf = [Int8](repeating: 0, count: Int(NS_MAXDNAME))

                            if res_9_ns_name_uncompress(handle._msg, handle._eom, rr.rdata, &buf, buf.count) != -1 {
                                entry["dns_result_address"] = JSON(String(cString: buf, encoding: .ascii))

                                if uint32_rr_type == ns_t_mx.rawValue {
                                    entry["dns_result_priority"] = JSON(res_9_ns_get16(rr.rdata))
                                }
                            }
                        }

                        taskLogger.debug("Adding entry \(entry)")

                        entries?.append(JSON(entry))
                    }
                }
            }
        }

        res_9_ndestroy(&res)

        taskLogger.debug("finished DNS \(uid)")
    }
}
