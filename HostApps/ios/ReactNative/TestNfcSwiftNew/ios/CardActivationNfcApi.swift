import PromiseKit
import Foundation
import CoreNFC

@available(iOS 13.0, *)
class CardActivationNfcApi: CardCoinManagerNfcApi {
  func turnOnWallet(newPin : String, password : String, commonSecret : String, initialVector : String) {
    print("Start card operation: turnOnColdWallet" )
    print("Got newPin:" + newPin)
    print("Got password:" + password)
    print("Got commonSecret:" + commonSecret)
    print("Got initialVector:" + initialVector)
    TonNfcApi.setPassword(password: password)
    TonNfcApi.setCommonSecret(commonSecret: commonSecret)
    TonNfcApi.setInitialVector(initialVector: initialVector)
    let newPinData = HexHelper.digitalStrIntoHex(digitalStr: newPin)
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
    executeTonWalletOperation(apduPromise: TonWalletAppletApduCommands.getVerifyPasswordApduPromise(password: HexHelper.hex(from: password).bytes, initialVector: HexHelper.hex(from: initialVector).bytes))
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
