
import Foundation
import CommonCrypto

extension Data {
    func authenticationCode(secretKey: Data) -> Data {
        let hashBytes = UnsafeMutablePointer<UInt8>.allocate(capacity:Int(CC_SHA256_DIGEST_LENGTH))
        defer { hashBytes.deallocate() }
        withUnsafeBytes { (bytes) -> Void in
            secretKey.withUnsafeBytes { (secretKeyBytes) -> Void in
                CCHmac(CCHmacAlgorithm(kCCHmacAlgSHA256), secretKeyBytes, secretKey.count, bytes, count, hashBytes)
            }
        }
        return Data(bytes: hashBytes, count: Int(CC_SHA256_DIGEST_LENGTH) /*algorithm.digestLength*/)
    }
    
    func hash() -> Data {
        let hashBytes = UnsafeMutablePointer<UInt8>.allocate(capacity: Int(CC_SHA256_DIGEST_LENGTH))
        defer { hashBytes.deallocate() }
        withUnsafeBytes { (buffer) -> Void in
            CC_SHA256(buffer.baseAddress!, CC_LONG(buffer.count), hashBytes)
        }
        return Data(bytes: hashBytes, count: Int(CC_SHA256_DIGEST_LENGTH))
    }
    
}

class HmacHelper {
    static func computeHmac(key : Data, data : Data) -> Data {
        data.authenticationCode(secretKey: key)
    }
}
