//
//  ApduRunner.swift
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 23.07.2020.
//

import Foundation
import PromiseKit
import CoreNFC

@available(iOS 13.0, *)
extension NFCISO7816APDU {
  func toHexString() -> String {
    let dataFieldInHex = (self.data ?? Data(_ : [])).hexEncodedString()
    return String(format:"%02X %02X %02X %02X", self.instructionClass, self.instructionCode, self.p1Parameter, self.p2Parameter) + " " + dataFieldInHex + String(format:"%02X", self.expectedResponseLength) + " "
  }
}

@available(iOS 13.0, *)
class ApduRunner: NSObject, NFCTagReaderSessionDelegate {
  var resolve: RCTPromiseResolveBlock?
  var reject: RCTPromiseRejectBlock?
  var sessionEx:NFCTagReaderSession?
  var cardOperation: (() -> Promise<String>)?
  
  
  func setResolve(resolveBlock: @escaping RCTPromiseResolveBlock) {
    self.resolve = resolveBlock
  }
  
  func setReject(rejectBlock: @escaping RCTPromiseRejectBlock) {
    self.reject = rejectBlock
  }
  
  func setCardOperation(cardOperation: @escaping () -> Promise<String>) {
    self.cardOperation = cardOperation
  }
    
  func startScan() {
    self.sessionEx = NFCTagReaderSession(pollingOption: .iso14443, delegate: self)
    self.sessionEx?.alertMessage = "Hold your iPhone near the ISO7816 tag to begin transaction."
    self.sessionEx?.begin()
  }
  
  func tagReaderSession(_ session: NFCTagReaderSession, didDetect tags: [NFCTag]) {
    guard self.resolve != nil && self.reject != nil && self.sessionEx != nil && self.cardOperation != nil && tags.count > 0 else {
      let errMsg = self.resolve == nil ? "RCTPromiseResolveBlock is empty.":
        self.reject == nil ? "RCTPromiseRejectBlock is empty." :
        self.sessionEx == nil ? "NFCSession is not established." :
        self.cardOperation == nil ? "CardOperation is not set." : "Tags array is empty."
      print("Error: " + errMsg)
      return
    }

    if case let NFCTag.iso7816(tag) = tags.first! {
      sessionEx?.connect(to: tags.first!) { (error: Error?) in
        if let err = error {
          print( "Error connecting to Nfc Tag" + err.localizedDescription)
          self.reject?("CARD_ERROR", "Error connecting to Nfc Tag: " + err.localizedDescription, err)
          return
        }
        print("Nfc Tag is connected.")
        self.handleApduResponse(apduResponse : self.cardOperation!())
      }
    }
  }
  
  private func handleApduResponse(apduResponse: Promise<String>) {
    apduResponse.done{response in
      self.resolve?(response)
      self.invalidateSession()
    }.catch{ error in
      self.reject?("CARD_ERROR", "APDU/Operation failed: " + error.localizedDescription, error)
      self.invalidateSession(msg: "Application failure: " +  error.localizedDescription)
    }
  }
  
  func sendCoinManagerAppletApdu(apduCommandPromise: Promise<NFCISO7816APDU>) -> Promise<Data>  {
    apduCommandPromise.then{(apdu : NFCISO7816APDU)  -> Promise<Data> in
        self.sendCoinManagerAppletApdu(apduCommand: apdu)
    }
  }
  
  func sendCoinManagerAppletApdu(apduCommand: NFCISO7816APDU) -> Promise<Data> {
    self.sendApdu(apduCommand: CoinManagerApduCommands.SELECT_COIN_MANAGER_APDU)
       .then{_ in self.sendApdu(apduCommand: apduCommand)}
  }
  
  func sendTonWalletAppletApdu(apduCommandPromise: Promise<NFCISO7816APDU>) -> Promise<Data>  {
    apduCommandPromise.then{(apdu : NFCISO7816APDU)  -> Promise<Data> in
        self.sendTonWalletAppletApdu(apduCommand: apdu)
    }
  }
  
  func sendTonWalletAppletApdu(apduCommand: NFCISO7816APDU) -> Promise<Data> {
    self.sendApdu(apduCommand: TonWalletAppletApduCommands.SELECT_TON_WALLET_APPLET_APDU)
    .then{_ in
      self.sendAppletApduAndCheckAppletState(apduCommand: apduCommand)
    }
  }
  
