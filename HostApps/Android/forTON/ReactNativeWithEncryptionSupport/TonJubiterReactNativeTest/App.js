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

import JubiterWrapper from './JubiterWrapper';


function Separator() {
  return <View style={styles.separator} />;
}

export default class HelloWorldApp extends Component {
	constructor(props) {
		super(props);
		this.state = {			
			pin:    '',
			data:   '',
			oldPin: '',
			newPin: ''
		};
		jw = new JubiterWrapper();
	}
	
    render() {
		return (	
			<SafeAreaView style={styles.container}>
				<ScrollView
				  contentInsetAdjustmentBehavior="automatic"
				  style={styles.scrollView}>

				  <Text style={{padding: 10, fontSize: 16}}>
					Enter PIN:
				  </Text>
				  
			      <TextInput
					 style={{height: 40}}
					 placeholder="PIN..."
					 onChangeText={(pin) => this.setState({pin})}
					 value={this.state.pin}
				  />
				  
				  <Text style={{padding: 10, fontSize: 16}}>
					Enter data to sign:
				  </Text>
				  
			      <TextInput
					 style={{height: 40}}
					 placeholder="Data..."
					 onChangeText={(data) => this.setState({data})}
					 value={this.state.data}
				  />
					
				  <View>
					<Button onPress={() => jw.scanDevice()} title= "Connect device" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.disconnectDevice()} title= "Disconnect device" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.isConnected() } title= "IsConnected?" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.turnOnWallet(this.state.pin)} title= "turnOnWallet" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.getPubKey()} title= "Get public key" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.verifyDefaultPin()} title= "verifyDefaultPin" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.verifyPin(this.state.pin)} title= "verifyPin" />
				  </View>
				  <Separator />
				  
				  <Text style={{padding: 10, fontSize: 16}}>
					Enter Old PIN:
				  </Text>
				  
			      <TextInput
					 style={{height: 40}}
					 placeholder="Old PIN..."
					 onChangeText={(oldPin) => this.setState({oldPin})}
					 value={this.state.oldPin}
				  />
				  
				  <Text style={{padding: 10, fontSize: 16}}>
					Enter fresh PIN:
				  </Text>
				  
			      <TextInput
					 style={{height: 40}}
					 placeholder="New PIN..."
					 onChangeText={(newPin) => this.setState({newPin})}
					 value={this.state.newPin}
				  />
				  
				  
				  <View>
					<Button onPress={() => jw.changePin(this.state.oldPin, this.state.newPin)} title= "changePin" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.sign(this.state.data)} title= "sign" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.getMaxPinTries()} title= "getMaxPinTries" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.getRemainingPinTries()} title= "getRemainingPinTries" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.getTonAppletState()} title= "getTonAppletState" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.isSeedInitialized()} title= "isSeedInitialized" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.generateSeed(this.state.pin)} title= "generateSeed" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.turnOffColdWallet()} title= "turnOffColdWallet" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.getDeviceCert()} title= "getDeviceCert" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.queryBattery()} title= "queryBattery" />
				  </View>
				  <Separator />
				  <View>
					<Button onPress={() => jw.enumApplets()} title= "enumApplets" />
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