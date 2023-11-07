#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(CardKeyChainNfcModule, NSObject)

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
