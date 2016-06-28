'use strict';

import Rx from 'rxjs';
import nav from './MyNavigator';
import net from './net';

const makeObservablesFromFuncs = (obj) => {
  for (var prop in obj) {
    if (obj[prop] && typeof obj[prop] === 'function') {
      const func = obj[prop];
      obj[`${prop}Obs`] = function() {
        const _this = this;
        const _args = arguments;
        return Rx.Observable.defer(() => {
          let funcResult;
          try {
            funcResult = func.apply(_this, _args);
          } catch(err) {
            return Rx.Observable.throw(err);
          }
          if (funcResult instanceof Promise) {
            return Rx.Observable.fromPromise(funcResult);
          }
          return Rx.Observable.of(funcResult);
        });
      };
    }
  }
};

module.exports = (presenterFunc, tag, tagBase, tagExtras, view) => {
  const presenter = new presenterFunc();
  presenter.tag = tag;
  presenter.tagBase = tagBase;
  presenter.tagExtras = tagExtras;
  presenter.view = view;
  presenter.nav = nav;
  presenter.net = net;

  presenter.view.state = { };

  presenter.view.sendState = (key, val) => {
    return new Promise((resolve) => {
      const sendStates = presenter.view.state.___sendStates ||
        (presenter.view.state.___sendStates = [ ]);
      const sendObj = { };
      sendObj[key] = val;
      sendStates.push(sendObj);
      resolve(presenter.view.send(sendObj));
    });
  };

  presenter.view.restoreSentState = () => {
    return new Promise((resolve) => {
      const sendStates = presenter.view.state.___sendStates;
      if (sendStates && sendStates.length) {
        resolve(presenter.view.sendBatch(sendStates));
      } else {
        resolve();
      }
    });
  };

  makeObservablesFromFuncs(presenter.view);
  makeObservablesFromFuncs(presenter.nav);
  makeObservablesFromFuncs(presenter.net);

  presenter.saveState = (permanentlyDestroying) => {
    presenter.___viewStateSaved = true;
    return new Promise((resolve) => {
      if (permanentlyDestroying) {
        presenter.view.state = { };
      } else
      if (presenter.beforeSave &&
          typeof presenter.beforeSave === 'function') {
        presenter.beforeSave();
      }
      const states = window.___globalStates || (window.___globalStates = { });
      states[presenter.tag] = presenter.view.state;
      resolve();
    })
    .then(() => console.log(`state saved for ${presenter.tag}`));
  };

  presenter.restoreState = () => {
    if (!window.___globalStatesInitialized || presenter.___viewStateSaved) {
      return new Promise((resolve) => { resolve(); });
    }
    return new Promise((resolve) => {
      const states = window.___globalStates || (window.___globalStates = { });
      presenter.view.state = states[presenter.tag] || presenter.view.state;
      if (presenter.afterRestore &&
          typeof presenter.afterRestore === 'function') {
        presenter.afterRestore();
      }
      resolve();
    })
    .then(() => console.log(`state restored for ${presenter.tag}`));
  };

  return presenter;
};
