//
//  Ed25519NfcApi.swift
//  TestNfcSwiftNew
//
//  Created by Alina Alinovna on 21.07.2020.
//
import Foundation
import PromiseKit
import Foundation
import CoreNFC

@available(iOS 13.0, *)
class CardEd25519NfcApi: TonNfcApi {
  func getPublicKeyForDefaultPath() {
    print("Start card operation: getPublicKeyForDefaultPath")
    executeTonWalletOperationAndSendHex(apdu: TonWalletAppletApduCommands.GET_PUB_KEY_WITH_DEFAULT_PATH_APDU)
  }

  func getPublicKey(hdIndex: String) {
    print("Start card operation: getPublicKey" )
    print("Got hdIndex:" + hdIndex)
    //todo: hdIndex conversion check
    let hdIndex = HexHelper.digitalStrIntoHex(digitalStr: hdIndex)
    executeTonWalletOperationAndSendHex(apduPromise: TonWalletAppletApduCommands.getPublicKeyApduPromise(ind: hdIndex))
  }
  
  func verifyPin(pin: String) {
    print("Start card operation: verifyPin" )
    print("Got PIN:" + pin)
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndVerifyPin(pin: pin)
      .then{(response : Data)  -> Promise<String> in
        Promise<String> { promise in promise.fulfill("done")}
      }
    })
    apduRunner.startScan()
  }
  
  
  func signForDefaultHdPath(data: String, pin: String) {
    print("Start card operation: signForDefaultHdPath" )
    print("Got Data:" + data)
    print("Got PIN:" + pin)
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndVerifyPin(pin: pin)
      .then{(response : Data) -> Promise<Data> in
        self.getSault()
      }
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendApdu(apduCommandPromise:
          TonWalletAppletApduCommands.getSignShortMessageWithDefaultPathApduPromise(dataForSigning:
            HexHelper.hexStrToUInt8Array(hexStr: data), sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
          return Promise<String> { promise in promise.fulfill(response.hexEncodedString())}
      }
    })
    apduRunner.startScan()
  }
  
  func sign(data: String, hdIndex: String, pin: String) {
    print("Start card operation: sign" )
    print("Got Data:" + data)
    print("Got hdIndex:" + hdIndex)
    print("Got PIN:" + pin)
    //todo: check correct conversion of hdIndex into byte array
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndVerifyPin(pin: pin)
      .then{(response : Data) -> Promise<Data> in
        self.getSault()
      }
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendApdu(apduCommandPromise:
          TonWalletAppletApduCommands.getSignShortMessageApduPromise(dataForSigning: HexHelper.hexStrToUInt8Array(hexStr: data), ind: HexHelper.digitalStrIntoHex(digitalStr: hdIndex), sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
          return Promise<String> { promise in promise.fulfill(response.hexEncodedString())}
      }
    })
    apduRunner.startScan()
  }
  
  private func selectTonAppletAndVerifyPin(pin: String) -> Promise<Data> {
    self.selectTonAppletAndGetSault()
    .then{(sault : Data) -> Promise<Data> in
      self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getVerifyPinApduPromise(pinBytes: HexHelper.digitalStrIntoHex(digitalStr: pin), sault: sault.bytes))
    }
  }
  
}


