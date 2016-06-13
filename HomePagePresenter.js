import {
  DeviceEventEmitter
} from 'react-native';
import Sender from './ViewEventSender';
import Navigator from './MyNavigator';

let buttonClickedSub;

const wait = (time) => new Promise((resolve) => {
  setTimeout(() => {
    resolve();
  }, time);
});

const presenter = {

  buttonClicked(evt) {
    console.log('HomePagePresenter buttonClicked');
    Sender.sendToView(
        'HomePageView', { setButtonColor: '#0000FF' }) // BLUE
      .then(() => wait(3000))
      .then(() => Sender.sendToView(
        'HomePageView', { setButtonColor: '#00FF00' })) // GREEN
      .then(() => wait(3000))
      .then(() => Sender.sendToView(
        'HomePageView', { setButtonColor: '#FF00FF' })) // MAGENTA
      .then(() => wait(3000))
      .then(() => Navigator.goBack());
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

};

module.exports = presenter;
