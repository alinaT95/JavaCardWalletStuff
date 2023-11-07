import Foundation
import CoreNFC
import PromiseKit

@available(iOS 13.0, *)
@objc(CardCoinManagerNfcModule)
class CardCoinManagerNfcModule: NSObject {
  var nfcApi: CardCoinManagerNfcApi = CardCoinManagerNfcApi()
  
  @objc
  func getSeVersion(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getSeVersion()
  }
  
  @objc
  func getCsn(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getCsn()
  }
  
  @objc
  func getDeviceLabel(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getDeviceLabel()
  }
  
  @objc
  func setDeviceLabel(_ label: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard label.count == 2 * CoinManagerApduCommands.LABEL_LENGTH &&  label.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Device label must be a nonempty hex string of length \(2 * CoinManagerApduCommands.LABEL_LENGTH).", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.setDeviceLabel(label: label)
  }
  
  @objc
  func getMaxPinTries(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getMaxPinTries()
  }
  
  @objc
  func getRemainingPinTries(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getRemainingPinTries()
  }
  
  @objc
  func getRootKeyStatus(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getRootKeyStatus()
  }
  
  @objc
  func getAvailableMemory(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getAvailableMemory()
  }
  
  @objc
  func getAppsList(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getAppsList()
  }
  
  @objc
  func generateSeed(_ pin: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard pin.count == TonWalletAppletConstants.PIN_SIZE &&  pin.isNumeric == true else {
      ErrorHelper.callRejectWith(errMsg :  "Pin must be a numeric string of length \(TonWalletAppletConstants.PIN_SIZE).", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.generateSeed(pin: pin)
  }
  
  @objc
  func resetWallet(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.resetWallet()
  }
  
  @objc
  func changePin(_ oldPin: String, newPin: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard oldPin.count == TonWalletAppletConstants.PIN_SIZE && newPin.count == TonWalletAppletConstants.PIN_SIZE && newPin.isNumeric == true && oldPin.isNumeric == true else{
      ErrorHelper.callRejectWith(errMsg :  "Pin must be a numeric string of length \(TonWalletAppletConstants.PIN_SIZE).", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.changePin(oldPin: oldPin, newPin: newPin)
  }
  
}
