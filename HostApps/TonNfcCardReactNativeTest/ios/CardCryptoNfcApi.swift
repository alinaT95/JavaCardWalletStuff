//
//  CardEd25519NfcApi.swift
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 23.07.2020.
//

import Foundation
import PromiseKit
import Foundation
import CoreNFC


@available(iOS 13.0, *)
class CardCryptoNfcApi: TonNfcApi {
  func getPublicKeyForDefaultPath(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getPublicKeyForDefaultPath()
  }
  
  func getPublicKey(hdIndex: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard  hdIndex.isNumeric == true else {
      //todo: add len verification
      ErrorHelper.callRejectWith(errMsg :  "hdIndex must be a numeric string", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getPublicKey(hdIndex: hdIndex)
  }
  
  func verifyPin(pin: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard pin.count == TonWalletAppletConstants.PIN_SIZE &&  pin.isNumeric == true else {
      ErrorHelper.callRejectWith(errMsg :  "Pin must be a numeric string of length \(TonWalletAppletConstants.PIN_SIZE).", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    verifyPin(pin: pin)
  }
  
  func signForDefaultHdPath(data: String, pin : String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard pin.count == TonWalletAppletConstants.PIN_SIZE &&  pin.isNumeric == true else {
      ErrorHelper.callRejectWith(errMsg :  "Pin must be a numeric string of length \(TonWalletAppletConstants.PIN_SIZE).", reject: reject)
      return
    }
    guard data.count <= TonWalletAppletConstants.DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH && data.count > 0 &&  data.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Data for signing must be a nonempty hex string of even length <= \(TonWalletAppletConstants.DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH).", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    signForDefaultHdPath(data: data, pin: pin)
  }
  
  func sign(data: String, hdIndex: String, pin : String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard pin.count == TonWalletAppletConstants.PIN_SIZE &&  pin.isNumeric == true else {
      ErrorHelper.callRejectWith(errMsg :  "Pin must be a numeric string of length \(TonWalletAppletConstants.PIN_SIZE).", reject: reject)
      return
    }
    guard data.count <= TonWalletAppletConstants.DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH && data.count > 0 &&  data.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Data for signing must be a nonempty hex string of even length <= \(TonWalletAppletConstants.DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH).", reject: reject)
      return
    }
    guard  hdIndex.isNumeric == true else {
      //todo: add len verification
      ErrorHelper.callRejectWith(errMsg :  "hdIndex must be a numeric string", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    sign(data: data, hdIndex: hdIndex, pin: pin)
  }
  
  func getPublicKeyForDefaultPath() {
    print("Start card operation: getPublicKeyForDefaultPath")
    executeTonWalletOperationAndSendHex(apdu: TonWalletAppletApduCommands.GET_PUB_KEY_WITH_DEFAULT_PATH_APDU)
  }
  
  func getPublicKey(hdIndex: String) {
    print("Start card operation: getPublicKey" )
    print("Got hdIndex:" + hdIndex)
    //todo: hdIndex conversion check
    let hdIndex = ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: hdIndex)
    executeTonWalletOperationAndSendHex(apduPromise: TonWalletAppletApduCommands.getPublicKeyApduPromise(ind: hdIndex))
  }
  
  func verifyPin(pin: String) {
    print("Start card operation: verifyPin" )
    print("Got PIN:" + pin)
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndVerifyPin(pin: pin)
        .then{(response : Data)  -> Promise<String> in
          Promise<String> { promise in promise.fulfill("done")}
      }
    })
    apduRunner.startScan()
  }
  
  func signForDefaultHdPath(data: String, pin: String) {
    print("Start card operation: signForDefaultHdPath" )
    print("Got Data:" + data)
    print("Got PIN:" + pin)
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndVerifyPin(pin: pin)
        .then{(response : Data) -> Promise<Data> in
          self.getSaultPromise()
      }
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendApdu(apduCommandPromise:
          TonWalletAppletApduCommands.getSignShortMessageWithDefaultPathApduPromise(dataForSigning:
            ByteArrayAndHexHelper.hexStrToUInt8Array(hexStr: data), sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
        return Promise<String> { promise in promise.fulfill(response.hexEncodedString())}
      }
    })
    apduRunner.startScan()
  }
  
  func sign(data: String, hdIndex: String, pin: String) {
    print("Start card operation: sign" )
    print("Got Data:" + data)
    print("Got hdIndex:" + hdIndex)
    print("Got PIN:" + pin)
    //todo: check correct conversion of hdIndex into byte array
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndVerifyPin(pin: pin)
        .then{(response : Data) -> Promise<Data> in
          self.getSaultPromise()
      }
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendApdu(apduCommandPromise:
          TonWalletAppletApduCommands.getSignShortMessageApduPromise(dataForSigning: ByteArrayAndHexHelper.hexStrToUInt8Array(hexStr: data), ind: ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: hdIndex), sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
        return Promise<String> { promise in promise.fulfill(response.hexEncodedString())}
      }
    })
    apduRunner.startScan()
  }
  
  private func selectTonAppletAndVerifyPin(pin: String) -> Promise<Data> {
    self.selectTonAppletAndGetSault()
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getVerifyPinApduPromise(pinBytes: ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: pin), sault: sault.bytes))
    }
  }
}
