//
//  NfcCardModule.swift
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 23.07.2020.
//

import Foundation
import CoreNFC
import PromiseKit

@available(iOS 13.0, *)
@objc(NfcCardModule)
class NfcCardModule: NSObject {
  var cardCoinManagerNfcApi: CardCoinManagerNfcApi = CardCoinManagerNfcApi()
  var cardCryptoNfcApi: CardCryptoNfcApi = CardCryptoNfcApi()
  var cardActivationNfcApi: CardActivationNfcApi = CardActivationNfcApi()
  var cardKeyChainNfcApi: CardKeyChainNfcApi = CardKeyChainNfcApi()
  
  /* Coinmanager stuff*/
  @objc
  func getSeVersion(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.getSeVersion(resolve: resolve, reject: reject);
  }
  
  @objc
  func getCsn(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.getCsn(resolve: resolve, reject: reject);
  }
  
  @objc
  func getDeviceLabel(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.getDeviceLabel(resolve: resolve, reject: reject);
  }
  
  @objc
  func setDeviceLabel(_ label: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.setDeviceLabel(label: label, resolve: resolve, reject: reject);
  }
  
  @objc
  func getMaxPinTries(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.getMaxPinTries(resolve: resolve, reject: reject)
  }
  
  @objc
  func getRemainingPinTries(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.getRemainingPinTries(resolve: resolve, reject: reject)
  }
  
  @objc
  func getRootKeyStatus(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.getRootKeyStatus(resolve: resolve, reject: reject)
  }
  
  @objc
  func getAvailableMemory(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.getAvailableMemory(resolve: resolve, reject: reject)
  }
  
  @objc
  func getAppsList(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.getAppsList(resolve: resolve, reject: reject)
  }
  
  @objc
  func generateSeed(_ pin: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.generateSeed(pin: pin, resolve: resolve, reject: reject)
  }
  
  @objc
  func resetWallet(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.resetWallet(resolve: resolve, reject: reject)
  }
  
  @objc
  func changePin(_ oldPin: String, newPin: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCoinManagerNfcApi.changePin(oldPin: oldPin, newPin: newPin, resolve: resolve, reject: reject)
  }
  
  /* Ton wallet applet common stuff */
  @objc
  func getSault(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCryptoNfcApi.getSault(resolve: resolve, reject: reject)
  }
  
  @objc
  func getTonAppletState(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCryptoNfcApi.getTonAppletState(resolve: resolve, reject: reject)
  }
  
  /* Ton wallet applet card activation related stuff */
  @objc
  func setKeyForHmac(_ password: String, commonSecret : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardActivationNfcApi.setKeyForHmac(password : password, commonSecret : commonSecret, resolve: resolve, reject: reject)
  }
  
  @objc
  func turnOnWallet(_ newPin: String, password: String, commonSecret : String, initialVector : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardActivationNfcApi.turnOnWallet(newPin: newPin, password: password, commonSecret: commonSecret, initialVector: initialVector, resolve: resolve, reject: reject)
  }
  
  @objc
  func verifyPassword(_ password: String, initialVector: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardActivationNfcApi.verifyPassword(password: password, initialVector: initialVector, resolve: resolve, reject: reject)
  }
  
  @objc
  func verifyHashOfEncryptedPassword(_ password: String, initialVector : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardActivationNfcApi.verifyHashOfEncryptedPassword(password: password, initialVector: initialVector, resolve: resolve, reject: reject)
  }
  
  @objc
  func verifyHashOfCommonSecret(_ commonSecret: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardActivationNfcApi.verifyHashOfCommonSecret(commonSecret : commonSecret, resolve: resolve, reject: reject)
  }
  
  @objc
  func getHashOfCommonSecret(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardActivationNfcApi.getHashOfCommonSecret(resolve: resolve, reject: reject)
  }
  
  @objc
  func getHashOfEncryptedPassword(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardActivationNfcApi.getHashOfEncryptedPassword(resolve: resolve, reject: reject)
  }
  
  /* Ton wallet applet ed25519 related stuff*/
  @objc
  func getPublicKeyForDefaultPath(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCryptoNfcApi.getPublicKeyForDefaultPath(resolve: resolve, reject: reject)
  }
  
  @objc
  func getPublicKey(_ hdIndex: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCryptoNfcApi.getPublicKey(hdIndex: hdIndex, resolve: resolve, reject: reject)
  }
  
  @objc
  func verifyPin(_ pin: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCryptoNfcApi.verifyPin(pin: pin, resolve: resolve, reject: reject)
  }
  
  @objc
  func verifyPinAndSignForDefaultHdPath(_ data: String, pin : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCryptoNfcApi.signForDefaultHdPath(data: data, pin: pin, resolve: resolve, reject: reject)
  }
  
  @objc
  func verifyPinAndSign(_ data: String, hdIndex: String, pin : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardCryptoNfcApi.sign(data: data, hdIndex: hdIndex, pin: pin, resolve: resolve, reject: reject)
  }
  
  /* Ton wallet applet keychain related stuff */
  @objc
  func getKeyChainDataAboutAllKeys(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.getKeyChainDataAboutAllKeys(resolve: resolve, reject: reject)
  }
  
  @objc
  func getKeyChainInfo(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.getKeyChainInfo(resolve: resolve, reject: reject)
  }
  
  @objc
  func finishDeleteKeyFromKeyChainAfterInterruption(_ keyMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.finishDeleteKeyFromKeyChainAfterInterruption(keyMac : keyMac, resolve: resolve, reject: reject)
  }
  
  @objc
  func deleteKeyFromKeyChain(_ keyMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.deleteKeyFromKeyChain(keyMac : keyMac, resolve: resolve, reject: reject)
  }
  
  @objc
  func getKeyFromKeyChain(_ keyMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.getKeyFromKeyChain(keyMac : keyMac, resolve: resolve, rejecter: reject)
  }
  
  @objc
  func addKeyIntoKeyChain(_ newKey: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.addKeyIntoKeyChain(newKey : newKey, resolve: resolve, reject: reject)
  }
  
  @objc
  func changeKeyInKeyChain(_ newKey: String, oldKeyHMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.changeKeyInKeyChain(newKey : newKey, oldKeyHMac: oldKeyHMac, resolve: resolve, reject: reject)
  }
  
  @objc
  func resetKeyChain(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.resetKeyChain(resolve: resolve, reject: reject)
  }
  
  @objc
  func getNumberOfKeys(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.getNumberOfKeys(resolve: resolve, reject: reject)
  }
  
  @objc
  func getOccupiedStorageSize(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.getOccupiedStorageSize(resolve: resolve, reject: reject)
  }
  
  @objc
  func getFreeStorageSize(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.getFreeStorageSize(resolve: resolve, reject: reject)
  }
  
  @objc
  func checkKeyHmacConsistency(_ keyHmac: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.checkKeyHmacConsistency(keyHmac: keyHmac, resolve: resolve, reject: reject)
  }
  
  @objc
  func checkAvailableVolForNewKey(_ keySize: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.checkAvailableVolForNewKey(keySize: keySize, resolve: resolve, reject: reject)
  }
  
  @objc
  func getIndexAndLenOfKeyInKeyChain(_ keyHmac: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.getIndexAndLenOfKeyInKeyChain(keyHmac: keyHmac, resolve: resolve, reject: reject)
  }
  
  @objc
  func getHmac(_ index: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    cardKeyChainNfcApi.getHmac(index: index, resolve: resolve, reject: reject)
  }
}
