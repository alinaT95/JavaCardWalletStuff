#coding:UTF-8

from sct import *
import os

CAPFILE = "./JuBiterEd25519/bin/JuBiterPackage/javacard/JuBiterPackage.cap"
PKG_AID, APP_AIDS = cardmgr.parse_cap_aids(CAPFILE)
APP_AID = APP_AIDS[0]
SCRPAHT = "./data"

def setup():    
    apdu.power_up()
    if PKG_AID in cardmgr.list_packages():
        cardmgr.remove_package(PKG_AID)
    cardmgr.load_cap(CAPFILE)
    cardmgr.install_applet(PKG_AID, APP_AID)
    
def teardown():
    #清除应用
    apdu.reset(True)
    cardmgr.remove_package(PKG_AID)
    apdu.power_off()

def test_operation():
    apdu.reset(True)
    cardmgr.select(APP_AID)
    list = os.listdir(SCRPAHT)
    print("test demo:buildkey--sign --verify")
    apdu.send(b("00 01 00 00 "), check = b("019000"), throw="buildkey failed")
    print("test demo:keypair--sign --verify")
    apdu.send(b("00 01 01 00 "), check = b("019000"), throw="keypair failed") 
    
    for file in list:
        if file.endswith(".input") : #
            DestFile = SCRPAHT+"\\"+file
            ParseSignData(DestFile)

def ParseSignData(srcFile):
    print srcFile  
    fp = open(srcFile, 'r')
    file1=fp.readlines()
    fp.close()

    i=0
    while(i<len(file1)) :
        line = file1[i].strip()
        print line
        #if line.endswith(":"):
        line_n = line.split(':') 
        pri_data = line_n[0][0:64]
        #print "private_data and public = %s "% line_n[0] 
        print "private_data_%d = %s "% (i,pri_data )
        
        pub_data = line_n[1]
        print "pub_data_%d = %s"%  (i,pub_data)
        
        msg = line_n[2]
        print "msg_%d = %s"%  (i,msg)
      
        expect_sign = line_n[3][0:128]
        print "expect_sign_%d = %s"%  (i,expect_sign) 
        
        i = i + 1
        if msg.strip() =="":
            continue
        else:            
            dowith(pri_data,pub_data,msg,expect_sign)        
        
def sendMsg(msg):
    lenx = len(msg)/2
    blockNo = 0
    while lenx > 255:        
        apdu.send(b("00 05 00 %02X FF %s" % (blockNo,msg[blockNo*255*2: (blockNo+1)*255*2])), check=0x9000, throw="set msg failed") 
        blockNo = blockNo + 1
        lenx = len(msg[blockNo*255*2:])/2
    
    apdu.send(b("00 05 80 %02X %02X %s" % (blockNo, lenx, msg[blockNo*255*2:])), check=0x9000, throw="set msg failed") 
        
def dowith(prikey,pubkey,msg,expect_sign):
    print("set_msg")
    #apdu.send(b("00 05 00 00 %02x%s" % (len(msg)/2,msg)), check=0x9000, throw="set msg failed") 
    sendMsg(msg)
    
    #sign
    print("GC opertaion")
    apdu.send(b("00 08 00 00 "), check = 0x9000, throw="GC opertaion failed")     
    
    print("CREATE_XEC_KEY:private")
    apdu.send(b("00 02 01 00 "), check = 0x9000, throw="build privatekey failed")    

    print("set_XEC_KEY:private")
    apdu.send(b("00 03 00 00 %02x%s" % (len(prikey)/2,prikey)), check = 0x9000, throw="set privatekey failed")
    
    print("sign opertaion")
    output =apdu.send(b("00 06 00 00 "),check =None,throw = "" )
    #print "output[16] = %s"% s(output[:16])
    if output[-2:] != b("9000"):
        raise Exception("sign failed") 
    elif output[:64] != b(expect_sign) :    
       raise Exception("sign failed") 
    print("sign opertaion successful")
    
    #verify
    
    print("GC opertaion")
    apdu.send(b("00 08 00 00 "), check = 0x9000, throw="GC opertaion failed")    
    
    print("CREATE_XEC_KEY:public")
    apdu.send(b("00 02 00 00 "), check = 0x9000, throw="build publickey failed")    

    print("set_XEC_KEY:public")
    apdu.send(b("00 03 00 00 %02x%s" % (len(pubkey)/2,pubkey)), check = 0x9000, throw="set publickey failed") 

    print("get_XEC_KEY:public")
    apdu.send(b("00 04 00 00"), check = 0x9000, throw="get publickey failed") 
    
    print("verify opertaion")
    apdu.send(b("00 07 00 00 %02x%s" % (len(expect_sign)/2,expect_sign)),check =b("019000"),throw = "verify failed" )
    print("verify opertaion successful")    
    
if __name__=="__main__":
    setup()
    test_operation()  