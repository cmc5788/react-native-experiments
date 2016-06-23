'use strict';

function DetailPagePresenter() {

  this.goBackClicked = () => {
    console.log('DetailPagePresenter goBackClicked');
    this.nav.goBack();
  };

  this.init = () => {
    console.log('DetailPagePresenter init');
  };

  this.back = () => {
    console.log('DetailPagePresenter back');
  };

  this.destroy = () => {
    console.log('DetailPagePresenter destroy');
  };
}

module.exports = DetailPagePresenter;
