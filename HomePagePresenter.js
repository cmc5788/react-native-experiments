'use strict';

import {
  DeviceEventEmitter
} from 'react-native';
import nav from './MyNavigator';

const wait = (time) => new Promise((resolve) => {
  setTimeout(() => {
    resolve();
  }, time);
});

function HomePagePresenter() {

  this.buttonClicked = (evt) => {
    console.log('HomePagePresenter buttonClicked');
    this.view.send({ setButtonColor: '#0000FF' }) // BLUE
      .then(() => wait(3000))
      .then(() => this.view.send({ setButtonColor: '#00FF00' })) // GREEN
      .then(() => wait(3000))
      .then(() => this.view.send({ setButtonColor: '#FF00FF' })) // MAGENTA
      .then(() => wait(3000))
      .then(() => nav.goBack())
      .then((wentBack) =>
        console.log(`goBack went back? ${JSON.stringify(wentBack)}`));
  };

  this.init = () => {
    console.log('HomePagePresenter init');
    this.buttonClickedSub = DeviceEventEmitter.addListener(
      'HomePageView.ButtonClicked', this.buttonClicked);
  };

  this.destroy = () => {
    console.log('HomePagePresenter destroy');
    this.buttonClickedSub && console.log('HomePagePresenter remove sub');
    this.buttonClickedSub && this.buttonClickedSub.remove();
    this.buttonClickedSub = null;
  }
}

module.exports = HomePagePresenter;
