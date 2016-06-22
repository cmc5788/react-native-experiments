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

    const activePresenters = { };

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

    DeviceEventEmitter.addListener('onGoBack', (evt) => {
      if (appPresenter.back && appPresenter.back()) return;
      for (var p in activePresenters) {
        if (activePresenters[p] &&
            activePresenters[p].back &&
            typeof activePresenters[p].back === 'function' &&
            activePresenters[p].back()) {
          return;
        }
      }
      Navigator.goBack();
    });

    DeviceEventEmitter.addListener('onInitView', (evt) => {
      if (appPresenter.viewPresenters) {
        if (appPresenter.viewPresenters[evt.tag]) {
          const presenterCtor = appPresenter.viewPresenters[evt.tag]();
          activePresenters[evt.tag] = BuildPresenter(
            presenterCtor, evt.tag, ViewEventSender(evt.tag));

          // auto register event listeners for presenter based on prop names
          // TODO - clean this code up
          for (var prop in activePresenters[evt.tag]) {
            if (prop !== 'init' && prop !== 'destroy' && prop !== 'back' &&
                typeof activePresenters[evt.tag][prop] === 'function') {
              console.log(`adding listener for ${evt.tag}.${prop}`);
              activePresenters[evt.tag][prop + 'Sub'] = DeviceEventEmitter.addListener(
                `${evt.tag}.${prop}`, activePresenters[evt.tag][prop]);
            }
          }

          activePresenters[evt.tag].init();
        }
      }
    });

    DeviceEventEmitter.addListener('onDestroyView', (evt) => {
      if (activePresenters[evt.tag]) {
        activePresenters[evt.tag].destroy();

        // auto cleanup event listeners for presenter based on prop names
        // TODO - clean this code up
        for (var prop in activePresenters[evt.tag]) {
          if (prop !== 'init' && prop !== 'destroy' && prop !== 'back' &&
              typeof activePresenters[evt.tag][prop] === 'function') {
            activePresenters[evt.tag][prop + 'Sub'] && console.log(`removing listener for ${evt.tag}.${prop}`);
            activePresenters[evt.tag][prop + 'Sub'] && activePresenters[evt.tag][prop + 'Sub'].remove();
            activePresenters[evt.tag][prop + 'Sub'] = null;
          }
        }

        activePresenters[evt.tag] = null;
      }
    });
  }

});
