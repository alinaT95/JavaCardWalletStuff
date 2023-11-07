//
//  TonWalletAppletApduCommands.swift
//  TestNfcSwiftNew
//
//  Created by Alina Alinovna on 14.06.2020.
//

import Foundation
import CoreNFC
import PromiseKit

@available(iOS 13.0, *)
class TonWalletAppletApduCommands {
  static let INS_SELECT:UInt8 = 0xA4;
  
  //Applet stuff
  
  static let WALLET_APPLET_AID: [UInt8] = [0x31, 0x31, 0x32, 0x32, 0x33, 0x33, 0x34, 0x34, 0x35, 0x35, 0x36, 0x36]
  
  static let WALLET_APPLET_CLA:UInt8 = 0xB0
  /****************************************
   * Instruction codes *
   ****************************************
   */
  //Personalization
  static let INS_FINISH_PERS:UInt8 = 0x90;
  static let INS_SET_ENCRYPTED_PASSWORD_FOR_CARD_AUTHENTICATION:UInt8 = 0x91
  static let INS_SET_COMMON_SECRET:UInt8 = 0x94
  
  //Waite for authentication mode
  static let INS_VERIFY_PASSWORD:UInt8 = 0x92
  static let INS_GET_HASH_OF_ENCRYPTED_PASSWORD:UInt8 = 0x93
  static let INS_GET_HASH_OF_COMMON_SECRET:UInt8 = 0x95
     
  // Main mode
  static let INS_GET_PUBLIC_KEY:UInt8 = 0xA0
  static let INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH:UInt8 = 0xA7
  static let INS_VERIFY_PIN:UInt8 = 0xA2
  static let INS_SIGN_SHORT_MESSAGE:UInt8 = 0xA3
  static let INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH:UInt8 = 0xA5
      
  static let INS_CHECK_KEY_HMAC_CONSISTENCY:UInt8 = 0xB0
  static let INS_GET_KEY_INDEX_IN_STORAGE_AND_LEN:UInt8 = 0xB1
  static let INS_GET_KEY_CHUNK:UInt8 = 0xB2
  static let INS_CHECK_AVAILABLE_VOL_FOR_NEW_KEY:UInt8 = 0xB3
  static let INS_ADD_KEY_CHUNK:UInt8 = 0xB4
  static let INS_INITIATE_CHANGE_OF_KEY:UInt8 = 0xB5
  static let INS_CHANGE_KEY_CHUNK:UInt8 = 0xB6
  static let INS_INITIATE_DELETE_KEY:UInt8 = 0xB7
  
  static let INS_GET_NUMBER_OF_KEYS:UInt8 = 0xB8
  static let INS_GET_FREE_STORAGE_SIZE:UInt8 = 0xB9
  static let INS_GET_OCCUPIED_STORAGE_SIZE:UInt8 = 0xBA
  static let INS_GET_HMAC:UInt8 = 0xBB
  static let INS_RESET_KEYCHAIN:UInt8 = 0xBC
  
  static let INS_DELETE_KEY_CHUNK:UInt8 = 0xBE
  static let INS_DELETE_KEY_RECORD:UInt8 = 0xBF
  
  static let INS_GET_SAULT:UInt8 = 0xBD
  static let INS_GET_APP_INFO:UInt8 = 0xC1
  
