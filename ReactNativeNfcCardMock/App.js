/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {Component} from 'react';

import {
    SafeAreaView,
    StyleSheet,
    ScrollView,
    View,
    Button,
} from 'react-native';

import {
    Colors,
} from 'react-native/Libraries/NewAppScreen';

import NfcWrapper from "./NfcWrapper";

function Separator() {
    return <View style={styles.separator}/>;
}

export default class HelloWorldApp extends Component {

    constructor(props) {
        super(props);
        this.state = {
            currentPin: '5555',
            newPin: '5555',
            wrongAuthenticationPassword: '1ADE7F020481F043749EC3C059CE8A743B2D6B1C0445F3F13857405E05BE1BA6D5550FAE8CDC7A7834CE468987B11BB9E33ECAD2B31FF7AF9F17266188BBF248BCE8D64A7FC7752A3634B23D7F748B677380CAE476877F153DEDD26B6740828B80C544BA75ACB477C8EC5563B2BB16939FA58E61BF54943F369693187D8392B3',
            authenticationPassword: '2BE000983E0A60DC9C2483B2EA6913CF6FCF79A5B6856FAA8D8E55A7B5D3F60A4D47C943922E88DAFC58E8405C3695BCFA51A53301BF0329504CB9B15C43A1E4E3B4620BCA6487C4B31809578F12BC71CDC56C9DA2D9B74DD129D5AB9A64C6593DEE829CC12CA6E1DF72B4A2A918DFC71981C068B211479BDE76E6795EC9E67E',
            commonSecret: '346243B69CF2F0910BC235CA0DD5605E7077A70D4BEA98B71C840B78B3883471',
            wrongLengthPassword: '1111',
            initialVector: '8EC699896AC80F8B2011146C06C56BA1',
        };
        nfcWrapper = new NfcWrapper();
    }

    render() {
        return (
            <SafeAreaView style={styles.container}>
                <ScrollView
                    contentInsetAdjustmentBehavior="automatic"
                    style={styles.scrollView}>
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
                    <View>
                        <Button onPress={() => {
                            nfcWrapper.turnOnWallet(this.state.newPin, this.state.wrongLengthPassword,
                                this.state.commonSecret, this.state.initialVector)
                        }} title="Try to turn on wallet by wrong length password"/>
                    </View>

                    <Separator/>
                    <View>
                        <Button onPress={() => {
                            nfcWrapper.turnOnWallet(this.state.newPin, this.state.wrongAuthenticationPassword,
                                this.state.commonSecret, this.state.initialVector)
                        }} title="Try to turn on wallet by wrong password"/>
                    </View>

                    <Separator/>
                    <View>
                        <Button onPress={() => {
                            nfcWrapper.turnOnWallet(this.state.newPin, this.state.authenticationPassword,
                                this.state.commonSecret, this.state.initialVector)
                        }} title="Turn on wallet correctly"/>
                    </View>
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
