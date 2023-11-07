//
//  NfcCardModule.m
//  TonNfcCardReactNativeTest
//
//  Created by Alina Alinovna on 23.07.2020.
//

#import <Foundation/Foundation.h>

#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(NfcCardModule, NSObject)
/* Coinmanager stuff*/
RCT_EXTERN_METHOD(
                  getSeVersion: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getCsn: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getDeviceLabel: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  setDeviceLabel: (NSString *)label
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getMaxPinTries: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getRemainingPinTries: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getRootKeyStatus: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getAvailableMemory: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getAppsList: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  generateSeed: (NSString *)pin
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  resetWallet: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  changePin: (NSString *)oldPin
                  newPin:(NSString *)newPin
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )

/* Ton wallet applet stuff*/
RCT_EXTERN_METHOD(
                  getSault: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getTonAppletState: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )

/* Ton wallet applet card activation related stuff */
RCT_EXTERN_METHOD(
                  setKeyForHmac: (NSString *) password
                  commonSecret: (NSString *) commonSecret
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  turnOnWallet: (NSString *)newPin
                  password: (NSString *) password
                  commonSecret: (NSString *) commonSecret
                  initialVector: (NSString *)initialVector
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  verifyPassword: (NSString *)password
                  initialVector: (NSString *)initialVector
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  verifyHashOfCommonSecret: (NSString *)commonSecret
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  verifyHashOfEncryptedPassword: (NSString *)password
                  initialVector: (NSString *)initialVector
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getHashOfCommonSecret: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getHashOfEncryptedPassword: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )

/* Ton wallet applet ed25519 related stuff*/
RCT_EXTERN_METHOD(
                  getPublicKeyForDefaultPath: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getPublicKey: (NSString *)hdIndex
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  verifyPin: (NSString *)pin
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  verifyPinAndSignForDefaultHdPath: (NSString *)data
                  pin: (NSString *)pin
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  verifyPinAndSign: (NSString *)data
                  hdIndex:(NSString *)hdIndex
                  pin: (NSString *)pin
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )

/* Ton wallet applet keychain related stuff*/
RCT_EXTERN_METHOD(
                  getKeyChainDataAboutAllKeys: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getKeyChainInfo: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  finishDeleteKeyFromKeyChainAfterInterruption: (NSString *)keyMac
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  deleteKeyFromKeyChain: (NSString *)keyMac
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getKeyFromKeyChain: (NSString *)keyMac
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  addKeyIntoKeyChain: (NSString *)newKey
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  changeKeyInKeyChain: (NSString *)newKey
                  oldKeyHMac:(NSString *)oldKeyHMac
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  resetKeyChain: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getNumberOfKeys: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getOccupiedStorageSize: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getFreeStorageSize: (RCTPromiseResolveBlock)resolve
                  rejecter: (RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  checkKeyHmacConsistency: (NSString *)keyHmac
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  checkAvailableVolForNewKey: (NSString *)keySize
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getIndexAndLenOfKeyInKeyChain: (NSString *)keyHmac
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
RCT_EXTERN_METHOD(
                  getHmac: (NSString *)index
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
/*RCT_EXTERN_METHOD(
 initiateChangeOfKey: (NSString *)index
 resolve:(RCTPromiseResolveBlock)resolve
 rejecter:(RCTPromiseRejectBlock)reject
 )
 RCT_EXTERN_METHOD(
 initiateDeleteOfKey: (NSString *)index
 resolve:(RCTPromiseResolveBlock)resolve
 rejecter:(RCTPromiseRejectBlock)reject
 )
 RCT_EXTERN_METHOD(
 deleteKeyChunk: (RCTPromiseResolveBlock)resolve
 rejecter: (RCTPromiseRejectBlock)reject
 )
 RCT_EXTERN_METHOD(
 deleteKeyRecord: (RCTPromiseResolveBlock)resolve
 rejecter: (RCTPromiseRejectBlock)reject
 )*/

@end
