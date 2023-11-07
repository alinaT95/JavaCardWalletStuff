//
//  ViewController.swift
//  TestNdef
//
//  Created by Alina Alinovna on 06.05.2020.
//  Copyright © 2020 Alina Alinovna. All rights reserved.
//

import UIKit
import CoreNFC

extension Data {
    public var bytes: [UInt8]
    {
        return [UInt8](self)
    }
}

@available(iOS 13.0, *)
class ViewController: UIViewController, NFCTagReaderSessionDelegate /*NFCNDEFReaderSessionDelegate */ {

    @IBOutlet weak var NFCText: UITextView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }

    var nfcSession: NFCNDEFReaderSession?
    var session:NFCTagReaderSession?
    var word = "none"
    
    @IBAction func ScanBtn(_ sender: Any) {
     /*   nfcSession = NFCNDEFReaderSession.init(delegate: self, queue: nil, invalidateAfterFirstRead: false)
        nfcSession?.begin()*/
       session = NFCTagReaderSession(pollingOption: .iso14443, delegate: self, queue: DispatchQueue.global())
        session?.alertMessage = "Hold your iPhone near the ISO7816 tag to begin transaction."
        session?.begin()   }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
   func tagReaderSession(_ session: NFCTagReaderSession, didDetect tags: [NFCTag]) {
        print("hey there")
        print(tags.count)
    print("ReaderSession thread: \(Thread.current)")
    if case let NFCTag.iso7816(tag) = tags.first! {
            
            //3
            session.connect(to: tags.first!) { (error: Error?) in
                print("connected")                //4
                print("connect thread: \(Thread.current)")
                let myAPDU =
                     NFCISO7816APDU(instructionClass:0xB0, instructionCode:0xC1, p1Parameter:0x00, p2Parameter:0x00,
                                    data: Data(_ : []), expectedResponseLength:1)
                tag.sendCommand(apdu: myAPDU) { (response: Data, sw1: UInt8, sw2: UInt8, error: Error?)
                    in
                    
                    // 5
                    print(error != nil)
                    print(!(sw1 == 0x90 && sw2 == 0))
                    print(sw1)
                    print(sw2)
                    guard sw1 == 0x90 && sw2 == 0 else {
                        session.invalidate(errorMessage: "Application failure")
                        return
                    }
                    print("SW:")
                    print(sw1==0x00)
                    print(sw2 == 0)
                    print(response.bytes)
                }
            }
        }
    }
    
    func tagReaderSessionDidBecomeActive(_ session: NFCTagReaderSession) {
        print("active")
    }
    
    func tagReaderSession(_ session: NFCTagReaderSession, didInvalidateWithError error: Error) {
        print("error")
    }
    
   /* func readerSession(_ session: NFCNDEFReaderSession, didInvalidateWithError error: Error) {
        print("The session is invalid \(error.localizedDescription)")
    }
    
    func readerSession(_ session: NFCNDEFReaderSession, didDetectNDEFs messages: [NFCNDEFMessage]) {
        var result = ""
        for payload in messages[0].records {
            result += String.init(data: payload.payload.advanced(by: 3), encoding: .utf8) ?? "Format uknown"
        }
        
        DispatchQueue.main.async {
            self.NFCText.text = result
        }
    }*/
    
}

