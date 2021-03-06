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

  // TODO option to simplify state stack less aggressively, using whole object
  // equality instead of keys only?
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
      for (let i=sendStates.length-1; i>=0; i--) {
        let restartOuter = false;
        const stateOuter = sendStates[i];
        for (let j=i-1; j>=0; j--) {
          const stateInner = sendStates[j];
          if (_.head(_.keys(stateInner)) === _.head(_.keys(stateOuter))) {
            sendStates.splice(j, 1);
            restartOuter = true;
            break;
          }
        }
        if (restartOuter) i = sendStates.length;
      }

      resolve(presenter.view.send(sendObj));
    });
  };

  presenter.view.___restoreSentStates = () => {
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

  presenter.___save = (permanentlyDestroying) => {
    if (!presenter.___restored) {
      return new Promise((resolve) => { resolve(); });
    }
    return new Promise((resolve) => {
      if (permanentlyDestroying) {
        presenter.view.state = { };
      } else
      if (presenter.save &&
          typeof presenter.save === 'function') {
        presenter.save();
      }
      const states = window.___globalStates || (window.___globalStates = { });
      states[presenter.tag] = _.cloneDeep(presenter.view.state);
      resolve();
    })
    .then(() => console.log(`state saved for ${presenter.tag}`));
  };

  presenter.___restore = () => {
    if (!window.___globalStatesInitialized) {
      return new Promise((resolve) => { resolve(); });
    }
    presenter.___restored = true;
    return new Promise((resolve) => {
      const states = window.___globalStates || (window.___globalStates = { });
      const savedState = states[presenter.tag];
      presenter.view.state = (savedState && _.cloneDeep(savedState)) ||
        presenter.view.state;
      if (_.get(presenter, 'view.state.___sendStates.length')) {
        presenter.view.___restoreSentStates();
      }
      if (presenter.init &&
          typeof presenter.init === 'function') {
        presenter.init();
      }
      resolve(presenter.view.send({ ___ackInit: true }));
    })
    .then(() => console.log(`state restored for ${presenter.tag}`));
  };

  return presenter;
};