  static let GET_APP_INFO_LE: Int = 0x01
  static let GET_NUMBER_OF_KEYS_LE: Int = 0x02
  static let GET_KEY_INDEX_IN_STORAGE_AND_LEN_LE: Int = 0x04
  static let INITIATE_DELETE_KEY_LE: Int = 0x02
  static let GET_FREE_SIZE_LE: Int = 0x02
  static let GET_OCCUPIED_SIZE_LE: Int = 0x02
  static let SEND_CHUNK_LE: Int = 0x02
  static let DELETE_KEY_CHUNK_LE: Int = 0x01
  static let DELETE_KEY_RECORD_LE: Int = 0x01
  
    
  static let APDU_COMMAND_NAMES = [INS_SELECT: "SELECT_TON_APPLET",
                                   INS_FINISH_PERS : "FINISH_PERS",
                                   INS_SET_ENCRYPTED_PASSWORD_FOR_CARD_AUTHENTICATION : "SET_ENCRYPTED_PASSWORD_FOR_CARD_AUTHENTICATION",
                                   INS_SET_COMMON_SECRET : "SET_COMMON_SECRET",
                                   INS_VERIFY_PASSWORD : "VERIFY_PASSWORD",
                                   INS_GET_HASH_OF_ENCRYPTED_PASSWORD :  "GET_HASH_OF_ENCRYPTED_PASSWORD",
                                   INS_GET_HASH_OF_COMMON_SECRET : "GET_HASH_OF_COMMON_SECRET",
                                   INS_VERIFY_PIN : "VERIFY_PIN",
                                   INS_GET_PUBLIC_KEY : "GET_PUBLIC_KEY",
                                   INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH : "GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH",
                                   INS_SIGN_SHORT_MESSAGE: "SIGN_SHORT_MESSAGE",
                                   INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH : "SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH",
                                   INS_GET_APP_INFO: "GET_APP_INFO",
                                   INS_GET_KEY_INDEX_IN_STORAGE_AND_LEN :  "GET_KEY_INDEX_IN_STORAGE_AND_LEN",
                                   INS_GET_KEY_CHUNK : "GET_KEY_CHUNK",
                                    INS_CHECK_AVAILABLE_VOL_FOR_NEW_KEY : "CHECK_AVAILABLE_VOL_FOR_NEW_KEY",
                                    INS_ADD_KEY_CHUNK : "ADD_KEY_CHUNK",
                                    INS_CHANGE_KEY_CHUNK : "CHANGE_KEY_CHUNK",
                                    INS_DELETE_KEY_CHUNK : "DELETE_KEY_CHUNK",
                                    INS_INITIATE_DELETE_KEY : "INITIATE_DELETE_KEY",
                                    INS_DELETE_KEY_RECORD : "DELETE_KEY_RECORD",
                                    INS_GET_NUMBER_OF_KEYS : "GET_NUMBER_OF_KEYS",
                                    INS_GET_FREE_STORAGE_SIZE : "GET_FREE_STORAGE_SIZE",
                                    INS_GET_OCCUPIED_STORAGE_SIZE : "GET_OCCUPIED_STORAGE_SIZE",
                                    INS_GET_HMAC : "GET_HMAC",
                                    INS_RESET_KEYCHAIN : "RESET_KEYCHAIN",
                                    INS_GET_SAULT : "GET_SAULT",
                                    INS_CHECK_KEY_HMAC_CONSISTENCY : "CHECK_KEY_HMAC_CONSISTENCY"  ]

  
static let APPLET_COMMAND_STATE_MAPPING = [
    INS_FINISH_PERS : TonWalletAppletConstants.INSTALLED_STATE,
    INS_SET_ENCRYPTED_PASSWORD_FOR_CARD_AUTHENTICATION: TonWalletAppletConstants.INSTALLED_STATE,
    INS_SET_COMMON_SECRET : TonWalletAppletConstants.INSTALLED_STATE,
    INS_VERIFY_PASSWORD : TonWalletAppletConstants.WAITE_AUTHORIZATION_STATE,
    INS_GET_HASH_OF_COMMON_SECRET : TonWalletAppletConstants.WAITE_AUTHORIZATION_STATE,
    INS_GET_HASH_OF_ENCRYPTED_PASSWORD : TonWalletAppletConstants.WAITE_AUTHORIZATION_STATE,
    INS_VERIFY_PIN : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_GET_PUBLIC_KEY : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_SIGN_SHORT_MESSAGE : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_GET_KEY_INDEX_IN_STORAGE_AND_LEN : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_GET_KEY_CHUNK : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_DELETE_KEY_CHUNK : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_DELETE_KEY_RECORD : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_INITIATE_DELETE_KEY : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_GET_NUMBER_OF_KEYS : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_GET_FREE_STORAGE_SIZE : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_GET_OCCUPIED_STORAGE_SIZE : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_GET_HMAC : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_RESET_KEYCHAIN : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_GET_SAULT : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_CHECK_KEY_HMAC_CONSISTENCY : TonWalletAppletConstants.PERSONALIZED_AND_DELETE_STATE,
    INS_CHECK_AVAILABLE_VOL_FOR_NEW_KEY : TonWalletAppletConstants.PERSONALIZED_STATE,
    INS_ADD_KEY_CHUNK : TonWalletAppletConstants.PERSONALIZED_STATE,
    INS_INITIATE_CHANGE_OF_KEY : TonWalletAppletConstants.PERSONALIZED_STATE,
    INS_CHANGE_KEY_CHUNK : TonWalletAppletConstants.PERSONALIZED_STATE,
    INS_GET_APP_INFO : TonWalletAppletConstants.ALL_APPLET_STATES
  ]
  
