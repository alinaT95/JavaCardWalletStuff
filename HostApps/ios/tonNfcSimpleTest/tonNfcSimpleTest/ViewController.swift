//
//  ViewController.swift
//  tonNfcSimpleTest
//
//  Created by Alina Alinovna on 07.05.2020.
//  Copyright © 2020 Alina Alinovna. All rights reserved.
//

import UIKit
import CoreNFC
import CommonCrypto

class ViewController: UIViewController, NFCTagReaderSessionDelegate {
    
    static let WALLET_APPLET_CLA:UInt8 = 0xB0
    /****************************************
     * Instruction codes *
     ****************************************
     */
    //Personalization
    static let INS_FINISH_PERS:UInt8 = 0x90
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
    
    static let DEFAULT_PIN:[UInt8] = [0x35, 0x35, 0x35, 0x35]
    
    static let PK_LEN = 0x20
    static let SIG_LEN = 0x40
    
    static let GET_APP_INFO_LE = 0x01
    static let GET_NUMBER_OF_KEYS_LE = 0x02
    static let GET_KEY_INDEX_IN_STORAGE_AND_LEN_LE = 0x04
    static let INITIATE_DELETE_KEY_LE = 0x02
    static let GET_FREE_SIZE_LE = 0x02
    static let GET_OCCUPIED_SIZE_LE  = 0x02
    static let SEND_CHUNK_LE  = 0x02
    static let DELETE_KEY_CHUNK_LE  = 0x01
    static let DELETE_KEY_RECORD_LE  = 0x01
    
    static let HMAC_SHA_SIG_SIZE = 32
    static let SHA_HASH_SIZE = 32
    static let SAULT_LENGTH = 32
    
