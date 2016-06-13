import {
  DeviceEventEmitter
} from 'react-native';

let buttonClickedSub;

const presenter = {

  buttonClicked(evt) {
    console.log('HomePagePresenter buttonClicked');
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