  static let SELECT_TON_WALLET_APPLET_APDU = NFCISO7816APDU(instructionClass:0x00, instructionCode:0xA4, p1Parameter:0x04, p2Parameter:0x00, data: Data(_ :  WALLET_APPLET_AID),expectedResponseLength:-1)
  
  static let GET_APP_INFO_APDU =  NFCISO7816APDU(instructionClass: WALLET_APPLET_CLA, instructionCode: INS_GET_APP_INFO, p1Parameter:0x00, p2Parameter:0x00, data: Data(), expectedResponseLength: GET_APP_INFO_LE)
  
  static let GET_HASH_OF_ENCRYPTED_PASSWORD_APDU = NFCISO7816APDU(instructionClass: WALLET_APPLET_CLA, instructionCode: INS_GET_HASH_OF_ENCRYPTED_PASSWORD, p1Parameter:0x00, p2Parameter:0x00, data: Data(), expectedResponseLength: TonWalletAppletConstants.SHA_HASH_SIZE)
  
  static let GET_HASH_OF_COMMON_SECRET_APDU = NFCISO7816APDU(instructionClass: WALLET_APPLET_CLA, instructionCode: INS_GET_HASH_OF_COMMON_SECRET, p1Parameter:0x00, p2Parameter:0x00, data: Data(), expectedResponseLength: TonWalletAppletConstants.SHA_HASH_SIZE)
  
