//
//  Ed25519SignTest.swift
//  TonNfcCardReactNativeTestTests
//
//  Created by Alina Alinovna on 28.07.2020.
//

import XCTest

import ed25519swift


class Ed25519SignTest: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() throws {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
      let keyPair = Ed25519.generateKeyPair()
      let sig = Data(_ : Ed25519.sign(message: [0x00, 0x00], secretKey: keyPair.secretKey)).toHexString()
  }

    func testPerformanceExample() throws {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }

}
