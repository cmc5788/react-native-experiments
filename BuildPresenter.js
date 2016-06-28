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

  presenter.___sendStateCtr = 0;

  presenter.view.sendState = (key, val) => {
    return new Promise((resolve) => {
      const ctr = presenter.___sendStateCtr++;
      presenter.view.state[`${ctr}___sendState___${key}`] = val;
      const sendObj = { };
      sendObj[key] = val;
      resolve(presenter.view.send(sendObj));
    });
  };

  presenter.view.restoreSentState = () => {
    return new Promise((resolve) => {
      const sentStates = [ ];
      for (var stateProp in presenter.view.state) {
        if (stateProp.contains('___sendState___')) {
          const prefixStart = stateProp.indexOf('___sendState___');
          const prefixEnd = prefixStart + '___sendState___'.length;
          sentStates.push({
            index: parseInt(stateProp.substring(0, prefixStart)),
            key: stateProp.substring(prefixEnd),
            val: presenter.view.state[stateProp]
          });
        }
      }
      if (sentStates.length) {
        resolve(presenter.view.sendBatch(
          sentStates.sort((a, b) => a.index - b.index).map((v) => {
            const o = { };
            o[v.key] = v.val;
            return o;
          })));
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
