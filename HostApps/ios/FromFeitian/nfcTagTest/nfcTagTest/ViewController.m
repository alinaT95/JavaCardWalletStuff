//
//  ViewController.m
//  nfcTagTest
//
//  Created by pengshanshan on 2019/10/21.
//  Copyright © 2019 pengshanshan. All rights reserved.
//

#import "ViewController.h"
#import <CoreNFC/CoreNFC.h>

@interface ViewController ()<NFCTagReaderSessionDelegate>

@property(strong,nonatomic)NFCTagReaderSession *session;

@property(strong,nonatomic)id<NFCISO7816Tag> cuurentTag;

@property(copy,nonatomic) NSString * APDUType;

@property (weak, nonatomic) IBOutlet UITableView *msgTableView;

@property (strong, nonatomic) NSMutableArray *msgData;


@end

static  NSString* SELECT_ISD =         @"00A4040008A000000003000001";

static  NSString* GET_APP_LIST =       @"80CB800005DFFF028106";
static  NSString* IMPORT_MNEMONIC =
                                   @"80CB80005DDFFE5A820257043535353510042BA54A95B4E29E89A10F7BFA6F1166400AE3F096B7B0F46AFECCCB1B9B170CA02342CC4948AD9E9D2259262F28783FBDBE16AF85228F9E0945923A4D65FDF5F549115D06E404AE6880048B745B2ACBFC00";

static  NSString* EXPORT_MNEMONIC =    @"80CB80000BDFFF08820205043535353500";
static  NSString* GENERATE_SEED =      @"80CB80000BDFFE08820305043535353500";
static  NSString* CHANGE_PIN =         @"80CB800010DFFE0D82040A0435353535043535353500";
static  NSString* RESET_WALLET =       @"80CB800005DFFE02820500";
static  NSString* GET_AVA_MEMORY =     @"80CB800005DFFF028146";
static  NSString* VERIFY_PIN =         @"002003000435353535";

static  NSString* SELECT_BTC =         @"00A4040010D1560001328300424C44000042544301";
static  NSString* BTC_GET_XPUB_KEY =   @"00E6000011080F6D2F3434272F30272F30272F302F30";
static  NSString* BTC_GET_ADDRESS =    @"00F6000011080F6D2F3434272F30272F30272F302F30";

static  NSString* SELECT_ETH =        @"00A4040010D1560001328300424C44000045544801";
static  NSString* ETH_GET_XPUB_KEY =   @"00E600011208106D2F3434272F3630272F30272F302F30";
static  NSString* ETH_GET_ADDRESS =    @"00F60000106D2F3434272F3630272F30272F302F3014";

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.msgData = [NSMutableArray array];
    [_msgTableView registerClass:[UITableViewCell class] forCellReuseIdentifier:@"Cell"];
    
    _APDUType = @"0";
}

#pragma mark - action

- (IBAction)touch_select_isd:(id)sender {
    [_msgData removeAllObjects];
        _APDUType = @"OP_ISD";
        _session = [[NFCTagReaderSession alloc]initWithPollingOption:NFCPollingISO14443 delegate:self queue:dispatch_get_main_queue()];
        self.session.alertMessage = @"Hold your iPhone near the ISO7816 tag to begin transaction.";
        [self.session beginSession]; //开始识别 弹出识别提示框
}

- (IBAction)touch_select_eth:(id)sender {
    [_msgData removeAllObjects];
        _APDUType = @"OP_ETH";
        _session = [[NFCTagReaderSession alloc]initWithPollingOption:NFCPollingISO14443 delegate:self queue:dispatch_get_main_queue()];
        self.session.alertMessage = @"Hold your iPhone near the ISO7816 tag to begin transaction";
        [self.session beginSession]; //开始识别 弹出识别提示框
}

#pragma mark - tableview delegate

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 120;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return _msgData.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell"];
    cell.textLabel.text = _msgData[indexPath.row];
    cell.textLabel.numberOfLines = 0;
    return  cell;
}

#pragma mark - delegate

- (void)tagReaderSession:(NFCTagReaderSession *)session didInvalidateWithError:(NSError *)error API_AVAILABLE(ios(13.0)) API_UNAVAILABLE(watchos, macos, tvos){
        
    if (error.code == 201) {
        NSLog(@"扫描超时");
    }
    if (error.code == 200) {
        
        NSLog(@"取消扫描或扫描断开");
    }
}
- (void)tagReaderSessionDidBecomeActive:(NFCTagReaderSession *)session API_AVAILABLE(ios(13.0)) API_UNAVAILABLE(watchos, macos, tvos){

    NSLog(@"扫描");
}

