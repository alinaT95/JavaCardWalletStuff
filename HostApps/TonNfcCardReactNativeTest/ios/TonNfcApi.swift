//
//  TonNfcApi.swift
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 23.07.2020.
//

import PromiseKit
import Foundation
import CoreNFC

@available(iOS 13.0, *)
class TonNfcApi: NSObject{
  static var commonSecret:Data?
  static var initialVector:Data?
  static var password:Data?
  static var hashOfCommonSecret:Data?
  static var keyForHmac: Data?
  
  var apduRunner = ApduRunner()
  
  var resolve: RCTPromiseResolveBlock?
  var reject: RCTPromiseRejectBlock?
  
  override init() {
  }
  
  static func setPassword(password : String) {
    self.password = ByteArrayAndHexHelper.hex(from: password)
  }
  
  static func setInitialVector(initialVector : String) {
    self.initialVector = ByteArrayAndHexHelper.hex(from: initialVector)
  }
  
  static func setCommonSecret(commonSecret : String) {
    self.commonSecret = ByteArrayAndHexHelper.hex(from: commonSecret)
  }
  
  func setPromiseStuff(resolveBlock: @escaping RCTPromiseResolveBlock, rejectBlock: @escaping RCTPromiseRejectBlock){
    self.resolve = resolveBlock
    self.reject = rejectBlock
    apduRunner.setReject(rejectBlock: rejectBlock)
    apduRunner.setResolve(resolveBlock: resolveBlock)
  }
  
  func getTonAppletState(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getTonAppletState()
  }
  
  func getSault(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getSault()
  }
  
  func setKeyForHmac(password: String, commonSecret : String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard password.count == 2 * TonWalletAppletConstants.PASSWORD_SIZE && password.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Password must be a hex string of even length \(2 * TonWalletAppletConstants.PASSWORD_SIZE). ", reject: reject)
      return
    }
    guard commonSecret.count == 2 * TonWalletAppletConstants.COMMON_SECRET_SIZE && commonSecret.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Common secret must be a hex string of even length \(2 * TonWalletAppletConstants.COMMON_SECRET_SIZE). ", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    TonNfcApi.setKeyForHmac(password : password, commonSecret : commonSecret)
  }
  
  static func setKeyForHmac(password : String, commonSecret : String) {
    print("Start operation: setKeyForHmac" )
    print("Got password:" + password)
    print("Got commonSecret:" + commonSecret)
    self.setPassword(password: password)
    self.setCommonSecret(commonSecret: commonSecret)
    setKeyForHmac()
  }
  
  static func setKeyForHmac() {
    let keyForHmac = HmacHelper.computeHmac(key : self.password!.hash(), data : self.commonSecret!)
    HmacHelper.setKey(key: keyForHmac)
  }
  
  func getSault() {
    print("Start APDU: getSault")
    executeTonWalletOperationAndSendHex(apdu: TonWalletAppletApduCommands.GET_SAULT_APDU)
  }
  
  func getTonAppletState() {
    print("Start card operation: getTonAppletState")
    apduRunner.setCardOperation(cardOperation: { () in
      self.apduRunner.sendTonWalletAppletApdu(apduCommand: TonWalletAppletApduCommands.GET_APP_INFO_APDU)
        .then{(response : Data)  -> Promise<String> in
          Promise<String> { promise in promise.fulfill(TonWalletAppletConstants.APPLET_STATES[response.bytes[0]]!)}
      }
    })
    apduRunner.startScan()
  }
  
  func executeTonWalletOperationAndSendHex(apdu: NFCISO7816APDU) {
    apduRunner.setCardOperation(cardOperation: { () in
      self.apduRunner.sendTonWalletAppletApdu(apduCommand: apdu)
        .then{(response : Data)  -> Promise<String> in
          return Promise<String> { promise in promise.fulfill(response.hexEncodedString())}
      }
    })
    apduRunner.startScan()
  }
  
  func executeTonWalletOperationAndSendHex(apduPromise: Promise<NFCISO7816APDU>) {
    apduRunner.setCardOperation(cardOperation: { () in
      self.apduRunner.sendTonWalletAppletApdu(apduCommandPromise: apduPromise)
        .then{(response : Data)  -> Promise<String> in
          return Promise<String> { promise in promise.fulfill(response.hexEncodedString())}
      }
    })
    apduRunner.startScan()
  }
  
  func executeTonWalletOperation(apduPromise: Promise<NFCISO7816APDU>) {
    apduRunner.setCardOperation(cardOperation: { () in
      self.apduRunner.sendTonWalletAppletApdu(apduCommandPromise: apduPromise)
        .then{(response : Data)  -> Promise<String> in
          return Promise<String> { promise in promise.fulfill("done")}
      }
    })
    apduRunner.startScan()
  }
  
  func selectTonAppletAndGetSault() -> Promise<Data> {
    self.apduRunner.sendTonWalletAppletApdu(apduCommand:
      TonWalletAppletApduCommands.GET_SAULT_APDU)
  }
  
  func getSaultPromise() -> Promise<Data> {
    self.apduRunner.sendApdu(apduCommand:
      TonWalletAppletApduCommands.GET_SAULT_APDU)
  }
  
  func checkStateAndGetSault() -> Promise<Data> {
    self.apduRunner.sendAppletApduAndCheckAppletState(apduCommand:
      TonWalletAppletApduCommands.GET_SAULT_APDU)
  }
}
