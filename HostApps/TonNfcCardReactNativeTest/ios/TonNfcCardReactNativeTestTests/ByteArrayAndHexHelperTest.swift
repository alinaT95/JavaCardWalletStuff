//
//  ByteArrayAndHexHelperTest.swift
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 27.07.2020.
//

import XCTest
@testable import TonNfcCardReactNativeTest

class ByteArrayAndHexHelperTest: XCTestCase {
  
  override func setUpWithError() throws {
    // Put setup code here. This method is called before the invocation of each test method in the class.
  }
  
  override func tearDownWithError() throws {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
  }
  
  func testDigitalStrIntoAsciiUInt8Array() throws {
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: ""), [])
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: "0"), [0x30])
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: "1"), [0x31])
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: "12"), [0x31, 0x32])
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: "123"), [0x31, 0x32, 0x33])
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: "1234"), [0x31, 0x32, 0x33, 0x34])
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: "12345"), [0x31, 0x32, 0x33, 0x34, 0x35])
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: "123456"), [0x31, 0x32, 0x33, 0x34, 0x35, 0x36])
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: "1234567"), [0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37])
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: "12345678"), [0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38])
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: "123456789"), [0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39])
    XCTAssertEqual(ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: "0123456789"), [0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39])
  }
  
  func testPerformanceExample() throws {
    // This is an example of a performance test case.
    self.measure {
      // Put the code you want to measure the time of here.
    }
  }
  
}
