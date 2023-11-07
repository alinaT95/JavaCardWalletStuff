import React, { Component } from 'react';

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
    Alert
} from 'react-native';

import {
    Header,
    LearnMoreLinks,
    Colors,
    Style,
    DebugInstructions,
    ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';

printResults = (error, result) => {
    if (error != null) {
        Alert.alert(
            'Error',
            error,
            [
                {text: 'OK', onPress: () => console.log('OK Pressed')},
            ],
            {cancelable: false}
        );
    } else {

        Alert.alert(
            'Response from card',
            result,
            [
                {text: 'OK', onPress: () => console.log('OK Pressed')},
            ],
            {cancelable: false}
        );
    }
}

showResponse = (result) => {
    Alert.alert(
        'Response from card',
        JSON.stringify(result),
        [
            {text: 'OK', onPress: () => console.log('OK Pressed')},
        ],
        {cancelable: false}
    );
}

showError = (error) => {
    Alert.alert(
        'Error',
        JSON.stringify(error),
        [
            {text: 'OK', onPress: () => console.log('OK Pressed')},
        ],
        {cancelable: false}
    );
}

export default class NfcWrapper extends Component {
    /* CoinManager commands */

    getSeVersion() {
        NativeModules.NfcCardModule.getSeVersion()
            .then((result) => showResponse("SE version : " + result)).catch((e) => showError(e.message))
    }

    getCsn() {
        NativeModules.NfcCardModule.getCsn()
            .then((result) => showResponse("CSN (SEID, Secure Element Id) : " + result)).catch((e) => showError(e.message))
    }

    getDeviceLabel() {
        NativeModules.NfcCardModule.getDeviceLabel()
            .then((result) => showResponse("Device label : " + result)).catch((e) => showError(e.message))
    }

    setDeviceLabel(label) {
        NativeModules.NfcCardModule.setDeviceLabel(label)
            .then((result) => showResponse("Set Device label status: " + result)).catch((e) => showError(e.message))
    }

    getMaxPinTries() {
        NativeModules.NfcCardModule.getMaxPinTries()
            .then((result) => showResponse("Maximum Pin tries : " + result)).catch((e) => showError(e.message))
    }

    getRemainingPinTries() {
        NativeModules.NfcCardModule.getRemainingPinTries()
            .then((result) => showResponse("Remaining Pin tries : " + result)).catch((e) => showError(e.message))
    }

    getRootKeyStatus() {
        NativeModules.NfcCardModule.getRootKeyStatus()
            .then((result) => showResponse("Root key status : " + result)).catch((e) => showError(e.message))
    }

    getAvailableMemory() {
        NativeModules.NfcCardModule.getAvailableMemory()
            .then((result) => showResponse("Available card's memory : " + result)).catch((e) => showError(e.message))
    }

    getAppsList() {
        NativeModules.NfcCardModule.getAppsList().then((result) => showResponse("Applet AIDs list : " + result)).catch((e) => showError(e.message))
    }

    generateSeed(pin) {
        NativeModules.NfcCardModule.generateSeed(pin).then((result) => showResponse("Generate seed status : " + result)).catch((e) => showError(e.message))
    }

    resetWallet() {
        NativeModules.NfcCardModule.resetWallet().then((result) => showResponse("Reset wallet status : " + result)).catch((e) => showError(e.message))
    }

    changePin(oldPin, newPin) {
        NativeModules.NfcCardModule.changePin(
            oldPin,
            newPin).then((result) => showResponse("Change Pin status : " + result)).catch((e) => showError(e.message))
    }

    /* Card activation commands (TonWalletApplet) */

    setKeyForHmac(authenticationPassword, commonSecret) {
        NativeModules.NfcCardModule.setKeyForHmac(authenticationPassword, commonSecret)
            .then((result) => showResponse("Set key for hmac status : " + result)).catch((e) => showError(e.message))
    }

    turnOnWallet(newPin, authenticationPassword, commonSecret, initialVector) {
        NativeModules.NfcCardModule.turnOnWallet(
            newPin,
            authenticationPassword,
            commonSecret,
            initialVector).then((result) => showResponse("TonWalletApplet state : " + result)).catch((e) => showError(e.message))
    }

    verifyHashOfCommonSecret(commonSecret){
        NativeModules.NfcCardModule.verifyHashOfCommonSecret(commonSecret)
            .then((result) => showResponse("verifyHashOfCommonSecret result: hash is correct"))
            .catch((e) => showError(e.message))
    }

    verifyHashOfEncryptedPassword(password, initialVector){
        NativeModules.NfcCardModule.verifyHashOfEncryptedPassword(password, initialVector)
            .then((result) => showResponse("verifyHashOfEncryptedPassword result: hash is correct"))
            .catch((e) => showError(e.message))
    }

    /* Common stuff (TonWalletApplet)  */

    getTonAppletState(){
        NativeModules.NfcCardModule.getTonAppletState()
            .then((result) => showResponse("TonWallet applet state: " + result))
            .catch((e) => showError(e.message))
    }

    /* TonWalletApplet commands (ed25519 related) */

   /* verifyDefaultPin(){
        NativeModules.NfcCardModule.verifyDefaultPin()
            .then((result) => showResponse("verifyDefaultPin result: " + result))
            .catch((e) => showError(e.message))
    }*/

    getPublicKeyForDefaultPath(){
        NativeModules.NfcCardModule.getPublicKeyForDefaultPath()
            .then((result) => showResponse("Public key for default HD path m/44'/396'/0'/0'/0' : " + result))
            .catch((e) => showError(e.message))
    }

    verifyPin(pin) {
        NativeModules.NfcCardModule.verifyPin(pin)
            .then((result) => showResponse("verifyPin result: " + result))
            .catch((e) => showError(e.message))
    }

    getPublicKey(hdIndex) {
        NativeModules.NfcCardModule.getPublicKey(hdIndex)
            .then((result) => showResponse("Public key for HD path m/44'/396'/0'/0'/" + hdIndex + "' : " + result))
          .catch((e) => showError(e.message))
    }

    signForDefaultHdPath(dataForSigning, pin) {
        NativeModules.NfcCardModule.verifyPinAndSignForDefaultHdPath(dataForSigning, pin)
            .then((result) => showResponse("Signature : " + result))
            .catch((e) => showError(e.message))
    }

    sign(dataForSigning, hdIndex, pin) {
        NativeModules.NfcCardModule.verifyPinAndSign(dataForSigning, hdIndex, pin)
            .then((result) => showResponse("Signature : " + result))
            .catch((e) => showError(e.message))
    }

    /* Keychain commands */

    resetKeyChain(){
        NativeModules.NfcCardModule.resetKeyChain().then((result) => showResponse("resetKeyChain status : " + result)).catch((e) => showError(e.message))
    }

    getKeyChainDataAboutAllKeys(){
        NativeModules.NfcCardModule.getKeyChainDataAboutAllKeys().then((result) => showResponse("getKeyChainDataAboutAllKeys status : " + result)).catch((e) => showError(e.message))
    }

    getKeyChainInfo(){
        NativeModules.NfcCardModule.getKeyChainInfo()
            .then((result) =>
                { var obj = JSON.parse(result);
                    showResponse("KeyChain info: Number of keys = " +  obj.numberOfKeys + ", Occupied size = "
                        + obj.occupiedSize + ", Free size = "  + obj.freeSize);
                })
            .catch((e) => showError(e.message))
    }

    getNumberOfKeys(){
        NativeModules.NfcCardModule.getNumberOfKeys().then((result) => showResponse("Number of keys : " + result)).catch((e) => showError(e.message))
    }

    getOccupiedStorageSize(){
        NativeModules.NfcCardModule.getOccupiedStorageSize().then((result) => showResponse("Occupied storage size : " + result)).catch((e) => showError(e.message))
    }

    getFreeStorageSize(){
        NativeModules.NfcCardModule.getFreeStorageSize().then((result) => showResponse("Free storage size : " + result)).catch((e) => showError(e.message))
    }

    getKeyFromKeyChain(keyHmac){
        NativeModules.NfcCardModule.getKeyFromKeyChain(keyHmac).then((result) => showResponse("Key : " + result)).catch((e) => showError(e.message))
    }

    addKeyIntoKeyChain(newKey){
        NativeModules.NfcCardModule.addKeyIntoKeyChain(newKey).then((result) => showResponse("Add key status (hmac of new key) : " + result))
            .catch((e) => showError(e.message))
    }

  /*  deleteKeyFromKeyChain(keyHmac){
        NativeModules.NfcCardModule.deleteKeyFromKeyChain(keyHmac).then((result) => showResponse("Delete key status (number of remained keys) : " + result))
            .catch((e) => showError(e.message))
    }

    finishDeleteKeyFromKeyChainAfterInterruption(keyHmac){
        NativeModules.NfcCardModule.finishDeleteKeyFromKeyChainAfterInterruption(keyHmac).then((result) => showResponse("Finish Delete key status after interruption (number of remained keys) : " + result))
            .catch((e) => showError(e.message))
    }*/

    changeKeyInKeyChain(newKey, oldKeyHmac){
        NativeModules.NfcCardModule.changeKeyInKeyChain(newKey, oldKeyHmac).then((result) => showResponse("Change key status (hmac of new key) : " + result))
            .catch((e) => showError(e.message))
    }

    getIndexAndLenOfKeyInKeyChain(keyHmac){
        NativeModules.NfcCardModule.getIndexAndLenOfKeyInKeyChain(keyHmac).then((result) => showResponse("getIndexAndLenOfKeyInKeyChain response : " + result))
            .catch((e) => showError(e.message))
    }

    checkAvailableVolForNewKey(keySize){
        NativeModules.NfcCardModule.checkAvailableVolForNewKey(keySize).then((result) => showResponse("checkAvailableVolForNewKey response : " + result))
            .catch((e) => showError(e.message))
    }

    checkKeyHmacConsistency(keyHmac){
        NativeModules.NfcCardModule.checkKeyHmacConsistency(keyHmac).then((result) => showResponse("checkKeyHmacConsistency response : " + result))
            .catch((e) => showError(e.message))
    }


}