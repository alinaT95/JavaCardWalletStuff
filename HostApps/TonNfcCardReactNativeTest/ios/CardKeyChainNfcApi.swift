//
//  CardKeyChainNfcApi.swift
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 23.07.2020.
//

import PromiseKit
import Foundation
import CoreNFC

@available(iOS 13.0, *)

class CardKeyChainNfcApi: TonNfcApi {
  var keyMacs: [Data]
  
  override init() {
    keyMacs = []
  }
  
  func getKeyChainDataAboutAllKeys(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getKeyChainDataAboutAllKeys()
  }
  
  func getKeyChainInfo(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getKeyChainInfo()
  }
  
  func finishDeleteKeyFromKeyChainAfterInterruption(keyMac : String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard keyMac.count == 2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE && keyMac.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "keyHmac is a hex string of length  = \(2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE).", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    //  nfcApi.finishDeleteKeyFromKeyChainAfterInterruption(keyMac : keyMac)
  }
  
  func deleteKeyFromKeyChain(keyMac : String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard keyMac.count == 2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE && keyMac.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "keyHmac is a hex string of length  = \(2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE).", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    deleteKeyFromKeyChain(keyMac : keyMac)
  }
  
  func getKeyFromKeyChain(keyMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard keyMac.count == 2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE && keyMac.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "keyHmac is a hex string of length  = \(2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE).", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getKeyFromKeyChain(keyMac : keyMac)
  }
  
  func addKeyIntoKeyChain(newKey: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard newKey.count > 0 && newKey.count <= 2 * TonWalletAppletConstants.MAX_KEY_SIZE_IN_KEYCHAIN && newKey.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "New key must be a nonempty hex string of even length <= \(2 * TonWalletAppletConstants.MAX_KEY_SIZE_IN_KEYCHAIN).", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    addKeyIntoKeyChain(newKey : newKey)
  }
  
