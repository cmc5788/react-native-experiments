'use strict';

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
