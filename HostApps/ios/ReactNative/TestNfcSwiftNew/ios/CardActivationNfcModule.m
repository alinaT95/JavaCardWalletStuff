#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(CardActivationNfcModule, NSObject)

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

@end
