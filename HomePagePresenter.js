'use strict';

import {
  DeviceEventEmitter
} from 'react-native';
import vp from './ViewPresenter';
import nav from './MyNavigator';

const wait = (time) => new Promise((resolve) => {
  setTimeout(() => {
    resolve();
  }, time);
});

module.exports = (tag, view) => vp((me) => ({

  buttonClicked: (evt) => {
    console.log('HomePagePresenter buttonClicked');
    view.send({ setButtonColor: '#0000FF' }) // BLUE
      .then(() => wait(3000))
      .then(() => view.send({ setButtonColor: '#00FF00' })) // GREEN
      .then(() => wait(3000))
      .then(() => view.send({ setButtonColor: '#FF00FF' })) // MAGENTA
      .then(() => wait(3000))
      .then(() => nav.goBack())
      .then((wentBack) =>
        console.log(`goBack went back? ${JSON.stringify(wentBack)}`));
  },

  init: () => {
    console.log('HomePagePresenter init');
    me.buttonClickedSub = DeviceEventEmitter.addListener(
      'HomePageView.ButtonClicked', me.buttonClicked);
  },

  destroy: () => {
    console.log('HomePagePresenter destroy');
    me.buttonClickedSub && me.buttonClickedSub.remove();
    me.buttonClickedSub = null;
  }

}));
