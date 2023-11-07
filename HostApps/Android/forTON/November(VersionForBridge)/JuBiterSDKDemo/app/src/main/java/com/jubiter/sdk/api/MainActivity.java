package com.jubiter.sdk.api;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jubiter.sdk.JuBiterWallet;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.appcompat.app.AppCompatActivity;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.jubiter.sdk.api.utils.ByteArrayHelper.hex;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private TextView mStateTx, mTxtInfo;
//    private ProgressDialog mDialog;
    private EditText mEditData;
    private TonJubiterApi tonJubiterApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
//        mContext = MainActivity.this;
//        mDeviceList = new ArrayList<>();
//
//        if (!hasPermissions()) {
//            requestPermissions("Permission request", REQUEST_PERMISSION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
//        } else {
//            JuBiterWallet.initDevice();
//        }

        tonJubiterApi = new TonJubiterApi(MainActivity.this);
        initUI();
    }

    private void initUI() {
        mStateTx = findViewById(R.id.state_tx);

        mTxtInfo = findViewById(R.id.info_tv);
        //mDialog = new ProgressDialog(this);

        mEditData = findViewById(R.id.edit_data);


        addListenerOnGenerateSeedBtn();
        addListenerOnGetDeviceInfoBtn();
        addListenerOnGetDeviceCertBtn();

        addListenerOnScanDeviceBtn();
        addListenerOnCancelConnectBtn();
        addListenerOnIsConnectedBtn();
        addListenerOnDisconnectDevice();

        addListenerOnIsInitializeBtn();
//        addListenerOnIsBootloaderBtn();
//        addListenerOnSetTimeoutBtn();
        addListenerOnEnumAppletsBtn();

        addListenerOnQueryBatteryBtn();

        addListenerOnTurnOnColdWalletBtn();
        addListenerOnGetPublicKeyBtn();
        addListenerOnGetMaxPinTriesBtn();
        addListenerGetRemainingPinTriesBtn();
        addListenerGetTonAppletStateBtn();
        addListenerOnGenerateSeedBtn();

        addListenerOnVerifyDefaultPINBtn();
        addListenerOnVerifyPINBtn();
        addListenerOnSignBtn();
        addListenerOnChangePINBtn();
        addListenerOnTurnOffColdWalletBtn();
    }

    private void addListenerOnScanDeviceBtn() {
        findViewById(R.id.scanDevice_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tonJubiterApi.scanDevice();
                    }
                });
            }
        });
    }


    private void addListenerOnDisconnectDevice() {
        findViewById(R.id.disconnectDevice_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tonJubiterApi.disconnectDevice();
            }
        });
    }

    private void addListenerOnCancelConnectBtn() {
        findViewById(R.id.cancelConnect_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tonJubiterApi.cancelConnect();
            }
        });
    }

    private void addListenerOnIsConnectedBtn() {
        findViewById(R.id.isConnected_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tonJubiterApi.isConnected();
            }
        });
    }

    private void addListenerOnTurnOnColdWalletBtn() {
        //turn_on_cold_wallet

        findViewById(R.id.turn_on_cold_wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //runOnUiThread(new Runnable() {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        tonJubiterApi.turnOnColdWallet();
                    }
                });
            }
        });
    }

    private void addListenerOnSignBtn() {
        findViewById(R.id.ton_sign_with_default_path).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        String dataForSigning = mEditData.getText().toString();
                        setTxtInfo(tonJubiterApi.sign(dataForSigning));
                    }
                });
            }
        });
    }


    private void addListenerOnGetPublicKeyBtn() {
        findViewById(R.id.ton_get_public_key).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        setTxtInfo(tonJubiterApi.getPublicKey());
                    }
                });
            }
        });
    }

    private void addListenerOnGetMaxPinTriesBtn() {
        findViewById(R.id.get_max_pin_tries).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        setTxtInfo(tonJubiterApi.getMaxPinTries());
                    }
                });
            }
        });
    }

    private void addListenerGetRemainingPinTriesBtn() {
        findViewById(R.id.get_remaining_pin_tries).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        setTxtInfo(tonJubiterApi.getRemainingPinTries());
                    }
                });
            }
        });
    }



    private void addListenerGetTonAppletStateBtn() {
        findViewById(R.id.ton_get_applet_state).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        tonJubiterApi.getTonAppletState();
                    }
                });
            }
        });
    }

    private void addListenerOnGenerateSeedBtn() {
        findViewById(R.id.generateSeed_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       tonJubiterApi.showVerifyPinForGenerateSeedDialog();
                    }
                });
            }
        });
    }

    private void addListenerOnTurnOffColdWalletBtn() {
        findViewById(R.id.turn_off_cold_wallet_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        setTxtInfo(tonJubiterApi.turnOffColdWallet());
                   }
                });
            }
        });
    }

    private void addListenerOnVerifyDefaultPINBtn() {
        findViewById(R.id.verifyDefaultPIN_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        setTxtInfo(tonJubiterApi.verifyDefaultPin());
                    }
                });
            }
        });
    }

    private void addListenerOnChangePINBtn() {
        findViewById(R.id.changePIN_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tonJubiterApi.changePinDialog();
                    }
                });
            }
        });
    }

    private void addListenerOnVerifyPINBtn() {
        findViewById(R.id.verifyPIN_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tonJubiterApi.showVerifyPinDialog();
                    }
                });
            }
        });
    }

    private void addListenerOnIsInitializeBtn() {
        findViewById(R.id.isInitialize_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        setTxtInfo(tonJubiterApi.isSeedInitialized());
                    }
                });
            }
        });
    }

