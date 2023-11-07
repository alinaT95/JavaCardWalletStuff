'use strict';
import {
    Platform
} from 'react-native'
import { NativeNfcManager, NfcManagerEmitter, callNative } from './NativeNfcManager'

const DEFAULT_REGISTER_TAG_EVENT_OPTIONS = {
    alertMessage: 'Please tap NFC tags',
    invalidateAfterFirstRead: false,
    isReaderModeEnabled: false,
    readerModeFlags: 0,
};

const NfcTech = {
    IsoDep: 'IsoDep'
}

const NfcEvents = {
    DiscoverTag: 'NfcManagerDiscoverTag',
    SessionClosed: 'NfcManagerSessionClosed',
    StateChanged: 'NfcManagerStateChanged',
}

class NfcManager {
    constructor() {
        this.cleanUpTagRegistration = false;
        this._subscribeNativeEvents();

        // if (Platform.OS === 'ios') {
        //   this._iso15693HandlerIOS = new Iso15693HandlerIOS();
        // }

        // legacy stuff
        this._clientTagDiscoveryListener = null;
        this._clientSessionClosedListener = null;
        this._subscription = null;
    }

    // -------------------------------------
    // public for both platforms
    // -------------------------------------
    setEventListener = (name, callback) => {
        const allNfcEvents = Object.keys(NfcEvents).map(k => NfcEvents[k]);
        if (allNfcEvents.indexOf(name) === -1) {
            throw new Error('no such event');
        }

        this._clientListeners[name] = callback;
    }

    start = () => callNative('start');

    registerTagEvent = (options = {}) => {
        const optionsWithDefault = {
            ...DEFAULT_REGISTER_TAG_EVENT_OPTIONS,
            ...options,
        };
        return callNative('registerTagEvent', [optionsWithDefault]);
    }

    unregisterTagEvent = () => callNative('unregisterTagEvent');

    getTag = () => callNative('getTag');

    requestTechnology = async (/*tech, */ options = {}) => {
        try {
            // if (typeof tech === 'string') {
            //     tech = [tech];
            // }

            let sessionAvailable = false;

            // // check if required session is available
            // if (Platform.OS === 'ios') {
            //     sessionAvailable = await this._isSessionExAvailableIOS();
            // } else {
                 sessionAvailable = await this._hasTagEventRegistrationAndroid();
            // }

            // make sure we do register for tag event 
            if (!sessionAvailable) {
                // if (Platform.OS === 'ios') {
                //     await this._registerTagEventExIOS(options);
                // } else {
                await this.registerTagEvent(options);
               // }

                this.cleanUpTagRegistration = true;
            }

            return callNative('requestTechnology'/*, [tech]*/);
        } catch (ex) {
            throw ex;
        }
    }

    cancelTechnologyRequest = async () => {
        if (!this.cleanUpTagRegistration) {
            await callNative('cancelTechnologyRequest');
            return;
        }

        this.cleanUpTagRegistration = false;

        // if (Platform.OS === 'ios') {
        //     let sessionAvailable = false;

        //     // because we don't know which tech currently requested
        //     // so we try both, and perform early return when hitting any
        //     sessionAvailable = await this._isSessionExAvailableIOS();
        //     if (sessionAvailable) {
        //         await this._unregisterTagEventExIOS();
        //         return;
        //     }

        //     sessionAvailable = await this._isSessionAvailableIOS();
        //     if (sessionAvailable) {
        //         await this.unregisterTagEvent();
        //         return;
        //     }
        // } else {
            await callNative('cancelTechnologyRequest');
            await this.unregisterTagEvent();
            return;
       // }
    }

    // -------------------------------------
    // public only for Android
    // -------------------------------------
    isEnabled = () => callNative('isEnabled');

    goToNfcSetting = () => callNative('goToNfcSetting');

    getLaunchTagEvent = () => callNative('getLaunchTagEvent');

    // -------------------------------------
    // (android) setTimeout works for NfcA, NfcF, IsoDep, MifareClassic, MifareUltralight
    // -------------------------------------
    setTimeout = (timeout) => callNative('setTimeout', [timeout]);

    connect = (techs) => callNative('connect', [techs]);

    close = () => callNative('close');

    // -------------------------------------
    // (android) transceive works for NfcA, NfcB, NfcF, NfcV, IsoDep and MifareUltralight
    // -------------------------------------
    transceive = (bytes) => callNative('transceive', [bytes]);

    getMaxTransceiveLength = () => callNative('getMaxTransceiveLength');


    // -------------------------------------
    // private
    // -------------------------------------
    _subscribeNativeEvents = () => {
        this._subscriptions = {}
        this._clientListeners = {};
        this._subscriptions[NfcEvents.DiscoverTag] = NfcManagerEmitter.addListener(
            NfcEvents.DiscoverTag, this._onDiscoverTag
        );

        // if (Platform.OS === 'ios') {
        //   this._subscriptions[NfcEvents.SessionClosed] = NfcManagerEmitter.addListener(
        //     NfcEvents.SessionClosed, this._onSessionClosedIOS
        //   );
        // }

        if (Platform.OS === 'android') {
            this._subscriptions[NfcEvents.StateChanged] = NfcManagerEmitter.addListener(
                NfcEvents.StateChanged, this._onStateChangedAndroid
            );
        }
    }

    _onDiscoverTag = tag => {
        const callback = this._clientListeners[NfcEvents.DiscoverTag];
        if (callback) {
            callback(tag);
        }
    }

    //   _onSessionClosedIOS = () => {
    //     const callback = this._clientListeners[NfcEvents.SessionClosed];
    //     if (callback) {
    //       callback();
    //     }
    //   }

    _onStateChangedAndroid = state => {
        const callback = this._clientListeners[NfcEvents.StateChanged];
        if (callback) {
            callback(state);
        }
    }

    // -------------------------------------
    // Android private
    // -------------------------------------
    _hasTagEventRegistrationAndroid = () => callNative('hasTagEventRegistration');
}

export default new NfcManager();

export {
    NfcTech,
    NfcEvents
}