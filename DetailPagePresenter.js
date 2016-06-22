'use strict';

const wait = (time) => new Promise((resolve) => {
  setTimeout(() => {
    resolve();
  }, time);
});

function DetailPagePresenter() {

  this.goBackClicked = (evt) => {
    console.log('DetailPagePresenter goBackClicked');
    this.nav.goBack().then((wentBack) =>
      console.log(`goBack went back? ${JSON.stringify(wentBack)}`));
  };

  this.init = () => {
    console.log('DetailPagePresenter init');
  };

  this.destroy = () => {
    console.log('DetailPagePresenter destroy');
  };
}

module.exports = DetailPagePresenter;
