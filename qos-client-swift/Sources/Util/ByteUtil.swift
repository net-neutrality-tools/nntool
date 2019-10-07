import Foundation

public final class ByteUtil {

    // TODO: use MemoryLayout and only specify starting index. Also check for size of data.
    public class func convertData<T: FixedWidthInteger>(_ data: Data, to type: T.Type) -> T {
        return T.init(bigEndian: data.withUnsafeBytes { $0.load(as: type) })
    }
}
