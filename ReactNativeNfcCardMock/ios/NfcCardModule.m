//
//  NfcCardModule.m
//  ReactNativeNfcCardMock
//
//  Created by Alina Alinovna on 09.06.2020.
//

#import <Foundation/Foundation.h>

#import "React/RCTBridgeModule.h"
@interface RCT_EXTERN_MODULE(NfcCardModule, NSObject)
RCT_EXTERN_METHOD(
  getTonAppletState: (RCTPromiseResolveBlock)resolve
  rejecter: (RCTPromiseRejectBlock)reject
)
RCT_EXTERN_METHOD(
  turnOnWallet: (NSString *)newPin
  password:(NSString *)password
  commonSecret:(NSString *)commonSecret
  initialVector:(NSString *)initialVector
  resolve:(RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject
)
RCT_EXTERN_METHOD(
  getPublicKeyForDefaultPath: (RCTPromiseResolveBlock)resolve
  rejecter: (RCTPromiseRejectBlock)reject
)
@end
