import Foundation
import CoreNFC
import PromiseKit

@available(iOS 13.0, *)
@objc(NfcCardModule)
class NfcCardModule: NSObject {
  var nfcApi: TonNfcApi = TonNfcApi()
  
  
  @objc
  func getSault(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getSault()
  }
  
  
  @objc
  func getTonAppletState(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    nfcApi.setPromiseStuff(resolveBlock: resolve, rejectBlock: reject)
    nfcApi.getTonAppletState()
  }
  
  

}

