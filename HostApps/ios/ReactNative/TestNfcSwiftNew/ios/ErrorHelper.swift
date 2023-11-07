import Foundation

class ErrorHelper: NSObject {
  static func callRejectWith(errMsg :  String, reject: @escaping RCTPromiseRejectBlock) {
    let error = NSError(domain: "DATA_FORMAT_ERROR", code: 0x00, userInfo: [NSLocalizedDescriptionKey: errMsg])
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
      reject("DATA_FORMAT_ERROR", "Incorrect data format: " + error.localizedDescription, error)
    }
  }
}
