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

  makeObservablesFromFuncs(presenter.view);
  makeObservablesFromFuncs(presenter.nav);
  makeObservablesFromFuncs(presenter.net);

  presenter.state = { };

  presenter.saveState = () => {
    nav.saveState(presenter.tag, JSON.stringify(presenter.state))
      .then(() => console.log(`state saved for ${presenter.tag}`));
  };

  presenter.restoreState = () => {
    nav.restoreState(presenter.tag)
      .then((state) => { presenter.state = (state && JSON.parse(state)) || presenter.state; })
      .then(() => console.log(`state restored for ${presenter.tag}`));
  };

  return presenter;
};
