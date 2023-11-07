//
//  Data.swift
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 23.07.2020.
//

import Foundation

extension Data {
  public var bytes: [UInt8]
  {
    return [UInt8](self)
  }
  
  func hexEncodedString() -> String {
    return map { String(format: "%02hhx", $0) }.joined()
  }
}
