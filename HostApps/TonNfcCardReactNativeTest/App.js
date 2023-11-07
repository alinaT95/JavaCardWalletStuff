/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {Component} from 'react';
import Dialog from "react-native-dialog";
import DialogInput from 'react-native-dialog-input';

import {
    Spacer,
    SafeAreaView,
    StyleSheet,
    ScrollView,
    View,
    Button,
    Text,
    TextInput,
    StatusBar,
    NativeModules,
    FlatList,
    Alert,
    DeviceEventEmitter,
    NativeEventEmitter
} from 'react-native';

import {
    Header,
    LearnMoreLinks,
    Colors,
    Style,
    DebugInstructions,
    ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

import NfcWrapper from "./NfcWrapper";

function Separator() {
    return <View style={styles.separator}/>;
}

/*const CounterEvents = new NativeEventEmitter(NativeModules.Counter)
CounterEvents.addListener(
    "onIncrement",
    res => alert("onIncrement event", res)
)*/

export default class HelloWorldApp extends Component {

    componentDidMount() {
        DeviceEventEmitter.addListener("nfcTagIsConnected", () => Alert.alert("NFC hardware touched."))
        DeviceEventEmitter.addListener("nfcAdapterStateChanged", (state) => Alert.alert("NFC adapter state is changed: " + state + "."))
        DeviceEventEmitter.addListener("keyCardOnDisconnected", () => console.log("keycard disconnected"));
        DeviceEventEmitter.addListener("keyCardOnNFCEnabled", () => console.log("nfc enabled"));
        DeviceEventEmitter.addListener("keyCardOnNFCDisabled", () => console.log("nfc disabled"));
    }

    constructor(props) {
        super(props);
        this.state = {
            deviceLabel: '005815A3942073A6ADC70C035780FDD09DF09AFEEA4173B92FE559C34DCA0550',
            currentPin: '5555',
            data: '000000',
            newPin: '5555',
            hdIndex: '1',
            newKey: '0000',
            newKeyForChangeKey: '',
            keyIndex: '0',
            oldKeyIndex: '0',
            keyIndexToDelete: '0',
           /* authenticationPassword: 'F4B072E1DF2DB7CF6CD0CD681EC5CD2D071458D278E6546763CBB4860F8082FE14418C8A8A55E2106CBC6CB1174F4BA6D827A26A2D205F99B7E00401DA4C15ACC943274B92258114B5E11C16DA64484034F93771547FBE60DA70E273E6BD64F8A4201A9913B386BCA55B6678CFD7E7E68A646A7543E9E439DD5B60B9615079FE',
            commonSecret: '7256EFE7A77AFC7E9088266EF27A93CB01CD9432E0DB66D600745D506EE04AC4',
            initialVector: '1A550F4B413D0E971C28293F9183EA8A',*/
            serialNumber: '358464153630021949155797',
            isGenerateSeedDialogVisible: false,
            isChangePinDialogVisible: false,
            isSetDeviceLabelDialogVisible: false,
            isGetPkDialogVisible: false,
            isVerifyPinDialogVisible: false,
            isSignForDefHdPathDialogVisible: false,
            isSignDialogVisible: false,
            enableScrollViewScroll: true,
            isSetKeyForHmacDialogVisible: false,
            isAddKeyIntoKeyChainDialogVisible: false,
            isGetKeyFromKeyChainDialogVisible: false,
            isChangeKeyFromKeyChainDialogVisible: false,
            isDeleteKeyFromKeyChainDialogVisible: false,
            isTurnOnWalletDialogVisible: false,
            authenticationPasswords: [
                "1A2B1ABC9E7FB7746E34F8D8CA415025257369010A082FE2F823BFAF8DF4C1ABC0C9B160358F55AF918878EB1F959EBE684F2835B868E742F10BF9D2A8DD7FA3F255A9BC1701DBBD551C6E221E06C570F3F3BE7C5A77C97F83485252FE89F91961EAF239DCBB4EF40BD87CA83815F60C7AC0A67C62484CBACE235CEB6ACCEEFE",
                "20923F97C4F27B89EC5443FF08DCDC8B3A1D3034E07900B23BCAD70A98FFDA83233AF9FE971D70EC438D5B50C26550B48B8C9714FA4C3F487003103080BF84A14C630DCC49E73ACBA69691C581AEDF011395F61CCD85E895869C9BCE1855C92B46036B76E40EC145EFED8C0C7671EFE021E7CB8D5DE618CBC7F21F591C78E44B",
                "262CBEBDFE9D6FB40D799A8931E8DD8317CB8DCACA7D7644BAF7E040E865C8F304A4E7C01C72EDAC2EEFD5252B813CC47B61C98D6D370E67B763E3E836BD8CE40D5E4B5DAA80243A12A11DBDDF4702DA327ABF7F4AE10964E9DCC30BC4C3540969EC4CD1A2C4ADCC9CFDC966C30198F8A36FB6EF770655754489710AAE5DC7B3",
                "BAE7CA786611D7F3CD8BCF2D9DEA0BD3B520BE3B3574ABE6E26CBD3E29CAFF2B15B754860BEE481C7C66C866585E910DFF1F9504E29A55EF0C80AF878665D1F69562E593C39382732B67F031FAEC2CF05415F3FA55AA2A8195455478A2085CEABE4D5340B6C9E86B787365E403D257E138663F318D10F320EF89179F6AE23CB6",
                "2FBB9A9EBC442A0F6A927F3CA929A8745903D1E0695F2445D0447D3B594D063E3F9E19F399A59CF883A6542079E01ECF8642C20DE429E790A17A556494F1260CD69BEA4E0BF0237F5842D73EA615DD4503ED63C64D4E53E9AA25C85D869B32C06C30E6D6F88F3E5D6F243B4D9F029031A85ACC6E42ADAFC3A0D3B964E6A85DB1",
                "0849ADDB758D96748606B00E994BD0BC379B3BFDC1446DEF2F55AE22B285170F2C754C9B96B332A4B9EB5ACBA162FDADCB3136D19F08ACBA51734316BD25DCD8496C009A0BBB33B85E2D04B1830516E9138B399CA0859EE60AD3CA40CAEE4EAFEE94DFCC167F0C960069A9893AC7A2CC5FB41E06A2E3424E770B5C49FEE2D819",
                "22C47E02A4E886F09961A60AFE5A9A14C0FB2E0D60A081B7183BBF085BF2095838B33AED854AE6CC6ED3F572E70C1D5339B5A5C13D51378211B6680A4350EEBD1F4C4B5EAD6FDE59CB716EBC707E9AD8536AB9668DBCF650BE118DAB8294089BF074EBD7FB49145DA9A2814692A08817AA94148F8C6E45DF6848E41DF882B52A",
                "4A0FD62FFC3249A45ED369BD9B9CB340829179E94B8BE546FB19A1BC67C9411BC5DC85B5E38F96689B921A64DEF1A3B6F4D2F5C7D2B0BD7CCE420DBD281BA1CC82EE0B233820EB5CFE505B7201903ABB12959B251A5A8525B2515F57ACDE30905E70C2A375D5C0EC10A5EA6E264206395BF163969632398FA4A88D359FEA21D9",
                "F4B072E1DF2DB7CF6CD0CD681EC5CD2D071458D278E6546763CBB4860F8082FE14418C8A8A55E2106CBC6CB1174F4BA6D827A26A2D205F99B7E00401DA4C15ACC943274B92258114B5E11C16DA64484034F93771547FBE60DA70E273E6BD64F8A4201A9913B386BCA55B6678CFD7E7E68A646A7543E9E439DD5B60B9615079FE",
                "364A30C3005BAF50F9B3EB92FDD2DD81316F2653D2C2800A44344D0C8EDA9E01D70872127C9874E2DB98751218D84D35D04F73C3ED3BA3CC1C3070190CFE78EFA39C36F5364C73E30E097879FEAFB5D459DFFC375DF061E4A56D1D7D9AA91EBC606DE10A7159C6DC433DD2D9D8201906B249748ADED182D782EB82B16E570D94"
            ],
            commonSecrets: [
                "158BDE7FD53048BFF95E85947BBC08A1F4EF1A158F5975B0D7065CFB41F122C0", 
                "A762176882D1EB42C3559B703C1F4A1FD47905B15EBCA5AE2A24E1A2D6EB5FCC",
                "1B35D3841E232FE6412CA1A74918E40F64DCD6B3656693630419784F30E550A9",
                "92D8D3FA221E74518D8A121B7BD8231F437C51190F80B0A2C2E5BB7D10498954",
                "73F8D3FAB77F14DFFF25FD7EE1F9ACDA6BFF1595BB75CA088C3FF34834AC7A1E",
                "E3C89931950C87703B4EF24EB5664E12BA7B0309A41C349F3E6C255D6160BFAF",
                "3E79A98F0765265A5DA80AA63E3DB5C2F020EB49AF5DE04613D3A73B4769BC2D",
                "F1265BE98E44FE4DE1816169A24268145A1D6634F3D2D97DFA70EFC808AEA0F1",
                "7256EFE7A77AFC7E9088266EF27A93CB01CD9432E0DB66D600745D506EE04AC4",
                "66C5161BFFB12B4E9A6D2926B197537B31974828D2C02B830D0E519E1A62F3E8"
            ],
            initialVectors: [
                "E439F75C6FC516F1C4725E825164216C", 
                "224D3648054FEA21FB25B1748A61715A",
                "E173EACCD500E27AF400D4293FB25DCC",
                "34BEBD605FF844C1C8997F0D67BC9EDF",
                "5EA37F59582CCC58D12A9E0207E49499",
                "4C0DEACDD063E0D86FF6D011B8A16C81",
                "EBB04334A40BB79CC1A7B906BA0E1551",
                "9A6137DEA87ABE205FC18A0425D9FAF9",
                "1A550F4B413D0E971C28293F9183EA8A",
                "364BEE6B02EEE6C83027FA59864F92A5"
            ],
            serialNumbers: [
                "535110459474599149736332", 
                "449634915078431948176852", 
                "314856935569386969029165",
                "115456704932151001962551",
                "124843680472432549475921",
                "126083846606069739011949",
                "343155875629760788267343",
                "334525464436284236725680",
                "504394802433901126813236",
                "358464153630021949155797"
            ]
        };
        nfcWrapper = new NfcWrapper();
    }

    printResults = (error, result) => {
        if (error != null) {
            alert("Error: " + error);
        } else {
            alert(result);
        }
    }

    showGenerateSeedDialog(isShow) {
        this.setState({isGenerateSeedDialogVisible: isShow})
    }

    showChangePinDialog(isShow) {
        this.setState({isChangePinDialogVisible: isShow})
    }

    showGetPkDialog(isShow) {
        this.setState({isGetPkDialogVisible: isShow})
    }

    showSignForDefaultHdPathDialog(isShow) {
        this.setState({isSignForDefHdPathDialogVisible: isShow})
    }

    showSignDialog(isShow) {
        this.setState({isSignDialogVisible: isShow})
    }

    showSetDeviceLabelDialog(isShow) {
        this.setState({isSetDeviceLabelDialogVisible: isShow})
    }

    showTurnOnWalletDialog(isShow) {
        this.setState({isTurnOnWalletDialogVisible: isShow})
    }

    showAddKeyIntoKeyChainDialog(isShow) {
        this.setState({isAddKeyIntoKeyChainDialogVisible: isShow})
    }

    showGetKeyFromKeyChainDialog(isShow) {
        this.setState({isGetKeyFromKeyChainDialogVisible: isShow})
    }

    showChangeKeyFromKeyChainDialog(isShow) {
        this.setState({isChangeKeyFromKeyChainDialogVisible: isShow})
    }

    showSetKeyForHmacDialog(isShow) {
        this.setState({isSetKeyForHmacDialogVisible: isShow})
    }


    render() {
        return (
            <SafeAreaView style={styles.container}>
                <ScrollView
                    contentInsetAdjustmentBehavior="automatic"
                    style={styles.scrollView}>

                    <Text style={{padding: 10, fontSize: 20}}>
                        Setup app:
                    </Text>
                    <Separator/>
                    <View>
                        <Dialog.Container visible={this.state.isSetKeyForHmacDialogVisible}>
                            <Dialog.Title>Set key for hmac</Dialog.Title>
                           {/* <Dialog.Input label="Authentication password"
                                          defaultValue={this.state.authenticationPassword} style={{height: 100}}
                                          multiline={true} numberOfLines={4}
                                          onChangeText={(authenticationPassword) => this.setState({authenticationPassword})}></Dialog.Input>
                            <Dialog.Input label="Common secret" style={{height: 70}}
                                          defaultValue={this.state.commonSecret} multiline={true} numberOfLines={4}
                                onChangeText={(commonSecret) => this.setState({commonSecret})}></Dialog.Input>*/}
                            <Dialog.Input label="Serial number" style={{height: 70}}
                                          defaultValue={this.state.serialNumber} multiline={true} numberOfLines={4}
                                onChangeText={(serialNumber) => this.setState({serialNumber})}></Dialog.Input>    
                            <Dialog.Button label="Close" onPress={() => this.showSetKeyForHmacDialog(false)}/>
                            <Dialog.Button label="Submit" onPress={() => {
                                var ind = this.state.serialNumbers.indexOf(this.state.serialNumber);
                                if (ind == -1) {
                                    alert("Serial number is not found.")
                                }
                                else {
                                    var authenticationPassword = this.state.authenticationPasswords[ind]
                                    var commonSecret = this.state.commonSecrets[ind]
                                    nfcWrapper.setKeyForHmac(authenticationPassword, commonSecret)
                                }
                                this.showSetKeyForHmacDialog(false)
                            }}/>
                        </Dialog.Container>
                    </View>
                    <View>
                        <Button onPress={() => this.showSetKeyForHmacDialog(true)} title="setKeyForHmac"/>
                    </View>
                    <Separator/>


                    <Separator/>
                    <Text style={{padding: 10, fontSize: 20}}>
                        Test APDU commands of CoinManager:
                    </Text>

                    <Separator/>

                    <View style={styles.container}>
                        <DialogInput isDialogVisible={this.state.isGenerateSeedDialogVisible}
                                     title={"Generate seed"}
                                     subTitleStyle={{color: 'red'}}
                                     message={"Enter PIN"}
                                     hintInput={"PIN..."}
                                     submitInput={(currentPin) => {
                                         nfcWrapper.generateSeed(currentPin)
                                         this.showGenerateSeedDialog(false)
                                     }}
                                     closeDialog={() => this.showGenerateSeedDialog(false)}>
                        </DialogInput>
                    </View>
                    <Separator/>
                    <View>
                        <Button onPress={() => this.showGenerateSeedDialog(true)} title="generateSeed"/>
                    </View>
                    <Separator/>

                    <View>
                        <Dialog.Container visible={this.state.isChangePinDialogVisible}>
                            <Dialog.Title>Change pin</Dialog.Title>
                            <Dialog.Input label="Current pin" style={{height: 40}} defaultValue={this.state.currentPin}
                                          onChangeText={(currentPin) => this.setState({currentPin})}></Dialog.Input>
                            <Dialog.Input label="New pin" style={{height: 40}} defaultValue={this.state.newPin}
                                          onChangeText={(newPin) => this.setState({newPin})}></Dialog.Input>
                            <Dialog.Button label="Close" onPress={() => {
                                this.showChangePinDialog(false)
                            }}/>
                            <Dialog.Button label="Submit" onPress={() => {
                                nfcWrapper.changePin(this.state.currentPin, this.state.newPin)
                                this.showChangePinDialog(false)
                            }
                            }/>
                        </Dialog.Container>
                    </View>
                    <View>
                        <Button onPress={() => {
                            this.showChangePinDialog(true)
                        }} title="changePin"/>
                    </View>
                    <Separator/>
                    <View>
                        <Button onPress={() => nfcWrapper.getMaxPinTries()} title="getMaxPinTries"/>
                    </View>
                    <Separator/>
                    <View>
                        <Button onPress={() => nfcWrapper.getRemainingPinTries()} title="getRemainingPinTries"/>
                    </View>
                    <Separator/>
                    <View>
                        <Button onPress={() => nfcWrapper.getRootKeyStatus()} title="getRootKeyStatus"/>
                    </View>
                    <Separator/>
                    <View>
                        <Button onPress={() => nfcWrapper.getAvailableMemory()} title="getAvailableMemory"/>
                    </View>
                    <Separator/>
                    <View>
                        <Button onPress={() => nfcWrapper.getAppsList()} title="getAppsList"/>
                    </View>
                    <Separator/>
                    <View>
                        <Button onPress={() => nfcWrapper.resetWallet()} title="resetWallet"/>
                    </View>
                    <Separator/>
                    <View>
                        <Button onPress={() => nfcWrapper.getSeVersion()} title="getSeVersion"/>
                    </View>
                    <Separator/>
                    <View>
                        <Button onPress={() => nfcWrapper.getCsn()} title="getCsn"/>
                    </View>
                    <Separator/>
                    <View>
                        <Button onPress={() => nfcWrapper.getDeviceLabel()} title="getDeviceLabel"/>
                    </View>
                    <Separator/>

                    <View style={styles.container}>
                        <DialogInput isDialogVisible={this.state.isSetDeviceLabelDialogVisible}
                                     title={"Set Device label"}
                                     subTitleStyle={{color: 'red'}}
                                     message={"Enter Label"}
                                     hintInput={" Device label..."}
                                     initValueTextInput={'005815A3942073A6ADC70C035780FDD09DF09AFEEA4173B92FE559C34DCA0550'}
                                     submitInput={(deviceLabel) => {
                                         nfcWrapper.setDeviceLabel(deviceLabel)
                                         this.showSetDeviceLabelDialog(false)
                                     }}
                                     closeDialog={() => this.showSetDeviceLabelDialog(false)}>
                        </DialogInput>
                    </View>
                    <View>
                        <Button onPress={() => this.showSetDeviceLabelDialog(true)} title="setDeviceLabel"/>
                    </View>
                    <Separator/>

                    <Separator/>

                    <Text style={{padding: 10, fontSize: 20}}>
                        Test card activation:
                    </Text>
                    <Separator/>

                    <View>
                        <Dialog.Container visible={this.state.isTurnOnWalletDialogVisible}>
                            <Dialog.Title>Turn on wallet</Dialog.Title>
                            <Dialog.Input label="New pin" style={{height: 40}} defaultValue={this.state.newPin}
                                          onChangeText={(newPin) => this.setState({newPin})}></Dialog.Input>
                           {/* <Dialog.Input label="Authentication password"
                                          defaultValue={this.state.authenticationPassword} style={{height: 100}}
                                          multiline={true} numberOfLines={4}
                                          onChangeText={(authenticationPassword) => this.setState({authenticationPassword})}></Dialog.Input>
                            <Dialog.Input label="Common secret" style={{height: 70}}
                                          defaultValue={this.state.commonSecret} multiline={true} numberOfLines={4}
                                          onChangeText={(commonSecret) => this.setState({commonSecret})}></Dialog.Input>
                            <Dialog.Input label="Initial vector" style={{height: 40}}
                                          defaultValue={this.state.initialVector}
                                    onChangeText={(initialVector) => this.setState({initialVector})}></Dialog.Input>*/}
                            <Dialog.Input label="Serial number" style={{height: 40}}
                                          defaultValue={this.state.serialNumber}
                                    onChangeText={(serialNumber) => this.setState({serialNumber})}></Dialog.Input>        
                            <Dialog.Button label="Close" onPress={() => this.showTurnOnWalletDialog(false)}/>
                            <Dialog.Button label="Submit" onPress={() => {
                                var ind = this.state.serialNumbers.indexOf(this.state.serialNumber);
                                if (ind == -1) {
                                    alert("Serial number is not found.")
                                }
                                else {
                                    var authenticationPassword = this.state.authenticationPasswords[ind]
                                    var commonSecret = this.state.commonSecrets[ind]
                                    var initialVector = this.state.initialVectors[ind]
                                    nfcWrapper.turnOnWallet(this.state.newPin, authenticationPassword, commonSecret, initialVector)
                                }
                                this.showTurnOnWalletDialog(false)
                            }
                            }/>
                        </Dialog.Container>
                    </View>
                    <View>
                        <Button onPress={() => this.showTurnOnWalletDialog(true)} title="turnOnWallet"/>
                    </View>
                    <Separator/>

                    <View>
                        <Button onPress={() => {
                            nfcWrapper.verifyHashOfEncryptedPassword(this.state.authenticationPassword, this.state.initialVector)
                        }} title="verifyHashOfEncryptedPassword"/>
                    </View>
                    <Separator/>

                    <View>
                        <Button onPress={() => {
                            nfcWrapper.verifyHashOfCommonSecret(this.state.commonSecret)
                        }} title="verifyHashOfCommonSecret"/>
                    </View>
                    <Separator/>

                    <Separator/>
                    <Separator/>


                    <Text style={{padding: 10, fontSize: 20}}>
                        Test APDU commands of TonWallet applet (related to Ed25519):
                    </Text>
                    <Separator/>


                    <View>
                        <Button onPress={() => {
                            nfcWrapper.getTonAppletState()
                        }} title="getTonAppletState"/>
                    </View>
                    <Separator/>
                    <View>
                        <Button onPress={() => {
                            nfcWrapper.getPublicKeyForDefaultPath()
                        }} title="Get public key for default HD path"/>
                    </View>
                    <Separator/>
                    {/*<View>*/}
                    {/*   <Button onPress={() => {nfcWrapper.verifyDefaultPin()}} title="Verify default PIN" />*/}
                    {/* </View>*/}
                    {/* <Separator />*/}

                    <View style={styles.container}>
                        <DialogInput isDialogVisible={this.state.isGetPkDialogVisible}
                                     title={"Get public key"}
                                     message={"Enter hdIndex (0 < number < 2147483647)"}
                                     hintInput={"hdIndex..."}
                                     submitInput={(hdIndex) => {
                                         nfcWrapper.getPublicKey(hdIndex);
                                         this.showGetPkDialog(false);
                                     }}
                                     closeDialog={() => this.showGetPkDialog(false)}>
                        </DialogInput>
                    </View>
                    <View>
                        <Button onPress={() => this.showGetPkDialog(true)} title="Get public key"/>
                    </View>
                    <Separator/>


                    <View style={styles.container}>
                        <Dialog.Container visible={this.state.isSignForDefHdPathDialogVisible}>
                            <Dialog.Title>Sign data for default HD path m/44'/396'/0'/0'/0'</Dialog.Title>
                            <Dialog.Input label="Data to sign" style={{height: 70}} multiline={true} numberOfLines={4}
                                          defaultValue={this.state.data}
                                          onChangeText={(data) => this.setState({data})}></Dialog.Input>
                            <Dialog.Input label="Pin" style={{height: 40}} defaultValue={this.state.currentPin}
                                          onChangeText={(currentPin) => this.setState({currentPin})}></Dialog.Input>
                            <Dialog.Button label="Close" onPress={() => {
                                this.showSignForDefaultHdPathDialog(false)
                            }}/>
                            <Dialog.Button label="Submit" onPress={() => {
                                nfcWrapper.signForDefaultHdPath(this.state.data, this.state.currentPin);
                                this.showSignDialog(false);
                            }}/>
                        </Dialog.Container>
                    </View>
                    <View>
                        <Button onPress={() => this.showSignForDefaultHdPathDialog(true)} title="signForDefaultHdPath"/>
                    </View>
                    <Separator/>

                    <View>
                        <Dialog.Container visible={this.state.isSignDialogVisible}>
                            <Dialog.Title>Sign data</Dialog.Title>
                            <Dialog.Input label="Data to sign" style={{height: 70}} multiline={true} numberOfLines={4}
                                          defaultValue={this.state.data}
                                          onChangeText={(data) => this.setState({data})}></Dialog.Input>
                            <Dialog.Input label="Hd index" style={{height: 40}} defaultValue={this.state.hdIndex}
                                          onChangeText={(hdIndex) => this.setState({hdIndex})}></Dialog.Input>
                            <Dialog.Input label="Pin" style={{height: 40}} defaultValue={this.state.currentPin}
                                          onChangeText={(currentPin) => this.setState({currentPin})}></Dialog.Input>
                            <Dialog.Button label="Close" onPress={() => {
                                this.showSignDialog(false)
                            }}/>
                            <Dialog.Button label="Submit" onPress={() => {
                                nfcWrapper.sign(this.state.data, this.state.hdIndex, this.state.currentPin);
                                this.showSignDialog(false);
                            }}/>
                        </Dialog.Container>
                    </View>
                    <View>
                        <Button onPress={() => this.showSignDialog(true)} title="sign"/>
                    </View>
                    <Separator/>
                    <Separator/>


                </ScrollView>

                <Text style={{padding: 10, fontSize: 20}}>
                    Test TonWallet applet keychain:
                </Text>
                <Text style={{padding: 10, fontSize: 15}}>
                    Keychain data:
                </Text>
                <Separator/>
                <ScrollView
                    contentInsetAdjustmentBehavior="automatic"
                    style={styles.scrollView}>
                    <FlatList
                        data={this.state.keyData}
                        extraData={this.state}
                        renderItem={({item, index}) =>
                            <View>
                                <Text> {index}. Key hmac = {item.hmac} </Text>
                                <Text> Key length = {item.length} </Text>
                                <Separator/>
                            </View>
                        }
                        ItemSeparatorComponent={this.renderSeparator}
                    />
                </ScrollView>

                <ScrollView
                    contentInsetAdjustmentBehavior="automatic"
                    style={styles.scrollView}>

                    <View>
                        <Button onPress={() => {
                            NativeModules.NfcCardModule.getKeyChainDataAboutAllKeys().then((result) => {
                                this.setState({keyData: JSON.parse(result)})
                            })
                                .catch((error) => {
                                    Alert.alert(
                                        'Response from card',
                                        JSON.stringify(error.message),
                                        [
                                            {text: 'OK', onPress: () => console.log('OK Pressed')},
                                        ],
                                        {cancelable: false}
                                    );
                                })
                        }} title="Read data about keys in KeyChain"/>
                    </View>
                    <Separator/>

                    <View>
                        <Button onPress={() => {
                            nfcWrapper.resetKeyChain();
                            this.setState({
                                keyData: []
                            });
                        }} title="Reset KeyChain"/>
                    </View>
                    <Separator/>

                    <View>
                        <Button onPress={() => nfcWrapper.getKeyChainInfo()} title="Get KeyChain info"/>
                    </View>
                    <Separator/>

                    <View>
                        <Button onPress={() => {
                            nfcWrapper.getNumberOfKeys();
                        }} title="getNumberOfKeys"/>
                    </View>
                    <Separator/>

                    <View>
                        <Button onPress={() => {
                            nfcWrapper.getOccupiedStorageSize();
                        }} title="getOccupiedStorageSize"/>
                    </View>
                    <Separator/>

                    <View>
                        <Button onPress={() => {
                            nfcWrapper.getFreeStorageSize();
                        }} title="getFreeStorageSize"/>
                    </View>
                    <Separator/>


                    <View>
                        <Dialog.Container visible={this.state.isAddKeyIntoKeyChainDialogVisible}>
                            <Dialog.Title>Add key into KeyChain</Dialog.Title>
                            <Dialog.Input label="Enter new key:" defaultValue={this.state.newKey} style={{height: 100}}
                                          multiline={true} numberOfLines={4}
                                          onChangeText={(newKey) => this.setState({newKey})}></Dialog.Input>
                            <Dialog.Button label="Close" onPress={() => this.showAddKeyIntoKeyChainDialog(false)}/>
                            <Dialog.Button label="Submit" onPress={() => {
                                NativeModules.NfcCardModule.addKeyIntoKeyChain(this.state.newKey)
                                    .then((res) => {
                                        Alert.alert(
                                            'Response from card',
                                            "Add key status: hmac of new key = " + res,
                                            [
                                                {text: 'OK', onPress: () => console.log('OK Pressed')},
                                            ],
                                            {cancelable: false}
                                        )
                                        this.state.keyData.push({
                                            hmac: res,
                                            length: this.state.newKey.length / 2
                                        })
                                        this.setState({
                                            keyData: this.state.keyData
                                        })
                                    })
                                    .catch((error) => {
                                        Alert.alert(
                                            'Error',
                                            JSON.stringify(error.message),
                                            [
                                                {text: 'OK', onPress: () => console.log('OK Pressed')},
                                            ],
                                            {cancelable: false}
                                        )
                                    })
                                this.showAddKeyIntoKeyChainDialog(false)
                            }}/>
                        </Dialog.Container>
                    </View>
                    <View>
                        <Button onPress={() => this.showAddKeyIntoKeyChainDialog(true)} title="Add key"/>
                    </View>
                    <Separator/>

                    <View style={styles.container}>
                        <DialogInput isDialogVisible={this.state.isGetKeyFromKeyChainDialogVisible}
                                     title={"Get key"}
                                     message={"Enter key index"}
                                     hintInput={"Key index..."}
                                     submitInput={(keyIndex) => {
                                         if (keyIndex < this.state.keyData.length) {
                                             nfcWrapper.getKeyFromKeyChain(this.state.keyData[keyIndex].hmac)
                                             this.showGetKeyFromKeyChainDialog(false)
                                         } else {
                                             Alert.alert(
                                                 'Error',
                                                 "Get key status: key index is too big",
                                                 [
                                                     {text: 'OK', onPress: () => console.log('OK Pressed')},
                                                 ],
                                                 {cancelable: false}
                                             );
                                             // this.showGetKeyFromKeyChainDialog(false);
                                         }

                                     }}
                                     closeDialog={() => this.showGetKeyFromKeyChainDialog(false)}>
                        </DialogInput>
                    </View>
                    <View>
                        <Button onPress={() => this.showGetKeyFromKeyChainDialog(true)} title="Get key"/>
                    </View>
                    <Separator/>

                    <View>
                        <Dialog.Container visible={this.state.isChangeKeyFromKeyChainDialogVisible}>
                            <Dialog.Title>Change key</Dialog.Title>
                            <Dialog.Input label="Old key index" style={{height: 40}}
                                          defaultValue={this.state.oldKeyIndex}
                                          onChangeText={(oldKeyIndex) => this.setState({oldKeyIndex})}></Dialog.Input>
                            <Dialog.Input label="New key" style={{height: 100}}
                                          defaultValue={this.state.newKeyForChangeKey}
                                          onChangeText={(newKeyForChangeKey) => this.setState({newKeyForChangeKey})}></Dialog.Input>
                            <Dialog.Button label="Close" onPress={() => this.showChangeKeyFromKeyChainDialog(false)}/>
                            <Dialog.Button label="Submit" onPress={() => {
                                NativeModules.NfcCardModule.changeKeyInKeyChain(this.state.newKeyForChangeKey, this.state.keyData[this.state.oldKeyIndex].hmac)
                                    .then((res) => {
                                        Alert.alert(
                                            'Response from card',
                                            "Change key status: hmac of new key = " + res,
                                            [
                                                {text: 'OK', onPress: () => console.log('OK Pressed')},
                                            ],
                                            {cancelable: false}
                                        );
                                        this.state.keyData[this.state.oldKeyIndex] = {
                                            hmac: res,
                                            length: this.state.keyData[this.state.oldKeyIndex].length
                                        }
                                        this.setState({
                                            keyData: this.state.keyData
                                        })
                                    })
                                    .catch((error) => {
                                        Alert.alert(
                                            'Error',
                                            JSON.stringify(error.message),
                                            [
                                                {text: 'OK', onPress: () => console.log('OK Pressed')},
                                            ],
                                            {cancelable: false}
                                        )
                                    })
                                this.showChangeKeyFromKeyChainDialog(false)
                            }}/>
                        </Dialog.Container>
                    </View>
                    <View>
                        <Button onPress={() => this.showChangeKeyFromKeyChainDialog(true)} title="Change key"/>
                    </View>
                    <Separator/>

                    {/*<View style={styles.container}>
              <DialogInput isDialogVisible={this.state.isDeleteKeyFromKeyChainDialogVisible}
                           title={"Delete key"}
                           message={"Enter key index"}
                           hintInput={"Key index..."}
                           submitInput={(keyIndexToDelete) => {
                             NativeModules.JubiterNfcModule.deleteKeyFromKeyChain(this.state.keyData[keyIndexToDelete].hmac)
                                 .then((result) => {
                                   Alert.alert(
                                       'Response from card',
                                       "Delete key status (number of remained keys) : " + result,
                                       [
                                         { text: 'OK', onPress: () => console.log('OK Pressed') },
                                       ],
                                       { cancelable: false }
                                   )
                                   this.state.keyData.splice(keyIndexToDelete, 1)
                                   this.setState({ keyData: this.state.keyData })
                                 })
                                 .catch((error) => {
                                   Alert.alert(
                                       'Error',
                                       error,
                                       [
                                         { text: 'OK', onPress: () => console.log('OK Pressed') },
                                       ],
                                       { cancelable: false }
                                   )
                                 })
                             this.showDeleteKeyFromKeyChainDialog(false)
                           }}
                           closeDialog={() => this.showDeleteKeyFromKeyChainDialog(false)}>
              </DialogInput>
            </View>
            <View>
              <Button onPress={() => this.showDeleteKeyFromKeyChainDialog(true)} title="Delete key" />
            </View>
            <Separator />*/}

                </ScrollView>


            </SafeAreaView>

        );
    }
}


const styles = StyleSheet.create({
    scrollView: {
        backgroundColor: Colors.lighter,
    },
    container: {
        flex: 1,
        marginHorizontal: 16,
    },
    title: {
        textAlign: 'center',
        marginVertical: 8,
    },
    fixToText: {
        flexDirection: 'row',
        justifyContent: 'space-between',
    },
    separator: {
        marginVertical: 8,
        borderBottomColor: '#737373',
        borderBottomWidth: StyleSheet.hairlineWidth,
    },
});