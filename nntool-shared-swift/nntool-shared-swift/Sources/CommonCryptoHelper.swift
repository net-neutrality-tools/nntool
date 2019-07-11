// MeasurementAgentKit: JsonHelper.swift, created on 02.07.19
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
import CommonCrypto

public class CommonCryptoHelper {
    
    public class func md5(_ data: Data) -> String {
        var digest = [UInt8](repeating: 0, count: Int(CC_MD5_DIGEST_LENGTH))
        
        _ = data.withUnsafeBytes {
            CC_MD5($0.baseAddress, UInt32(data.count), &digest)
        }
        
        var md5String = ""
        
        for byte in digest {
            md5String += String(format: "%02x", UInt8(byte))
        }
        
        return md5String
    }
}