  static let GET_PUB_KEY_WITH_DEFAULT_PATH_APDU =  NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH, p1Parameter:0x00, p2Parameter:0x00, data: Data(), expectedResponseLength:TonWalletAppletConstants.PK_LEN)
  
  static let GET_SAULT_APDU = NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_GET_SAULT, p1Parameter:0x00, p2Parameter:0x00, data: Data(), expectedResponseLength:TonWalletAppletConstants.SAULT_LENGTH)
  
  static func getVerifyPasswordApdu(password: [UInt8], initialVector : [UInt8]) throws -> NFCISO7816APDU {
    if password.count != TonWalletAppletConstants.PASSWORD_SIZE || initialVector.count != TonWalletAppletConstants.IV_SIZE {
     throw "Bad length of password (must be 32 bytes) or IV (must be 16 bytes)."
    }
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_VERIFY_PASSWORD, p1Parameter:0x00, p2Parameter:0x00, data: Data(_ : password + initialVector), expectedResponseLength:-1)
  }
  
  static func getVerifyPasswordApduPromise(password: [UInt8], initialVector : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getVerifyPasswordApdu(password: password, initialVector : initialVector)) }
  }
  
  static func getVerifyPinApdu(pinBytes: [UInt8], sault : [UInt8]) throws -> NFCISO7816APDU {
    if pinBytes.count != TonWalletAppletConstants.PIN_SIZE || sault.count != TonWalletAppletConstants.SAULT_LENGTH {
     throw "Bad length of PIN (must be 4 bytes) or sault (must be 32 bytes)."
    }
    let data = try prepareApduData(dataChunk : pinBytes + sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_VERIFY_PIN, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:-1)
  }
  
  static func getVerifyPinApduPromise(pinBytes: [UInt8], sault : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getVerifyPinApdu(pinBytes: pinBytes, sault: sault))}
  }
  
  static func getSignShortMessageWithDefaultPathApdu(dataForSigning: [UInt8], sault : [UInt8]) throws -> NFCISO7816APDU {
    if (2 + dataForSigning.count + TonWalletAppletConstants.SAULT_LENGTH + TonWalletAppletConstants.HMAC_SHA_SIG_SIZE) > TonWalletAppletConstants.APDU_DATA_MAX_SIZE || dataForSigning.count == 0 || dataForSigning.count > TonWalletAppletConstants.DATA_FOR_SIGNING_MAX_SIZE || sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad data for signing length."
    }
    let data = try prepareApduData(dataChunk : [0x00, UInt8(dataForSigning.count)] + dataForSigning + sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:TonWalletAppletConstants.SIG_LEN)
  }
  
  static func getSignShortMessageWithDefaultPathApduPromise(dataForSigning: [UInt8], sault : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getSignShortMessageWithDefaultPathApdu(dataForSigning: dataForSigning, sault: sault))}
  }
  
  static func getSignShortMessageApdu(dataForSigning: [UInt8], ind:[UInt8], sault: [UInt8]) throws -> NFCISO7816APDU {
    if (3 + dataForSigning.count + ind.count + TonWalletAppletConstants.SAULT_LENGTH + TonWalletAppletConstants.HMAC_SHA_SIG_SIZE) > TonWalletAppletConstants.APDU_DATA_MAX_SIZE  || dataForSigning.count == 0 ||
      dataForSigning.count > TonWalletAppletConstants.DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH || ind.count > TonWalletAppletConstants.MAX_IND_SIZE || sault.count != TonWalletAppletConstants.SAULT_LENGTH {
     throw "Bad data for signing length."
    }
    let data = try prepareApduData( dataChunk : [0x00, UInt8(dataForSigning.count)] + dataForSigning + [UInt8(ind.count)] + ind + sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_SIGN_SHORT_MESSAGE, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:TonWalletAppletConstants.SIG_LEN)
  }
  
  static func getSignShortMessageApduPromise(dataForSigning: [UInt8], ind: [UInt8], sault: [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getSignShortMessageApdu(dataForSigning: dataForSigning, ind: ind, sault: sault))}
  }
  
  static func getPublicKeyApdu(ind: [UInt8]) throws -> NFCISO7816APDU {
    if ind.count > TonWalletAppletConstants.MAX_IND_SIZE {
     throw "Bad hd index length."
    }
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_GET_PUBLIC_KEY, p1Parameter:0x00, p2Parameter:0x00, data: Data(_ : ind), expectedResponseLength:TonWalletAppletConstants.PK_LEN)
  }
  
  static func getPublicKeyApduPromise(ind: [UInt8]) -> Promise<NFCISO7816APDU> {
    return Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getPublicKeyApdu(ind: ind))}
  }
  
  static func getResetKeyChainApdu(sault : [UInt8]) throws -> NFCISO7816APDU {
    if sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad sault length."
    }
    let data = try prepareApduData(dataChunk : sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_RESET_KEYCHAIN, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:-1)
  }
  
  static func getResetKeyChainApduPromise(sault : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getResetKeyChainApdu(sault: sault))}
  }
  
  static func getNumberOfKeysApdu(sault : [UInt8]) throws -> NFCISO7816APDU {
    if sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad sault length."
    }
    let data = try prepareApduData(dataChunk : sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_GET_NUMBER_OF_KEYS, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:GET_NUMBER_OF_KEYS_LE)
  }
  
  static func getNumberOfKeysApduPromise(sault : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getNumberOfKeysApdu(sault: sault))}
  }
  
  static func getCheckKeyHmacConsistencyApdu(keyHmac: [UInt8], sault : [UInt8]) throws -> NFCISO7816APDU {
    if keyHmac.count != TonWalletAppletConstants.HMAC_SHA_SIG_SIZE || sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad sault or hmac length."
    }
    let data = try prepareApduData(dataChunk : keyHmac + sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_CHECK_KEY_HMAC_CONSISTENCY, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:-1)
  }
  
  static func getCheckKeyHmacConsistencyApduPromise(keyHmac: [UInt8], sault: [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getCheckKeyHmacConsistencyApdu(keyHmac: keyHmac, sault: sault))}
  }
  
  static func getCheckAvailableVolForNewKeyApdu(keySize: [UInt8], sault : [UInt8]) throws -> NFCISO7816APDU {
    if keySize.count != 2 || sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad sault or keySize length."
    }
    let data = try prepareApduData(dataChunk : keySize + sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_CHECK_AVAILABLE_VOL_FOR_NEW_KEY, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:-1)
  }
  
  static func getCheckAvailableVolForNewKeyApduPromise(keySize: [UInt8], sault: [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getCheckAvailableVolForNewKeyApdu(keySize: keySize, sault: sault))}
  }
  
  static func getInitiateChangeOfKeyApdu(index: [UInt8], sault: [UInt8]) throws -> NFCISO7816APDU {
    if index.count != 2 || sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad key index or sault length."
    }
    let data = try prepareApduData(dataChunk : index + sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_INITIATE_CHANGE_OF_KEY, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:-1)
  }
  
  static func getInitiateChangeOfKeyApduPromise(index: [UInt8], sault: [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getInitiateChangeOfKeyApdu(index: index, sault: sault))}
  }
  
  static func getGetIndexAndLenOfKeyInKeyChainApdu(keyHmac:[UInt8], sault : [UInt8]) throws -> NFCISO7816APDU {
    if keyHmac.count != TonWalletAppletConstants.HMAC_SHA_SIG_SIZE || sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad sault or hmac length."
    }
    let data = try prepareApduData(dataChunk : keyHmac + sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_GET_KEY_INDEX_IN_STORAGE_AND_LEN, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:GET_KEY_INDEX_IN_STORAGE_AND_LEN_LE)
  }
  
  static func getGetIndexAndLenOfKeyInKeyChainApduPromise(keyHmac: [UInt8], sault: [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getGetIndexAndLenOfKeyInKeyChainApdu(keyHmac: keyHmac, sault: sault))}
  }
  
  static func getInitiateDeleteOfKeyApdu(index:[UInt8], sault : [UInt8]) throws -> NFCISO7816APDU {
    if index.count != 2 || sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad key index or sault length."
    }
    let data = try prepareApduData(dataChunk : index + sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_INITIATE_DELETE_KEY, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:INITIATE_DELETE_KEY_LE)
  }
  
  static func getInitiateDeleteOfKeyApduPromise(index: [UInt8], sault: [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getInitiateDeleteOfKeyApdu(index: index, sault: sault))}
  }
  
  static func getDeleteKeyChunkApdu(sault : [UInt8]) throws -> NFCISO7816APDU {
    if sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad sault length."
    }
    let data = try prepareApduData(dataChunk : sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_DELETE_KEY_CHUNK, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:DELETE_KEY_CHUNK_LE)
  }
  
  static func getDeleteKeyChunkApduPromise(sault : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getDeleteKeyChunkApdu(sault: sault))}
  }
  
  static func getDeleteKeyRecordApdu(sault : [UInt8]) throws -> NFCISO7816APDU {
    if sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad sault length."
    }
    let data = try prepareApduData(dataChunk : sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_DELETE_KEY_RECORD, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:DELETE_KEY_RECORD_LE)
  }
  
  static func getDeleteKeyRecordApduPromise(sault : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getDeleteKeyRecordApdu(sault: sault))}
  }
  
  static func getGetOccupiedSizeApdu(sault : [UInt8]) throws -> NFCISO7816APDU {
    if sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad sault length."
    }
    let data = try prepareApduData(dataChunk : sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_GET_OCCUPIED_STORAGE_SIZE, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:GET_OCCUPIED_SIZE_LE)
  }
  
  static func getGetOccupiedSizeApduPromise(sault : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getGetOccupiedSizeApdu(sault: sault))}
  }
  
  static func getGetFreeSizeApdu(sault : [UInt8]) throws -> NFCISO7816APDU {
    if sault.count != TonWalletAppletConstants.SAULT_LENGTH {
       throw "Bad sault length."
    }
    let data = try prepareApduData(dataChunk : sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_GET_FREE_STORAGE_SIZE, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength:GET_FREE_SIZE_LE)
  }
  
  static func getGetFreeSizeApduPromise(sault : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getGetFreeSizeApdu(sault: sault))}
  }
  
  static func getGetHmacApdu(ind : [UInt8], sault : [UInt8]) throws -> NFCISO7816APDU {
    if ind.count != 2 || sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad key index or sault length."
    }
    let data = try prepareApduData(dataChunk : ind + sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_GET_HMAC, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength: Int(TonWalletAppletConstants.HMAC_SHA_SIG_SIZE + 2))
  }
  
  static func getGetHmacApduPromise(ind : [UInt8],  sault : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU> { promise in promise.fulfill(try getGetHmacApdu(ind: ind, sault: sault))}
  }
  
  static func getGetKeyChunkApdu(ind : [UInt8], startPos: UInt16, sault : [UInt8], le : Int) throws -> NFCISO7816APDU {
    if ind.count != 2 || sault.count != TonWalletAppletConstants.SAULT_LENGTH || le > TonWalletAppletConstants.DATA_PORTION_MAX_SIZE {
      throw "Bad key index or sault length."
    }
    let data = try prepareApduData(dataChunk : ind + [UInt8(startPos >> 8), UInt8(startPos)] + sault)
    return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_GET_KEY_CHUNK, p1Parameter:0x00, p2Parameter:0x00, data: data, expectedResponseLength: le)
  }
  
  static func getGetKeyChunkPromise(ind : [UInt8], startPos: UInt16, sault : [UInt8], le : Int) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU>{ promise in promise.fulfill(try getGetKeyChunkApdu(ind : ind, startPos: startPos, sault : sault, le : le))}
  }
  
  static func getAddKeyChunkApduPromise(p1 : UInt8, keyChunkOrMacBytes : [UInt8], sault : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU> { promise in promise.fulfill(try getAddKeyChunkApdu(p1 : p1, keyChunkOrMacBytes : keyChunkOrMacBytes, sault : sault))}
  }
  
  static func getAddKeyChunkApdu(p1 : UInt8, keyChunkOrMacBytes : [UInt8], sault : [UInt8]) throws -> NFCISO7816APDU {
    try getSendKeyChunkApdu(ins: INS_ADD_KEY_CHUNK, p1: p1, keyChunkOrMacBytes:keyChunkOrMacBytes, sault: sault)
  }
  
  static func getChangeKeyChunkApdu(p1 : UInt8, keyChunkOrMacBytes : [UInt8], sault : [UInt8] ) throws -> NFCISO7816APDU {
    try getSendKeyChunkApdu(ins: INS_CHANGE_KEY_CHUNK, p1: p1, keyChunkOrMacBytes:keyChunkOrMacBytes, sault: sault)
  }
  
  static func getSendKeyChunkApduPromise(ins : UInt8, p1 : UInt8, keyChunkOrMacBytes : [UInt8], sault : [UInt8]) -> Promise<NFCISO7816APDU> {
    Promise<NFCISO7816APDU> { promise in promise.fulfill(try getSendKeyChunkApdu(ins : ins, p1 : p1, keyChunkOrMacBytes : keyChunkOrMacBytes, sault : sault))}
  }
  
  static func getSendKeyChunkApdu(ins : UInt8, p1 : UInt8, keyChunkOrMacBytes : [UInt8], sault : [UInt8] ) throws -> NFCISO7816APDU {
    if keyChunkOrMacBytes.count > TonWalletAppletConstants.DATA_PORTION_MAX_SIZE || keyChunkOrMacBytes.count == 0 || sault.count != TonWalletAppletConstants.SAULT_LENGTH {
      throw "Bad keyChunkOrMacBytes length."
    }
    let data = (p1 == 2) ? try prepareApduData(dataChunk : keyChunkOrMacBytes + sault) :
      try prepareApduData(dataChunk : [UInt8(keyChunkOrMacBytes.count)] + keyChunkOrMacBytes + sault)
    return (p1 == 2) ? NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:ins, p1Parameter:p1, p2Parameter:0x00, data: Data(_ : data), expectedResponseLength: SEND_CHUNK_LE) :
    NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:ins, p1Parameter:p1, p2Parameter:0x00, data: data, expectedResponseLength: -1)
  }
  
  static func prepareApduData(dataChunk : [UInt8]) throws -> Data {
    var dataField = Data(_  :  dataChunk)
    var mac = try HmacHelper.computeHmac(data: dataField)
    dataField.append(mac)
    return dataField
  }

}

