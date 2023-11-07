//
//  NfcCardModule.swift
//  ReactNativeNfcCardMock
//
//  Created by Alina Alinovna on 09.06.2020.
//

import Foundation

@objc(NfcCardModule)
class NfcCardModule: NSObject {
  static let hashOfCommonSecretStoredByCard = "4C6E76B27D8B5C1AB227E9670915AB077C6DC4D57B0D7125320DABAF19003618"
  static let commonSecretStoredByCard = "346243B69CF2F0910BC235CA0DD5605E7077A70D4BEA98B71C840B78B3883471"
  static let encryptedPasswordStoredByCard = "F5568AD6FA82AFDA7A4888C5E106FCDB499F864A50506AA329428F1522E9E15919E059DCC1027ECF85BB8BDF4E5A17E2B54F83C684936E70B8AFBFAECA566A73D65212CE51349D2B404C7DC33EFC6602B165B32048827AB36A7600C46639CF4B5CC9D36D36571E1A08294010DA36F2AFFAB10462BABDCD4891365D0D6C9BA3A9"
  static let hashOfEncryptedPasswordStoredByCard = "4694955916A4CDB4D68FDD63D38E15B7188D11E15BAED9D624F07FE48FA32272"
  static let passwordStoredByCard = "2BE000983E0A60DC9C2483B2EA6913CF6FCF79A5B6856FAA8D8E55A7B5D3F60A4D47C943922E88DAFC58E8405C3695BCFA51A53301BF0329504CB9B15C43A1E4E3B4620BCA6487C4B31809578F12BC71CDC56C9DA2D9B74DD129D5AB9A64C6593DEE829CC12CA6E1DF72B4A2A918DFC71981C068B211479BDE76E6795EC9E67E"
  static let hashOfPasswordStoredByCard = "64AA75506D760099F0406D12D7099192B27471021D22F224AD96CEFA69854562"
  static let initialVectorStoredByCard = "8EC699896AC80F8B2011146C06C56BA1"
  
  static let ed25519PK = "01020356A101020356A1235689A100235689A10012A100090112A10009012233"
  static let APP_PERSONALIZED = 0x17
  static let APP_WAITE_AUTHORIZATION_MODE = 0x27
  static let APP_BLOCKED_MODE = 0x47
  
  static let PASSWORD_SIZE = 128
  static let IV_SIZE = 16
  
  static let SW_WRONG_LENGTH :UInt16 = 0x6700
  static let SW_INS_NOT_SUPPORTED   :UInt16 = 0x6D00
  static let SW_INCORRECT_PASSWORD_FOR_CARD_AUTHENICATION   :UInt16 =  0x5F00;
  static let SW_INCORRECT_PASSWORD_CARD_IS_BLOCKED   :UInt16 =  0x5F01;
  
  static let errorMessages = [NfcCardModule.SW_WRONG_LENGTH : "Wrong length. ", NfcCardModule.SW_INS_NOT_SUPPORTED : "INS value not supported.", NfcCardModule.SW_INCORRECT_PASSWORD_FOR_CARD_AUTHENICATION : "Incorrect password for card authentication. ", NfcCardModule.SW_INCORRECT_PASSWORD_CARD_IS_BLOCKED : "Incorrect password, card is locked."]
  
  static let MAX_COUNTER   :UInt16 =  20;
  
  var appletState = APP_WAITE_AUTHORIZATION_MODE
  var counter = 0
  
  
  @objc
  func getTonAppletState(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    resolve(getAppletStateMsg())
  }
  
  @objc
  func turnOnWallet(_ newPin: String, password: String, commonSecret: String, initialVector: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    if self.appletState == NfcCardModule.APP_WAITE_AUTHORIZATION_MODE{
     /* print(password)
      print(password.count/2)
      print(NfcCardModule.PASSWORD_SIZE)
      print(NfcCardModule.passwordStoredByCard.count/2)*/
      if Int(password.count/2) != NfcCardModule.PASSWORD_SIZE || Int(initialVector.count/2) != NfcCardModule.IV_SIZE {
        let error = NSError(domain: "", code: Int(NfcCardModule.SW_WRONG_LENGTH), userInfo: nil)
        reject("CARD_ERROR ", NfcCardModule.errorMessages[NfcCardModule.SW_WRONG_LENGTH], error)
      }
      else if password == NfcCardModule.passwordStoredByCard && commonSecret == NfcCardModule.commonSecretStoredByCard && initialVector == NfcCardModule.initialVectorStoredByCard {
          self.appletState = NfcCardModule.APP_PERSONALIZED
          resolve(getAppletStateMsg())
      }
      else {
        counter+=1
        if (counter < NfcCardModule.MAX_COUNTER) {
          let error = NSError(domain: "", code: Int(NfcCardModule.SW_INCORRECT_PASSWORD_FOR_CARD_AUTHENICATION), userInfo: nil)
          reject("CARD_ERROR ", NfcCardModule.errorMessages[NfcCardModule.SW_INCORRECT_PASSWORD_FOR_CARD_AUTHENICATION], error)
        }
        else {
          self.appletState = NfcCardModule.APP_BLOCKED_MODE
          let error = NSError(domain: "", code: Int(NfcCardModule.SW_INCORRECT_PASSWORD_CARD_IS_BLOCKED), userInfo: nil)
          reject("CARD_ERROR ", NfcCardModule.errorMessages[NfcCardModule.SW_INCORRECT_PASSWORD_CARD_IS_BLOCKED], error)        }
      }
    }
    else {
      let error = NSError(domain: "", code: Int(NfcCardModule.SW_INS_NOT_SUPPORTED), userInfo: nil)
      reject("CARD_ERROR ", NfcCardModule.errorMessages[NfcCardModule.SW_INS_NOT_SUPPORTED], error)
    }
  }
  
  @objc
  func getPublicKeyForDefaultPath(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    if self.appletState == NfcCardModule.APP_PERSONALIZED {
      resolve("Dummy public key for ed25519 (just for joy) = " + NfcCardModule.ed25519PK)
    }
    else {
      let error = NSError(domain: "", code: Int(NfcCardModule.SW_INS_NOT_SUPPORTED), userInfo: nil)
      reject("CARD_ERROR ", NfcCardModule.errorMessages[NfcCardModule.SW_INS_NOT_SUPPORTED], error)
    }
  }
  
  func getAppletStateMsg() -> String {
    var msg: String
    switch appletState {
      case NfcCardModule.APP_WAITE_AUTHORIZATION_MODE:
        msg = "Applet state = " + String(appletState) + "  (Waite for to factor authorization)"
      case NfcCardModule.APP_BLOCKED_MODE:
        msg = "Applet state = " + String(appletState) + "  (Blocked)"
      default:
        msg = "Applet state = " + String(appletState) + "  (Personalized, ready to work)"
    }
    return msg
  }

}