- (void)tagReaderSession:(NFCTagReaderSession *)session didDetectTags:(NSArray<__kindof id<NFCTag>> *)tags API_AVAILABLE(ios(13.0)) API_UNAVAILABLE(watchos, macos, tvos){
    NSLog(@"tags:%@",tags);
    self.cuurentTag = [tags firstObject];
    __weak typeof(self) weakSelf = self;
    //建立连接
    [_session connectToTag:self.cuurentTag completionHandler:^(NSError * _Nullable error) {
           if (error) {
               weakSelf.session.alertMessage = [NSString stringWithFormat:@"connect error:%@",error] ;
               [weakSelf.session restartPolling];
            
           }else{
               
               if ( [weakSelf.APDUType  isEqual: @"OP_ETH"]) {
                   NSData *select_eth_data = [weakSelf stringToByte:SELECT_ETH];
                   NFCISO7816APDU *select_eth_apdu = [[NFCISO7816APDU alloc] initWithData:select_eth_data];
  
                  [weakSelf.cuurentTag sendCommandAPDU:select_eth_apdu completionHandler:^(NSData * _Nonnull responseData, uint8_t sw1, uint8_t sw2, NSError * _Nullable error) {
                      if (error) {
                                                     
                          NSLog(@"SELECT_ETH error:%@",error);
                             
                          weakSelf.session.alertMessage =[NSString stringWithFormat:@"SELECT_ETH error:%@",error];
                          [weakSelf.session restartPolling];
                          
                      }else{
                          NSLog(@"SELECT_ETH data:%@",responseData);
                          NSLog(@"SELECT_ETH sw1: %@", [weakSelf byteFromUInt8: sw1]);
                          NSLog(@"SELECT_ETH sw2:。 %@", [weakSelf byteFromUInt8: sw2]);
                          
                          [weakSelf.msgData addObject:[NSString stringWithFormat:@"SELECT_ETH,data:%@, sw1:%@, sw2: %@", responseData, [weakSelf byteFromUInt8: sw1], [weakSelf byteFromUInt8: sw2]]];
                          
                          NSData *get_address_data = [weakSelf stringToByte:ETH_GET_ADDRESS];
                          NFCISO7816APDU *get_address_data_apdu = [[NFCISO7816APDU alloc] initWithData:get_address_data];
                                        
                         [weakSelf.cuurentTag sendCommandAPDU:get_address_data_apdu completionHandler:^(NSData * _Nonnull responseData, uint8_t sw1, uint8_t sw2, NSError * _Nullable error) {
                                                         
                            if (error) {
                                                           
                                NSLog(@"get_address error:%@",error);

                                weakSelf.session.alertMessage =[NSString stringWithFormat:@"get_address error :%@",error];
                                [weakSelf.session restartPolling];
                                
                            }else{
                         
                                NSLog(@"get_address data：%@",responseData);
                                NSLog(@"get_address sw1 %@", [weakSelf byteFromUInt8: sw1]);
                                NSLog(@"get_address sw2 %@", [weakSelf byteFromUInt8: sw2]);
                                
                                 [weakSelf.msgData addObject:[NSString stringWithFormat:@"get_address,data:%@, sw1:%@, sw2: %@", responseData, [weakSelf byteFromUInt8: sw1], [weakSelf byteFromUInt8: sw2]]];
                                
                                [weakSelf.msgTableView reloadData];
                            }}];
                  }}];
               

               }else if ([weakSelf.APDUType  isEqual: @"OP_ISD"]){//获取卡号
  
                   NSData *select_isd_data = [weakSelf stringToByte:SELECT_ISD];
                   NFCISO7816APDU *select_isd_apdu = [[NFCISO7816APDU alloc] initWithData:select_isd_data];
                   
                   [weakSelf.cuurentTag sendCommandAPDU:select_isd_apdu completionHandler:^(NSData * _Nonnull responseData, uint8_t sw1, uint8_t sw2, NSError * _Nullable error) {
                                                    
                       if (error) {
                                                      
                           NSLog(@"SELECT_ISD error:%@",error);
                              
                           weakSelf.session.alertMessage =[NSString stringWithFormat:@"SELECT_ISD error:%@",error];
                           [weakSelf.session restartPolling];
                           
                       }else{
                        
                           NSLog(@"SELECT_ISD data：%@",responseData);
                           NSLog(@"SELECT_ISD sw1: %@", [weakSelf byteFromUInt8: sw1]);
                           NSLog(@"SELECT_ISD sw2: %@", [weakSelf byteFromUInt8: sw2]);
                           
                           [weakSelf.msgData addObject:[NSString stringWithFormat:@"SELECT_ISD,data:%@, sw1:%@, sw2: %@", responseData, [weakSelf byteFromUInt8: sw1], [weakSelf byteFromUInt8: sw2]]];
                           
                           NSData *reset_wallet_data = [weakSelf stringToByte:RESET_WALLET];
                           
                           NFCISO7816APDU *reset_wallet_apdu = [[NFCISO7816APDU alloc] initWithData:reset_wallet_data];
                           
                           [weakSelf.cuurentTag sendCommandAPDU:reset_wallet_apdu completionHandler:^(NSData * _Nonnull responseData, uint8_t sw1, uint8_t sw2, NSError * _Nullable error) {
                                                                               
                              if (error) {
                                                             
                                  NSLog(@"reset_wallet_data error:%@",error);

                                  weakSelf.session.alertMessage =[NSString stringWithFormat:@"reset_wallet_data error:%@",error];
                                  [weakSelf.session restartPolling];
                                  
                              }else{
                             
                                  NSLog(@"reset_wallet_data：%@",responseData);
                                  NSLog(@"reset_wallet_sw1。 %@", [weakSelf byteFromUInt8: sw1]);
                                  NSLog(@"reset_wallet_sw2 %@", [weakSelf byteFromUInt8: sw2]);
                                  
                                    [weakSelf.msgData addObject:[NSString stringWithFormat:@"reset_wallet,data:%@, sw1:%@, sw2: %@", responseData, [weakSelf byteFromUInt8: sw1], [weakSelf byteFromUInt8: sw2]]];
                                                       
                                  NSData *import_mnemoic_data = [weakSelf stringToByte:IMPORT_MNEMONIC];
                                                       
                                  NFCISO7816APDU *import_mnemoic_apdu = [[NFCISO7816APDU alloc] initWithData:import_mnemoic_data];
                                  
                                  [weakSelf.cuurentTag sendCommandAPDU:import_mnemoic_apdu completionHandler:^(NSData * _Nonnull responseData, uint8_t sw1, uint8_t sw2, NSError * _Nullable error) {
                                                                   
                                      if (error) {
                                                                     
                                          NSLog(@"import_mnemoic error:%@",error);
                     
                                          weakSelf.session.alertMessage =[NSString stringWithFormat:@"import_mnemoic error:%@",error];
                                          [weakSelf.session restartPolling];
                                          
                                      }else{
                        
                                          NSLog(@"import_mnemoic data：%@",responseData);
                                          NSLog(@"import_mnemoic sw1 %@", [weakSelf byteFromUInt8: sw1]);
                                          NSLog(@"import_mnemoic sw2 %@", [weakSelf byteFromUInt8: sw2]);
                                          

                                          [weakSelf.msgData addObject:[NSString stringWithFormat:@"import_mnemoic,data:%@, sw1:%@, sw2: %@", responseData, [weakSelf byteFromUInt8: sw1], [weakSelf byteFromUInt8: sw2]]];
                                          
                                          [weakSelf.msgTableView reloadData];
                                          
                                      }
                                      
                                  }];
                                  
                              }
                               
                           }];
                       }
                   }];
               }
           }
       }];
}