//    private void addListenerOnIsBootloaderBtn() {
//        findViewById(R.id.isBootLoader_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean result = JuBiterWallet.isBootLoader(deviceID);
//               // Log.d(TAG, ">>> isInitialize : " + result);
//                setTxtInfo(">>> isBootLoader : " + result);
//            }
//        });
//    }
//
//    private void addListenerOnSetTimeoutBtn() {
//        findViewById(R.id.setTimeout_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              //  CommonProtos.ResultString result = JuBiterWallet.setTimeout(contextID, 10);
//            }
//        });
//    }

    private void addListenerOnEnumAppletsBtn() {
        findViewById(R.id.enumApplets_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        setTxtInfo(tonJubiterApi.enumApplets());
                    }
                });
            }
        });
    }

    private void addListenerOnQueryBatteryBtn() {
        findViewById(R.id.queryBattery_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        setTxtInfo(String.valueOf(tonJubiterApi.queryBattery()));
                    }
                });
            }
        });
    }

    private void addListenerOnGetDeviceInfoBtn() {
        findViewById(R.id.getDeviceInfo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        setTxtInfo(tonJubiterApi.getDeviceInfo());
                    }
                });
            }
        });
    }

    private void addListenerOnGetDeviceCertBtn() {
        findViewById(R.id.getDeviceCert_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadUtils.execute(new Runnable() {
                    @Override
                    public void run() {
                        setTxtInfo(tonJubiterApi.getDeviceCert());
                    }
                });
            }
        });
    }

//    private void addListenerOnGenerateSeedBtn() {
//        findViewById(R.id.generateSeed_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
////                        CommonProtos.ResultString result = JuBiterWallet.generateSeed(mnemonic, "123");
////                        Log.d(TAG, ">>> generateSeed - rv : " + result.getStateCode() + " value: " + result.getValue());
////                        seed = result.getValue();
//                    }
//                });
//            }
//        });
//    }




//    private void addListenerOnGenerateMnemonicBtn() {
//        findViewById(R.id.generateMnemonic_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        CommonProtos.ResultString result = JuBiterWallet.generateMnemonic(CommonProtos.ENUM_MNEMONIC_STRENGTH.STRENGTH128);
//                        Log.d(TAG, ">>> generateMnemonic - rv : " + result.getStateCode() + " value: " + result.getValue());
//                        mnemonic = result.getValue();
//                    }
//                });
//            }
//        });
//    }
//
//    private void addListenerOnCheckMnemonicBtn() {
//        findViewById(R.id.checkMnemonic_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int result = JuBiterWallet.checkMnemonic(mnemonic);
//                Log.d(TAG, ">>> generateMnemonic - rv : " + result);
//            }
//        });
//    }



