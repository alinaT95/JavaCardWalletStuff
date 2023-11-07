//
//  CardCoinManagerNfcApi.swift
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 23.07.2020.
//

import Foundation
import PromiseKit
import Foundation
import CoreNFC

@available(iOS 13.0, *)
class CardCoinManagerNfcApi: TonNfcApi {
  func getSeVersion(resolve: @escaping RCTPromiseResolveBlock,  reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getSeVersion()
  }
  
  func getCsn(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getCsn()
  }
  
  func getDeviceLabel(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getDeviceLabel()
  }
  
  func setDeviceLabel(label: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard label.count == 2 * CoinManagerApduCommands.LABEL_LENGTH &&  label.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Device label must be a nonempty hex string of length \(2 * CoinManagerApduCommands.LABEL_LENGTH).", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    setDeviceLabel(label: label)
  }
  
  func getMaxPinTries(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getMaxPinTries()
  }
  
  func getRemainingPinTries(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getRemainingPinTries()
  }
  
  func getRootKeyStatus(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getRootKeyStatus()
  }
  
  func getAvailableMemory(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getAvailableMemory()
  }
  
  func getAppsList(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getAppsList()
  }
  
  func generateSeed(pin: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard pin.count == TonWalletAppletConstants.PIN_SIZE &&  pin.isNumeric == true else {
      ErrorHelper.callRejectWith(errMsg :  "Pin must be a numeric string of length \(TonWalletAppletConstants.PIN_SIZE).", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    generateSeed(pin: pin)
  }
  
  func resetWallet(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    resetWallet()
  }
  
  func changePin(oldPin: String, newPin: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard oldPin.count == TonWalletAppletConstants.PIN_SIZE && newPin.count == TonWalletAppletConstants.PIN_SIZE && newPin.isNumeric == true && oldPin.isNumeric == true else{
      ErrorHelper.callRejectWith(errMsg :  "Pin must be a numeric string of length \(TonWalletAppletConstants.PIN_SIZE).", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    changePin(oldPin: oldPin, newPin: newPin)
  }
  
  func getSeVersion() {
    print("Start APDU: getSeVersion")
    executeCoinManagerOperationAndSendHex(apdu: CoinManagerApduCommands.GET_SE_VERSION_APDU)
  }
  
  func getCsn() {
    print("Start APDU: getCsn")
    executeCoinManagerOperationAndSendHex(apdu: CoinManagerApduCommands.GET_CSN_APDU)
  }
  
  func getDeviceLabel() {
    print("Start APDU: getDeviceLabel")
    executeCoinManagerOperationAndSendHex(apdu: CoinManagerApduCommands.GET_DEVICE_LABEL_APDU)
  }
  
  func getMaxPinTries() {
    print("Start APDU: getMaxPinTries")
    executeCoinManagerOperationAndSendNumber(apdu: CoinManagerApduCommands.GET_PIN_TLT_APDU)
  }
  
  func getRemainingPinTries() {
    print("Start APDU: getRemainingPinTries")
    executeCoinManagerOperationAndSendNumber(apdu: CoinManagerApduCommands.GET_PIN_RTL_APDU)
  }
  
  func getAvailableMemory() {
    print("Start APDU: getAvailableMemory")
    executeCoinManagerOperationAndSendHex(apdu: CoinManagerApduCommands.GET_AVAILABLE_MEMORY_APDU)
  }
  
  func getAppsList() {
    print("Start APDU: getAppsList")
    executeCoinManagerOperationAndSendHex(apdu: CoinManagerApduCommands.GET_APPLET_LIST_APDU)
  }
  
  func setDeviceLabel(label: String) {
    print("Start APDU: setDeviceLabel")
    print("Got label: " + label)
    executeCoinManagerOperation(apduPromise: CoinManagerApduCommands.getSetDeviceLabelApduPromise(label: ByteArrayAndHexHelper.hexStrToUInt8Array(hexStr: label)))
  }
  
  func getRootKeyStatus() {
    print("Start APDU: getRootKeyStatus")
    apduRunner.setCardOperation(cardOperation: { () in
      self.apduRunner.sendCoinManagerAppletApdu(apduCommand: CoinManagerApduCommands.GET_ROOT_KEY_STATUS_APDU)
        .then{(response : Data)  -> Promise<String> in
          return Promise<String> { promise in
            promise.fulfill(response.hexEncodedString() == CoinManagerApduCommands.POSITIVE_ROOT_KEY_STATUS ? "generated" : "not generated")
          }
      }
    })
    apduRunner.startScan()
  }
  
  func resetWallet() {
    print("Start APDU: resetWallet")
    executeCoinManagerOperation(apdu: CoinManagerApduCommands.RESET_WALLET_APDU)
  }
  
  func generateSeed(pin: String) {
    print("Start APDU: genGenerateSeed")
    print("Got pin:" + pin)
    executeCoinManagerOperation(apduPromise: CoinManagerApduCommands.getGenSeedApduPromise(pin: ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: pin)))
  }
  
  func changePin(oldPin: String, newPin: String) {
    print("Start APDU: changePin" )
    print("Got oldPin:" + oldPin)
    print("Got newPin:" + newPin)
    let oldPin = ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: oldPin)
    let newPin = ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: newPin)
    executeCoinManagerOperation(apduPromise: CoinManagerApduCommands.getChangePinApduPromise(oldPin: oldPin, newPin: newPin))
  }
  
  private func executeCoinManagerOperationAndSendHex(apdu: NFCISO7816APDU) {
    apduRunner.setCardOperation(cardOperation: { () in
      self.apduRunner.sendCoinManagerAppletApdu(apduCommand: apdu)
        .then{(response : Data)  -> Promise<String> in
          return Promise<String> { promise in promise.fulfill(response.hexEncodedString())}
      }
    })
    apduRunner.startScan()
  }
  
  private func executeCoinManagerOperationAndSendNumber(apdu: NFCISO7816APDU) {
    apduRunner.setCardOperation(cardOperation: { () in
      self.apduRunner.sendCoinManagerAppletApdu(apduCommand: apdu)
        .then{(response : Data)  -> Promise<String> in
          return Promise<String> { promise in promise.fulfill(String(response.bytes[0]))}
      }
    })
    apduRunner.startScan()
  }
  
  private func executeCoinManagerOperation(apdu: NFCISO7816APDU) {
    apduRunner.setCardOperation(cardOperation: { () in
      self.apduRunner.sendCoinManagerAppletApdu(apduCommand: apdu)
        .then{(response : Data)  -> Promise<String> in
          return Promise<String> { promise in promise.fulfill("done")}
      }
    })
    apduRunner.startScan()
  }
  
  private func executeCoinManagerOperation(apduPromise: Promise<NFCISO7816APDU>) {
    apduRunner.setCardOperation(cardOperation: { () in
      self.apduRunner.sendCoinManagerAppletApdu(apduCommandPromise: apduPromise)
        .then{(response : Data)  -> Promise<String> in
          return Promise<String> { promise in promise.fulfill("done")}
      }
    })
    apduRunner.startScan()
  }
}
