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

    const ignoreNextDestroy = { };

    const onAppInit = (evt) => {
      Navigator.restoreViewStates().then((states) => {
        if (states) {
          for (var stateProp in states) {
            states[stateProp] = JSON.parse(states[stateProp]);
          }
          window.___globalStates = states;
        }
        window.___globalStatesInitialized = true;
        for (var p in activePresenters) {
          if (activePresenters[p] &&
              activePresenters[p].restoreState &&
              typeof activePresenters[p].restoreState === 'function') {
            activePresenters[p].restoreState();
          }
        }
      });

      if (!appPresenter.emptyView) throw new Error('emptyView required.');
      Navigator.empty(appPresenter.emptyView);
      if (appPresenter.init) appPresenter.init();
    };

    const onAppResume = (evt) => {
      if (appPresenter.resume) appPresenter.resume();
      for (var p in activePresenters) {
        if (activePresenters[p] &&
            activePresenters[p].resume &&
            typeof activePresenters[p].resume === 'function') {
          activePresenters[p].resume();
        }
      }
    };

    const onAppPause = (evt) => {
      for (var p in activePresenters) {
        if (activePresenters[p] &&
            activePresenters[p].saveState &&
            typeof activePresenters[p].saveState === 'function') {
          activePresenters[p].saveState();
        }
      }
      const states = window.___globalStates || (window.___globalStates = { });
      for (var stateProp in states) {
        states[stateProp] = JSON.stringify(states[stateProp]);
      }
      Navigator.saveViewStates(states);
      if (appPresenter.pause) appPresenter.pause();
      for (var p in activePresenters) {
        if (activePresenters[p] &&
            activePresenters[p].pause &&
            typeof activePresenters[p].pause === 'function') {
          activePresenters[p].pause();
        }
      }
    };

    const onGoBack = (evt) => {
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
    };

    const onDestroyView = (evt) => {
      if (ignoreNextDestroy.tag) {
        if (evt.tag === ignoreNextDestroy.tag) {
          ignoreNextDestroy.tag = null;
          return;
        }
        throw Error(
          `expected onDestroyView ${ignoreNextDestroy.tag}, got ${evt.tag}`);
      }
      const presenter = activePresenters[evt.tag];
      if (presenter) {

        if (presenter.saveState &&
            typeof presenter.saveState === 'function') {
          presenter.saveState(evt.permanent);
        }

        presenter.destroy();

        // auto cleanup event listeners for presenter based on prop names
        // TODO - clean this code up
        for (var prop in presenter) {
          if (prop !== 'init' && prop !== 'destroy' && prop !== 'back' &&
              prop !== 'sub' && prop !== 'unsub' &&
              prop !== 'pause' && prop !== 'resume' &&
              prop !== 'saveState' && prop !== 'restoreState' &&
              prop !== 'afterRestore' && prop !== 'beforeSave' &&
              typeof presenter[prop] === 'function') {
            presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`] &&
              console.log(`removing listener for ${evt.tag}.${prop}`);
            presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`] &&
              presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`].remove();
            presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`] &&
              presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`]
              .subscriber.removeAllSubscriptions(`${evt.tag}.${prop}`);
            presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`] &&
              (presenter[`${prop}_DEVICE_EVENT_SUBSCRIPTION`] = null);
          }
        }

        activePresenters[evt.tag] = null;
      }
    };

    const onInitView = (evt) => {
      if (appPresenter.viewPresenters) {
        if (appPresenter.viewPresenters[evt.tagBase]) {
          const presenterCtor = appPresenter.viewPresenters[evt.tagBase]();

          if (activePresenters[evt.tag]) {
            onDestroyView(evt);
            ignoreNextDestroy.tag = evt.tag;
          }

          activePresenters[evt.tag] = BuildPresenter(
            presenterCtor, evt.tag, evt.tagBase, evt.tagExtras,
            ViewEventSender(evt.tag));

          const presenter = activePresenters[evt.tag];

          // auto register event listeners for presenter based on prop names
          // TODO - clean this code up
          for (var prop in presenter) {
            if (prop !== 'init' && prop !== 'destroy' && prop !== 'back' &&
                prop !== 'sub' && prop !== 'unsub' &&
                prop !== 'pause' && prop !== 'resume' &&
                prop !== 'saveState' && prop !== 'restoreState' &&
                prop !== 'afterRestore' && prop !== 'beforeSave' &&
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

          if (presenter.restoreState &&
              typeof presenter.restoreState === 'function') {
            presenter.restoreState();
          }
        }
      }
    };

    DeviceEventEmitter.addListener('onAppInit', onAppInit);
    DeviceEventEmitter.addListener('onAppResume', onAppResume);
    DeviceEventEmitter.addListener('onAppPause', onAppPause);
    DeviceEventEmitter.addListener('onGoBack', onGoBack);
    DeviceEventEmitter.addListener('onInitView', onInitView);
    DeviceEventEmitter.addListener('onDestroyView', onDestroyView);
  }

});