//    private void addListenerOnSeedToMasterKeyBtn() {
//        findViewById(R.id.seedToMasterKey_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        CommonProtos.ResultString result = JuBiterWallet.seedToMasterPrivateKey(seed, CommonProtos.CURVES.secp256k1);
//                        Log.d(TAG, ">>> seedToMasterPrivateKey - rv : " + result.getStateCode() + " value: " + result.getValue());
//                    }
//                });
//
//            }
//        });
//    }



//    private void addListenerOnEnumSupportCoins() {
//        findViewById(R.id.enumSupportCoins_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CommonProtos.ResultString result = JuBiterWallet.enumSupportCoins(deviceID);
//                Log.d(TAG, ">>> enumSupportCoins - rv : " + result.getStateCode() + " value: " + result.getValue());
//            }
//        });
//    }
//
//    private void addListenerOnGetAppletVersionBtn() {
//        findViewById(R.id.getAppletVersion_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CommonProtos.ResultString result = JuBiterWallet.getAppletVersion(deviceID, "d1560001328300424c44000042544301");
//                Log.d(TAG, ">>> getAppletVersion - rv : " + result.getStateCode() + " value: " + result.getValue());
//            }
//        });
//    }



//    private void addListenerOnClearContextBtn() {
//        findViewById(R.id.clearContext_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int result = JuBiterWallet.clearContext(contextID);
//                Log.d(TAG, ">>> rv: " + result);
//            }
//        });
//    }
//
//


    //*********************************** BTC 钱包 **********************************************

//    private void addListenerOnBTCCreateContext_SoftwareBtn() {
//        findViewById(R.id.btcCreateContext_software_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BitcoinProtos.ContextCfgBTC config = BitcoinProtos.ContextCfgBTC.newBuilder()
//                        .setCoinType(BitcoinProtos.ENUM_COIN_TYPE_BTC.COINBTC)
//                        .setMainPath("m/44'/0'/0'")
//                        .setTransType(BitcoinProtos.ENUM_TRAN_STYPE_BTC.p2pkh)
//                        .build();
//                CommonProtos.ResultInt result = JuBiterBitcoin.createContext_Software(config, "xpub6CAxrkiSbwkn4LayKD6qBcZg4tQvhHBH7TofQjNV9Lb3cB5u8owxdLGfc2bKoz2McoviAMXzWHwSaqc5Sm8C9SWMsnvuBw1bjEwtWsMZZFX");
//                Log.d(TAG, ">>> rv: " + result.getValue());
//            }
//        });
//    }
//
//    private void addListenerOnBTCCreateContextBtn() {
//        findViewById(R.id.btcCreateContext_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        BitcoinProtos.ContextCfgBTC config = BitcoinProtos.ContextCfgBTC.newBuilder()
//                                .setCoinType(BitcoinProtos.ENUM_COIN_TYPE_BTC.COINBTC)
//                                .setMainPath("m/44'/0'/0'")
//                                .setTransType(BitcoinProtos.ENUM_TRAN_STYPE_BTC.p2pkh)
//                                .build();
//                        CommonProtos.ResultInt result = JuBiterBitcoin.createContext(config, deviceID);
//                        Log.d(TAG, ">>> rv: " + result.getValue());
//                        contextID = result.getValue();
//                    }
//                });
//            }
//        });
//    }
//
//    private void addListenerOnBTCGetMainHDNodetn() {
//        findViewById(R.id.btcGetMainHDNode_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        CommonProtos.ResultString result = JuBiterBitcoin.getMainHDNode(contextID);
//                        Log.d(TAG, ">>> rv: " + result.getValue());
//                    }
//                });
//            }
//        });
//    }
//
//    private void addListenerOnBTCGetHDNodeBtn() {
//        findViewById(R.id.btcGetHDNode_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        CommonProtos.Bip32Path bip32Path = CommonProtos.Bip32Path.newBuilder()
//                                .setAddressIndex(0)
//                                .setChange(false)
//                                .build();
//                        CommonProtos.ResultString result = JuBiterBitcoin.getHDNode(contextID, bip32Path);
//                        Log.d(TAG, ">>> rv: " + result.getValue());
//                    }
//                });
//            }
//        });
//    }
//
//    private void addListenerOnBTCGetAddressBtn() {
//        findViewById(R.id.btcGetAddress_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        CommonProtos.Bip32Path bip32Path = CommonProtos.Bip32Path.newBuilder()
//                                .setAddressIndex(0)
//                                .setChange(false)
//                                .build();
//                        CommonProtos.ResultString result = JuBiterBitcoin.getAddress(contextID, bip32Path, true);
//                        Log.d(TAG, ">>> rv: " + result.getValue());
//                    }
//                });
//            }
//        });
//    }
//
//    private void addListenerOnBTCTransactionBtn() {
//        findViewById(R.id.btcTransaction_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        BitcoinProtos.ContextCfgBTC config = BitcoinProtos.ContextCfgBTC.newBuilder()
//                                .setCoinType(BitcoinProtos.ENUM_COIN_TYPE_BTC.COINBTC)
//                                .setMainPath("m/44'/0'/0'")
//                                .setTransType(BitcoinProtos.ENUM_TRAN_STYPE_BTC.p2pkh)
//                                .build();
//                        CommonProtos.ResultInt result = JuBiterBitcoin.createContext(config, deviceID);
//                        if (0 != result.getStateCode()) {
//                            Log.d(TAG, "createContext : " + result.getStateCode());
//                            return;
//                        }
//
//                        final int contextID2 = result.getValue();
//                        int rv = JuBiterWallet.showVirtualPWD(contextID2);
//                        Log.d(TAG, "showVirtualPWD : " + rv);
//                        if (0 != rv) {
//                            return;
//                        }
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                showPINDialog(COIN_TYPE.BTC, contextID2);
//                            }
//                        });
//                    }
//                });
//            }
//        });
//    }



