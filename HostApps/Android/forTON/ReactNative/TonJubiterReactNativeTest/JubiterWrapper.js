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
  NativeModules
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  Style,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';


import JubiterModule from './JubiterModule';

printResults = (error, result) => {
	if (error != null){
		alert("Error: " + error);
	   }
	else {
		alert(result);
	}
}

export default class JubiterWrapper extends Component {

    turnOnWallet(pin) {	  
		NativeModules.JubiterModule.turnOnWallet(
			pin,
			(err, res) => {
				printResults(err, "turnOnWallet result: " + res);
			}
		);
    }
   
    verifyPin(pin) {
		NativeModules.JubiterModule.verifyPin(
			pin,
			(err, res) => {
				printResults(err, "verifyPin result: " + res);
			}
		);
	}
	
	getPubKey() {
		NativeModules.JubiterModule.getPublicKey(
			(err, res) => {
				printResults(err, "Public key: " + res);		
			} 
		);
	}
	
	sign(dataForSigning) {
		NativeModules.JubiterModule.sign(
			dataForSigning,
			(err, res) => {	
				printResults(err, "Signature: " + res);			
			}		
		);
	}
	
	changePin(oldPin, newPin) {
		NativeModules.JubiterModule.changePin(
			oldPin,
			newPin,
			(err, res) => {
				printResults(err, "changePin result: " + res);
			}
		);
	}
	
	generateSeed(pin) {
		NativeModules.JubiterModule.generateSeed(
			pin,
			(err , res) => {
				printResults(err, "generateSeed result: " + res);
			}
		);
	}
	
	verifyDefaultPin() {
		NativeModules.JubiterModule.verifyDefaultPin((err , res) => {
			printResults(err, "verifyDefaultPin result: " + res);		
		});
	}
	
	getMaxPinTries() {
		NativeModules.JubiterModule.getMaxPinTries((err , res) => {
			printResults(err, "MaxPinTries: " + res);
		});
	}

	getRemainingPinTries() {
		NativeModules.JubiterModule.getRemainingPinTries((err , res) => {
			printResults(err, "RemainingPinTries: " + res);
		});
	}

	getTonAppletState(){
		NativeModules.JubiterModule.getTonAppletState((err , res) => {
			printResults(err, "TonAppletState: " + res);
		});
	}

	isSeedInitialized() {
		NativeModules.JubiterModule.isSeedInitialized((err , res) => {
			printResults(err, "isSeedInitialized: " + res);
		});
	}

	turnOffColdWallet() {
		NativeModules.JubiterModule.turnOffColdWallet((err , res) => {
			printResults(err, "turnOffColdWallet result: " + res);
		});
	}

	getDeviceCert() {
		NativeModules.JubiterModule.getDeviceCert((err , res) => {
			printResults(err, "getDeviceCert: " + res);
		});
	}

	queryBattery() {
		NativeModules.JubiterModule.queryBattery((err , res) => {
			printResults(err, "queryBattery: " + res);
		});
	}

	enumApplets() {
		NativeModules.JubiterModule.enumApplets((err , res) => {
			printResults(err, "enumApplets: " + res);
		});
	}
	  
	scanDevice() {
		NativeModules.JubiterModule.scan();
	}
	  
	disconnectDevice() {
		NativeModules.JubiterModule.disconnectDevice((err , res) => {
			printResults(err, "Returned code: " + res);
		});
	}
	
	isConnected() {
		NativeModules.JubiterModule.isConnected((err , res) => {
			printResults(err, "isConnected result: " + res);
		});
	}
}