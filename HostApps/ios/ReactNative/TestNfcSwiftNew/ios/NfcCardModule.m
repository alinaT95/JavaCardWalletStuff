//
//  NfcCardModule.m
//  TestNfcSwiftNew
//
//  Created by Alina Alinovna on 14.06.2020.
//

#import <Foundation/Foundation.h>

#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(NfcCardModule, NSObject)



/* Ton wallet applet stuff*/

RCT_EXTERN_METHOD(
  getSault: (RCTPromiseResolveBlock)resolve
  rejecter: (RCTPromiseRejectBlock)reject
)
RCT_EXTERN_METHOD(
  getTonAppletState: (RCTPromiseResolveBlock)resolve
  rejecter: (RCTPromiseRejectBlock)reject
)


@end

