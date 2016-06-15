import React, { Component } from 'react';
import {
  DeviceEventEmitter,
  AppRegistry
} from 'react-native';
import MyAppRoot from './MyAppRoot';
import Navigator from './MyNavigator';
import BuildPresenter from './BuildPresenter';
import ViewEventSender from './ViewEventSender';

module.exports = (appPresenter) =>
  AppRegistry.registerComponent('MainComponent', () =>
  class extends Component {

  // this is weird and boilerplatey, find a way to get rid of it
  render = () => React.createElement(MyAppRoot, { style: { flex: 1 } })

  componentWillMount = () => {

    DeviceEventEmitter.addListener('onAppInit', (evt) => {
      if (!appPresenter.emptyView) throw new Error('emptyView required.');
      Navigator.empty(appPresenter.emptyView);
      if (appPresenter.init) appPresenter.init();
    });

    DeviceEventEmitter.addListener('onAppResume', (evt) => {
      if (appPresenter.resume) appPresenter.resume();
    });

    DeviceEventEmitter.addListener('onAppPause', (evt) => {
      if (appPresenter.pause) appPresenter.pause();
    });

    const activePresenters = { };

    DeviceEventEmitter.addListener('onInitView', (evt) => {
      if (appPresenter.viewPresenters) {
        if (appPresenter.viewPresenters[evt.tag]) {
          const presenterCtor = appPresenter.viewPresenters[evt.tag]();
          activePresenters[evt.tag] = BuildPresenter(
            presenterCtor, evt.tag, ViewEventSender(evt.tag));
          activePresenters[evt.tag].init();
        }
      }
    });

    DeviceEventEmitter.addListener('onDestroyView', (evt) => {
      if (activePresenters[evt.tag]) {
        activePresenters[evt.tag].destroy();
        activePresenters[evt.tag] = null;
      }
    });
  }

});