//    private void btcTransaction(int contextID) {
//        // inputs
//        CommonProtos.Bip32Path bip32Path_1 = CommonProtos.Bip32Path.newBuilder()
//                .setAddressIndex(0)
//                .setChange(false)
//                .build();
//        BitcoinProtos.InputBTC inputBTC_1 = BitcoinProtos.InputBTC.newBuilder()
//                .setPath(bip32Path_1)
//                .setPreHash("2a2e910f9fb2b04f7f1ddbfb4ab05785250c2b395f572ce591167c8451f0891e")
//                .setAmount(1200)
//                .setPreIndex(0)
//                .build();
//
//        CommonProtos.Bip32Path bip32Path_2 = CommonProtos.Bip32Path.newBuilder()
//                .setAddressIndex(0)
//                .setChange(false)
//                .build();
//        BitcoinProtos.InputBTC inputBTC_2 = BitcoinProtos.InputBTC.newBuilder()
//                .setPath(bip32Path_2)
//                .setPreHash("0fe4fafd846b18fe545bbc2dcb70ecb1290ec0de6219cd2299cd0a1561c8d583")
//                .setAmount(1000)
//                .setPreIndex(0)
//                .build();
//
//        // outputs
//        BitcoinProtos.StandardOutput ouput_1 = BitcoinProtos.StandardOutput.newBuilder()
//                .setAddress("3MA1nkDJhthnC7DxoixmhLiXTyFo1eKJu7")
//                .setAmount(800)
//                .setChangeAddress(false)
//                .build();
//        BitcoinProtos.OutputBTC outputBTC_1 = BitcoinProtos.OutputBTC.newBuilder()
//                .setStandardOputput(ouput_1)
//                .setType(BitcoinProtos.ENUM_SCRIPT_TYPE_BTC.STANDARD)
//                .build();
//
//        CommonProtos.Bip32Path bip32Path_3 = CommonProtos.Bip32Path.newBuilder()
//                .setAddressIndex(0)
//                .setChange(false)
//                .build();
//        // 找零
//        BitcoinProtos.StandardOutput ouput_2 = BitcoinProtos.StandardOutput.newBuilder()
//                .setAddress("1JpuFuiBfMzm99JzZG4rpZexxjortaH42t")
//                .setChangeAddress(true)
//                .setAmount(500)
//                .setPath(bip32Path_3)
//                .build();
//        BitcoinProtos.OutputBTC outputBTC_2 = BitcoinProtos.OutputBTC.newBuilder()
//                .setStandardOputput(ouput_2)
//                .setType(BitcoinProtos.ENUM_SCRIPT_TYPE_BTC.STANDARD)
//                .build();
//
//        BitcoinProtos.TransactionBTC transactionBTC = BitcoinProtos.TransactionBTC.newBuilder()
//                .setVersion(1)
//                .addInputs(0, inputBTC_1)
//                .addInputs(1, inputBTC_2)
//                .addOutputs(0, outputBTC_1)
//                .addOutputs(1, outputBTC_2)
//                .setLocktime(0)
//                .build();
//        CommonProtos.ResultString signResult = JuBiterBitcoin.signTransaction(contextID, transactionBTC);
//        Log.d(TAG, ">>> signTransaction - rv : " + signResult.getStateCode() + " value: " + signResult.getValue());
//    }
//
//    private void addListenerOnBuildUSDTOutputBtn() {
//        findViewById(R.id.buildUSDToutput_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CommonProtos.ResultAny result = JuBiterBitcoin.buildUSDTOutput(contextID, "1JpuFuiBfMzm99JzZG4rpZexxjortaH42t", 2000);
//
//                List<BitcoinProtos.OutputBTC> outputBTCList = new ArrayList<>();
//                for (com.google.protobuf.Any res : result.getValueList()) {
//                    try {
//                        BitcoinProtos.OutputBTC output = res.unpack(BitcoinProtos.OutputBTC.class);
//                        outputBTCList.add(output);
//                    } catch (InvalidProtocolBufferException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.d(TAG, "rv : " + outputBTCList.toString());
//            }
//        });
//    }
//
//    private void addListenerOnSetUintBtn() {
//        findViewById(R.id.setUint_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int rv = JuBiterBitcoin.setUint(contextID, BitcoinProtos.BTC_UNIT_TYPE.BTC);
//                Log.d(TAG, ">>> setUint rv: " + rv);
//            }
//        });
//    }
//
//    private void addListenerOnETHCreateContext_SoftwareBtn() {
//        findViewById(R.id.ethCreateContext_software_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EthereumProtos.ContextCfgETH config = EthereumProtos.ContextCfgETH.newBuilder()
//                        .setMainPath("m/44'/60'/0'")
//                        .build();
//                CommonProtos.ResultInt result = JuBiterEthereum.createContext_Software(config, "");
//                Log.d(TAG, ">>> rv: " + result.getValue());
//            }
//        });
//    }
//
//    private void addListenerOnETHCreateContextBtn() {
//        findViewById(R.id.ethCreateContext_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        EthereumProtos.ContextCfgETH config = EthereumProtos.ContextCfgETH.newBuilder()
//                                .setMainPath("m/44'/60'/0'")
//                                .build();
//                        CommonProtos.ResultInt result = JuBiterEthereum.createContext(config, deviceID);
//                        Log.d(TAG, ">>> rv: " + result.getValue());
//                        contextID = result.getValue();
//                    }
//                });
//            }
//        });
//    }
//
//    private void addListenerOnETHGetMainHDNodetn() {
//        findViewById(R.id.ethGetMainHDNode_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        CommonProtos.ResultString result = JuBiterEthereum.getMainHDNode(contextID, EthereumProtos.ENUM_PUB_FORMAT.XPUB);
//                        Log.d(TAG, ">>> rv: " + result.getValue());
//                    }
//                });
//            }
//        });
//    }
//
//    private void addListenerOnETHGetHDNodeBtn() {
//        findViewById(R.id.ethGetHDNode_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        CommonProtos.Bip32Path bip32Path = CommonProtos.Bip32Path.newBuilder()
//                                .setAddressIndex(0)
//                                .setChange(false)
//                                .build();
//                        CommonProtos.ResultString result = JuBiterEthereum.getHDNode(contextID, EthereumProtos.ENUM_PUB_FORMAT.XPUB, bip32Path);
//                        Log.d(TAG, ">>> rv: " + result.getValue());
//                    }
//                });
//            }
//        });
//    }
//
//    private void addListenerOnETHGetAddressBtn() {
//        findViewById(R.id.ethGetAddress_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        CommonProtos.Bip32Path bip32Path = CommonProtos.Bip32Path.newBuilder()
//                                .setAddressIndex(0)
//                                .setChange(false)
//                                .build();
//                        CommonProtos.ResultString result = JuBiterEthereum.getAddress(contextID, bip32Path, false);
//                        Log.d(TAG, ">>> rv: " + result.getValue());
//                    }
//                });
//            }
//        });
//    }
//
//    private void addListenerOnETHTransactionBtn() {
//        findViewById(R.id.ethTransaction_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ThreadUtils.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        EthereumProtos.ContextCfgETH config = EthereumProtos.ContextCfgETH.newBuilder()
//                                .setMainPath("m/44'/60'/0'")
//                                .build();
//                        CommonProtos.ResultInt result = JuBiterEthereum.createContext(config, deviceID);
//                        if (0 != result.getStateCode()) {
//                            Log.d(TAG, "createContext : " + result.getStateCode());
//                            return;
//                        }
//
//                        final int contextID2 = result.getValue();
//                        int rv = JuBiterWallet.showVirtualPWD(contextID2);
//                        Log.d(TAG, "showVirtualPWD : " + rv);
//                        if (0 != rv) {
//                            return;
//                        }
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                showPINDialog(COIN_TYPE.ETH, contextID2);
//                            }
//                        });
//                    }
//                });
//            }
//        });
//    }
//
//    private void ethTransaction(int contextID) {
//        CommonProtos.Bip32Path bip32Path = CommonProtos.Bip32Path.newBuilder()
//                .setChange(false)
//                .setAddressIndex(0)
//                .build();
//        EthereumProtos.TransactionETH transactionETH = EthereumProtos.TransactionETH.newBuilder()
//                .setPath(bip32Path)
//                .setNonce(13)
//                .setGasLimit(310000)
//                .setGasPriceInWei("10000000000")
//                .setTo("0xef31DEc147DCDcd64F6a0ABFA7D441B62A216BC9")
//                .setValueInWei("500000000000000")
//                .setInput("4A75626974657257616C6C6574")
//                .build();
//        CommonProtos.ResultString result = JuBiterEthereum.signTransaction(contextID, transactionETH);
//        Log.d(TAG, ">>> signTransaction - rv : " + result.getStateCode() + " value: " + result.getValue());
//    }
//
//
//    private void addListenerOnBuildERC20AbiBtn() {
//        findViewById(R.id.buildErc20Abi_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CommonProtos.ResultString result = JuBiterEthereum.buildERC20Abi(contextID, "", "10000000");
//                Log.d(TAG, ">>> buildERC20Abi rv: " + result);
//            }
//        });
//    }
//
//
//    private void printDevice(String name, String mac, int deviceType) {
//        Log.d(TAG, "name : " + name + " mac: " + mac + " type: " + deviceType);
//    }

    public boolean hasPermissions() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public void requestPermissions(@NonNull String rationale,
                                   int requestCode, @Size(min = 1) @NonNull String... perms) {
        EasyPermissions.requestPermissions(this, rationale, requestCode, perms);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        JuBiterWallet.initDevice();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

//    void showToast(final String tip) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    void updateStateInfo(final String info) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mStateTx.setText(info);
//            }
//        });
//    }


    void setTxtInfo(final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTxtInfo.setText(info);
            }
        });
    }






}
