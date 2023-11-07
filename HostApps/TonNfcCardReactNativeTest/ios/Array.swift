//
//  Array.swift
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 23.07.2020.
//

import Foundation

extension Array {
  
  subscript (range r: Range<Int>) -> Array {
    return Array(self[r])
  }
  
  
  subscript (range r: ClosedRange<Int>) -> Array {
    return Array(self[r])
  }
}
