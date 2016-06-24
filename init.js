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

          const presenter = activePresenters[evt.tag];

          // auto register event listeners for presenter based on prop names
          // TODO - clean this code up
          for (var prop in presenter) {
            if (prop !== 'init' && prop !== 'destroy' && prop !== 'back' &&
                prop !== 'sub' && prop !== 'unsub' &&
                typeof presenter[prop] === 'function') {
              console.log(`adding listener for ${evt.tag}.${prop}`);
              presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`] =
                DeviceEventEmitter.addListener(
                  `${evt.tag}.${prop}`, presenter[prop]);
            }
          }

          presenter.unsub = (prop) => {
            if (presenter[prop]) {
              presenter[prop].unsubscribe();
              presenter[prop] = null;
              console.log(`${prop} unsubscribed.`);
            }
          };

          presenter.sub = (prop, obs, next, err, complete) => {
            presenter[prop] = obs.subscribe(next, err, complete);
            console.log(`${prop} subscribed.`);
          };

          presenter.init();
        }
      }
    });

    DeviceEventEmitter.addListener('onDestroyView', (evt) => {
      const presenter = activePresenters[evt.tag];
      if (presenter) {
        presenter.destroy();

        // auto cleanup event listeners for presenter based on prop names
        // TODO - clean this code up
        for (var prop in presenter) {
          if (prop !== 'init' && prop !== 'destroy' && prop !== 'back' &&
              prop !== 'sub' && prop !== 'unsub' &&
              typeof presenter[prop] === 'function') {
            presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`] &&
              console.log(`removing listener for ${evt.tag}.${prop}`);
            presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`] &&
              presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`].remove();
            presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`] &&
              (presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`] = null);
          }
        }

        activePresenters[evt.tag] = null;
      }
    });
  }

});
