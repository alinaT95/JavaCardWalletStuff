//
//  HexHelper.swift
//  TestNfcSwiftNew
//
//  Created by Alina Alinovna on 14.06.2020.
//


import Foundation

class HexHelper {
  static func digitalStrIntoHex(digitalStr : String) -> [UInt8]{
    var bytes = [UInt8]()
    for s in digitalStr {
      if let byte = UInt8(String(s)/*, where (byte >= 0 && byte <= 9)*/) {
        bytes.append(0x30 + byte)
      }
    }
    return bytes
  }
  
  static func hexStrToUInt8Array(hexStr: String) -> [UInt8] {
    var startIndex = hexStr.startIndex
    return (0..<hexStr.count/2).compactMap { _ in
      let endIndex = hexStr.index(after: startIndex)
      defer { startIndex = hexStr.index(after: endIndex) }
        return UInt8(hexStr[startIndex...endIndex], radix: 16)
    }
  }
  
  static func hex(from string: String) -> Data {
      .init(stride(from: 0, to: string.count, by: 2).map {
          string[string.index(string.startIndex, offsetBy: $0) ... string.index(string.startIndex, offsetBy: $0 + 1)]
      }.map {
          UInt8($0, radix: 16)!
      })
  }
  
  static func makeShort(src: [UInt8], srcOff : Int) -> Int {
     // if (srcOff < 0  ||  src.length < (srcOff + 2))
       //   throw new IllegalArgumentException("Bad args!");
      let b0 = Int(src[srcOff] & 0xFF);
      let b1 = Int(src[srcOff + 1] & 0xFF);
      return (b0 << 8) + b1
  }
  
}
