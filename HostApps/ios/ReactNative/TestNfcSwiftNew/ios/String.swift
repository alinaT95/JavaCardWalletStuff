//
//  String.swift
//  TestNfcSwiftNew
//
//  Created by Alina Alinovna on 16.06.2020.
//

import Foundation

extension String: LocalizedError {
  public var errorDescription: String? { return self }
  
  var isNumeric: Bool {
      guard self.count > 0 else { return false }
      let nums: Set<Character> = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
      return Set(self).isSubset(of: nums)
  }
  
  var isHex: Bool {
    guard self.count > 0 && self.count % 2 == 0 else { return false }
      let nums: Set<Character> = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F" ]
      return Set(self).isSubset(of: nums)
  }
}

