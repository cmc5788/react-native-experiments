'use strict';

import _ from 'lodash';
import Rx from 'rxjs';
import nav from './MyNavigator';
import net from './net';

const makeObservablesFromFuncs = (obj) => {
  for (let prop in obj) {
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

      // remove duplicated work...
      // this only makes sense if view state sends are all idempotent.
      // need to think about this more, and whether or not it will work.
      for (let i=0; i<sendStates.length; i++) {
        let restartOuter = false;
        for (let j=i+1; j<=sendStates.length; j++) {
          const slcLen = j - i;
          const slc1 = sendStates.slice(i, j);
          const slc2 = sendStates.slice(j, j + slcLen);
          if (_.isEqual(slc1, slc2)) {
            sendStates.splice(i, slcLen);
            restartOuter = true;
            break;
          }
        }
        if (restartOuter) i = -1;
      }

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
