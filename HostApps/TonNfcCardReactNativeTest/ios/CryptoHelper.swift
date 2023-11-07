//
//  CryptoHelper.swift
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 23.07.2020.
//

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
    return Data(bytes: hashBytes, count: Int(CC_SHA256_DIGEST_LENGTH))
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
  static var key : Data?
  
  static func setKey(key : Data) {
    self.key = key
  }
  
  static func computeHmac(data : Data) throws -> Data {
    if let key = key {
      return data.authenticationCode(secretKey: key)
    }
    else {
      throw "Key for hmac is not set."
    }
  }
  
  static func computeHmac(key : Data, data : Data) -> Data {
    data.authenticationCode(secretKey: key)
  }
}

struct AES {
  private let key: Data
  private let iv: Data
  
  
  /*
   init?(key: String, iv: String) {
   guard key.count == kCCKeySizeAES128 || key.count == kCCKeySizeAES256, let keyData = key.data(using: .utf8) else {
   debugPrint("Error: Failed to set a key.")
   return nil
   }
   
   guard iv.count == kCCBlockSizeAES128, let ivData = iv.data(using: .utf8) else {
   debugPrint("Error: Failed to set an initial vector.")
   return nil
   }
   
   
   self.key = keyData
   self.iv  = ivData
   }*/
  
  init?(key: Data, iv: Data) {
    guard key.count == kCCKeySizeAES128  else {
      debugPrint("Error: Failed to set a key.")
      return nil
    }
    
    guard iv.count == kCCBlockSizeAES128 else {
      debugPrint("Error: Failed to set an initial vector.")
      return nil
    }
    
    self.key = key
    self.iv  = iv
  }
  
  /*func encrypt(string: String) -> Data? {
   return crypt(data: string.data(using: .utf8), option: CCOperation(kCCEncrypt))
   }*/
  
  func encrypt(msg: Data) -> Data? {
    return crypt(data: msg, option: CCOperation(kCCEncrypt))
  }
  
  /*func decrypt(data: Data?) -> String? {
   guard let decryptedData = crypt(data: data, option: CCOperation(kCCDecrypt)) else { return nil }
   return String(bytes: decryptedData, encoding: .utf8)
   }*/
  
  func crypt(data: Data?, option: CCOperation) -> Data? {
    guard let data = data else { return nil }
    
    let cryptLength = data.count + kCCBlockSizeAES128
    var cryptData   = Data(count: cryptLength)
    
    print(" cryptLength = " + String(cryptLength))
    print(" data.count = " + String(data.count) )
    
    let keyLength = key.count
    let options   = CCOptions(kCCOptionPKCS7Padding)
    
    var bytesLength = Int(0)
    
    let status = cryptData.withUnsafeMutableBytes { cryptBytes in
      data.withUnsafeBytes { dataBytes in
        iv.withUnsafeBytes { ivBytes in
          key.withUnsafeBytes { keyBytes in
            CCCrypt(option, CCAlgorithm(kCCAlgorithmAES), options, keyBytes.baseAddress, keyLength, ivBytes.baseAddress, dataBytes.baseAddress, data.count, cryptBytes.baseAddress, cryptLength, &bytesLength)
          }
        }
      }
    }
    
    guard UInt32(status) == UInt32(kCCSuccess) else {
      debugPrint("Error: Failed to crypt data. Status \(status)")
      return nil
    }
    cryptData.removeSubrange(data.count..<cryptData.count)
    return cryptData
  }
}

