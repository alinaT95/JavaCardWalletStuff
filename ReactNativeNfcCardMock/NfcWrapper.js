import React, { Component } from 'react';

import {
    NativeModules,
    Alert
} from 'react-native';

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

    getTonAppletState(){
        NativeModules.NfcCardModule.getTonAppletState()
            .then((result) => showResponse("TonWallet applet state: " + result))
            .catch((e) => showError(e.message))
    }

    getPublicKeyForDefaultPath(){
        NativeModules.NfcCardModule.getPublicKeyForDefaultPath()
            .then((result) => showResponse("Public key for default HD path m/44'/396'/0'/0'/0' : " + result))
            .catch((e) => showError(e.message))
    }

    turnOnWallet(newPin, authenticationPassword, commonSecret, initialVector) {
        NativeModules.NfcCardModule.turnOnWallet(
            newPin,
            authenticationPassword,
            commonSecret,
            initialVector)
            .then((result) => showResponse("TonWalletApplet state : " + result))
            .catch((e) => showError(e.message))
    }
}