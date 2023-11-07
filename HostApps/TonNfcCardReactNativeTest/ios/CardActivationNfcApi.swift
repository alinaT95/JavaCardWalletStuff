//
//  CardActivationNfcApi.swift
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 23.07.2020.
//

import PromiseKit
import Foundation
import CoreNFC

@available(iOS 13.0, *)
class CardActivationNfcApi: CardCoinManagerNfcApi {
  
  func turnOnWallet(newPin: String, password: String, commonSecret : String, initialVector : String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
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
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    turnOnWallet(newPin : newPin, password : password, commonSecret : commonSecret, initialVector : initialVector)
  }
  
  func verifyPassword(password: String, initialVector: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard password.count == 2 * TonWalletAppletConstants.PASSWORD_SIZE && password.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Password must be a hex string of length \(2 * TonWalletAppletConstants.PASSWORD_SIZE). ", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    verifyPassword(password: password, initialVector: initialVector)
  }
  
  func verifyHashOfEncryptedPassword(password: String, initialVector : String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard password.count == 2 * TonWalletAppletConstants.PASSWORD_SIZE && password.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Password must be a hex string of even length \(2 * TonWalletAppletConstants.PASSWORD_SIZE). ", reject: reject)
      return
    }
    guard initialVector.count == 2 * TonWalletAppletConstants.IV_SIZE && initialVector.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Initial vector must be a hex string of even length \(2 * TonWalletAppletConstants.IV_SIZE). ", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    verifyHashOfEncryptedPassword(password : password, initialVector: initialVector)
  }
  
  func verifyHashOfCommonSecret(commonSecret: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard commonSecret.count == 2 * TonWalletAppletConstants.COMMON_SECRET_SIZE && commonSecret.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "Common secret must be a hex string of even length \(2 * TonWalletAppletConstants.COMMON_SECRET_SIZE). ", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    verifyHashOfCommonSecret(commonSecret : commonSecret)
  }
  
  func getHashOfCommonSecret(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getHashOfCommonSecret()
  }
  
  func getHashOfEncryptedPassword(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getHashOfEncryptedPassword()
  }
  
  func turnOnWallet(newPin : String, password : String, commonSecret : String, initialVector : String) {
    print("Start card operation: turnOnColdWallet" )
    print("Got newPin:" + newPin)
    print("Got password:" + password)
    print("Got commonSecret:" + commonSecret)
    print("Got initialVector:" + initialVector)
    TonNfcApi.setPassword(password: password)
    TonNfcApi.setCommonSecret(commonSecret: commonSecret)
    TonNfcApi.setInitialVector(initialVector: initialVector)
    let newPinData = ByteArrayAndHexHelper.digitalStrIntoAsciiUInt8Array(digitalStr: newPin)
    apduRunner.setCardOperation(cardOperation: {() in
      self.apduRunner.sendCoinManagerAppletApdu(apduCommand: CoinManagerApduCommands.RESET_WALLET_APDU)
        .then{(response : Data)  -> Promise<Data> in
          self.apduRunner.sendApdu(apduCommand: CoinManagerApduCommands.GEN_SEED_FOR_DEFAULT_PIN)
      }
      .then{(response : Data)  -> Promise<Data> in
        self.apduRunner.sendCoinManagerAppletApdu(apduCommandPromise: CoinManagerApduCommands.getChangePinApduPromise(oldPin : TonWalletAppletConstants.DEFAULT_PIN, newPin : newPinData))
      }
      .then{(response : Data)  -> Promise<Data> in
        self.apduRunner.sendTonWalletAppletApdu(apduCommand: TonWalletAppletApduCommands.GET_HASH_OF_COMMON_SECRET_APDU)
      }
      .then{(hashOfCommonSecretFromCard : Data)  -> Promise<Data> in
        return Promise { promise in
          if hashOfCommonSecretFromCard == TonNfcApi.commonSecret?.hash() {
            promise.fulfill(Data())
          }
          else {
            let errMsg = "Card two-factor authorization failed: Hash of common secret is invalid."
            let error = NSError(domain: "AUTHORIZATION_ERROR", code: 0x00, userInfo: [NSLocalizedDescriptionKey: errMsg])
            print(errMsg)
            promise.reject(error)
            self.apduRunner.invalidateSession(msg: errMsg)
          }
        }
      }
      .then{(response : Data)  -> Promise<Data> in
        self.apduRunner.sendTonWalletAppletApdu(apduCommand: TonWalletAppletApduCommands.GET_HASH_OF_ENCRYPTED_PASSWORD_APDU)
      }
      .then{(hashOfEncryptetdPasswordFromCard : Data)  ->  Promise<Data> in
        return Promise { promise in
          if let password = TonNfcApi.password, let passwordHash = TonNfcApi.password?.hash(), let initialVector = TonNfcApi.initialVector
          {
            
            let aes128 = AES(key: passwordHash.subdata(in: 0..<16), iv: initialVector)
            let encryptedPassword = aes128?.encrypt(msg: password)
            if hashOfEncryptetdPasswordFromCard == encryptedPassword?.hash() {
              promise.fulfill(Data())
            }
            else {
              let errMsg = "Card two-factor authorization failed: Hash of encrypted password is invalid."
              let error = NSError(domain: "AUTHORIZATION_ERROR", code: 0x00, userInfo: [NSLocalizedDescriptionKey: errMsg])
              print(errMsg)
              promise.reject(error)
              self.apduRunner.invalidateSession(msg: errMsg)
            }
          }
          else {
            let errMsg = "Card two-factor authorization failed: please set password and initialVector."
            let error = NSError(domain: "AUTHORIZATION_ERROR", code: 0x00, userInfo: [NSLocalizedDescriptionKey: errMsg])
            print(errMsg)
            promise.reject(error)
            self.apduRunner.invalidateSession(msg: errMsg)
          }
        }
      }
      .then{(response : Data)  -> Promise<Data> in
        self.apduRunner.sendAppletApduAndCheckAppletState(apduCommandPromise: TonWalletAppletApduCommands.getVerifyPasswordApduPromise(password: TonNfcApi.password!.bytes, initialVector: TonNfcApi.initialVector!.bytes))
      }
      .then{(response : Data)  -> Promise<Data> in
        TonNfcApi.setKeyForHmac()
        return self.apduRunner.sendApdu(apduCommand: TonWalletAppletApduCommands.GET_APP_INFO_APDU)
      }
      .then{(response : Data)  -> Promise<String> in
        Promise<String> { promise in promise.fulfill(TonWalletAppletConstants.APPLET_STATES[response.bytes[0]]!)}
      }
    })
    apduRunner.startScan()
  }
  
  func verifyHashOfEncryptedPassword(password : String, initialVector : String) {
    print("Start card operation: verifyHashOfEncryptedPassword")
    print("Got password:" + password)
    print("Got initialVector:" + initialVector)
    TonNfcApi.setPassword(password: password)
    TonNfcApi.setInitialVector(initialVector: initialVector)
    
    apduRunner.setCardOperation(cardOperation: {() in
      self.apduRunner.sendTonWalletAppletApdu(apduCommand: TonWalletAppletApduCommands.GET_HASH_OF_ENCRYPTED_PASSWORD_APDU)
        .then{(hashOfEncryptetdPasswordFromCard : Data)  ->  Promise<String> in
          print("hashOfEncryptetdPasswordFromCard = " + hashOfEncryptetdPasswordFromCard.hexEncodedString())
          return Promise { promise in
            if let password = TonNfcApi.password, let passwordHash = TonNfcApi.password?.hash(), let initialVector = TonNfcApi.initialVector
            {
              print("passwordHash  = " + passwordHash.hexEncodedString())
              let aes128 = AES(key: passwordHash.subdata(in: 0..<16), iv: initialVector)
              let encryptedPassword = aes128?.encrypt(msg: password)
              print(password.count)
              print("encryptedPassword = " + encryptedPassword!.hexEncodedString())
              print(encryptedPassword?.count)
              if hashOfEncryptetdPasswordFromCard == encryptedPassword?.hash() {
                promise.fulfill("done")
              }
              else {
                let errMsg = "Card two-factor authorization failed: Hash of encrypted password is invalid."
                let error = NSError(domain: "AUTHORIZATION_ERROR", code: 0x00, userInfo: [NSLocalizedDescriptionKey: errMsg])
                print(errMsg)
                promise.reject(error)
                self.apduRunner.invalidateSession(msg: errMsg)
              }
            }
            else {
              let errMsg = "Card two-factor authorization failed: please set password and initialVector."
              let error = NSError(domain: "AUTHORIZATION_ERROR", code: 0x00, userInfo: [NSLocalizedDescriptionKey: errMsg])
              print(errMsg)
              promise.reject(error)
              self.apduRunner.invalidateSession(msg: errMsg)
            }
          }
      }
    })
    apduRunner.startScan()
  }
  
  func verifyHashOfCommonSecret(commonSecret : String) {
    print("Start card operation: verifyHashOfCommonSecret")
    print("Got commonSecret:" + commonSecret)
    TonNfcApi.setCommonSecret(commonSecret: commonSecret)
    apduRunner.setCardOperation(cardOperation: {() in
      self.apduRunner.sendTonWalletAppletApdu(apduCommand: TonWalletAppletApduCommands.GET_HASH_OF_COMMON_SECRET_APDU)
        .then{(hashOfCommonSecretFromCard : Data)  -> Promise<String> in
          print("hashOfCommonSecretFromCard = " + hashOfCommonSecretFromCard.hexEncodedString())
          print("hashOfCommonSecret = " + TonNfcApi.commonSecret!.hash().hexEncodedString())
          return Promise { promise in
            if hashOfCommonSecretFromCard == TonNfcApi.commonSecret?.hash() {
              promise.fulfill("done")
            }
            else {
              let errMsg = "Card two-factor authorization failed: Hash of common secret is invalid."
              let error = NSError(domain: "AUTHORIZATION_ERROR", code: 0x00, userInfo: [NSLocalizedDescriptionKey: errMsg])
              print(errMsg)
              promise.reject(error)
              self.apduRunner.invalidateSession(msg: errMsg)
            }
          }
      }
    })
    apduRunner.startScan()
  }
  
  func verifyPassword(password : String, initialVector : String) {
    print("Start card operation: verifyPassword")
    print("Got password:" + password)
    print("Got initialVector:" + initialVector)
    TonNfcApi.setPassword(password: password)
    TonNfcApi.setInitialVector(initialVector: initialVector)
    executeTonWalletOperation(apduPromise: TonWalletAppletApduCommands.getVerifyPasswordApduPromise(password: ByteArrayAndHexHelper.hex(from: password).bytes, initialVector: ByteArrayAndHexHelper.hex(from: initialVector).bytes))
  }
  
  func getHashOfCommonSecret() {
    print("Start card operation: getHashOfCommonSecret")
    executeTonWalletOperationAndSendHex(apdu: TonWalletAppletApduCommands.GET_HASH_OF_COMMON_SECRET_APDU)
  }
  
  func getHashOfEncryptedPassword() {
    print("Start card operation: getHashOfEncryptedPassword")
    executeTonWalletOperationAndSendHex(apdu: TonWalletAppletApduCommands.GET_HASH_OF_ENCRYPTED_PASSWORD_APDU)
  }
}