  func sendAppletApduAndCheckAppletState(apduCommandPromise: Promise<NFCISO7816APDU>) -> Promise<Data>  {
    apduCommandPromise.then{(apdu : NFCISO7816APDU)  -> Promise<Data> in
        self.sendAppletApduAndCheckAppletState(apduCommand: apdu)
    }
  }
  
  func sendAppletApduAndCheckAppletState(apduCommand: NFCISO7816APDU) -> Promise<Data> {
    let cardPromise = self.sendApdu(apduCommand: TonWalletAppletApduCommands.GET_APP_INFO_APDU)
    if apduCommand.instructionCode == TonWalletAppletApduCommands.INS_GET_APP_INFO {
      return cardPromise
    }
    return cardPromise.then{ (response : Data) -> Promise<Data> in
      guard response.count == TonWalletAppletApduCommands.GET_APP_INFO_LE else {
        let errMsg = "Application failure: can not read Ton wallet applet state"
        print(errMsg)
        self.sessionEx?.invalidate(errorMessage: errMsg)
        throw errMsg
      }
      if TonWalletAppletApduCommands.APPLET_COMMAND_STATE_MAPPING[apduCommand.instructionCode]?.contains(response.last!) ?? false {
        return self.sendApdu(apduCommand: apduCommand)
      }
      throw "APDU command " + TonWalletAppletApduCommands.APDU_COMMAND_NAMES[apduCommand.instructionCode]! + " is not supported in state " + TonWalletAppletConstants.APPLET_STATES[response.last!]!
    }
  }
  
  func sendApdu(apduCommandPromise: Promise<NFCISO7816APDU>) -> Promise<Data>  {
    apduCommandPromise.then{(apdu : NFCISO7816APDU)  -> Promise<Data> in
        self.sendApdu(apduCommand: apdu)
    }
  }
  
  func sendApdu(apduCommand: NFCISO7816APDU) -> Promise<Data> {
    return Promise { promise in
      print("\n\r===============================================================")
      print("===============================================================")
      print(">>> Send apdu  " + apduCommand.toHexString())
      if let commandName = TonWalletAppletApduCommands.APDU_COMMAND_NAMES[apduCommand.instructionCode] {
        print("(" + commandName + ")")
      }
      if let commandName = CoinManagerApduCommands.getApduName(apdu: apduCommand){
        print("(" + commandName + ")")
      }
      if case let NFCTag.iso7816(nfcTag) = self.sessionEx!.connectedTag! {
        nfcTag.sendCommand(apdu: apduCommand) { (response: Data, sw1: UInt8, sw2: UInt8, error: Error?)
            in
            
            print("SW1-SW2: " + String(format: "%02X, %02X", sw1, sw2))
            
            guard sw1 == 0x90 && sw2 == 0x00 else {
              let swCode : Int =  CardErrorCodes.convertSw1Sw2IntoOneSw(sw1 : sw1, sw2 : sw2)
              let swMessage = CardErrorCodes.CARD_ERROR_MSGS[UInt16(swCode)] ?? "Uknown error"
              print("Card error message:" + swMessage)
              print("===============================================================")
              
              if let session = self.sessionEx {
                session.invalidate(errorMessage: "Application failure: " + String(format: "%02X, %02X", sw1, sw2) + ", " + swMessage)
              }
              
              let error = NSError(domain: "CARD_ERROR", code: swCode, userInfo: [NSLocalizedDescriptionKey: String(format: "%02X, %02X", sw1, sw2) + ", " + swMessage])
              promise.reject(error)
              return
            }
            
            if response.count > 0 {
              print("APDU Response: " + response.hexEncodedString())
            }
            print("===============================================================")
            promise.fulfill(response)
        }
      }
      else {
        print("Tag is empty, make card scanning first")
        let error = NSError(domain: "NFC_TAG_ERROR", code: 0x00, userInfo: [NSLocalizedDescriptionKey: "NFC tag is not connected"])
        promise.reject(error)
      }
    }
  }
  
  func invalidateSession() {
    sessionEx?.invalidate()
  }
  
  func invalidateSession(msg : String) {
    sessionEx?.invalidate(errorMessage: msg)
  }
  
  func tagReaderSessionDidBecomeActive(_ session: NFCTagReaderSession) {
    print("Nfc session is active")
  }
   
  func tagReaderSession(_ session: NFCTagReaderSession, didInvalidateWithError error: Error) {
    print("Error happend: " + error.localizedDescription)
  }
  
}
