import React, { Component } from 'react';
import {
  DeviceEventEmitter,
  AppRegistry
} from 'react-native';
import MyAppRoot from './MyAppRoot';
import Navigator from './MyNavigator';

module.exports = (appPresenter) => AppRegistry.registerComponent('MainComponent', () => class extends Component {
  // this is weird and boilerplatey, find a way to get rid of it
  render = () => React.createElement(MyAppRoot, { style: { flex: 1 } })

  componentWillMount = () => {
    DeviceEventEmitter.addListener('onAppInit', (evt) => {
      // console.log('got onAppInit');
      if (!appPresenter.emptyView) throw new Error('emptyView required.');
      Navigator.empty(appPresenter.emptyView);
      if (appPresenter.init) appPresenter.init();
    });

    DeviceEventEmitter.addListener('onAppResume', (evt) => {
      // console.log('got onAppResume');
      // Navigator.debugLog('got onAppResume');
      if (appPresenter.resume) appPresenter.resume();
    });

    DeviceEventEmitter.addListener('onAppPause', (evt) => {
      // console.log('got onAppPause');
      // Navigator.debugLog('got onAppPause');
      if (appPresenter.pause) appPresenter.pause();
    });
  }
});
