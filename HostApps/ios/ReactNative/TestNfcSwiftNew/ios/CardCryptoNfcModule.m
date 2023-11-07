//
//  CardCryptoNfcModule.m
//  TestNfcSwiftNew
//
//  Created by Alina Alinovna on 21.07.2020.
//

#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(CardCryptoNfcModule, NSObject)

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
  signForDefaultHdPath: (NSString *)data
  pin: (NSString *)pin
  resolve:(RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject
)
RCT_EXTERN_METHOD(
  sign: (NSString *)data
  hdIndex:(NSString *)hdIndex
  pin: (NSString *)pin
  resolve:(RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject
)
@end