  func changeKeyInKeyChain(newKey: String, oldKeyHMac : String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    guard oldKeyHMac.count == 2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE && oldKeyHMac.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "keyHmac is a hex string of length  = \(2 * TonWalletAppletConstants.HMAC_SHA_SIG_SIZE).", reject: reject)
      return
    }
    guard newKey.count > 0 && newKey.count <= 2 * TonWalletAppletConstants.MAX_KEY_SIZE_IN_KEYCHAIN && newKey.isHex == true else {
      ErrorHelper.callRejectWith(errMsg :  "New key must be a nonempty hex string of even length <= \(2 * TonWalletAppletConstants.MAX_KEY_SIZE_IN_KEYCHAIN).", reject: reject)
      return
    }
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    changeKeyInKeyChain(newKey : newKey, oldKeyHMac: oldKeyHMac)
  }
  
  func resetKeyChain(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    resetKeyChain()
  }
  
  func getNumberOfKeys(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getNumberOfKeys()
  }
  
  func getOccupiedStorageSize(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getOccupiedStorageSize()
  }
  
  func getFreeStorageSize(resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getFreeStorageSize()
  }
  
  func checkKeyHmacConsistency(keyHmac: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    checkKeyHmacConsistency(keyHmac: keyHmac)
  }
  
  func checkAvailableVolForNewKey(keySize: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    checkAvailableVolForNewKey(keySize: keySize)
  }
  
  func getIndexAndLenOfKeyInKeyChain(keyHmac: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getIndexAndLenOfKeyInKeyChain(keyHmac: keyHmac)
  }
  
  func getHmac(index: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    getHmac(index: index)
  }
  
  func getKeyChainDataAboutAllKeys() {
    print("Start card operation: getAllHmacsOfKeysFromCard")
    var keyHmacsAndLens = [[String: String]]()
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndGetSault()
        .then{(sault : Data) -> Promise<Data> in
          self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getNumberOfKeysApduPromise(sault: sault.bytes))
      }
      .then{(numOfKeys : Data) -> Promise<Data> in
        let numOfKeys = ByteArrayAndHexHelper.makeShort(src: numOfKeys.bytes, srcOff: 0)
        var hmacPromise = Promise<Data> { promise in promise.fulfill(Data(_ : []))}
        //
        for ind in 0..<numOfKeys {
          hmacPromise = hmacPromise.then{ (response : Data) -> Promise<Data> in
            self.getHmac(keyIndex: UInt16(ind))
          }
          .then { (hmacAndLen : Data) -> Promise<Data> in
            self.keyMacs.append(Data(_ : hmacAndLen.bytes[range : 0..<TonWalletAppletConstants.HMAC_SHA_SIG_SIZE]))
            keyHmacsAndLens.append(["hmac" : Data(_ : hmacAndLen.bytes[range : 0..<TonWalletAppletConstants.HMAC_SHA_SIG_SIZE]).hexEncodedString(), "length" :
              String(ByteArrayAndHexHelper.makeShort(src: hmacAndLen.bytes, srcOff: TonWalletAppletConstants.HMAC_SHA_SIG_SIZE))
            ])
            return Promise<Data> { promise in promise.fulfill(Data(_ : []))}
          }
        }
        
        return hmacPromise
      }
      .then{(response : Data) -> Promise<String> in
        Promise<String> { promise in
          let jsonData = try JSONSerialization.data(withJSONObject: keyHmacsAndLens, options: .prettyPrinted)
          let jsonString = String(data: jsonData, encoding: .utf8)!
          print(jsonString)
          promise.fulfill(jsonString)
        }
      }
    })
    apduRunner.startScan()
  }
  
  func getKeyChainInfo() {
    print("Start card operation: getKeyChainInfo")
    var keyChainInfo: [String : String] = [:]
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndGetSault()
        .then{(sault : Data) -> Promise<Data> in
          self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getNumberOfKeysApduPromise(sault: sault.bytes))
      }
      .then{(numOfKeys : Data) -> Promise<Data> in
        keyChainInfo["numberOfKeys"] = String(ByteArrayAndHexHelper.makeShort(src: numOfKeys.bytes, srcOff: 0))
        return self.getSaultPromise()
      }
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getGetOccupiedSizeApduPromise(sault: sault.bytes))
      }
      .then{(occupiedSize : Data) -> Promise<Data> in
        keyChainInfo["occupiedSize"] = String(ByteArrayAndHexHelper.makeShort(src: occupiedSize.bytes, srcOff: 0))
        return self.getSaultPromise()
      }
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getGetFreeSizeApduPromise(sault: sault.bytes))
      }
      .then{(freeSize : Data) -> Promise<Data> in
        keyChainInfo["freeSize"] = String(ByteArrayAndHexHelper.makeShort(src: freeSize.bytes, srcOff: 0))
        return self.getSaultPromise()
      }
      .then{(response : Data) -> Promise<String> in
        Promise<String> { promise in
          let jsonData = try JSONSerialization.data(withJSONObject: keyChainInfo, options: .prettyPrinted)
          let jsonString = String(data: jsonData, encoding: .utf8)!
          print(jsonString)
          promise.fulfill(jsonString)
        }
      }
    })
    apduRunner.startScan()
  }
  
  func addKeyIntoKeyChain(newKey: String){
    print("Start card operation: addKeyIntoKeyChain" )
    print("Got newKey to add:" + newKey)
    //Todo: add verificaion that key was not added already
    let newKeyBytes = ByteArrayAndHexHelper.hexStrToUInt8Array(hexStr: newKey)
    let keySize = UInt16(newKeyBytes.count)
    var macOfKey = Data(_ : [])
    print("keySize = " + String(keySize))
    apduRunner.setCardOperation(cardOperation: { () in
      Promise<Data> { promise in
        macOfKey.append(try HmacHelper.computeHmac(data: Data(_ : newKeyBytes)))
        print("macOfKey = ")
        print(macOfKey)
        guard !self.keyMacs.contains(macOfKey) else {
          throw "Key with mac \(macOfKey.hexEncodedString())  already exists. You can not add it."
        }
        promise.fulfill(macOfKey)
      }
      .then{(response : Data) -> Promise<Data> in
        self.selectTonAppletAndGetSault()
      }
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendAppletApduAndCheckAppletState(apduCommandPromise: TonWalletAppletApduCommands.getCheckAvailableVolForNewKeyApduPromise(keySize: [UInt8(keySize >> 8), UInt8(keySize)], sault: sault.bytes))
      }
      .then{(response : Data) -> Promise<Data> in
        self.addKey(keyBytes: newKeyBytes, macOfKey: macOfKey.bytes)
      }
      .then{(response : Data) -> Promise<String> in
        self.keyMacs.append(macOfKey)
        return Promise<String> { promise in
          promise.fulfill(macOfKey.hexEncodedString())
        }
      }
    })
    apduRunner.startScan()
  }
  
  func changeKeyInKeyChain(newKey : String, oldKeyHMac : String){
    print("Start card operation: changeKeyInKeyChain" )
    print("Got newKey: " + newKey)
    print("Got oldKeyHMac: " + oldKeyHMac)
    let newKeyBytes = ByteArrayAndHexHelper.hexStrToUInt8Array(hexStr: newKey)
    let oldKeyHMacBytes = ByteArrayAndHexHelper.hexStrToUInt8Array(hexStr: oldKeyHMac)
    var macOfNewKey = Data(_ : [])
    print("newKeySize = " + String(newKeyBytes.count))
    var keyIndexToChange: [UInt8] = []
    apduRunner.setCardOperation(cardOperation: { () in
      Promise<Data> { promise in
        guard self.keyMacs.contains(Data(_ : oldKeyHMacBytes)) else {
          throw "Key with mac \(oldKeyHMac) does not exist. You can not replace it."
        }
        promise.fulfill(Data(_ : []))
      }
      .then{(response : Data) -> Promise<Data> in
        Promise<Data> { promise in
          macOfNewKey.append(try HmacHelper.computeHmac(data: Data(_ : newKeyBytes)))
          print("macOfNewKey = " + macOfNewKey.hexEncodedString())
          guard !self.keyMacs.contains(macOfNewKey) else {
            throw "Key with mac \(macOfNewKey.hexEncodedString()) already exists. You can not add it."
          }
          promise.fulfill(Data(_ : []))
        }
      }
      .then{(response : Data) -> Promise<Data> in
        self.selectTonAppletAndGetSault()
      }
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendAppletApduAndCheckAppletState(apduCommandPromise: TonWalletAppletApduCommands.getGetIndexAndLenOfKeyInKeyChainApduPromise(keyHmac: oldKeyHMacBytes, sault: sault.bytes))
      }
      .then{(response : Data) -> Promise<Data> in
        let keyIndex = ByteArrayAndHexHelper.makeShort(src : response.bytes, srcOff : 0)
        let keyLen = ByteArrayAndHexHelper.makeShort(src : response.bytes, srcOff : 2)
        print("keyIndex = " + String(keyIndex))
        print("keyLen = " + String(keyLen))
        guard keyLen == newKeyBytes.count else {
          throw "Bad new key length. It must be = \(String(keyLen))"
        }
        keyIndexToChange = response.bytes[range : 0...1]
        return Promise<Data> {promise in promise.fulfill(Data(_ : []))}
      }
      .then{(response : Data) -> Promise<Data> in
        self.checkStateAndGetSault()
      }
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendAppletApduAndCheckAppletState(apduCommandPromise: TonWalletAppletApduCommands.getInitiateChangeOfKeyApduPromise(index: keyIndexToChange, sault: sault.bytes))
      }
      .then{(response : Data) -> Promise<Data> in
        self.changeKey(keyBytes : newKeyBytes, macOfKey: macOfNewKey.bytes)
      }
      .then{(response : Data) -> Promise<String> in
        let ind = self.keyMacs.firstIndex(of: Data(_ : oldKeyHMacBytes))
        self.keyMacs[ind!] = macOfNewKey
        return Promise<String> { promise in
          promise.fulfill(macOfNewKey.hexEncodedString())
        }
        
      }
    })
    apduRunner.startScan()
  }
  
  private func changeKey(keyBytes : [UInt8], macOfKey: [UInt8])  -> Promise<Data> {
    sendKey(keyBytes : keyBytes, macOfKey: macOfKey, ins : TonWalletAppletApduCommands.INS_CHANGE_KEY_CHUNK)
  }
  
  private func addKey(keyBytes : [UInt8], macOfKey: [UInt8])  -> Promise<Data> {
    sendKey(keyBytes : keyBytes, macOfKey: macOfKey, ins : TonWalletAppletApduCommands.INS_ADD_KEY_CHUNK)
  }
  
  private func sendKey(keyBytes : [UInt8], macOfKey: [UInt8], ins : UInt8) -> Promise<Data> {
    let numberOfFullPackets = keyBytes.count / TonWalletAppletConstants.DATA_PORTION_MAX_SIZE
    print("numberOfFullPackets = " + String(numberOfFullPackets))
    
    var sendKeyPromise = self.apduRunner.sendApdu(apduCommand:
      TonWalletAppletApduCommands.SELECT_TON_WALLET_APPLET_APDU)
    
    for index in 0..<numberOfFullPackets {
      var newSendKeyPromise = sendKeyPromise.then{(response : Data) -> Promise<Data> in
        self.checkStateAndGetSault()
      }
      .then{(sault : Data) -> Promise<Data> in
        print("#packet " + String(index))
        let keyChunk = keyBytes[range: index * TonWalletAppletConstants.DATA_PORTION_MAX_SIZE..<(index + 1) * TonWalletAppletConstants.DATA_PORTION_MAX_SIZE]
        return self.apduRunner.sendAppletApduAndCheckAppletState(apduCommandPromise: TonWalletAppletApduCommands.getSendKeyChunkApduPromise(ins : ins, p1: index == 0 ? 0x00 : 0x01, keyChunkOrMacBytes: keyChunk, sault: sault.bytes))
      }
      sendKeyPromise = newSendKeyPromise
    }
    
    let tailLen = keyBytes.count % TonWalletAppletConstants.DATA_PORTION_MAX_SIZE
    if tailLen > 0 {
      sendKeyPromise = sendKeyPromise.then{(response : Data) -> Promise<Data> in
        self.checkStateAndGetSault()
      }
      .then{(sault : Data) -> Promise<Data> in
        let keyChunk = keyBytes[range: numberOfFullPackets * TonWalletAppletConstants.DATA_PORTION_MAX_SIZE..<numberOfFullPackets * TonWalletAppletConstants.DATA_PORTION_MAX_SIZE + tailLen]
        return self.apduRunner.sendAppletApduAndCheckAppletState(apduCommandPromise: TonWalletAppletApduCommands.getSendKeyChunkApduPromise(ins : ins, p1: numberOfFullPackets == 0 ? 0x00 : 0x01, keyChunkOrMacBytes: keyChunk, sault: sault.bytes))
      }
    }
    
    return sendKeyPromise.then{(response : Data) -> Promise<Data> in
      self.checkStateAndGetSault()
    }
    .then{(sault : Data) -> Promise<Data> in
      self.apduRunner.sendAppletApduAndCheckAppletState(apduCommandPromise: TonWalletAppletApduCommands.getSendKeyChunkApduPromise(ins : ins, p1: 0x02, keyChunkOrMacBytes: macOfKey, sault: sault.bytes))
    }
  }
  
  func getKeyFromKeyChain(keyMac: String) {
    print("Start card operation: getKeyFromKeyChain" )
    print("Got mac: " + keyMac)
    let macBytes = ByteArrayAndHexHelper.hexStrToUInt8Array(hexStr: keyMac)
    apduRunner.setCardOperation(cardOperation: { () in
      Promise<Data> { promise in
        guard self.keyMacs.contains(Data(_ : macBytes)) else {
          throw "Key with mac \(keyMac) does not exist. You can not get it."
        }
        promise.fulfill(Data(_ : []))
      }
      .then{(response : Data) -> Promise<Data> in
        self.selectTonAppletAndGetSault()
      }
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendAppletApduAndCheckAppletState(apduCommandPromise: TonWalletAppletApduCommands.getGetIndexAndLenOfKeyInKeyChainApduPromise(keyHmac: macBytes, sault: sault.bytes))
      }
      .then{(response : Data) -> Promise<String> in
        let keyLen = ByteArrayAndHexHelper.makeShort(src : response.bytes, srcOff : 2)
        print("keyIndex = " + String(ByteArrayAndHexHelper.makeShort(src : response.bytes, srcOff : 0)))
        print("keyLen = " + String(keyLen))
        return self.getKeyFromKeyChain(keyLen : keyLen, ind: response.bytes[range : 0...1])
      }
    })
    apduRunner.startScan()
  }
  
  
  private func getKeyFromKeyChain(keyLen : Int, ind : [UInt8]) -> Promise<String> {
    let numberOfFullPackets = keyLen / TonWalletAppletConstants.DATA_PORTION_MAX_SIZE
    print("numberOfFullPackets = " + String(numberOfFullPackets))
    var getKeyPromise = self.apduRunner.sendApdu(apduCommand:
      TonWalletAppletApduCommands.SELECT_TON_WALLET_APPLET_APDU)
    var startPos: UInt16 = 0
    var key = Data(_ : [])
    
    for index in 0..<numberOfFullPackets {
      var newGetKeyPromise = getKeyPromise.then{(response : Data) -> Promise<Data> in
        self.checkStateAndGetSault()
      }
      .then{(sault : Data) -> Promise<Data> in
        print("packet# " + String(index))
        return self.apduRunner.sendAppletApduAndCheckAppletState(apduCommandPromise: TonWalletAppletApduCommands.getGetKeyChunkPromise(ind: ind, startPos: startPos, sault: sault.bytes, le: TonWalletAppletConstants.DATA_PORTION_MAX_SIZE))
        
      }
      .then{(keyChunk : Data) -> Promise<Data> in
        startPos = startPos + UInt16(TonWalletAppletConstants.DATA_PORTION_MAX_SIZE)
        key.append(keyChunk)
        return Promise { promise in promise.fulfill(Data(_ : []))}
      }
      getKeyPromise = newGetKeyPromise
    }
    
    let tailLen = keyLen % TonWalletAppletConstants.DATA_PORTION_MAX_SIZE
    if tailLen > 0 {
      getKeyPromise = getKeyPromise.then{(response : Data) -> Promise<Data> in
        self.checkStateAndGetSault()
      }
      .then{(sault : Data) -> Promise<Data> in
        return self.apduRunner.sendAppletApduAndCheckAppletState(apduCommandPromise: TonWalletAppletApduCommands.getGetKeyChunkPromise(ind: ind, startPos: startPos, sault: sault.bytes, le: tailLen))
      }
      .then{(keyChunk : Data) -> Promise<Data> in
        key.append(keyChunk)
        return Promise<Data> { promise in promise.fulfill(key)}
      }
    }
    return getKeyPromise.then{(key : Data) -> Promise<String> in
      Promise<String> { promise in promise.fulfill(key.hexEncodedString())}
    }
  }
  
  func deleteKeyFromKeyChain(keyMac: String) {
    print("Start card operation: deleteKeyFromKeyChain" )
    print("Got mac: " + keyMac)
    let macBytes = ByteArrayAndHexHelper.hexStrToUInt8Array(hexStr: keyMac)
    
    var deleteKeyPromise = Promise<Data> { promise in
      guard self.keyMacs.contains(Data(_ : macBytes)) else {
        throw "Key with mac \(keyMac) does not exist. You can not get it."
      }
      promise.fulfill(Data(_ : []))
    }
    .then{(response : Data) -> Promise<Data> in
      self.selectTonAppletAndGetSault()
    }
    .then{(sault : Data) -> Promise<Data> in
      self.apduRunner.sendAppletApduAndCheckAppletState(apduCommandPromise: TonWalletAppletApduCommands.getGetIndexAndLenOfKeyInKeyChainApduPromise(keyHmac: macBytes, sault: sault.bytes))
    }
    .then{(response : Data) -> Promise<Data> in
      let keyLen = ByteArrayAndHexHelper.makeShort(src : response.bytes, srcOff : 2)
      print("keyIndex = " + String(ByteArrayAndHexHelper.makeShort(src : response.bytes, srcOff : 0)))
      print("keyLen = " + String(keyLen))
      return self.initiateDeleteOfKey(keyIndex: response.bytes[range : 0...1])
    }
    
    /* var deleteKeyChunkIsDone = 0
     while deleteKeyChunkIsDone == 0 {
     var newDeleteKeyPromise = deleteKeyPromise.then{(response : Data) -> Promise<Data> in
     self.deleteKeyChunk()
     }
     .then{ (response : Data) -> Promise<Data> in
     deleteKeyChunkIsDone = response.bytes[0]
     return Promise<Data> { promise in promise.fulfill(Data(_ : []))}
     }
     deleteKeyPromise = newDeleteKeyPromisese
     }
     
     var deleteKeyRecordIsDone = 0
     while deleteKeyRecordIsDone == 0 {
     var newDeleteKeyPromise = deleteKeyPromise.then{(response : Data) -> Promise<Data> in
     self.deleteKeyChunk()
     }
     .then{ (response : Data) -> Promise<Data> in
     deleteKeyChunkIsDone = response.bytes[0]
     return Promise<Data> { promise in promise.fulfill(Data(_ : []))}
     }
     deleteKeyPromise = newDeleteKeyPromise
     }*/
    
    //  apduRunner.setCardOperation(cardOperation: { () in
    // return deleteKeyPromise
    //})
    
    
  }
  
  /*
   public int deleteKeyFromKeyChain(byte[] macBytes) throws Exception {
   //        System.out.println("\n\n\n Delete key from keychain: ");
   //        String mac = ByteArrayHelper.hex(macBytes);
   //        int lenOfKey = keyChainData.get(mac).length() / 2;
   //        System.out.println("mac to delete = " + mac);
   //        System.out.println("key to delete = " + keyChainData.get(mac));
   //        System.out.println("key length to delete = " + lenOfKey + "\n\n");
   
   byte[] data = getIndexAndLenOfKeyInKeyChain(macBytes).getData();
   byte[] index  = bSub(data, 0, 2);
   
   
   initiateDeleteOfKey(index);
   
   int deleteKeyChunkIsDone = 0;
   while (deleteKeyChunkIsDone == 0) {
   deleteKeyChunkIsDone = deleteKeyChunk().getData()[0];
   }
   
   int deleteKeyRecordIsDone = 0;
   while (deleteKeyRecordIsDone == 0) {
   deleteKeyRecordIsDone = deleteKeyRecord().getData()[0];
   }
   
   int numOfKeysInKeyChain = ByteArrayHelper.makeShort(getNumberOfKeys().getData(), 0);
   
   int ind = keyMacs.indexOf(ByteArrayHelper.hex(macBytes));
   keyMacs.remove(ind);
   return numOfKeysInKeyChain;
   }   */
  
  private func initiateDeleteOfKey(keyIndex: [UInt8]) -> Promise<Data> {
    //  let keyIndexBytes = [UInt8(keyIndex >> 8), UInt8(keyIndex)]
    return self.selectTonAppletAndGetSault()
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getInitiateDeleteOfKeyApduPromise(index:keyIndex, sault: sault.bytes))
    }
  }
  
  private func deleteKeyChunk() -> Promise<Data>{
    self.selectTonAppletAndGetSault()
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getDeleteKeyChunkApduPromise(sault: sault.bytes))
    }
  }
  
  func deleteKeyRecord() -> Promise<Data> {
    self.selectTonAppletAndGetSault()
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getDeleteKeyRecordApduPromise(sault: sault.bytes))
    }
  }
  
  func resetKeyChain() {
    print("Start card operation resetKeyChain" )
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndGetSault()
        .then{(sault : Data) -> Promise<Data> in
          self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getResetKeyChainApduPromise(sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
        self.keyMacs = []
        return Promise<String> { promise in promise.fulfill("done")}
      }
    })
    apduRunner.startScan()
  }
  
  
  func getNumberOfKeys() {
    print("Start card operation: getNumberOfKeys" )
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndGetSault()
        .then{(sault : Data) -> Promise<Data> in
          self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getNumberOfKeysApduPromise(sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
        return Promise<String> { promise in promise.fulfill(String(ByteArrayAndHexHelper.makeShort(src: response.bytes, srcOff: 0)))}
      }
    })
    apduRunner.startScan()
  }
  
  func getOccupiedStorageSize() {
    print("Start card operation: getOccupiedSize")
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndGetSault()
        .then{(sault : Data) -> Promise<Data> in
          self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getGetOccupiedSizeApduPromise(sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
        return Promise<String> { promise in promise.fulfill(String(ByteArrayAndHexHelper.makeShort(src: response.bytes, srcOff: 0)))}
      }
    })
    apduRunner.startScan()
  }
  
  func getFreeStorageSize() {
    print("Start card operation: getFreeSize")
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndGetSault()
        .then{(sault : Data) -> Promise<Data> in
          self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getGetFreeSizeApduPromise(sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
        return Promise<String> { promise in promise.fulfill(String(ByteArrayAndHexHelper.makeShort(src: response.bytes, srcOff: 0)))}
      }
    })
    apduRunner.startScan()
  }
  
  func checkKeyHmacConsistency(keyHmac: String){
    print("Start card operation: checkKeyHmacConsistency" )
    print("Got keyHmac:" + keyHmac)
    let keyHmacBytes = ByteArrayAndHexHelper.hexStrToUInt8Array(hexStr: keyHmac)
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndGetSault()
        .then{(sault : Data) -> Promise<Data> in
          self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getCheckKeyHmacConsistencyApduPromise(keyHmac: keyHmacBytes, sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
        return Promise<String> { promise in promise.fulfill("done")}
      }
    })
    apduRunner.startScan()
  }
  
  func getIndexAndLenOfKeyInKeyChain(keyHmac: String){
    print("Start card operation: getIndexAndLenOfKeyInKeyChain" )
    print("Got keyHmac:" + keyHmac)
    let keyHmacBytes = ByteArrayAndHexHelper.hexStrToUInt8Array(hexStr: keyHmac)
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndGetSault()
        .then{(sault : Data) -> Promise<Data> in
          self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getGetIndexAndLenOfKeyInKeyChainApduPromise(keyHmac: keyHmacBytes, sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
        return Promise<String> { promise in promise.fulfill(response.hexEncodedString())}
      }
    })
    apduRunner.startScan()
  }
  
  func checkAvailableVolForNewKey(keySize: String){
    print("Start card operation: checkAvailableVolForNewKey")
    print("Got keySize:" + keySize)
    let keySize = UInt16(keySize)
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndGetSault()
        .then{(sault : Data) -> Promise<Data> in
          self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getCheckAvailableVolForNewKeyApduPromise(keySize: [UInt8(keySize! >> 8), UInt8(keySize!)], sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
        return Promise<String> { promise in promise.fulfill("done")}
      }
    })
    apduRunner.startScan()
    
  }
  
  private func getHmac(keyIndex: UInt16) -> Promise<Data> {
    self.selectTonAppletAndGetSault()
      .then{(sault : Data) -> Promise<Data> in
        self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getGetHmacApduPromise(ind: [UInt8(keyIndex >> 8), UInt8(keyIndex)], sault: sault.bytes))
    }
  }
  
  func getHmac(index: String){
    print("Start card operation: getHmac" )
    print("Got index of key:" + index)
    let keyIndex = UInt16(index)!
    //let keyIndexBytes =
    apduRunner.setCardOperation(cardOperation: { () in
      self.selectTonAppletAndGetSault()
        .then{(sault : Data) -> Promise<Data> in
          self.apduRunner.sendApdu(apduCommandPromise: TonWalletAppletApduCommands.getGetHmacApduPromise(ind: [UInt8(keyIndex >> 8), UInt8(keyIndex)], sault: sault.bytes))
      }
      .then{(response : Data)  -> Promise<String> in
        return Promise<String> { promise in promise.fulfill(response.hexEncodedString())}
      }
    })
    apduRunner.startScan()
  }
}
