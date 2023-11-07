
import Foundation
import CoreNFC
import PromiseKit

@available(iOS 13.0, *)
@objc(CardCryptoNfcModule)
class CardCryptoNfcModule: NSObject {
  var nfcApi: CardEd25519NfcApi = CardEd25519NfcApi()
  
  @objc
  func getPublicKeyForDefaultPath(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getPublicKeyForDefaultPath()
  }
  
  @objc
  func getPublicKey(_ hdIndex: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard  hdIndex.isNumeric == true else {
      //todo: add len verification
      ErrorHelper.callRejectWith(errMsg :  "hdIndex must be a numeric string", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getPublicKey(hdIndex: hdIndex)
  }
  
  @objc
  func verifyPin(_ pin: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard pin.count == TonWalletAppletConstants.PIN_SIZE &&  pin.isNumeric == true else {
      ErrorHelper.callRejectWith(errMsg :  "Pin must be a numeric string of length \(TonWalletAppletConstants.PIN_SIZE).", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.verifyPin(pin: pin)
  }
  
  @objc
  func signForDefaultHdPath(_ data: String, pin : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard pin.count == TonWalletAppletConstants.PIN_SIZE &&  pin.isNumeric == true else {
      ErrorHelper.callRejectWith(errMsg :  "Pin must be a numeric string of length \(TonWalletAppletConstants.PIN_SIZE).", reject: reject)
      return
    }
    guard data.count <= TonWalletAppletConstants.DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH && data.count > 0 &&  data.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Data for signing must be a nonempty hex string of even length <= \(TonWalletAppletConstants.DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH).", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.signForDefaultHdPath(data: data, pin: pin)
  }
  
  @objc
  func sign(_ data: String, hdIndex: String, pin : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
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
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.sign(data: data, hdIndex: hdIndex, pin: pin)
  }
}
