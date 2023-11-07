import Foundation
import CoreNFC
import PromiseKit

@available(iOS 13.0, *)
@objc(CardActivationNfcModule)
class CardActivationNfcModule: NSObject {
  var nfcApi: CardActivationNfcApi = CardActivationNfcApi()
  @objc
  func setKeyForHmac(_ password: String, commonSecret : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard password.count == 2 * TonWalletAppletConstants.PASSWORD_SIZE && password.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Password must be a hex string of even length \(2 * TonWalletAppletConstants.PASSWORD_SIZE). ", reject: reject)
      return
    }
    guard commonSecret.count == 2 * TonWalletAppletConstants.COMMON_SECRET_SIZE && commonSecret.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Common secret must be a hex string of even length \(2 * TonWalletAppletConstants.COMMON_SECRET_SIZE). ", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    TonNfcApi.setKeyForHmac(password : password, commonSecret : commonSecret)
  }
  
  @objc
  func turnOnWallet(_ newPin: String, password: String, commonSecret : String, initialVector : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard password.count == 2 * TonWalletAppletConstants.PASSWORD_SIZE && password.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Password must be a hex string of even length \(2 * TonWalletAppletConstants.PASSWORD_SIZE). ", reject: reject)
      return
    }
    guard initialVector.count == 2 * TonWalletAppletConstants.IV_SIZE && initialVector.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Initial vector must be a hex string of even length \(2 * TonWalletAppletConstants.IV_SIZE). ", reject: reject)
      return
    }
    guard commonSecret.count == 2 * TonWalletAppletConstants.COMMON_SECRET_SIZE && commonSecret.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Common secret must be a hex string of even length \(2 * TonWalletAppletConstants.COMMON_SECRET_SIZE). ", reject: reject)
      return
    }
    guard newPin.count == TonWalletAppletConstants.PIN_SIZE &&  newPin.isNumeric == true else {
      ErrorHelper.callRejectWith(errMsg :  "Pin must be a numeric string of length \(TonWalletAppletConstants.PIN_SIZE).", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.turnOnWallet(newPin : newPin, password : password, commonSecret : commonSecret, initialVector : initialVector)
  }
  
  @objc
  func verifyPassword(_ password: String, initialVector: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard password.count == 2 * TonWalletAppletConstants.PASSWORD_SIZE && password.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Password must be a hex string of length \(2 * TonWalletAppletConstants.PASSWORD_SIZE). ", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.verifyPassword(password: password, initialVector: initialVector)
  }
  
  @objc
  func verifyHashOfEncryptedPassword(_ password: String, initialVector : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard password.count == 2 * TonWalletAppletConstants.PASSWORD_SIZE && password.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Password must be a hex string of even length \(2 * TonWalletAppletConstants.PASSWORD_SIZE). ", reject: reject)
      return
    }
    guard initialVector.count == 2 * TonWalletAppletConstants.IV_SIZE && initialVector.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Initial vector must be a hex string of even length \(2 * TonWalletAppletConstants.IV_SIZE). ", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.verifyHashOfEncryptedPassword(password : password, initialVector: initialVector)
  }
  
  @objc
  func verifyHashOfCommonSecret(_ commonSecret: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard commonSecret.count == 2 * TonWalletAppletConstants.COMMON_SECRET_SIZE && commonSecret.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Common secret must be a hex string of even length \(2 * TonWalletAppletConstants.COMMON_SECRET_SIZE). ", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.verifyHashOfCommonSecret(commonSecret : commonSecret)
  }
  
  @objc
  func getHashOfCommonSecret(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getHashOfCommonSecret()
  }
  
  @objc
  func getHashOfEncryptedPassword(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getHashOfEncryptedPassword()
  }
}