    static let SAULT_APDU = NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_GET_SAULT, p1Parameter:0x00, p2Parameter:0x00, data: Data(), expectedResponseLength:SAULT_LENGTH)
    
    static let AUTHENTICATION_PASSWORD = ViewController.hex(from:
    "F4B072E1DF2DB7CF6CD0CD681EC5CD2D071458D278E6546763CBB4860F8082FE14418C8A8A55E2106CBC6CB1174F4BA6D827A26A2D205F99B7E00401DA4C15ACC943274B92258114B5E11C16DA64484034F93771547FBE60DA70E273E6BD64F8A4201A9913B386BCA55B6678CFD7E7E68A646A7543E9E439DD5B60B9615079FE")    //"1ADE7F020481F043749EC3C059CE8A743B2D6B1C0445F3F13857405E05BE1BA6D5550FAE8CDC7A7834CE468987B11BB9E33ECAD2B31FF7AF9F17266188BBF248BCE8D64A7FC7752A3634B23D7F748B677380CAE476877F153DEDD26B6740828B80C544BA75ACB477C8EC5563B2BB16939FA58E61BF54943F369693187D8392B3")
    static let COMMON_SECRET = ViewController.hex(from:"7256EFE7A77AFC7E9088266EF27A93CB01CD9432E0DB66D600745D506EE04AC4") //"7F5815A3942073A6ADC70C035780FDD09DF09AFEEA4173B92FE559C34DCA0552")
    static let INITIAL_VECTOR = ViewController.hex(from: "1A550F4B413D0E971C28293F9183EA8A")//"F8322CDC5895F61BFE626A2704CD5718")
    var keyForHmac: Data?
    
  /*  static func getVerifyPinApdu(pinBytes: [UInt8], sault : [UInt8]) -> NFCISO7816APDU {
       var data = prepareApduData(dataChunk : pinBytes + sault)
       return NFCISO7816APDU(instructionClass:WALLET_APPLET_CLA, instructionCode:INS_VERIFY_PIN, p1Parameter:0x00, p2Parameter:0x00, data: Data(_ : data), expectedResponseLength:0x00)
    }*/
    
    static func hex(from string: String) -> Data {
        .init(stride(from: 0, to: string.count, by: 2).map {
            string[string.index(string.startIndex, offsetBy: $0) ... string.index(string.startIndex, offsetBy: $0 + 1)]
        }.map {
            UInt8($0, radix: 16)!
        })
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    var session:NFCTagReaderSession?
    var apduIns: UInt8 = 0

    @IBAction func selectApplet(_ sender: Any) {
        print("\r\n Select applet")
        apduIns = 0
        startScan()
    }
    
    @IBAction func signDefault(_ sender: Any) {
        print("\r\n Get Ed25519 signature")
        apduIns = ViewController.INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH
        startScan()
    }

    @IBAction func getDefaultPublic(_ sender: Any) {
        print("\r\n Get Ed25519 public key")
        apduIns = ViewController.INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH
        startScan()
    }
    
    @IBAction func getAppletState(_ sender: Any) {
        print("\r\n Get applet state")
        apduIns = ViewController.INS_GET_APP_INFO
        startScan()
    }
    
    func showAlert(msg : String) {
        let alert = UIAlertController(title: "Alert", message: msg, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { action in
              switch action.style{
              case .default:
                    print("default")

              case .cancel:
                    print("cancel")

              case .destructive:
                    print("destructive")


        }}))
        self.present(alert, animated: true, completion: nil)
    }
    
    func startScan() {
        keyForHmac = HmacHelper.computeHmac(key : ViewController.AUTHENTICATION_PASSWORD.hash(), data :ViewController.COMMON_SECRET)
        session = NFCTagReaderSession(pollingOption: .iso14443, delegate: self)
        session?.alertMessage = "Hold your iPhone near the ISO7816 tag to begin transaction."
        session?.begin()
    }
    
    func tagReaderSession(_ session: NFCTagReaderSession, didDetect tags: [NFCTag]) {
       // print(tags.count)
        //for tag in tags {
         // print(tag)
        //}
        
        if case let NFCTag.iso7816(tag) = tags.first! {
           // print(tag.description)
            session.connect(to: tags.first!) { (error: Error?) in
                
                switch self.apduIns{
                case ViewController.INS_GET_APP_INFO:
                    let myAPDU = NFCISO7816APDU(instructionClass:ViewController.WALLET_APPLET_CLA, instructionCode:self.apduIns, p1Parameter:0x00, p2Parameter:0x00, data: Data(), expectedResponseLength:ViewController.GET_APP_INFO_LE)
                    self.sendSimpleApduCommand(nfcSession: session, nfcTag: tag, apduCommand: myAPDU)
                case  ViewController.INS_GET_PUBLIC_KEY_WITH_DEFAULT_HD_PATH:
                    let myAPDU = NFCISO7816APDU(instructionClass:ViewController.WALLET_APPLET_CLA, instructionCode:self.apduIns, p1Parameter:0x00, p2Parameter:0x00, data: Data(), expectedResponseLength:ViewController.PK_LEN)
                    self.sendSimpleApduCommand(nfcSession: session, nfcTag: tag, apduCommand: myAPDU)
                case ViewController.INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH:
                    let data:[UInt8] = [0x00, 0x04, 0x01, 0x01, 0x01, 0x01]
                    self.sendApduSignatureRequest(nfcSession: session, nfcTag: tag, dataField: data)
                case ViewController.INS_GET_NUMBER_OF_KEYS:
                    self.sendApduCommandWithSaultRequest(nfcSession: session, nfcTag: tag, cla: ViewController.WALLET_APPLET_CLA, ins: self.apduIns, p1: 0x00, p2: 0x00, dataField: [], le:ViewController.GET_NUMBER_OF_KEYS_LE)
                case ViewController.INS_GET_FREE_STORAGE_SIZE:
                    self.sendApduCommandWithSaultRequest(nfcSession: session, nfcTag: tag, cla: ViewController.WALLET_APPLET_CLA, ins: self.apduIns, p1: 0x00, p2: 0x00, dataField: [], le:ViewController.GET_FREE_SIZE_LE)
                case ViewController.INS_GET_OCCUPIED_STORAGE_SIZE:
                    self.sendApduCommandWithSaultRequest(nfcSession: session, nfcTag: tag, cla: ViewController.WALLET_APPLET_CLA, ins: self.apduIns, p1: 0x00, p2: 0x00, dataField: [], le:ViewController.GET_OCCUPIED_SIZE_LE)
                case ViewController.INS_RESET_KEYCHAIN:
                    self.sendApduCommandWithSaultRequest(nfcSession: session, nfcTag: tag, cla: ViewController.WALLET_APPLET_CLA, ins: self.apduIns, p1: 0x00, p2: 0x00, dataField: [], le: -1)
                case ViewController.INS_GET_HMAC:
                    let index:[UInt8] = [0x00, 0x00]
                    self.sendApduCommandWithSaultRequest(nfcSession: session, nfcTag: tag, cla: ViewController.WALLET_APPLET_CLA, ins: self.apduIns, p1: 0x00, p2: 0x00, dataField: index, le:ViewController.HMAC_SHA_SIG_SIZE + 2)
                case ViewController.INS_GET_HASH_OF_COMMON_SECRET:
                    let myAPDU = NFCISO7816APDU(instructionClass:ViewController.WALLET_APPLET_CLA, instructionCode:self.apduIns, p1Parameter:0x00, p2Parameter:0x00, data: Data(), expectedResponseLength:ViewController.SHA_HASH_SIZE)
                    self.sendSimpleApduCommand(nfcSession: session, nfcTag: tag, apduCommand: myAPDU)
                case ViewController.INS_GET_HASH_OF_ENCRYPTED_PASSWORD:
                    let myAPDU = NFCISO7816APDU(instructionClass:ViewController.WALLET_APPLET_CLA, instructionCode:self.apduIns, p1Parameter:0x00, p2Parameter:0x00, data: Data(), expectedResponseLength:ViewController.SHA_HASH_SIZE)
                    self.sendSimpleApduCommand(nfcSession: session, nfcTag: tag, apduCommand: myAPDU)
                case ViewController.INS_VERIFY_PASSWORD:
                    var dataField = Data(_ : [])
                    dataField.append(contentsOf: ViewController.AUTHENTICATION_PASSWORD)
                    dataField.append(contentsOf: ViewController.INITIAL_VECTOR)
                    let myAPDU = NFCISO7816APDU(instructionClass:ViewController.WALLET_APPLET_CLA, instructionCode:self.apduIns, p1Parameter:0x00, p2Parameter:0x00, data: Data(_ : dataField), expectedResponseLength: -1)
                    self.sendSimpleApduCommand(nfcSession: session, nfcTag: tag, apduCommand: myAPDU)
                default:
                    let myAPDU = NFCISO7816APDU(instructionClass:0x00, instructionCode:0xA4, p1Parameter:0x04, p2Parameter:0x00, data: Data(_ : [0x31, 0x31, 0x32, 0x32, 0x33, 0x33, 0x34, 0x34, 0x35, 0x35, 0x36, 0x36]),
                        expectedResponseLength:-1)
                    self.sendSimpleApduCommand(nfcSession: session, nfcTag: tag, apduCommand: myAPDU)
                }
            }
        }
    }
    
    func sendSimpleApduCommand(nfcSession: NFCTagReaderSession, nfcTag: NFCISO7816Tag, apduCommand: NFCISO7816APDU) {
        nfcTag.sendCommand(apdu: apduCommand) { (response: Data, sw1: UInt8, sw2: UInt8, error: Error?)
            in
            print("APDU Response status: " + String(format: "%02X, %02X", sw1, sw2))
            guard sw1 == 0x90 && sw2 == 0 else {
                nfcSession.invalidate(errorMessage: "Application failure")
                return
            }
            if response.count > 0 {
                print("APDU Response:")
                print(response.hexEncodedString())
            }
            
            DispatchQueue.main.async { () -> Void in
                 self.showAlert(msg: "APDU Response status: " + String(format: "%02X, %02X", sw1, sw2) + "\r\n APDU Response: " + response.hexEncodedString())
            }
            
            
            self.session?.invalidate()
        }
    }
    
    func sendApduSignatureRequest(nfcSession: NFCTagReaderSession, nfcTag: NFCISO7816Tag, dataField: [UInt8]) {
        nfcTag.sendCommand(apdu: ViewController.SAULT_APDU) { (saultResponse: Data, sw1: UInt8, sw2: UInt8, error: Error?)
                in
                print("GET_SAULT Response status: " + String(format: "%02X, %02X", sw1, sw2))
                guard sw1 == 0x90 && sw2 == 0 else {
                    nfcSession.invalidate(errorMessage: "Application failure")
                    return
                }
                
                var dataFieldWithSault = Data(_  :  ViewController.DEFAULT_PIN)
                dataFieldWithSault.append(contentsOf: saultResponse)
                dataFieldWithSault.append(dataFieldWithSault.authenticationCode(secretKey: self.keyForHmac!))
                let verifyPinApdu = NFCISO7816APDU(instructionClass:ViewController.WALLET_APPLET_CLA, instructionCode:ViewController.INS_VERIFY_PIN, p1Parameter:0x00, p2Parameter:0x00, data: Data(_ : dataFieldWithSault), expectedResponseLength:-1)
            
                nfcTag.sendCommand(apdu: verifyPinApdu) { (response: Data, sw1: UInt8, sw2: UInt8, error: Error?)
                    in
                    print("Verify PIN APDU Response status: " + String(format: "%02X, %02X", sw1, sw2))
                    guard sw1 == 0x90 && sw2 == 0 else {
                        nfcSession.invalidate(errorMessage: "Application failure")
                        return
                    }
                    
                    self.sendApduCommandWithSaultRequest(nfcSession: nfcSession, nfcTag: nfcTag, cla: ViewController.WALLET_APPLET_CLA, ins: ViewController.INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH, p1: 0x00, p2: 0x00, dataField: dataField, le:ViewController.SIG_LEN)
                }
            }
    }

    
    func sendApduCommandWithSaultRequest(nfcSession: NFCTagReaderSession, nfcTag: NFCISO7816Tag, cla: UInt8, ins: UInt8, p1: UInt8, p2: UInt8, dataField: [UInt8], le: Int) {
        nfcTag.sendCommand(apdu: ViewController.SAULT_APDU) { (saultResponse: Data, sw1: UInt8, sw2: UInt8, error: Error?)
            in
            
            print("GET_SAULT Response status: " + String(format: "%02X, %02X", sw1, sw2))
    
            guard sw1 == 0x90 && sw2 == 0 else {
                nfcSession.invalidate(errorMessage: "Application failure")
                return
            }
            
            var dataFieldWithSault = Data(_  :  dataField)
            dataFieldWithSault.append(contentsOf: saultResponse)
            dataFieldWithSault.append(dataFieldWithSault.authenticationCode(secretKey: self.keyForHmac!))
            let apdu = NFCISO7816APDU(instructionClass:cla, instructionCode:ins, p1Parameter:p1, p2Parameter:p2, data: Data(_ : dataFieldWithSault), expectedResponseLength:le)
            
            self.sendSimpleApduCommand(nfcSession: nfcSession, nfcTag: nfcTag, apduCommand: apdu)
        }
    }
    
    func tagReaderSessionDidBecomeActive(_ session: NFCTagReaderSession) {
        print("Nfc session active")
    }
    
    func tagReaderSession(_ session: NFCTagReaderSession, didInvalidateWithError error: Error) {
       // print("Nfc session error")

    }
    
}