#pragma mark - func

- (NSData *)byteFromUInt8:(uint8_t)val
{
    NSMutableData *valData = [[NSMutableData alloc] init];
    
    unsigned char valChar[1];
    valChar[0] = 0xff & val;
    [valData appendBytes:valChar length:1];
    
    return [self dataWithReverse:valData];
}
- (NSData *)dataWithReverse:(NSData *)srcData
{
    NSUInteger byteCount = srcData.length;
    NSMutableData *dstData = [[NSMutableData alloc] initWithData:srcData];
    NSUInteger halfLength = byteCount / 2;
    for (NSUInteger i=0; i<halfLength; i++) {
        NSRange begin = NSMakeRange(i, 1);
        NSRange end = NSMakeRange(byteCount - i - 1, 1);
        NSData *beginData = [srcData subdataWithRange:begin];
        NSData *endData = [srcData subdataWithRange:end];
        [dstData replaceBytesInRange:begin withBytes:endData.bytes];
        [dstData replaceBytesInRange:end withBytes:beginData.bytes];
    }//for
      
    return dstData;
}


- (NSData *)stringToByte:(NSString*)string {
    NSString *hexString=[[string uppercaseString] stringByReplacingOccurrencesOfString:@" " withString:@""];
    if ([hexString length]%2!=0) {
        return nil;
    }
    Byte bytes[100] = {0};
    int j=0;
    for(int i=0;i<[hexString length];i++) {
        int int_ch;
        unichar hex_char1 = [hexString characterAtIndex:i]; ////两位16进制数中的第一位(高位*16)
        int int_ch1;
        if(hex_char1 >= '0' && hex_char1 <='9')
            int_ch1 = (hex_char1-48)*16;   //// 0 的Ascll - 48
        else if(hex_char1 >= 'A' && hex_char1 <='F')
            int_ch1 = (hex_char1-55)*16; //// A 的Ascll - 65
        else
            return nil;
        i++;

        unichar hex_char2 = [hexString characterAtIndex:i]; ///两位16进制数中的第二位(低位)
             int int_ch2;
                if(hex_char2 >= '0' && hex_char2 <='9')
                    int_ch2 = (hex_char2-48); //// 0 的Ascll - 48
                else if(hex_char2 >= 'A' && hex_char2 <='F')
                    int_ch2 = hex_char2-55; //// A 的Ascll - 65
                else
                    return nil;

                int_ch = int_ch1+int_ch2;  ///将转化后的数放入Byte数组里
                bytes[j] = int_ch;  ///将转化后的数放入Byte数组里
                j++;

            }
        NSData *byteData = [[NSData alloc] initWithBytes:bytes length:hexString.length / 2];
        return byteData;
}



@end
