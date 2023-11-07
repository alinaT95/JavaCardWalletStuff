import Foundation
import CoreNFC
import PromiseKit

@available(iOS 13.0, *)
@objc(CardKeyChainNfcModule)
class CardKeyChainNfcModule: NSObject {
  var nfcApi: CardKeyChainNfcApi = CardKeyChainNfcApi()
  
  @objc
  func getKeyChainDataAboutAllKeys(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getKeyChainDataAboutAllKeys()
  }
  
  @objc
  func getKeyChainInfo(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getKeyChainInfo()
  }
  
  @objc
  func finishDeleteKeyFromKeyChainAfterInterruption(_ keyMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard keyMac.count == 2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE && keyMac.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "keyHmac is a hex string of length  = \(2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE).", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    //  nfcApi.finishDeleteKeyFromKeyChainAfterInterruption(keyMac : keyMac)
  }
  
  @objc
  func deleteKeyFromKeyChain(_ keyMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard keyMac.count == 2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE && keyMac.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "keyHmac is a hex string of length  = \(2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE).", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.deleteKeyFromKeyChain(keyMac : keyMac)
  }
  
  @objc
  func getKeyFromKeyChain(_ keyMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard keyMac.count == 2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE && keyMac.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "keyHmac is a hex string of length  = \(2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE).", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getKeyFromKeyChain(keyMac : keyMac)
  }
  
  @objc
  func addKeyIntoKeyChain(_ newKey: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard newKey.count > 0 && newKey.count <= 2 * TonWalletAppletConstants.MAX_KEY_SIZE_IN_KEYCHAIN && newKey.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "New key must be a nonempty hex string of even length <= \(2 * TonWalletAppletConstants.MAX_KEY_SIZE_IN_KEYCHAIN).", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.addKeyIntoKeyChain(newKey : newKey)
  }
  
  @objc
  func changeKeyInKeyChain(_ newKey: String, oldKeyHMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard oldKeyHMac.count == 2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE && oldKeyHMac.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "keyHmac is a hex string of length  = \(2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE).", reject: reject)
      return
    }
    guard newKey.count > 0 && newKey.count <= 2 * TonWalletAppletConstants.MAX_KEY_SIZE_IN_KEYCHAIN && newKey.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "New key must be a nonempty hex string of even length <= \(2 * TonWalletAppletConstants.MAX_KEY_SIZE_IN_KEYCHAIN).", reject: reject)
      return
    }
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.changeKeyInKeyChain(newKey : newKey, oldKeyHMac: oldKeyHMac)
  }
  
  
  
  @objc
  func resetKeyChain(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.resetKeyChain()
  }
  
  @objc
  func getNumberOfKeys(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getNumberOfKeys()
  }
  
  @objc
  func getOccupiedStorageSize(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getOccupiedStorageSize()
  }
  
  @objc
  func getFreeStorageSize(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getFreeStorageSize()
  }
  
  @objc
  func checkKeyHmacConsistency(_ keyHmac: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.checkKeyHmacConsistency(keyHmac: keyHmac)
  }
  
  @objc
  func checkAvailableVolForNewKey(_ keySize: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.checkAvailableVolForNewKey(keySize: keySize)
  }
  
  @objc
  func getIndexAndLenOfKeyInKeyChain(_ keyHmac: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getIndexAndLenOfKeyInKeyChain(keyHmac: keyHmac)
  }
  
  @objc
  func getHmac(_ index: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getHmac(index: index)
  }
  
}
