import {
  DeviceEventEmitter
} from 'react-native';
import nav from './MyNavigator';

let buttonClickedSub;

const wait = (time) => new Promise((resolve) => {
  setTimeout(() => {
    resolve();
  }, time);
});

const presenter = (tag, view) => ({

  buttonClicked(evt) {
    console.log('HomePagePresenter buttonClicked');
    view.send({ setButtonColor: '#0000FF' }) // BLUE
      .then(() => wait(3000))
      .then(() => view.send({ setButtonColor: '#00FF00' })) // GREEN
      .then(() => wait(3000))
      .then(() => view.send({ setButtonColor: '#FF00FF' })) // MAGENTA
      .then(() => wait(3000))
      .then(() => nav.goBack());
  },

  init() {
    console.log('HomePagePresenter init');
    buttonClickedSub = DeviceEventEmitter.addListener(
      'HomePageView.ButtonClicked', this.buttonClicked);
  },

  destroy() {
    console.log('HomePagePresenter destroy');
    buttonClickedSub && buttonClickedSub.remove();
    buttonClickedSub = null;
  }

});

module.exports = presenter;
