/***************************************************************************
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

public final class ByteUtil {

    // TODO: use MemoryLayout and only specify starting index. Also check for size of data.
    public class func convertData<T: FixedWidthInteger>(_ data: Data, to type: T.Type) -> T {
        return T.init(bigEndian: data.withUnsafeBytes { $0.load(as: type) })
    }
}
